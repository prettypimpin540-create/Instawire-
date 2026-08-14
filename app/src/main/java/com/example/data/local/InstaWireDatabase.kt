package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BurnerLine
import com.example.data.model.Channel
import com.example.data.model.Contact
import com.example.data.model.Transmission
import com.example.data.model.UserIdentity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserIdentity::class,
        Contact::class,
        Channel::class,
        Transmission::class,
        BurnerLine::class
    ],
    version = 6,
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

            // Default Channels
            val defaultChannels = listOf(
                Channel(
                    id = "tactical_alpha",
                    name = "TACTICAL ALPHA 01",
                    frequency = "462.5625 MHz",
                    description = "Primary operational group channel with AES-256 GCM encryption",
                    activeMembersCount = 14,
                    isEncrypted = true,
                    safetyFingerprint = "ALPHA-256-AES-GCM",
                    safetyKeyBlocks = "83910 28491 94820 18274 02938 48192 73910 82941 02948 19284 84019 92847"
                ),
                Channel(
                    id = "global_guard",
                    name = "GLOBAL GUARD SECURE",
                    frequency = "467.5875 MHz",
                    description = "Zero-lag low-latency worldwide encrypted perimeter channel",
                    activeMembersCount = 42,
                    isEncrypted = true,
                    safetyFingerprint = "GUARD-256-MIL-E2EE",
                    safetyKeyBlocks = "10293 84719 20491 73910 82941 02948 48192 19284 72910 92847 84019 28401"
                ),
                Channel(
                    id = "ghost_recon",
                    name = "GHOST RECON [VIP]",
                    frequency = "462.6125 MHz",
                    description = "Anonymous burner-only frequency with zero metadata logging",
                    activeMembersCount = 8,
                    isEncrypted = true,
                    safetyFingerprint = "GHOST-ZERO-KNOWLEDGE",
                    safetyKeyBlocks = "94820 18274 02938 83910 28491 48192 73910 82941 02948 19284 92847 84019"
                ),
                Channel(
                    id = "emergency_sos",
                    name = "EMERGENCY DISPATCH",
                    frequency = "462.6750 MHz",
                    description = "Priority emergency channel with active noise cancellation bypass",
                    activeMembersCount = 19,
                    isEncrypted = true,
                    safetyFingerprint = "DISPATCH-PRIORITY-911",
                    safetyKeyBlocks = "20491 73910 82941 10293 84719 02948 48192 19284 72910 92847 84019 28401"
                ),
                Channel(
                    id = "tacops_support",
                    name = "TACOPS SPECIALIST SUPPORT",
                    frequency = "469.9000 MHz",
                    description = "Direct 24/7 technical specialist support frequency with instant voice dispatch",
                    activeMembersCount = 3,
                    isEncrypted = true,
                    safetyFingerprint = "SUPPORT-E2EE-DIRECT",
                    safetyKeyBlocks = "99401 18274 02938 83910 28491 48192 73910 82941 02948 19284 92847 84019"
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
        }
    }
}
