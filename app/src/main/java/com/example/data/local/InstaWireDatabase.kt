package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BlockedUser
import com.example.data.model.BurnerLine
import com.example.data.model.Channel
import com.example.data.model.CoinCashoutTransaction
import com.example.data.model.Contact
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
        CoinCashoutTransaction::class
    ],
    version = 9,
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
                        if (dao.getUserIdentitySync() == null) {
                            populateInitialData(dao)
                        } else {
                            // Ensure real-life channels exist
                            populateInitialData(dao)
                        }
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: InstaWireDao) {
            // Default User Identity - requires initial Human Verification & Terms Agreement
            dao.insertOrUpdateIdentity(
                UserIdentity(
                    id = 1,
                    phoneNumber = "+1 (555) 839-2041",
                    isPhoneVerified = false,
                    isHumanVerified = false,
                    hasAgreedToTerms = false,
                    termsAgreedTimestamp = 0L,
                    burnerNumber = "+1 (888) WIRE-7734",
                    hasBurnerSubscription = true,
                    callsign = "VIPER-7"
                )
            )

            // Real-Life Practical Channels
            val defaultChannels = listOf(
                Channel(
                    id = "chan_community",
                    name = "Local Community & Neighborhood Watch",
                    frequency = "462.5625 MHz (FRS/GMRS 1)",
                    description = "Open neighborhood safety, alerts, community check-ins & local communication",
                    activeMembersCount = 38,
                    isEncrypted = true,
                    safetyFingerprint = "COMMUNITY-256-AES",
                    safetyKeyBlocks = "83910 28491 94820 18274 02938 48192 73910 82941 02948 19284 84019 92847",
                    isEmergency = false,
                    channelCategory = "Community"
                ),
                Channel(
                    id = "chan_road_cb19",
                    name = "Highway Travel & Road Help (CB 19)",
                    frequency = "27.1850 MHz (CB Ch 19)",
                    description = "Interstate traffic alerts, road hazards, trucker reports, detours & vehicle help",
                    activeMembersCount = 74,
                    isEncrypted = false,
                    safetyFingerprint = "ROAD-CB19-OPEN",
                    safetyKeyBlocks = "10293 84719 20491 73910 82941 02948 48192 19284 72910 92847 84019 28401",
                    isEmergency = false,
                    channelCategory = "Travel & Road"
                ),
                Channel(
                    id = "chan_marine16",
                    name = "Marine VHF Ch 16 & Boating Safety",
                    frequency = "156.8000 MHz (VHF Ch 16)",
                    description = "International marine calling, coastal water safety, harbor traffic & boat hailing",
                    activeMembersCount = 29,
                    isEncrypted = false,
                    safetyFingerprint = "MARINE-VHF16-INTL",
                    safetyKeyBlocks = "94820 18274 02938 83910 28491 48192 73910 82941 02948 19284 92847 84019",
                    isEmergency = false,
                    channelCategory = "Marine"
                ),
                Channel(
                    id = "chan_sar_cert",
                    name = "Search & Rescue / CERT Teams",
                    frequency = "155.1600 MHz (National SAR)",
                    description = "Disaster preparedness, community emergency response (CERT) & volunteer SAR ops",
                    activeMembersCount = 18,
                    isEncrypted = true,
                    safetyFingerprint = "SAR-CERT-NATIONAL",
                    safetyKeyBlocks = "48192 73910 82941 02948 19284 72910 84019 28401 92847 10293 84719 20491",
                    isEmergency = false,
                    channelCategory = "Search & Rescue"
                ),
                Channel(
                    id = "chan_family_camp",
                    name = "Family & Outdoor Camping",
                    frequency = "462.6125 MHz (FRS Ch 3)",
                    description = "Hiking trails, national parks, family outings, campground chats & outdoor activity",
                    activeMembersCount = 45,
                    isEncrypted = true,
                    safetyFingerprint = "FAMILY-OUTDOOR-SAFE",
                    safetyKeyBlocks = "92847 10293 84719 20491 48192 73910 82941 02948 19284 72910 84019 28401",
                    isEmergency = false,
                    channelCategory = "Outdoors"
                ),
                Channel(
                    id = "chan_city_dispatch",
                    name = "City Events & Venue Security",
                    frequency = "467.5875 MHz (Commercial 4)",
                    description = "Venue staff, event coordination, business security patrols & crowd safety",
                    activeMembersCount = 22,
                    isEncrypted = true,
                    safetyFingerprint = "CITY-SECURITY-COMM",
                    safetyKeyBlocks = "84019 28401 92847 10293 84719 20491 48192 73910 82941 02948 19284 72910",
                    isEmergency = false,
                    channelCategory = "Security"
                ),
                Channel(
                    id = "chan_emergency_911",
                    name = "🚨 EMERGENCY SOS & DISPATCH (CH 9)",
                    frequency = "462.6750 MHz (Emergency SAR)",
                    description = "Life-safety emergency & distress only. REQUIRES LEGAL COMPLIANCE DISCLAIMER.",
                    activeMembersCount = 62,
                    isEncrypted = true,
                    safetyFingerprint = "EMERGENCY-SOS-PRIORITY",
                    safetyKeyBlocks = "20491 73910 82941 10293 84719 02948 48192 19284 72910 92847 84019 28401",
                    isEmergency = true,
                    channelCategory = "Emergency"
                ),
                Channel(
                    id = "chan_tacops_support",
                    name = "Specialist Radio Support 24/7",
                    frequency = "469.9000 MHz (Support)",
                    description = "Direct 24/7 radio technician dispatch, audio testing & live help",
                    activeMembersCount = 6,
                    isEncrypted = true,
                    safetyFingerprint = "SUPPORT-RADIO-DIRECT",
                    safetyKeyBlocks = "99401 18274 02938 83910 28491 48192 73910 82941 02948 19284 92847 84019",
                    isEmergency = false,
                    channelCategory = "Support"
                )
            )
            dao.insertChannels(defaultChannels)

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
        }
    }
}

