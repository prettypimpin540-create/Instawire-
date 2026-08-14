package com.example.data.repository

import com.example.data.local.InstaWireDao
import com.example.data.model.BurnerLine
import com.example.data.model.Channel
import com.example.data.model.Contact
import com.example.data.model.NoiseFilterMode
import com.example.data.model.NumberType
import com.example.data.model.Transmission
import com.example.data.model.UserIdentity
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest
import kotlin.random.Random

class InstaWireRepository(private val dao: InstaWireDao) {

    val userIdentity: Flow<UserIdentity?> = dao.getUserIdentity()
    val allContacts: Flow<List<Contact>> = dao.getAllContacts()
    val allChannels: Flow<List<Channel>> = dao.getAllChannels()
    val recentTransmissions: Flow<List<Transmission>> = dao.getRecentTransmissions()

    val allBurnerLines: Flow<List<BurnerLine>> = dao.getAllBurnerLines()

    fun getTransmissionsForTarget(targetId: String): Flow<List<Transmission>> =
        dao.getTransmissionsForTarget(targetId)

    suspend fun updateIdentity(identity: UserIdentity) {
        dao.insertOrUpdateIdentity(identity)
    }

    suspend fun setLayoutType(layout: com.example.data.model.WalkieLayoutType) {
        val current = dao.getUserIdentitySync() ?: UserIdentity()
        dao.insertOrUpdateIdentity(current.copy(layoutType = layout))
    }

    suspend fun setThemeScheme(scheme: com.example.data.model.AppThemeScheme) {
        val current = dao.getUserIdentitySync() ?: UserIdentity()
        dao.insertOrUpdateIdentity(current.copy(themeScheme = scheme))
    }

    suspend fun setSoundProfile(profile: com.example.data.model.PttSoundProfile) {
        val current = dao.getUserIdentitySync() ?: UserIdentity()
        dao.insertOrUpdateIdentity(current.copy(soundProfile = profile))
    }

    suspend fun unlockItem(itemKey: String) {
        val current = dao.getUserIdentitySync() ?: UserIdentity()
        val unlockedList = current.unlockedLayouts.split(",").toMutableSet()
        unlockedList.add(itemKey)
        dao.insertOrUpdateIdentity(current.copy(unlockedLayouts = unlockedList.joinToString(",")))
    }

    suspend fun purchaseAllThemesPass() {
        val current = dao.getUserIdentitySync() ?: UserIdentity()
        dao.insertOrUpdateIdentity(current.copy(hasPurchasedThemePack = true))
    }

    suspend fun addBurnerLine(burnerLine: BurnerLine) {
        dao.insertBurnerLine(burnerLine)
    }

    suspend fun deleteBurnerLine(number: String) {
        dao.deleteBurnerLine(number)
    }

    suspend fun setActiveBurnerLine(number: String) {
        val current = dao.getUserIdentitySync() ?: UserIdentity()
        dao.insertOrUpdateIdentity(
            current.copy(
                burnerNumber = number,
                hasBurnerSubscription = true,
                activeNumberType = NumberType.BURNER
            )
        )
    }

    suspend fun setKeyVerified(contactId: Long, verified: Boolean) {
        dao.setKeyVerified(contactId, verified)
    }

    suspend fun addContact(name: String, number: String, callsign: String, isBurner: Boolean) {
        val hash = generateSafetyFingerprint(number + name)
        val blocks = generateSafetyBlocks(number)
        val contact = Contact(
            name = name,
            number = number,
            callsign = if (callsign.isNotBlank()) callsign else "WIRE-${Random.nextInt(100, 999)}",
            isBurner = isBurner,
            isOnline = true,
            isKeyVerified = false,
            safetyFingerprint = hash,
            safetyKeyBlocks = blocks,
            lastTransmissionTime = System.currentTimeMillis()
        )
        dao.insertContact(contact)
    }

    suspend fun addChannel(name: String, frequency: String, description: String) {
        val id = "channel_" + System.currentTimeMillis()
        val channel = Channel(
            id = id,
            name = name.uppercase(),
            frequency = frequency,
            description = description,
            activeMembersCount = Random.nextInt(2, 12),
            isEncrypted = true,
            safetyFingerprint = generateSafetyFingerprint(name),
            safetyKeyBlocks = generateSafetyBlocks(frequency),
            isSystemChannel = false
        )
        dao.insertChannel(channel)
    }

