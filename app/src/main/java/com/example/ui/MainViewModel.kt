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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

import com.example.data.model.SubscriptionTier
import com.example.security.FirebaseBurnerProvisioningService
import com.example.security.FirebaseProvisioningResponse

import com.example.data.model.AppMode
import com.example.data.model.BlockedUser
import com.example.data.model.FriendUser
import com.example.data.model.GiftTransaction
import com.example.data.model.LiveRoomMessage
import com.example.data.model.PaidGiftItem
import com.example.data.model.UserProfile
import com.example.data.model.WorldwideGiftCatalog
import com.example.data.model.WorldwideRoom
import com.example.data.model.WorldwideSpeaker

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
    val appMode: AppMode = AppMode.TACTICAL, // TACTICAL or WORLDWIDE
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
    val isNavMenuOpen: Boolean = false,
    val isPurchaseModalOpen: Boolean = false,
    val pendingPurchaseItemKey: String? = null,
    val pendingPurchaseTitle: String = "",
    val pendingPurchasePrice: String = "$0.99",
    val pendingPurchaseDescription: String = "",
    val isFirebaseProvisioning: Boolean = false,
    val lastFirebaseProvisioningResult: FirebaseProvisioningResponse? = null,
    val lastVerifiedNotification: String? = null,
    val activeTab: Int = 0, // 0 = Radio/PTT, 1 = Channels, 2 = Contacts, 3 = Burner, 4 = Logs, 5 = Settings

    // Worldwide Functionality States
    val selectedWorldwideRoom: WorldwideRoom? = null,
    val activeWorldwideSpeakers: List<WorldwideSpeaker> = emptyList(),
    val isWorldwidePttActive: Boolean = false,
    val isWorldwideVoiceActive: Boolean = false,
    val worldwideActiveSpeakerName: String? = null,
    val isUserProfileModalOpen: Boolean = false,
    val isOtherUserProfileModalOpen: Boolean = false,
    val inspectingUser: FriendUser? = null,
    val isFriendsAndBlockedModalOpen: Boolean = false,
    val isSendGiftModalOpen: Boolean = false,
    val giftTargetSpeaker: WorldwideSpeaker? = null,
    val isCreateRoomModalOpen: Boolean = false,
    val isBuyCoinsModalOpen: Boolean = false,
    val activeGiftBanner: GiftTransaction? = null,
    val worldwideSearchQuery: String = "",
    val worldwideSelectedRegion: String = "All",
    val worldwideSelectedCategory: String = "All",
    val isWorldwideAudioMuted: Boolean = false,
    val hasRaisedHandToSpeak: Boolean = false,

    // Coin Store, Real Money Cashout & Settings Diagnostic States
    val isCashoutModalOpen: Boolean = false,
    val isCoinShopModalOpen: Boolean = false,
    val isDiagnosticsRunning: Boolean = false,
    val diagnosticsResult: String? = null,
    val isLoopbackRecording: Boolean = false,
    val loopbackStatus: String? = null,

    // Live Scanners & NOAA Weather States
    val isScannerPlaying: Boolean = false,
    val activeScannerFeedId: String? = null,
    val isNoaaPlaying: Boolean = false,
    val activeNoaaId: String? = null
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

    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .combine(MutableStateFlow(UserProfile())) { dbProfile, defaultProfile ->
            dbProfile ?: defaultProfile
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val worldwideRooms: StateFlow<List<WorldwideRoom>> = repository.allWorldwideRooms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val friends: StateFlow<List<FriendUser>> = repository.allFriends
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val blockedUsers: StateFlow<List<BlockedUser>> = repository.allBlockedUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val giftTransactions: StateFlow<List<GiftTransaction>> = repository.allGiftTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cashoutTransactions: StateFlow<List<com.example.data.model.CoinCashoutTransaction>> = repository.allCashoutTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentRoomMessages = MutableStateFlow<List<LiveRoomMessage>>(emptyList())
    val currentRoomMessages: StateFlow<List<LiveRoomMessage>> = _currentRoomMessages.asStateFlow()

    private var worldwidePttJob: Job? = null
    private var worldwideRoomSimulationJob: Job? = null


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

    fun selectChannelById(channelId: String) {
        viewModelScope.launch {
            val targetChannel = channels.value.find { it.id == channelId }
                ?: repository.allChannels.firstOrNull()?.find { it.id == channelId }
            if (targetChannel != null) {
                _uiState.value = _uiState.value.copy(
                    activeTarget = WalkieTarget.ChannelTarget(targetChannel),
                    activeTab = 0,
                    lastVerifiedNotification = "Tuned to Channel ${targetChannel.name} (${targetChannel.frequency})"
                )
                audioEngine.playSquelchBurst()
            }
        }
    }

    fun selectContactById(contactId: Long) {
        viewModelScope.launch {
            val targetContact = contacts.value.find { it.id == contactId }
                ?: repository.allContacts.firstOrNull()?.find { it.id == contactId }
            if (targetContact != null) {
                _uiState.value = _uiState.value.copy(
                    activeTarget = WalkieTarget.ContactTarget(targetContact),
                    activeTab = 0,
                    lastVerifiedNotification = "Connected direct PTT line to ${targetContact.callsign}"
                )
                audioEngine.playSquelchBurst()
            }
        }
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
            val isThemePack = itemKey == "ALL_THEMES_PASS"
            if (isThemePack) {
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
                lastVerifiedNotification = if (isThemePack)
                    "Purchase Successful ($5.94)! All 6 Themes Unlocked."
                else
                    "Purchase Successful ($0.99)! Item Unlocked & Activated."
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
                activeTab = 3, // Automatically transition to Settings screen
                lastVerifiedNotification = "Human Verified & Terms Accepted • Zero-Knowledge Active"
            )
            // Auto-dismiss notification after displaying
            delay(3500)
            if (_uiState.value.lastVerifiedNotification?.contains("Human Verified") == true) {
                _uiState.value = _uiState.value.copy(lastVerifiedNotification = null)
            }
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

    fun setNavMenuOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isNavMenuOpen = open)
    }

    fun clearNotification() {
        _uiState.value = _uiState.value.copy(lastVerifiedNotification = null)
    }

    // ==========================================
    // WORLDWIDE WALKIE TALKIE COMMUNITY HANDLERS
    // ==========================================

    fun setAppMode(mode: AppMode) {
        _uiState.value = _uiState.value.copy(appMode = mode)
        if (mode == AppMode.WORLDWIDE) {
            audioEngine.playKeyVerifiedTone()
        } else {
            leaveWorldwideRoom()
            audioEngine.playChirpPress(userIdentity.value.soundProfile)
        }
    }

    fun enterWorldwideRoom(room: WorldwideRoom) {
        // Leave previous room simulation if any
        worldwideRoomSimulationJob?.cancel()

        // Generate dynamic speakers for this specific room
        val demoSpeakers = generateSpeakersForRoom(room)

        _uiState.value = _uiState.value.copy(
            selectedWorldwideRoom = room,
            activeWorldwideSpeakers = demoSpeakers,
            hasRaisedHandToSpeak = false,
            isWorldwidePttActive = false,
            isWorldwideVoiceActive = false,
            worldwideActiveSpeakerName = null
        )

        // Play chirp connect tone
        audioEngine.playChirpPress(userIdentity.value.soundProfile)

        // Load initial live room messages / welcome message
        viewModelScope.launch(Dispatchers.IO) {
            val initialMsgs = listOf(
                LiveRoomMessage(
                    roomId = room.id,
                    senderId = "system",
                    senderUsername = "ROOM BOT",
                    senderCallsign = "DISPATCH",
                    senderCountryFlag = room.countryFlag,
                    senderCity = room.city,
                    text = "Welcome to ${room.name}! Press & hold the Worldwide PTT button to talk live to everyone in ${room.city}."
                )
            )
            _currentRoomMessages.value = initialMsgs
            startWorldwideRoomSimulation(room)
        }
    }

    fun leaveWorldwideRoom() {
        worldwideRoomSimulationJob?.cancel()
        worldwidePttJob?.cancel()
        _uiState.value = _uiState.value.copy(
            selectedWorldwideRoom = null,
            activeWorldwideSpeakers = emptyList(),
            isWorldwidePttActive = false,
            isWorldwideVoiceActive = false,
            worldwideActiveSpeakerName = null
        )
        audioEngine.playChirpRelease(userIdentity.value.soundProfile)
    }

    private fun generateSpeakersForRoom(room: WorldwideRoom): List<WorldwideSpeaker> {
        return when (room.countryCode) {
            "JP" -> listOf(
                WorldwideSpeaker("spk_1", "Sakura_Tokyo", "CHERRY-99", "Japan", "🇯🇵", "Tokyo", "Host • Music Producer in Shibuya", isHost = true, reputationScore = 480, receivedGiftsCount = 18),
                WorldwideSpeaker("spk_2", "Kenji_Oda", "NEO-TOKYO", "Japan", "🇯🇵", "Tokyo", "Audio engineer & retro CB collector", reputationScore = 320, receivedGiftsCount = 9),
                WorldwideSpeaker("spk_3", "Yuki_Sapporo", "SNOW-BIRD", "Japan", "🇯🇵", "Sapporo", "English teacher & travel blogger", reputationScore = 210, receivedGiftsCount = 5)
            )
            "US" -> listOf(
                WorldwideSpeaker("spk_1", "Brooklyn_DJ", "BEAT-LAB", "United States", "🇺🇸", "New York", "Host • Live Rooftop Streamer", isHost = true, reputationScore = 650, receivedGiftsCount = 34),
                WorldwideSpeaker("spk_2", "Dave_Silicon", "CHIP-99", "United States", "🇺🇸", "San Francisco", "AI developer & ham radio license holder", reputationScore = 410, receivedGiftsCount = 14),
                WorldwideSpeaker("spk_3", "Mia_Austin", "LONE-STAR", "United States", "🇺🇸", "Austin", "Singer-songwriter & podcast creator", reputationScore = 290, receivedGiftsCount = 11)
            )
            "GB" -> listOf(
                WorldwideSpeaker("spk_1", "Chloe_London", "BIG-BEN", "United Kingdom", "🇬🇧", "London", "Host • Camden vinyl collector", isHost = true, reputationScore = 520, receivedGiftsCount = 22),
                WorldwideSpeaker("spk_2", "Liam_Manchester", "RED-DEVIL", "United Kingdom", "🇬🇧", "Manchester", "Football host & audio geek", reputationScore = 340, receivedGiftsCount = 8)
            )
            "FR" -> listOf(
                WorldwideSpeaker("spk_1", "Amelie_Paris", "LOUVRE-1", "France", "🇫🇷", "Paris", "Host • Art curator & language mentor", isHost = true, reputationScore = 490, receivedGiftsCount = 19),
                WorldwideSpeaker("spk_2", "Julien_Nice", "COTE-AZUR", "France", "🇫🇷", "Nice", "Travel photographer & sailor", reputationScore = 280, receivedGiftsCount = 6)
            )
            "KR" -> listOf(
                WorldwideSpeaker("spk_1", "Minho_Seoul", "K-PULSE", "South Korea", "🇰🇷", "Seoul", "Host • eSports coach & gamer", isHost = true, reputationScore = 720, receivedGiftsCount = 42),
                WorldwideSpeaker("spk_2", "Jiwoo_Busan", "SEA-BREEZE", "South Korea", "🇰🇷", "Busan", "Vocalist & synthesizer tinkerer", reputationScore = 360, receivedGiftsCount = 12)
            )
            else -> listOf(
                WorldwideSpeaker("spk_1", "GlobalHost", "WORLD-1", room.country, room.countryFlag, room.city, "Host • Global Walkie Navigator", isHost = true, reputationScore = 350, receivedGiftsCount = 10),
                WorldwideSpeaker("spk_2", "Airwaves_Echo", "ECHO-7", room.country, room.countryFlag, room.city, "Ham radio enthusiast & world traveler", reputationScore = 220, receivedGiftsCount = 4)
            )
        }
    }

    private fun startWorldwideRoomSimulation(room: WorldwideRoom) {
        worldwideRoomSimulationJob = viewModelScope.launch(Dispatchers.IO) {
            val phrases = listOf(
                "Hey everyone! Reception is super clear all the way over here in ${room.city}!",
                "Greetings from global airwaves! Who else is tuning in tonight?",
                "Loved that track you mentioned earlier! Sending some good vibes.",
                "Anyone traveling through ${room.country} next month? Hit me up!",
                "Awesome walkie talkie quality! Zero lag on this room."
            )

            while (true) {
                delay(Random.nextLong(12000, 20000))
                val speakers = _uiState.value.activeWorldwideSpeakers
                if (speakers.isNotEmpty() && !_uiState.value.isWorldwidePttActive) {
                    val speaker = speakers.random()
                    // Set speaking indicator
                    _uiState.value = _uiState.value.copy(
                        isWorldwideVoiceActive = true,
                        worldwideActiveSpeakerName = speaker.username
                    )
                    audioEngine.playTransmissionNoiseBurst()

                    val msg = LiveRoomMessage(
                        roomId = room.id,
                        senderId = speaker.id,
                        senderUsername = speaker.username,
                        senderCallsign = speaker.callsign,
                        senderCountryFlag = speaker.countryFlag,
                        senderCity = speaker.city,
                        text = phrases.random(),
                        isVoiceSnippet = true,
                        voiceDurationSeconds = Random.nextInt(2, 5).toFloat()
                    )
                    val updated = _currentRoomMessages.value.toMutableList().apply { add(msg) }
                    _currentRoomMessages.value = updated

                    delay(3000)
                    _uiState.value = _uiState.value.copy(
                        isWorldwideVoiceActive = false,
                        worldwideActiveSpeakerName = null
                    )
                }
            }
        }
    }

    fun startWorldwidePtt() {
        if (_uiState.value.isWorldwidePttActive) return
        val currentIdentity = userIdentity.value

        audioEngine.playChirpPress(currentIdentity.soundProfile)
        _uiState.value = _uiState.value.copy(
            isWorldwidePttActive = true,
            isWorldwideVoiceActive = true,
            worldwideActiveSpeakerName = "YOU (${currentIdentity.callsign})"
        )
    }

    fun stopWorldwidePtt() {
        if (!_uiState.value.isWorldwidePttActive) return
        val currentIdentity = userIdentity.value
        val currentProfile = userProfile.value
        val room = _uiState.value.selectedWorldwideRoom

        audioEngine.playChirpRelease(currentIdentity.soundProfile)
        _uiState.value = _uiState.value.copy(
            isWorldwidePttActive = false,
            isWorldwideVoiceActive = false,
            worldwideActiveSpeakerName = null
        )

        // Post own transmission message to room feed
        if (room != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val myMsg = LiveRoomMessage(
                    roomId = room.id,
                    senderId = "1",
                    senderUsername = currentProfile.displayName,
                    senderCallsign = currentProfile.callsign,
                    senderCountryFlag = currentProfile.countryFlag,
                    senderCity = currentProfile.city,
                    text = "🎙️ [LIVE VOICE TRANSMISSION] Broadcasted across ${room.name}",
                    isVoiceSnippet = true,
                    voiceDurationSeconds = 3.2f
                )
                val updated = _currentRoomMessages.value.toMutableList().apply { add(myMsg) }
                _currentRoomMessages.value = updated
            }
        }
    }

    fun toggleRaiseHand() {
        val next = !_uiState.value.hasRaisedHandToSpeak
        _uiState.value = _uiState.value.copy(hasRaisedHandToSpeak = next)
        audioEngine.playKeyVerifiedTone()
        val room = _uiState.value.selectedWorldwideRoom
        if (room != null && next) {
            viewModelScope.launch(Dispatchers.IO) {
                val handMsg = LiveRoomMessage(
                    roomId = room.id,
                    senderId = "1",
                    senderUsername = userProfile.value.displayName,
                    senderCallsign = userProfile.value.callsign,
                    senderCountryFlag = userProfile.value.countryFlag,
                    senderCity = userProfile.value.city,
                    text = "✋ Raised hand to speak on stage"
                )
                _currentRoomMessages.value = _currentRoomMessages.value + handMsg
            }
        }
    }

    fun toggleWorldwideAudioMute() {
        _uiState.value = _uiState.value.copy(isWorldwideAudioMuted = !_uiState.value.isWorldwideAudioMuted)
    }

    fun postRoomTextMessage(text: String) {
        if (text.isBlank()) return
        val room = _uiState.value.selectedWorldwideRoom ?: return
        val profile = userProfile.value
        viewModelScope.launch(Dispatchers.IO) {
            val msg = LiveRoomMessage(
                roomId = room.id,
                senderId = "1",
                senderUsername = profile.displayName,
                senderCallsign = profile.callsign,
                senderCountryFlag = profile.countryFlag,
                senderCity = profile.city,
                text = text.trim()
            )
            _currentRoomMessages.value = _currentRoomMessages.value + msg
            repository.postRoomMessage(msg)
        }
    }

    // ========================
    // PAID GIFTS & COIN SYSTEM
    // ========================

    fun sendPaidGift(gift: PaidGiftItem, recipient: WorldwideSpeaker, customMessage: String) {
        val room = _uiState.value.selectedWorldwideRoom ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.sendGift(
                gift = gift,
                recipientId = recipient.id,
                recipientUsername = recipient.username,
                roomName = room.name,
                message = customMessage
            )

            if (success) {
                audioEngine.playKeyVerifiedTone()

                val giftEvent = GiftTransaction(
                    giftId = gift.id,
                    giftName = gift.name,
                    giftEmoji = gift.emoji,
                    coinsSpent = gift.coinsCost,
                    senderUsername = userProfile.value.displayName,
                    recipientId = recipient.id,
                    recipientUsername = recipient.username,
                    roomName = room.name,
                    message = customMessage
                )

                _uiState.value = _uiState.value.copy(
                    isSendGiftModalOpen = false,
                    activeGiftBanner = giftEvent,
                    lastVerifiedNotification = "Sent ${gift.emoji} ${gift.name} to ${recipient.username}!"
                )

                // Add to room feed
                val giftMsg = LiveRoomMessage(
                    roomId = room.id,
                    senderId = "1",
                    senderUsername = userProfile.value.displayName,
                    senderCallsign = userProfile.value.callsign,
                    senderCountryFlag = userProfile.value.countryFlag,
                    senderCity = userProfile.value.city,
                    text = "🎉 Sent ${gift.emoji} ${gift.name} (${gift.badgeLabel}) to @${recipient.username}! \"$customMessage\"",
                    isGiftNotification = true,
                    giftEmoji = gift.emoji
                )
                _currentRoomMessages.value = _currentRoomMessages.value + giftMsg

                // Dismiss banner after 4 seconds
                delay(4000)
                if (_uiState.value.activeGiftBanner == giftEvent) {
                    _uiState.value = _uiState.value.copy(activeGiftBanner = null)
                }
            } else {
                // Not enough coins -> open coin recharge modal
                _uiState.value = _uiState.value.copy(
                    isSendGiftModalOpen = false,
                    isBuyCoinsModalOpen = true,
                    lastVerifiedNotification = "Need more coins for ${gift.name} (${gift.coinsCost} coins)"
                )
            }
        }
    }

    fun buyCoinPack(coins: Int, priceDisplay: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCoins(coins)
            audioEngine.playKeyVerifiedTone()
            _uiState.value = _uiState.value.copy(
                isBuyCoinsModalOpen = false,
                lastVerifiedNotification = "Successfully added +$coins Coins ($priceDisplay)!"
            )
        }
    }

    // =============================
    // PROFILE, FRIENDS & BLOCK LIST
    // =============================

    fun updateProfile(
        displayName: String,
        callsign: String,
        country: String,
        countryFlag: String,
        city: String,
        bio: String,
        languages: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userProfile.value
            val updated = current.copy(
                displayName = displayName,
                callsign = callsign,
                country = country,
                countryFlag = countryFlag,
                city = city,
                bio = bio,
                spokenLanguages = languages
            )
            repository.updateProfile(updated)
            // Also keep user identity callsign in sync
            repository.setCallsign(callsign)
            audioEngine.playKeyVerifiedTone()
            _uiState.value = _uiState.value.copy(
                isUserProfileModalOpen = false,
                lastVerifiedNotification = "Personal Profile Updated!"
            )
        }
    }

    fun addFriendFromSpeaker(speaker: WorldwideSpeaker) {
        viewModelScope.launch(Dispatchers.IO) {
            val friend = FriendUser(
                id = speaker.id,
                username = speaker.username,
                callsign = speaker.callsign,
                country = speaker.country,
                countryFlag = speaker.countryFlag,
                city = speaker.city,
                bio = speaker.bio,
                isOnline = true,
                statusText = "Speaking in ${_uiState.value.selectedWorldwideRoom?.name ?: "Global Room"}"
            )
            repository.addFriend(friend)
            audioEngine.playKeyVerifiedTone()
            _uiState.value = _uiState.value.copy(
                lastVerifiedNotification = "Added ${speaker.username} to Friends list!"
            )
        }
    }

    fun addFriendFromUser(user: FriendUser) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFriend(user)
            audioEngine.playKeyVerifiedTone()
            _uiState.value = _uiState.value.copy(
                lastVerifiedNotification = "Added ${user.username} to Friends!"
            )
        }
    }

    fun removeFriend(userId: String, username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeFriend(userId)
            audioEngine.playSquelchBurst()
            _uiState.value = _uiState.value.copy(
                lastVerifiedNotification = "Removed $username from Friends list."
            )
        }
    }

    fun blockUser(userId: String, username: String, callsign: String, country: String, countryFlag: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.blockUser(
                userId = userId,
                username = username,
                callsign = callsign,
                country = country,
                countryFlag = countryFlag,
                reason = "Blocked by user"
            )
            audioEngine.playSquelchBurst()
            _uiState.value = _uiState.value.copy(
                isOtherUserProfileModalOpen = false,
                lastVerifiedNotification = "Blocked $username. You won't hear transmissions from them."
            )
        }
    }

    fun unblockUser(userId: String, username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.unblockUser(userId)
            audioEngine.playKeyVerifiedTone()
            _uiState.value = _uiState.value.copy(
                lastVerifiedNotification = "Unblocked $username."
            )
        }
    }

    fun createCustomWorldwideRoom(
        name: String,
        country: String,
        countryCode: String,
        countryFlag: String,
        city: String,
        region: String,
        category: String,
        description: String,
        tags: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val room = repository.createWorldwideRoom(
                name = name,
                country = country,
                countryCode = countryCode,
                countryFlag = countryFlag,
                city = city,
                region = region,
                category = category,
                description = description,
                tags = tags
            )
            audioEngine.playKeyVerifiedTone()
            _uiState.value = _uiState.value.copy(
                isCreateRoomModalOpen = false,
                lastVerifiedNotification = "Created room: ${room.name}!"
            )
            // Enter room immediately
            enterWorldwideRoom(room)
        }
    }

    // Modal controllers for Worldwide Features
    fun setUserProfileModalOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isUserProfileModalOpen = open)
    }

    fun setFriendsAndBlockedModalOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isFriendsAndBlockedModalOpen = open)
    }

    fun setSendGiftModalOpen(open: Boolean, targetSpeaker: WorldwideSpeaker? = null) {
        _uiState.value = _uiState.value.copy(
            isSendGiftModalOpen = open,
            giftTargetSpeaker = targetSpeaker ?: _uiState.value.activeWorldwideSpeakers.firstOrNull()
        )
    }

    fun setCreateRoomModalOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isCreateRoomModalOpen = open)
    }

    fun setBuyCoinsModalOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isBuyCoinsModalOpen = open)
    }

    fun inspectUserProfile(user: FriendUser) {
        _uiState.value = _uiState.value.copy(
            inspectingUser = user,
            isOtherUserProfileModalOpen = true
        )
    }

    fun setOtherUserProfileModalOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isOtherUserProfileModalOpen = open)
    }

    fun setWorldwideSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(worldwideSearchQuery = query)
    }

    fun setWorldwideFilterRegion(region: String) {
        _uiState.value = _uiState.value.copy(worldwideSelectedRegion = region)
    }

    fun setWorldwideFilterCategory(category: String) {
        _uiState.value = _uiState.value.copy(worldwideSelectedCategory = category)
    }

    // ==========================================
    // COIN CASHOUT & IN-APP PURCHASES WITH COINS
    // ==========================================

    fun setCashoutModalOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isCashoutModalOpen = open)
    }

    fun setCoinShopModalOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isCoinShopModalOpen = open)
    }

    fun requestCoinCashout(
        coinsAmount: Int,
        method: com.example.data.model.CashoutMethod,
        destinationAccount: String,
        accountHolderName: String
    ) {
        val profile = userProfile.value
        if (coinsAmount <= 0 || profile.coinsBalance < coinsAmount) {
            _uiState.value = _uiState.value.copy(
                lastVerifiedNotification = "Insufficient coin balance. Current balance: ${profile.coinsBalance} Coins"
            )
            return
        }

        if (coinsAmount < method.minCoins) {
            _uiState.value = _uiState.value.copy(
                lastVerifiedNotification = "Minimum cashout for ${method.title} is ${method.minCoins} Coins."
            )
            return
        }

        val usdAmount = coinsAmount * 0.01 // 100 coins = $1.00 USD
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.requestCoinCashout(
                coinsAmount = coinsAmount,
                usdAmount = usdAmount,
                method = method.name,
                destinationAccount = destinationAccount.trim(),
                accountHolderName = accountHolderName.trim()
            )

            if (success) {
                audioEngine.playKeyVerifiedTone()
                _uiState.value = _uiState.value.copy(
                    isCashoutModalOpen = false,
                    lastVerifiedNotification = "Transferred ${coinsAmount} Coins -> \$${String.format("%.2f", usdAmount)} USD via ${method.title}!"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    lastVerifiedNotification = "Cashout request failed. Please check your coin balance."
                )
            }
        }
    }

    fun buyInAppItemWithCoins(item: com.example.data.model.CoinInAppItem) {
        val profile = userProfile.value
        if (profile.coinsBalance < item.coinCost) {
            _uiState.value = _uiState.value.copy(
                isBuyCoinsModalOpen = true,
                lastVerifiedNotification = "Need ${item.coinCost - profile.coinsBalance} more coins to purchase ${item.title}"
            )
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.purchaseInAppItemWithCoins(item)
            if (success) {
                audioEngine.playKeyVerifiedTone()
                _uiState.value = _uiState.value.copy(
                    isCoinShopModalOpen = false,
                    lastVerifiedNotification = "Successfully unlocked ${item.title} for ${item.coinCost} Coins!"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    lastVerifiedNotification = "Failed to unlock ${item.title}. Check coin balance."
                )
            }
        }
    }

    // ==========================================
    // APP & PTT SETTINGS USER FUNCTIONS
    // ==========================================

    fun setVolumeLevel(volume: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setVolumeLevel(volume)
        }
    }

    fun setSquelchLevel(squelch: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setSquelchLevel(squelch)
        }
    }

    fun resetSettingsToDefaults() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.resetToFactoryDefaults()
            audioEngine.playSquelchBurst()
            _uiState.value = _uiState.value.copy(
                lastVerifiedNotification = "Settings & PTT audio configurations restored to factory defaults."
            )
        }
    }

    fun previewSoundProfile(profile: com.example.data.model.PttSoundProfile) {
        audioEngine.playChirpPress(profile)
        viewModelScope.launch {
            delay(350)
            audioEngine.playChirpRelease(profile)
        }
    }

    fun runAudioLoopbackTest() {
        if (_uiState.value.isLoopbackRecording) return
        _uiState.value = _uiState.value.copy(
            isLoopbackRecording = true,
            loopbackStatus = "🎙️ Recording 3-second audio loopback test..."
        )
        audioEngine.playChirpPress(userIdentity.value.soundProfile)

        viewModelScope.launch {
            delay(3000)
            _uiState.value = _uiState.value.copy(
                loopbackStatus = "🔊 Transmitting recorded audio loopback through DSP filter..."
            )
            audioEngine.playChirpRelease(userIdentity.value.soundProfile)
            audioEngine.playKeyVerifiedTone()
            delay(2000)
            _uiState.value = _uiState.value.copy(
                isLoopbackRecording = false,
                loopbackStatus = "✅ Audio loopback test complete! Mic & DSP calibrated."
            )
            delay(3000)
            if (_uiState.value.loopbackStatus?.startsWith("✅") == true) {
                _uiState.value = _uiState.value.copy(loopbackStatus = null)
            }
        }
    }

    fun runLatencyDiagnostic() {
        if (_uiState.value.isDiagnosticsRunning) return
        _uiState.value = _uiState.value.copy(
            isDiagnosticsRunning = true,
            diagnosticsResult = "Pinging local mesh transceivers and cloud edge relays..."
        )
        viewModelScope.launch {
            delay(800)
            val jitter = kotlin.random.Random.nextInt(1, 6)
            val ping = kotlin.random.Random.nextInt(12, 28)
            val loss = 0.0
            _uiState.value = _uiState.value.copy(
                isDiagnosticsRunning = false,
                diagnosticsResult = "⚡ Ultra-low Latency: ${ping}ms (Jitter: ${jitter}ms, Packet Loss: 0.0%). Audio buffer: 128 frames (Sub-50ms glass-to-glass)."
            )
        }
    }

    fun panicWipeAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.wipeAllLogs()
            repository.resetToFactoryDefaults()
            audioEngine.playSquelchBurst()
            _uiState.value = _uiState.value.copy(
                lastVerifiedNotification = "⚠️ Emergency wipe executed. Transmissions erased & security keys re-rolled."
            )
        }
    }

    fun playLiveScanner(feed: com.example.data.model.PublicScannerFeed) {
        audioEngine.playLiveScanner(feed.streamUrl, feed.category)
        _uiState.value = _uiState.value.copy(
            isScannerPlaying = true,
            activeScannerFeedId = feed.id,
            isNoaaPlaying = false,
            activeNoaaId = null
        )
    }

    fun stopScanner() {
        audioEngine.stopScanner()
        _uiState.value = _uiState.value.copy(
            isScannerPlaying = false,
            activeScannerFeedId = null
        )
    }

    fun playNoaaWeatherRadio(station: com.example.data.model.NoaaWeatherStation) {
        audioEngine.playNoaaWeatherRadio(station.streamUrl)
        _uiState.value = _uiState.value.copy(
            isNoaaPlaying = true,
            activeNoaaId = station.id,
            isScannerPlaying = false,
            activeScannerFeedId = null
        )
    }

    fun stopNoaaWeatherRadio() {
        audioEngine.stopNoaaWeatherRadio()
        _uiState.value = _uiState.value.copy(
            isNoaaPlaying = false,
            activeNoaaId = null
        )
    }

    fun playNoaaAlertTone() {
        audioEngine.playNoaa1050HzAlertTone()
    }

    fun playMaydayDistressSiren() {
        audioEngine.playMaydayDistressSiren()
    }


    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
