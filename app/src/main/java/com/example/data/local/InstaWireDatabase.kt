package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BlockedUser
import com.example.data.model.BurnerLine
import com.example.data.model.BurnerNumberMetadata
import com.example.data.model.Channel
import com.example.data.model.CoinCashoutTransaction
import com.example.data.model.Contact
import com.example.data.model.EncryptedMessageRecord
import com.example.data.model.FriendUser
import com.example.data.model.GiftTransaction
import com.example.data.model.LiveRoomMessage
import com.example.data.model.Transmission
import com.example.data.model.UserIdentity
import com.example.data.model.UserProfile
import com.example.data.model.WorldwideRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserIdentity::class,
        Contact::class,
        Channel::class,
        Transmission::class,
        BurnerLine::class,
        UserProfile::class,
        WorldwideRoom::class,
        FriendUser::class,
        BlockedUser::class,
        GiftTransaction::class,
        LiveRoomMessage::class,
        CoinCashoutTransaction::class,
        EncryptedMessageRecord::class,
        BurnerNumberMetadata::class
    ],
    version = 13,
    exportSchema = false
)
abstract class InstaWireDatabase : RoomDatabase() {

    abstract fun instaWireDao(): InstaWireDao

    companion object {
        @Volatile
        private var INSTANCE: InstaWireDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): InstaWireDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InstaWireDatabase::class.java,
                    "instawire_database"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.instaWireDao())
                    }
                }
            }

            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.instaWireDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        val dao = database.instaWireDao()
                        // Always purge old pre-seeded quick/system channels
                        dao.deleteSystemChannels()
                        if (dao.getUserIdentitySync() == null) {
                            populateInitialData(dao)
                        } else {
                            // Ensure initial custom channel exists if list is completely empty
                            val existingChannels = dao.getAllChannelsSync()
                            if (existingChannels.isEmpty()) {
                                dao.insertChannel(
                                    Channel(
                                        id = "custom_primary",
                                        name = "SQUAD ALPHA (CUSTOM)",
                                        frequency = "462.5625 MHz (FRS 1)",
                                        description = "Your private custom channel. Share Frequency Code FRQ-7734 to let others join.",
                                        activeMembersCount = 1,
                                        isEncrypted = true,
                                        safetyFingerprint = "ALPHA-CUSTOM-256",
                                        safetyKeyBlocks = "83910 28491 94820 18274 02938 48192 73910 82941 02948 19284 84019 92847",
                                        isSystemChannel = false,
                                        channelCategory = "Custom",
                                        frequencyCode = "FRQ-7734"
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: InstaWireDao) {
            // Always purge any old system channels
            dao.deleteSystemChannels()

            // Default User Identity - 100% anonymous, instant access
            if (dao.getUserIdentitySync() == null) {
                dao.insertOrUpdateIdentity(
                    UserIdentity(
                        id = 1,
                        phoneNumber = "+1 (555) 839-2041",
                        isPhoneVerified = true,
                        isHumanVerified = true,
                        hasAgreedToTerms = true,
                        termsAgreedTimestamp = System.currentTimeMillis(),
                        burnerNumber = "+1 (888) WIRE-7734",
                        hasBurnerSubscription = true,
                        callsign = "VIPER-7",
                        disclaimerAcknowledged = true
                    )
                )
            }

            // User-created Custom Channel with unique Frequency Code
            val initialChannel = Channel(
                id = "custom_primary",
                name = "SQUAD ALPHA (CUSTOM)",
                frequency = "462.5625 MHz (FRS 1)",
                description = "Your private custom channel. Share Frequency Code FRQ-7734 to let others join.",
                activeMembersCount = 1,
                isEncrypted = true,
                safetyFingerprint = "ALPHA-CUSTOM-256",
                safetyKeyBlocks = "83910 28491 94820 18274 02938 48192 73910 82941 02948 19284 84019 92847",
                isSystemChannel = false,
                channelCategory = "Custom",
                frequencyCode = "FRQ-7734"
            )
            dao.insertChannel(initialChannel)

            // Default Burner Lines
            val defaultBurners = listOf(
                BurnerLine(
                    number = "+1 (888) WIRE-7734",
                    label = "Primary Burner Line",
                    areaCode = "888",
                    cityRegion = "Toll-Free USA"
                ),
                BurnerLine(
                    number = "+1 (415) 890-4122",
                    label = "San Francisco Tech Line",
                    areaCode = "415",
                    cityRegion = "San Francisco, CA"
                )
            )
            defaultBurners.forEach { dao.insertBurnerLine(it) }

            // Default Contacts (TiKL style phone contacts & burner IDs)
            val defaultContacts = listOf(
                Contact(
                    name = "Marcus 'Shadow' Vance",
                    number = "+1 (555) 749-1029",
                    callsign = "SHADOW-01",
                    isBurner = false,
                    isOnline = true,
                    isKeyVerified = true,
                    safetyFingerprint = "VANCE-AES-7F91-C8A2",
                    safetyKeyBlocks = "48192 73910 82941 02948 19284 72910 84019 28401 92847 10293 84719 20491",
                    isFavorite = true
                ),
                Contact(
                    name = "Elena 'Raven' Rostova",
                    number = "+1 (888) WIRE-9942",
                    callsign = "RAVEN-9",
                    isBurner = true,
                    isOnline = true,
                    isKeyVerified = true,
                    safetyFingerprint = "RAVEN-SEC-3B21-D4E9",
                    safetyKeyBlocks = "92847 10293 84719 20491 48192 73910 82941 02948 19284 72910 84019 28401",
                    isFavorite = true
                ),
                Contact(
                    name = "Ghost Operator #314",
                    number = "+1 (800) GHOST-314",
                    callsign = "PHANTOM",
                    isBurner = true,
                    isOnline = true,
                    isKeyVerified = false,
                    safetyFingerprint = "ANON-GHOST-9A10-184C",
                    safetyKeyBlocks = "84019 28401 92847 10293 84719 20491 48192 73910 82941 02948 19284 72910",
                    isFavorite = false
                ),
                Contact(
                    name = "Chief David Miller",
                    number = "+1 (555) 923-4410",
                    callsign = "COMMAND-CHIEF",
                    isBurner = false,
                    isOnline = false,
                    isKeyVerified = false,
                    safetyFingerprint = "MILLER-SEC-89E2-11AA",
                    safetyKeyBlocks = "19284 72910 84019 28401 92847 10293 48192 73910 82941 02948 84719 20491",
                    isFavorite = false
                )
            )
            defaultContacts.forEach { dao.insertContact(it) }

            // Initial Transmissions
            val defaultTransmissions = listOf(
                Transmission(
                    senderName = "Marcus 'Shadow' Vance",
                    senderNumber = "+1 (555) 749-1029",
                    senderCallsign = "SHADOW-01",
                    targetType = "CHANNEL",
                    targetId = "tactical_alpha",
                    durationSeconds = 2.8f,
                    timestamp = System.currentTimeMillis() - 120000,
                    isEncrypted = true,
                    isKeyVerified = true,
                    waveAmplitudes = "10,25,60,85,90,75,40,20,5",
                    audioEffect = "CRYSTAL_CLEAR"
                ),
                Transmission(
                    senderName = "Elena 'Raven' Rostova",
                    senderNumber = "+1 (888) WIRE-9942",
                    senderCallsign = "RAVEN-9",
                    targetType = "CHANNEL",
                    targetId = "tactical_alpha",
                    durationSeconds = 3.4f,
                    timestamp = System.currentTimeMillis() - 45000,
                    isEncrypted = true,
                    isKeyVerified = true,
                    waveAmplitudes = "15,40,75,95,90,85,60,30,10",
                    audioEffect = "CRYSTAL_CLEAR"
                )
            )
            defaultTransmissions.forEach { dao.insertTransmission(it) }

            // Default User Profile
            dao.insertOrUpdateProfile(
                com.example.data.model.UserProfile(
                    id = 1,
                    displayName = "Alex Vance",
                    callsign = "VIPER-7",
                    country = "United States",
                    countryCode = "US",
                    countryFlag = "🇺🇸",
                    city = "New York",
                    bio = "Global radio enthusiast & audio hacker. Always down to talk tech, gear, and travel!",
                    spokenLanguages = "English, Spanish",
                    avatarIcon = "radio_operator",
                    coinsBalance = 350,
                    totalGiftsReceived = 12,
                    totalGiftsSent = 8,
                    reputationLevel = 5,
                    badges = "GLOBETROTTER,EARLY_ADOPTER,VIP_SUPPORTER"
                )
            )

            // Default Worldwide Voice Chat Rooms (Categorized by Countries & Cities)
            val defaultWorldwideRooms = listOf(
                com.example.data.model.WorldwideRoom(
                    id = "room_tokyo_shibuya",
                    name = "Tokyo Shibuya Night Lounge 🗼",
                    country = "Japan",
                    countryCode = "JP",
                    countryFlag = "🇯🇵",
                    city = "Tokyo",
                    region = "Asia-Pacific",
                    category = "Night Owls",
                    activeListeners = 148,
                    activeSpeakersCount = 4,
                    language = "Japanese / English",
                    description = "Late night chill & lo-fi conversation across Shibuya, Shinjuku, and global listeners.",
                    tags = "CHILL,LO-FI,MEET"
                ),
                com.example.data.model.WorldwideRoom(
                    id = "room_nyc_broadway",
                    name = "New York 24/7 Rooftop Radio 🗽",
                    country = "United States",
                    countryCode = "US",
                    countryFlag = "🇺🇸",
                    city = "New York",
                    region = "Americas",
                    category = "General",
                    activeListeners = 312,
                    activeSpeakersCount = 6,
                    language = "English",
                    description = "The pulse of NYC! Tech founders, artists, music producers, and international travelers.",
                    tags = "NYC,TECH,TALK"
                ),
                com.example.data.model.WorldwideRoom(
                    id = "room_london_soho",
                    name = "London Soho Pub & Audio Club 🇬🇧",
                    country = "United Kingdom",
                    countryCode = "GB",
                    countryFlag = "🇬🇧",
                    city = "London",
                    region = "Europe",
                    category = "Music & Jam",
                    activeListeners = 205,
                    activeSpeakersCount = 5,
                    language = "English",
                    description = "Live acoustics, indie music discussions, football banter, and worldwide storytelling.",
                    tags = "MUSIC,BANTER,UK"
                ),
                com.example.data.model.WorldwideRoom(
                    id = "room_paris_montmartre",
                    name = "Paris Montmartre Café & Travel 🥐",
                    country = "France",
                    countryCode = "FR",
                    countryFlag = "🇫🇷",
                    city = "Paris",
                    region = "Europe",
                    category = "Travel & Meet",
                    activeListeners = 98,
                    activeSpeakersCount = 3,
                    language = "French / English",
                    description = "Exchange language tips, travel recommendations, art, and philosophy from the heart of Paris.",
                    tags = "TRAVEL,FRENCH,ART"
                ),
                com.example.data.model.WorldwideRoom(
                    id = "room_seoul_gangnam",
                    name = "Seoul Gangnam K-Wave Studio 🇰🇷",
                    country = "South Korea",
                    countryCode = "KR",
                    countryFlag = "🇰🇷",
                    city = "Seoul",
                    region = "Asia-Pacific",
                    category = "Tech & Gaming",
                    activeListeners = 276,
                    activeSpeakersCount = 5,
                    language = "Korean / English",
                    description = "Gaming meta, eSports, K-Pop production, robotics, and fast-paced live chatter.",
                    tags = "GAMING,KPOP,TECH"
                ),
                com.example.data.model.WorldwideRoom(
                    id = "room_rio_copacabana",
                    name = "Rio Copacabana Sunset Beats 🇧🇷",
                    country = "Brazil",
                    countryCode = "BR",
                    countryFlag = "🇧🇷",
                    city = "Rio de Janeiro",
                    region = "Americas",
                    category = "Music & Jam",
                    activeListeners = 164,
                    activeSpeakersCount = 4,
                    language = "Portuguese / English",
                    description = "Bossa nova vibes, carnival stories, beach life, and warm South American energy.",
                    tags = "BEATS,SAMBA,BRAZIL"
                ),
                com.example.data.model.WorldwideRoom(
                    id = "room_berlin_kreuzberg",
                    name = "Berlin Techno Underground 🇩🇪",
                    country = "Germany",
                    countryCode = "DE",
                    countryFlag = "🇩🇪",
                    city = "Berlin",
                    region = "Europe",
                    category = "Music & Jam",
                    activeListeners = 189,
                    activeSpeakersCount = 4,
                    language = "German / English",
                    description = "Electronic sound design, modular synths, DJ live sets, and underground club culture.",
                    tags = "TECHNO,SYNTH,BERLIN"
                ),
                com.example.data.model.WorldwideRoom(
                    id = "room_sydney_harbour",
                    name = "Sydney Harbour Surfers & Explorers 🏄",
                    country = "Australia",
                    countryCode = "AU",
                    countryFlag = "🇦🇺",
                    city = "Sydney",
                    region = "Asia-Pacific",
                    category = "Travel & Meet",
                    activeListeners = 115,
                    activeSpeakersCount = 3,
                    language = "English",
                    description = "Outback adventures, coastal surfing spots, road trips, and friendly global conversations.",
                    tags = "SURF,OUTBACK,TRAVEL"
                ),
                com.example.data.model.WorldwideRoom(
                    id = "room_dubai_marina",
                    name = "Dubai Marina Sky Lounge 🇦🇪",
                    country = "United Arab Emirates",
                    countryCode = "AE",
                    countryFlag = "🇦🇪",
                    city = "Dubai",
                    region = "Middle East",
                    category = "General",
                    activeListeners = 230,
                    activeSpeakersCount = 5,
                    language = "Arabic / English",
                    description = "Global entrepreneurship, architecture, luxury automotive, and cross-cultural trade.",
                    tags = "DUBAI,STARTUPS,GLOBAL"
                ),
                com.example.data.model.WorldwideRoom(
                    id = "room_toronto_downtown",
                    name = "Toronto High-Rise Night Owl Cafe 🍁",
                    country = "Canada",
                    countryCode = "CA",
                    countryFlag = "🇨🇦",
                    city = "Toronto",
                    region = "Americas",
                    category = "Night Owls",
                    activeListeners = 142,
                    activeSpeakersCount = 3,
                    language = "English",
                    description = "Cozy late-night conversations with coffee lovers, coders, and northern stargazers.",
                    tags = "NIGHT,COFFEE,CANADA"
                )
            )
            dao.insertWorldwideRooms(defaultWorldwideRooms)

            // Default Worldwide Friends
            val defaultFriends = listOf(
                com.example.data.model.FriendUser(
                    id = "user_sakura_jp",
                    username = "Sakura_Tokyo",
                    callsign = "CHERRY-99",
                    country = "Japan",
                    countryFlag = "🇯🇵",
                    city = "Tokyo",
                    bio = "Illustrator & synth musician in Shibuya. Loves anime sound design.",
                    isOnline = true,
                    statusText = "Live in Tokyo Shibuya Lounge 🗼",
                    mutualFriendsCount = 3
                ),
                com.example.data.model.FriendUser(
                    id = "user_mateo_es",
                    username = "Mateo_Barcelona",
                    callsign = "SOL-CAT",
                    country = "Spain",
                    countryFlag = "🇪🇸",
                    city = "Barcelona",
                    bio = "Architect and coffee roaster. Open to radio walkie chats!",
                    isOnline = true,
                    statusText = "Online • Idle",
                    mutualFriendsCount = 5
                ),
                com.example.data.model.FriendUser(
                    id = "user_chloe_uk",
                    username = "Chloe_London",
                    callsign = "BIG-BEN",
                    country = "United Kingdom",
                    countryFlag = "🇬🇧",
                    city = "London",
                    bio = "Podcast producer & record collector in Camden.",
                    isOnline = false,
                    statusText = "Last active 1h ago",
                    mutualFriendsCount = 2
                )
            )
            defaultFriends.forEach { dao.insertFriend(it) }

            // Default demo gift transactions
            val defaultGifts = listOf(
                com.example.data.model.GiftTransaction(
                    giftId = "gift_crown",
                    giftName = "Royal Diamond Crown",
                    giftEmoji = "👑",
                    coinsSpent = 250,
                    senderUsername = "Mateo_Barcelona",
                    recipientId = "1",
                    recipientUsername = "Alex Vance",
                    roomName = "New York 24/7 Rooftop Radio 🗽",
                    message = "Awesome radio host!"
                ),
                com.example.data.model.GiftTransaction(
                    giftId = "gift_walkie",
                    giftName = "Gold Walkie-Talkie",
                    giftEmoji = "📻",
                    coinsSpent = 150,
                    senderUsername = "Sakura_Tokyo",
                    recipientId = "1",
                    recipientUsername = "Alex Vance",
                    roomName = "Tokyo Shibuya Night Lounge 🗼",
                    message = "Arigato for the great music track!"
                )
            )
            defaultGifts.forEach { dao.insertGiftTransaction(it) }

            // Default demo cashout transactions
            val defaultCashouts = listOf(
                com.example.data.model.CoinCashoutTransaction(
                    coinsAmount = 500,
                    usdAmount = 5.00,
                    method = "PAYPAL",
                    destinationAccount = "alex.vance.radio@gmail.com",
                    accountHolderName = "Alex Vance",
                    status = "COMPLETED",
                    referenceId = "PAY-WIRE-9821-USD",
                    feeUsd = 0.0,
                    timestamp = System.currentTimeMillis() - 86400000L * 2
                ),
                com.example.data.model.CoinCashoutTransaction(
                    coinsAmount = 250,
                    usdAmount = 2.50,
                    method = "CASH_APP",
                    destinationAccount = "\$AlexVanceRadio",
                    accountHolderName = "Alex Vance",
                    status = "COMPLETED",
                    referenceId = "CASH-WIRE-4412-USD",
                    feeUsd = 0.0,
                    timestamp = System.currentTimeMillis() - 86400000L * 5
                )
            )
            defaultCashouts.forEach { dao.insertCashoutTransaction(it) }

            // Default Encrypted Offline Messages
            val defaultEncryptedMessages = listOf(
                EncryptedMessageRecord(
                    messageId = "msg_enc_001",
                    conversationId = "chan_community",
                    senderNumber = "+1 (555) 749-1029",
                    senderCallsign = "SHADOW-01",
                    encryptedPayloadBase64 = "GCM/qV8M1jL2mO+3kP4/aes256==enc",
                    encryptionIv = "A3F809B2C19E45781290EF31",
                    keyFingerprint = "COMMUNITY-256-AES",
                    messageType = "VOICE_PTT",
                    audioDurationMs = 2800L,
                    waveAmplitudes = "10,25,60,85,90,75,40,20,5",
                    timestamp = System.currentTimeMillis() - 120000,
                    isOutgoing = false,
                    deliveryStatus = "DELIVERED",
                    isOfflineAccessible = true
                ),
                EncryptedMessageRecord(
                    messageId = "msg_enc_002",
                    conversationId = "chan_community",
                    senderNumber = "+1 (888) WIRE-7734",
                    senderCallsign = "VIPER-7",
                    encryptedPayloadBase64 = "GCM/91kM3zL4aB+5qX7/aes256==enc",
                    encryptionIv = "C81920AF3B489012DE876123",
                    keyFingerprint = "COMMUNITY-256-AES",
                    messageType = "VOICE_PTT",
                    audioDurationMs = 3200L,
                    waveAmplitudes = "15,40,75,95,90,85,60,30,10",
                    timestamp = System.currentTimeMillis() - 60000,
                    isOutgoing = true,
                    deliveryStatus = "ENCRYPTED_LOCAL",
                    isOfflineAccessible = true
                )
            )
            defaultEncryptedMessages.forEach { dao.insertEncryptedMessage(it) }

            // Default Burner Number Metadata (Encrypted Offline Access Vault)
            val defaultBurnerMetadataList = listOf(
                BurnerNumberMetadata(
                    phoneNumber = "+1 (888) WIRE-7734",
                    label = "Primary Encrypted Burner",
                    areaCode = "888",
                    cityRegion = "Toll-Free USA",
                    countryCode = "US",
                    firebaseUid = "fb_burner_usr_8887734",
                    verificationStatus = "ACTIVE",
                    allocatedAt = System.currentTimeMillis() - 86400000L,
                    expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000L),
                    keyStoreAlias = "burner_vault_888_7734",
                    encryptedMetadataBlob = "ENC_SIP_METADATA_AES256_GCM_OK",
                    isOfflineVaultEnabled = true,
                    transmissionLimit = 500,
                    transmissionsUsed = 14
                ),
                BurnerNumberMetadata(
                    phoneNumber = "+1 (415) 890-4122",
                    label = "San Francisco Recon Burner",
                    areaCode = "415",
                    cityRegion = "San Francisco, CA",
                    countryCode = "US",
                    firebaseUid = "fb_burner_usr_4158904",
                    verificationStatus = "ACTIVE",
                    allocatedAt = System.currentTimeMillis() - 3600000L * 4,
                    expiresAt = System.currentTimeMillis() + (20 * 60 * 60 * 1000L),
                    keyStoreAlias = "burner_vault_415_8904",
                    encryptedMetadataBlob = "ENC_SIP_METADATA_AES256_GCM_SF",
                    isOfflineVaultEnabled = true,
                    transmissionLimit = 250,
                    transmissionsUsed = 6
                )
            )
            defaultBurnerMetadataList.forEach { dao.insertBurnerMetadata(it) }
        }
    }
}