    suspend fun recordTransmission(
        senderName: String,
        senderNumber: String,
        senderCallsign: String,
        targetType: String,
        targetId: String,
        durationSec: Float,
        waveAmps: String = "20,50,80,95,70,90,60,30"
    ) {
        val transmission = Transmission(
            senderName = senderName,
            senderNumber = senderNumber,
            senderCallsign = senderCallsign,
            targetType = targetType,
            targetId = targetId,
            durationSeconds = durationSec,
            timestamp = System.currentTimeMillis(),
            isEncrypted = true,
            isKeyVerified = true,
            waveAmplitudes = waveAmps,
            audioEffect = "CRYSTAL_CLEAR"
        )
        dao.insertTransmission(transmission)
    }

    suspend fun purgeExpiredTransmissions(timeoutSeconds: Int) {
        val cutoff = System.currentTimeMillis() - (timeoutSeconds * 1000L)
        dao.deleteOldTransmissions(cutoff)
    }

    suspend fun wipeAllLogs() {
        dao.clearAllTransmissions()
    }

    suspend fun setSubscriptionTier(tier: com.example.data.model.SubscriptionTier) {
        val current = dao.getUserIdentitySync() ?: UserIdentity()
        val updated = current.copy(
            subscriptionTier = tier,
            hasBurnerSubscription = tier != com.example.data.model.SubscriptionTier.FREE,
            noiseFilterEnabled = if (!tier.hasNoiseCancellation) false else current.noiseFilterEnabled
        )
        dao.insertOrUpdateIdentity(updated)
    }

    suspend fun generateNewBurnerNumber(tier: com.example.data.model.SubscriptionTier = com.example.data.model.SubscriptionTier.PRO): String {
        val prefixes = when (tier) {
            com.example.data.model.SubscriptionTier.GHOST_SENTINEL -> listOf("888", "800", "VIP")
            com.example.data.model.SubscriptionTier.BLACK_OPS -> listOf("877", "866", "855")
            else -> listOf("888", "800", "877", "866")
        }
        val randomPrefix = prefixes.random()
        val num = Random.nextInt(1000, 9999)
        val newBurner = if (randomPrefix == "VIP") "+1 (VIP) GHOST-$num" else "+1 ($randomPrefix) WIRE-$num"

        val current = dao.getUserIdentitySync() ?: UserIdentity()
        val updated = current.copy(
            burnerNumber = newBurner,
            hasBurnerSubscription = true,
            activeNumberType = NumberType.BURNER
        )
        dao.insertOrUpdateIdentity(updated)
        return newBurner
    }

    suspend fun toggleActiveNumberType() {
        val current = dao.getUserIdentitySync() ?: UserIdentity()
        val newType = if (current.activeNumberType == NumberType.PHONE) NumberType.BURNER else NumberType.PHONE
        dao.insertOrUpdateIdentity(current.copy(activeNumberType = newType))
    }

    suspend fun toggleHardwareVolumePtt() {
        val current = dao.getUserIdentitySync() ?: UserIdentity()
        dao.insertOrUpdateIdentity(current.copy(hardwareVolumePttEnabled = !current.hardwareVolumePttEnabled))
    }

    suspend fun toggleBackgroundMonitoring() {
        val current = dao.getUserIdentitySync() ?: UserIdentity()
        dao.insertOrUpdateIdentity(current.copy(backgroundMonitoringEnabled = !current.backgroundMonitoringEnabled))
    }

    suspend fun toggleBackgroundAudioBeep() {
        val current = dao.getUserIdentitySync() ?: UserIdentity()
        dao.insertOrUpdateIdentity(current.copy(backgroundAudioBeepEnabled = !current.backgroundAudioBeepEnabled))
    }

    suspend fun toggleNoiseCancellation() {
        val current = dao.getUserIdentitySync() ?: UserIdentity()
        dao.insertOrUpdateIdentity(current.copy(noiseFilterEnabled = !current.noiseFilterEnabled))
    }

    suspend fun setNoiseFilterMode(mode: NoiseFilterMode) {
        val current = dao.getUserIdentitySync() ?: UserIdentity()
        dao.insertOrUpdateIdentity(current.copy(noiseFilterMode = mode))
    }

    companion object {
        fun generateSafetyFingerprint(input: String): String {
            val md = MessageDigest.getInstance("SHA-256")
            val bytes = md.digest(input.toByteArray())
            return bytes.take(4).joinToString("-") { "%02X".format(it) }
        }

        fun generateSafetyBlocks(seed: String): String {
            val random = java.util.Random(seed.hashCode().toLong())
            val blocks = mutableListOf<String>()
            repeat(12) {
                blocks.add("%05d".format(random.nextInt(100000)))
            }
            return blocks.joinToString(" ")
        }
    }
}
