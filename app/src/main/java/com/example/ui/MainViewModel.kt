package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioEngine
import com.example.data.local.InstaWireDatabase
import com.example.data.model.Channel
import com.example.data.model.Contact
import com.example.data.model.NoiseFilterMode
import com.example.data.model.NumberType
import com.example.data.model.Transmission
import com.example.data.model.UserIdentity
import com.example.data.repository.InstaWireRepository
import com.example.security.SecurityEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

import com.example.data.model.SubscriptionTier
import com.example.security.FirebaseBurnerProvisioningService
import com.example.security.FirebaseProvisioningResponse

sealed class WalkieTarget {
    data class ChannelTarget(val channel: Channel) : WalkieTarget()
    data class ContactTarget(val contact: Contact) : WalkieTarget()
}

enum class PttState {
    IDLE,
    TRANSMITTING,
    INCOMING_TRANSMISSION
}

data class UiState(
    val activeTarget: WalkieTarget? = null,
    val pttState: PttState = PttState.IDLE,
    val transmitElapsedSeconds: Float = 0f,
    val isSafetyKeyModalOpen: Boolean = false,
    val isNoiseCancelModalOpen: Boolean = false,
    val isBurnerStoreModalOpen: Boolean = false,
    val isSubscriptionModalOpen: Boolean = false,
    val isPhoneConfirmModalOpen: Boolean = false,
    val isTermsModalOpen: Boolean = false,
    val isAddContactModalOpen: Boolean = false,
    val isAddChannelModalOpen: Boolean = false,
    val isThemeLayoutModalOpen: Boolean = false,
    val isPurchaseModalOpen: Boolean = false,
    val pendingPurchaseItemKey: String? = null,
    val pendingPurchaseTitle: String = "",
    val pendingPurchasePrice: String = "$0.99",
    val pendingPurchaseDescription: String = "",
    val isFirebaseProvisioning: Boolean = false,
    val lastFirebaseProvisioningResult: FirebaseProvisioningResponse? = null,
    val lastVerifiedNotification: String? = null,
    val activeTab: Int = 0 // 0 = Radio/PTT, 1 = Channels, 2 = Burner, 3 = Settings, 4 = Logs
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = InstaWireDatabase.getDatabase(application, viewModelScope)
    private val repository = InstaWireRepository(database.instaWireDao())
    val audioEngine = AudioEngine(application, viewModelScope)

    val userIdentity: StateFlow<UserIdentity> = repository.userIdentity
        .combine(MutableStateFlow(UserIdentity())) { dbUser, defaultUser ->
            dbUser ?: defaultUser
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserIdentity())

    val contacts: StateFlow<List<Contact>> = repository.allContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val channels: StateFlow<List<Channel>> = repository.allChannels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentTransmissions: StateFlow<List<Transmission>> = repository.recentTransmissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val burnerLines: StateFlow<List<com.example.data.model.BurnerLine>> = repository.allBurnerLines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var transmitTimerJob: Job? = null
    private var simulatedChatterJob: Job? = null

    init {
        // Automatically select the first channel once channels load
        viewModelScope.launch {
            channels.collect { channelList ->
                if (channelList.isNotEmpty() && _uiState.value.activeTarget == null) {
                    _uiState.value = _uiState.value.copy(
                        activeTarget = WalkieTarget.ChannelTarget(channelList.first())
                    )
                }
            }
        }

        // Ephemeral log cleaner
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(15000)
                val identity = userIdentity.value
                if (identity.zeroLogsEnabled) {
                    repository.purgeExpiredTransmissions(identity.ephemeralTimeoutSeconds)
                }
            }
        }
    }

    fun selectTarget(target: WalkieTarget) {
        _uiState.value = _uiState.value.copy(activeTarget = target)
        audioEngine.playSquelchBurst()
    }

    fun setActiveTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(activeTab = tabIndex)
    }

    fun connectToSupportChannel() {
        val supportChan = channels.value.find { it.id == "tacops_support" }
        if (supportChan != null) {
            _uiState.value = _uiState.value.copy(
                activeTarget = WalkieTarget.ChannelTarget(supportChan),
                activeTab = 0,
                lastVerifiedNotification = "Tuned into TacOps 24/7 Specialist Support Channel. Press PTT to speak."
            )
        } else {
            _uiState.value = _uiState.value.copy(activeTab = 0)
        }
    }

    fun onPttPressed() {
        if (_uiState.value.pttState == PttState.TRANSMITTING) return

        val identity = userIdentity.value
        _uiState.value = _uiState.value.copy(
            pttState = PttState.TRANSMITTING,
            transmitElapsedSeconds = 0f
        )

        audioEngine.triggerPttPress(
            enableChirp = identity.chirpSoundEnabled,
            soundProfile = identity.soundProfile
        )

        transmitTimerJob?.cancel()
        transmitTimerJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            while (true) {
                delay(100)
                val elapsed = (System.currentTimeMillis() - startTime) / 1000f
                _uiState.value = _uiState.value.copy(transmitElapsedSeconds = elapsed)
            }
        }
    }

    fun onPttReleased() {
        if (_uiState.value.pttState != PttState.TRANSMITTING) return

        transmitTimerJob?.cancel()
        val duration = _uiState.value.transmitElapsedSeconds
        val identity = userIdentity.value
        val target = _uiState.value.activeTarget

        audioEngine.triggerPttRelease(
            enableRogerBeep = identity.rogerBeepEnabled,
            soundProfile = identity.soundProfile
        ) { recordedDuration, waveAmps ->
            _uiState.value = _uiState.value.copy(
                pttState = PttState.IDLE,
                transmitElapsedSeconds = 0f
            )

            // Record transmission in local Room DB
            viewModelScope.launch(Dispatchers.IO) {
                val targetType = when (target) {
                    is WalkieTarget.ChannelTarget -> "CHANNEL"
                    is WalkieTarget.ContactTarget -> "DIRECT"
                    null -> "CHANNEL"
                }
                val targetId = when (target) {
                    is WalkieTarget.ChannelTarget -> target.channel.id
                    is WalkieTarget.ContactTarget -> target.contact.number
                    null -> "tactical_alpha"
                }

                repository.recordTransmission(
                    senderName = "Me (${identity.callsign})",
                    senderNumber = identity.activeDisplayNumber,
                    senderCallsign = identity.callsign,
                    targetType = targetType,
                    targetId = targetId,
                    durationSec = if (duration > 0.5f) duration else recordedDuration,
                    waveAmps = waveAmps
                )

                // Trigger realistic simulated radio reply from channel/contact after a brief pause
                triggerRealisticRadioReply(target)
            }
        }
    }

    private fun triggerRealisticRadioReply(target: WalkieTarget?) {
        simulatedChatterJob?.cancel()
        simulatedChatterJob = viewModelScope.launch(Dispatchers.IO) {
            delay(2200) // Realistic walkie response delay
            val identity = userIdentity.value
            if (target is WalkieTarget.ChannelTarget) {
                val isSupport = target.channel.id == "tacops_support"
                val senderName = if (isSupport) {
                    when (identity.subscriptionTier) {
                        SubscriptionTier.GHOST_SENTINEL -> "Cmdr. Vance [Dedicated VIP Lead]"
                        SubscriptionTier.BLACK_OPS -> "Specialist Novak [Priority Queue 1]"
                        SubscriptionTier.PRO -> "Specialist Miller [Tech Support]"
                        SubscriptionTier.FREE -> "TacOps AI Automated Diagnostics"
                    }
                } else {
                    listOf("Bravo-2", "Phantom-Leader", "Echo-Squad", "Valkyrie-6").random()
                }

                val replyDuration = Random.nextDouble(2.2, 4.0).toFloat()
                val waveAmps = List(8) { Random.nextInt(30, 95) }.joinToString(",")

                _uiState.value = _uiState.value.copy(pttState = PttState.INCOMING_TRANSMISSION)
                audioEngine.playTransmissionAudio(replyDuration, waveAmps)

                repository.recordTransmission(
                    senderName = senderName,
                    senderNumber = if (isSupport) "+1 (800) TACOPS-HQ" else "+1 (888) WIRE-${Random.nextInt(1000, 9999)}",
                    senderCallsign = if (isSupport) "TACOPS-DISPATCH" else senderName,
                    targetType = "CHANNEL",
                    targetId = target.channel.id,
                    durationSec = replyDuration,
                    waveAmps = waveAmps
                )

                delay((replyDuration * 1000).toLong())
                _uiState.value = _uiState.value.copy(pttState = PttState.IDLE)
            } else if (target is WalkieTarget.ContactTarget) {
                val replyDuration = Random.nextDouble(2.0, 3.8).toFloat()
                val waveAmps = List(8) { Random.nextInt(35, 99) }.joinToString(",")

                _uiState.value = _uiState.value.copy(pttState = PttState.INCOMING_TRANSMISSION)
                audioEngine.playTransmissionAudio(replyDuration, waveAmps)

                repository.recordTransmission(
                    senderName = target.contact.name,
                    senderNumber = target.contact.number,
                    senderCallsign = target.contact.callsign,
                    targetType = "DIRECT",
                    targetId = target.contact.number,
                    durationSec = replyDuration,
                    waveAmps = waveAmps
                )

                delay((replyDuration * 1000).toLong())
                _uiState.value = _uiState.value.copy(pttState = PttState.IDLE)
            }
        }
    }

    fun setLayoutType(layout: com.example.data.model.WalkieLayoutType) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userIdentity.value
            if (!user.isLayoutUnlocked(layout)) {
                // Trigger $0.99 purchase modal
                openPurchaseModal(
                    itemKey = layout.name,
                    title = layout.title,
                    price = layout.priceDisplay,
                    description = layout.description
                )
                return@launch
            }
            repository.setLayoutType(layout)
            audioEngine.playKeyVerifiedTone()
            _uiState.value = _uiState.value.copy(
                lastVerifiedNotification = "Layout Changed: ${layout.title}"
            )
        }
    }

    fun setThemeScheme(scheme: com.example.data.model.AppThemeScheme) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userIdentity.value
            if (!user.isThemeUnlocked(scheme)) {
                // Trigger $0.99 purchase modal
                openPurchaseModal(
                    itemKey = scheme.name,
                    title = "${scheme.title} Color Palette",
                    price = scheme.priceDisplay,
                    description = "${scheme.subtitle} • High-contrast visual styling for all PTT controls & HUD"
                )
                return@launch
            }
            repository.setThemeScheme(scheme)
            audioEngine.playKeyVerifiedTone()
            _uiState.value = _uiState.value.copy(
                lastVerifiedNotification = "Theme Updated: ${scheme.title}"
            )
        }
    }

    fun setSoundProfile(profile: com.example.data.model.PttSoundProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userIdentity.value
            if (!user.isSoundUnlocked(profile)) {
                // Trigger $0.99 purchase modal
                openPurchaseModal(
                    itemKey = profile.name,
                    title = "${profile.title} PTT Sound Pack",
                    price = profile.priceDisplay,
                    description = "${profile.description} • High-fidelity audio chirps for PTT transmit and release"
                )
                return@launch
            }
            repository.setSoundProfile(profile)
            audioEngine.previewSound(profile, isPress = true)
            _uiState.value = _uiState.value.copy(
                lastVerifiedNotification = "PTT Sound Set: ${profile.title}"
            )
        }
    }

    fun openPurchaseModal(itemKey: String, title: String, price: String, description: String) {
        _uiState.value = _uiState.value.copy(
            isPurchaseModalOpen = true,
            pendingPurchaseItemKey = itemKey,
            pendingPurchaseTitle = title,
            pendingPurchasePrice = price,
            pendingPurchaseDescription = description
        )
        audioEngine.playSquelchBurst()
    }

    fun closePurchaseModal() {
        _uiState.value = _uiState.value.copy(
            isPurchaseModalOpen = false,
            pendingPurchaseItemKey = null
        )
    }

    fun completePurchase(itemKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (itemKey == "ALL_THEMES_PASS") {
                repository.purchaseAllThemesPass()
            } else {
                repository.unlockItem(itemKey)
                // If it's a layout, also activate it
                val matchingLayout = com.example.data.model.WalkieLayoutType.values().find { it.name == itemKey }
                if (matchingLayout != null) {
                    repository.setLayoutType(matchingLayout)
                }
                // If it's a theme, also activate it
                val matchingTheme = com.example.data.model.AppThemeScheme.values().find { it.name == itemKey }
                if (matchingTheme != null) {
                    repository.setThemeScheme(matchingTheme)
                }
                // If it's a sound, also activate it
                val matchingSound = com.example.data.model.PttSoundProfile.values().find { it.name == itemKey }
                if (matchingSound != null) {
                    repository.setSoundProfile(matchingSound)
                }
            }
            audioEngine.playKeyVerifiedTone()
            _uiState.value = _uiState.value.copy(
                isPurchaseModalOpen = false,
                pendingPurchaseItemKey = null,
                lastVerifiedNotification = "Purchase Successful ($0.99)! Item Unlocked & Activated."
            )
        }
    }

    fun previewPttSound(profile: com.example.data.model.PttSoundProfile, isPress: Boolean) {
        audioEngine.previewSound(profile, isPress)
    }

    fun setCallsign(newCallsign: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userIdentity.value
            repository.updateIdentity(current.copy(callsign = newCallsign.trim().uppercase()))
            audioEngine.playKeyVerifiedTone()
            _uiState.value = _uiState.value.copy(
                lastVerifiedNotification = "Callsign Updated to ${newCallsign.trim().uppercase()}"
            )
        }
    }

    fun addAndActivateBurnerLine(number: String, label: String, areaCode: String, cityRegion: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val line = com.example.data.model.BurnerLine(
                number = number,
                label = if (label.isNotBlank()) label else "Burner Line ($areaCode)",
                areaCode = areaCode,
                cityRegion = cityRegion
            )
            repository.addBurnerLine(line)
            repository.setActiveBurnerLine(number)
            audioEngine.playKeyVerifiedTone()
            _uiState.value = _uiState.value.copy(
                lastVerifiedNotification = "Swapped Active Caller ID to $number"
            )
        }
    }

    fun activateBurnerLine(burnerLine: com.example.data.model.BurnerLine) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setActiveBurnerLine(burnerLine.number)
            audioEngine.playKeyVerifiedTone()
            _uiState.value = _uiState.value.copy(
                lastVerifiedNotification = "Swapped Active Caller ID to ${burnerLine.number}"
            )
        }
    }

    fun deleteBurnerLine(number: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteBurnerLine(number)
            val current = userIdentity.value
            if (current.burnerNumber == number) {
                repository.updateIdentity(current.copy(activeNumberType = NumberType.PHONE))
            }
            audioEngine.playSquelchBurst()
            _uiState.value = _uiState.value.copy(
                lastVerifiedNotification = "Burner Line Destroyed & De-provisioned"
            )
        }
    }

    fun playAudioClip(transmission: Transmission) {
        audioEngine.playTransmissionAudio(transmission.durationSeconds, transmission.waveAmplitudes)
    }

    fun toggleActiveNumber() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleActiveNumberType()
            audioEngine.playKeyVerifiedTone()
        }
    }

    fun setSubscriptionTier(tier: SubscriptionTier) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setSubscriptionTier(tier)
            audioEngine.playKeyVerifiedTone()
            _uiState.value = _uiState.value.copy(
                isSubscriptionModalOpen = false,
                lastVerifiedNotification = "Plan Updated: ${tier.title} Active (${tier.priceDisplay})"
            )
        }
    }

    fun generateNewBurnerNumber() {
        viewModelScope.launch(Dispatchers.IO) {
            val identity = userIdentity.value
            _uiState.value = _uiState.value.copy(isFirebaseProvisioning = true)
            try {
                // Provision temporary burner number through simulated Firebase Cloud Functions
                val fbResult = FirebaseBurnerProvisioningService.callProvisionBurnerFunction(
                    callsign = identity.callsign,
                    tierName = identity.subscriptionTier.name
                )
                repository.updateIdentity(
                    identity.copy(
                        burnerNumber = fbResult.burnerNumber,
                        hasBurnerSubscription = true,
                        activeNumberType = NumberType.BURNER
                    )
                )
                audioEngine.playKeyVerifiedTone()
                _uiState.value = _uiState.value.copy(
                    isFirebaseProvisioning = false,
                    lastFirebaseProvisioningResult = fbResult,
                    lastVerifiedNotification = "Firebase Function provisioned ${fbResult.burnerNumber} [${fbResult.region}]"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isFirebaseProvisioning = false)
            }
        }
    }

    fun setSubscriptionModalOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isSubscriptionModalOpen = open)
    }

    fun confirmPhoneNumber(newPhoneNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userIdentity.value
            val updated = current.copy(
                phoneNumber = newPhoneNumber,
                isPhoneVerified = true,
                isHumanVerified = true,
                hasAgreedToTerms = true,
                termsAgreedTimestamp = if (current.termsAgreedTimestamp > 0) current.termsAgreedTimestamp else System.currentTimeMillis(),
                activeNumberType = NumberType.PHONE
            )
            repository.updateIdentity(updated)
            audioEngine.playKeyVerifiedTone()
            _uiState.value = _uiState.value.copy(
                isPhoneConfirmModalOpen = false,
                lastVerifiedNotification = "Human Identity Verified • Zero-Knowledge Connected"
            )
        }
    }

    fun verifyHumanAndAgreeToTerms(confirmedPhoneNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userIdentity.value
            val updated = current.copy(
                phoneNumber = confirmedPhoneNumber,
                isPhoneVerified = true,
                isHumanVerified = true,
                hasAgreedToTerms = true,
                termsAgreedTimestamp = System.currentTimeMillis(),
                activeNumberType = NumberType.PHONE
            )
            repository.updateIdentity(updated)
            audioEngine.playKeyVerifiedTone()
            _uiState.value = _uiState.value.copy(
                isPhoneConfirmModalOpen = false,
                isTermsModalOpen = false,
                lastVerifiedNotification = "Human Verified & Terms Accepted • Zero-Knowledge Active"
            )
        }
    }

    fun revokeTermsAndVerification() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userIdentity.value
            val updated = current.copy(
                isPhoneVerified = false,
                isHumanVerified = false,
                hasAgreedToTerms = false,
                termsAgreedTimestamp = 0L
            )
            repository.updateIdentity(updated)
            _uiState.value = _uiState.value.copy(
                lastVerifiedNotification = "Verification Revoked. Please complete human verification to re-enter."
            )
        }
    }

    fun toggleNoiseCancellation() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleNoiseCancellation()
        }
    }

    fun setNoiseFilterMode(mode: NoiseFilterMode) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setNoiseFilterMode(mode)
        }
    }

    fun toggleHardwareVolumePtt() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userIdentity.value
            val nextState = !current.hardwareVolumePttEnabled
            repository.updateIdentity(current.copy(hardwareVolumePttEnabled = nextState))
            audioEngine.playKeyVerifiedTone()
            _uiState.value = _uiState.value.copy(
                lastVerifiedNotification = if (nextState) "Hardware Volume Up PTT Enabled" else "Hardware Volume Up PTT Disabled"
            )
        }
    }

    fun toggleBackgroundMonitoring() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userIdentity.value
            val nextState = !current.backgroundMonitoringEnabled
            repository.updateIdentity(current.copy(backgroundMonitoringEnabled = nextState))
            audioEngine.playKeyVerifiedTone()
            if (nextState) {
                com.example.service.WalkieBackgroundService.start(getApplication())
            } else {
                com.example.service.WalkieBackgroundService.stop(getApplication())
            }
            _uiState.value = _uiState.value.copy(
                lastVerifiedNotification = if (nextState) "Background Radio Monitoring Active (Rx Live)" else "Background Radio Monitoring Stopped"
            )
        }
    }

    fun toggleBackgroundAudioBeep() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userIdentity.value
            repository.updateIdentity(current.copy(backgroundAudioBeepEnabled = !current.backgroundAudioBeepEnabled))
        }
    }

    fun toggleChirp() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userIdentity.value
            repository.updateIdentity(current.copy(chirpSoundEnabled = !current.chirpSoundEnabled))
        }
    }

    fun toggleRogerBeep() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userIdentity.value
            repository.updateIdentity(current.copy(rogerBeepEnabled = !current.rogerBeepEnabled))
        }
    }

    fun toggleZeroLogs() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userIdentity.value
            repository.updateIdentity(current.copy(zeroLogsEnabled = !current.zeroLogsEnabled))
        }
    }

    fun setEphemeralTimeout(seconds: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userIdentity.value
            repository.updateIdentity(current.copy(ephemeralTimeoutSeconds = seconds))
        }
    }

    fun verifyContactKey(contactId: Long, isVerified: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setKeyVerified(contactId, isVerified)
            if (isVerified) {
                audioEngine.playKeyVerifiedTone()
            }
            // refresh active target if needed
            val currentTarget = _uiState.value.activeTarget
            if (currentTarget is WalkieTarget.ContactTarget && currentTarget.contact.id == contactId) {
                _uiState.value = _uiState.value.copy(
                    activeTarget = WalkieTarget.ContactTarget(currentTarget.contact.copy(isKeyVerified = isVerified))
                )
            }
        }
    }

    fun addContact(name: String, number: String, callsign: String, isBurner: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addContact(name, number, callsign, isBurner)
            audioEngine.playKeyVerifiedTone()
            _uiState.value = _uiState.value.copy(isAddContactModalOpen = false)
        }
    }

    fun addChannel(name: String, frequency: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addChannel(name, frequency, description)
            audioEngine.playKeyVerifiedTone()
            _uiState.value = _uiState.value.copy(isAddChannelModalOpen = false)
        }
    }

    fun wipeAllLogs() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.wipeAllLogs()
            audioEngine.playSquelchBurst()
        }
    }

    // Modal controllers
    fun setSafetyKeyModalOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isSafetyKeyModalOpen = open)
    }

    fun setNoiseCancelModalOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isNoiseCancelModalOpen = open)
    }

    fun setBurnerStoreModalOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isBurnerStoreModalOpen = open)
    }

    fun setPhoneConfirmModalOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isPhoneConfirmModalOpen = open)
    }

    fun setTermsModalOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isTermsModalOpen = open)
    }

    fun setAddContactModalOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isAddContactModalOpen = open)
    }

    fun setAddChannelModalOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isAddChannelModalOpen = open)
    }

    fun setThemeLayoutModalOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isThemeLayoutModalOpen = open)
    }

    fun clearNotification() {
        _uiState.value = _uiState.value.copy(lastVerifiedNotification = null)
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
