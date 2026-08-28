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
    val userProfile: Flow<com.example.data.model.UserProfile?> = dao.getUserProfile()
    val allWorldwideRooms: Flow<List<com.example.data.model.WorldwideRoom>> = dao.getAllWorldwideRooms()
    val allFriends: Flow<List<com.example.data.model.FriendUser>> = dao.getAllFriends()
    val allBlockedUsers: Flow<List<com.example.data.model.BlockedUser>> = dao.getAllBlockedUsers()
    val allGiftTransactions: Flow<List<com.example.data.model.GiftTransaction>> = dao.getAllGiftTransactions()
    val allCashoutTransactions: Flow<List<com.example.data.model.CoinCashoutTransaction>> = dao.getAllCashoutTransactions()

    fun getRoomMessages(roomId: String): Flow<List<com.example.data.model.LiveRoomMessage>> =
        dao.getRoomMessages(roomId)

    fun getWorldwideRoom(roomId: String): Flow<com.example.data.model.WorldwideRoom?> =
        dao.getWorldwideRoom(roomId)

    suspend fun updateProfile(profile: com.example.data.model.UserProfile) {
        dao.insertOrUpdateProfile(profile)
    }

    suspend fun addCoins(amount: Int) {
        dao.addCoins(amount)
    }

    suspend fun sendGift(
        gift: com.example.data.model.PaidGiftItem,
        recipientId: String,
        recipientUsername: String,
        roomName: String,
        message: String = ""
    ): Boolean {
        val profile = dao.getUserProfileSync() ?: return false
        if (profile.coinsBalance < gift.coinsCost) return false

        dao.deductCoinsForGift(gift.coinsCost)
        val transaction = com.example.data.model.GiftTransaction(
            giftId = gift.id,
            giftName = gift.name,
            giftEmoji = gift.emoji,
            coinsSpent = gift.coinsCost,
            senderUsername = profile.displayName,
            recipientId = recipientId,
            recipientUsername = recipientUsername,
            roomName = roomName,
            message = message
        )
        dao.insertGiftTransaction(transaction)
        return true
    }

    suspend fun createWorldwideRoom(
        name: String,
        country: String,
        countryCode: String,
        countryFlag: String,
        city: String,
        region: String,
        category: String,
        description: String,
        tags: String
    ): com.example.data.model.WorldwideRoom {
        val roomId = "custom_room_${System.currentTimeMillis()}"
        val room = com.example.data.model.WorldwideRoom(
            id = roomId,
            name = name,
            country = country,
            countryCode = countryCode,
            countryFlag = countryFlag,
            city = city,
            region = region,
            category = category,
            activeListeners = 1,
            activeSpeakersCount = 1,
            language = "Global / English",
            description = description,
            isOfficial = false,
            isLive = true,
            tags = tags
        )
        dao.insertWorldwideRoom(room)
        return room
    }

    suspend fun addFriend(user: com.example.data.model.FriendUser) {
        dao.insertFriend(user)
    }

    suspend fun removeFriend(userId: String) {
        dao.deleteFriend(userId)
    }

    suspend fun blockUser(
        userId: String,
        username: String,
        callsign: String,
        country: String,
        countryFlag: String,
        reason: String
    ) {
        // Also remove from friends if present
        dao.deleteFriend(userId)
        dao.blockUser(
            com.example.data.model.BlockedUser(
                id = userId,
                username = username,
                callsign = callsign,
                country = country,
                countryFlag = countryFlag,
                reason = reason
            )
        )
    }

    suspend fun unblockUser(userId: String) {
        dao.unblockUser(userId)
    }

    suspend fun isUserBlocked(userId: String): Boolean {
        return dao.isUserBlocked(userId)
    }

    suspend fun postRoomMessage(message: com.example.data.model.LiveRoomMessage) {
        dao.insertRoomMessage(message)
    }


    fun getTransmissionsForTarget(targetId: String): Flow<List<Transmission>> =
        dao.getTransmissionsForTarget(targetId)

    suspend fun setCallsign(callsign: String) {
        val current = dao.getUserIdentitySync() ?: UserIdentity()
        dao.insertOrUpdateIdentity(current.copy(callsign = callsign))
    }

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

    suspend fun requestCoinCashout(
        coinsAmount: Int,
        usdAmount: Double,
        method: String,
        destinationAccount: String,
        accountHolderName: String,
        feeUsd: Double = 0.0
    ): Boolean {
        val profile = dao.getUserProfileSync() ?: return false
        if (profile.coinsBalance < coinsAmount) return false

        val rows = dao.deductCoins(coinsAmount)
        if (rows <= 0) return false

        val randomRef = "TX-WIRE-${Random.nextInt(1000, 9999)}-${method.take(3).uppercase()}"
        val transaction = com.example.data.model.CoinCashoutTransaction(
            coinsAmount = coinsAmount,
            usdAmount = usdAmount,
            method = method,
            destinationAccount = destinationAccount,
            accountHolderName = accountHolderName,
            status = "COMPLETED",
            referenceId = randomRef,
            feeUsd = feeUsd,
            timestamp = System.currentTimeMillis()
        )
        dao.insertCashoutTransaction(transaction)
        return true
    }

    suspend fun purchaseInAppItemWithCoins(item: com.example.data.model.CoinInAppItem): Boolean {
        val profile = dao.getUserProfileSync() ?: return false
        if (profile.coinsBalance < item.coinCost) return false

        val rows = dao.deductCoins(item.coinCost)
        if (rows <= 0) return false

        val identity = dao.getUserIdentitySync() ?: UserIdentity()
        when (item.id) {
            "coin_item_pro_month" -> {
                dao.insertOrUpdateIdentity(
                    identity.copy(
                        subscriptionTier = com.example.data.model.SubscriptionTier.PRO,
                        hasBurnerSubscription = true,
                        noiseFilterEnabled = true
                    )
                )
            }
            "coin_item_black_ops_month" -> {
                dao.insertOrUpdateIdentity(
                    identity.copy(
                        subscriptionTier = com.example.data.model.SubscriptionTier.BLACK_OPS,
                        hasBurnerSubscription = true,
                        noiseFilterEnabled = true
                    )
                )
            }
            "coin_item_ghost_sentinel" -> {
                dao.insertOrUpdateIdentity(
                    identity.copy(
                        subscriptionTier = com.example.data.model.SubscriptionTier.GHOST_SENTINEL,
                        hasBurnerSubscription = true,
                        noiseFilterEnabled = true
                    )
                )
            }
            "coin_item_master_theme_pack" -> {
                dao.insertOrUpdateIdentity(identity.copy(hasPurchasedThemePack = true))
            }
            "coin_item_extra_burners" -> {
                val newBurner = generateNewBurnerNumber(identity.subscriptionTier)
                dao.insertBurnerLine(
                    BurnerLine(
                        number = newBurner,
                        label = "Coin Unlocked Burner",
                        areaCode = "888",
                        cityRegion = "Encrypted Cloud Proxy",
                        createdAt = System.currentTimeMillis(),
                        isEphemeral = false
                    )
                )
            }
            "coin_item_dsp_extreme_filter" -> {
                dao.insertOrUpdateIdentity(
                    identity.copy(
                        noiseFilterEnabled = true,
                        noiseFilterMode = NoiseFilterMode.HEAVY_SUPPRESSION
                    )
                )
            }
            "coin_item_verified_gold_badge" -> {
                val updatedProfile = profile.copy(
                    badges = if (profile.badges.contains("GOLD_VERIFIED")) profile.badges else "${profile.badges},GOLD_VERIFIED"
                )
                dao.insertOrUpdateProfile(updatedProfile)
            }
            "coin_item_soundboard_pack" -> {
                val allSounds = com.example.data.model.PttSoundProfile.values().joinToString(",") { it.name }
                dao.insertOrUpdateIdentity(identity.copy(unlockedLayouts = "${identity.unlockedLayouts},$allSounds"))
            }
            "coin_item_stage_megaphone" -> {
                // Boost active user status
                val updatedProfile = profile.copy(
                    reputationLevel = profile.reputationLevel + 1
                )
                dao.insertOrUpdateProfile(updatedProfile)
            }
        }
        return true
    }

    suspend fun setVolumeLevel(volume: Float) {
        val current = dao.getUserIdentitySync() ?: UserIdentity()
        dao.insertOrUpdateIdentity(current.copy(volumeLevel = volume))
    }

    suspend fun setSquelchLevel(squelch: Float) {
        val current = dao.getUserIdentitySync() ?: UserIdentity()
        dao.insertOrUpdateIdentity(current.copy(squelchLevel = squelch))
    }

    suspend fun resetToFactoryDefaults() {
        val current = dao.getUserIdentitySync() ?: UserIdentity()
        dao.insertOrUpdateIdentity(
            current.copy(
                chirpSoundEnabled = true,
                rogerBeepEnabled = true,
                hardwareVolumePttEnabled = true,
                backgroundMonitoringEnabled = true,
                backgroundAudioBeepEnabled = true,
                zeroLogsEnabled = true,
                ephemeralTimeoutSeconds = 60,
                volumeLevel = 0.85f,
                squelchLevel = 0.40f,
                noiseFilterEnabled = true,
                noiseFilterMode = NoiseFilterMode.STUDIO_CLEAR,
                soundProfile = com.example.data.model.PttSoundProfile.NEXTEL_TACTICAL,
                themeScheme = com.example.data.model.AppThemeScheme.TACTICAL_GREEN,
                layoutType = com.example.data.model.WalkieLayoutType.CLASSIC_TACTICAL
            )
        )
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
