package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NumberType {
    PHONE,
    BURNER
}

enum class NoiseFilterMode(val label: String, val description: String) {
    STUDIO_CLEAR("Crystal Studio", "Multi-band noise suppression with pristine clarity"),
    TACTICAL_BANDPASS("Tactical Radio", "Military VHF bandpass filter with squelch clamp"),
    HEAVY_SUPPRESSION("Extreme Wind & Noise", "High-rejection gate for extreme background noise")
}

enum class WalkieLayoutType(
    val title: String,
    val subtitle: String,
    val description: String,
    val isPremium: Boolean,
    val priceDisplay: String,
    val iconName: String
) {
    CLASSIC_TACTICAL(
        title = "Classic Rugged Walkie",
        subtitle = "Standard Mil-Spec Chassis",
        description = "Heavy-duty rubberized walkie with centered tactical PTT, frequency banner, and squelch meters.",
        isPremium = false,
        priceDisplay = "FREE",
        iconName = "Radio"
    ),
    MIL_SPEC_COCKPIT(
        title = "Tactical Cockpit HUD",
        subtitle = "Combat Pilot Telemetry (Unlocked)",
        description = "Dual TX/RX decibel meters, live audio waterfall spectrogram, target crosshair, and combat flip controls.",
        isPremium = false,
        priceDisplay = "FREE",
        iconName = "Flight"
    ),
    CYBER_SYNTH_WAVE(
        title = "Cyberpunk Matrix HUD",
        subtitle = "Neon Terminal Uplink (Unlocked)",
        description = "Glowing neon ring PTT trigger, digital hex oscilloscope, high-tech waveform analyzer, and matrix telemetry.",
        isPremium = false,
        priceDisplay = "FREE",
        iconName = "Terminal"
    ),
    MINIMALIST_STEALTH(
        title = "Stealth Low-Profile",
        subtitle = "Blackout Ergonomic Edge",
        description = "Distraction-free deep blackout interface with huge lower-half ergonomic thumb trigger and micro-telemetry.",
        isPremium = false,
        priceDisplay = "FREE",
        iconName = "Shield"
    ),
    RETRO_VINTAGE_CB(
        title = "Retro 1980s CB Radio",
        subtitle = "Analog Chrome Transceiver (Unlocked)",
        description = "Vintage backlit needle VU meter, rotary channel selector knob, chrome bezel rivets, and CB mic clip.",
        isPremium = false,
        priceDisplay = "FREE",
        iconName = "SettingsRemote"
    )
}

enum class AppThemeScheme(
    val title: String,
    val subtitle: String,
    val primaryHex: Long,
    val glowHex: Long,
    val darkHex: Long,
    val borderHex: Long,
    val isPremium: Boolean = false,
    val priceDisplay: String = "FREE"
) {
    TACTICAL_GREEN(
        title = "Tactical Neon",
        subtitle = "Mil-Spec Operator Green",
        primaryHex = 0xFF3FB950,
        glowHex = 0xFF56D364,
        darkHex = 0xFF0D381E,
        borderHex = 0xFF1B4D2E,
        isPremium = false
    ),
    CYBER_AMBER(
        title = "Cyber Amber",
        subtitle = "High-Visibility Gold Alert",
        primaryHex = 0xFFF59E0B,
        glowHex = 0xFFFBBF24,
        darkHex = 0xFF451A03,
        borderHex = 0xFF78350F,
        isPremium = false
    ),
    STEALTH_CYAN(
        title = "Stealth Cyan",
        subtitle = "Night Recon Navy & Cyan",
        primaryHex = 0xFF38BDF8,
        glowHex = 0xFF79C0FF,
        darkHex = 0xFF082F49,
        borderHex = 0xFF0369A1,
        isPremium = false
    ),
    CRIMSON_ALERT(
        title = "Crimson Alert",
        subtitle = "Emergency Tactical Red",
        primaryHex = 0xFFEF4444,
        glowHex = 0xFFF87171,
        darkHex = 0xFF450A0A,
        borderHex = 0xFF991B1B,
        isPremium = false
    ),
    MATRIX_EMERALD(
        title = "Matrix Terminal",
        subtitle = "Deep Terminal Matrix (Unlocked)",
        primaryHex = 0xFF10B981,
        glowHex = 0xFF34D399,
        darkHex = 0xFF064E3B,
        borderHex = 0xFF065F46,
        isPremium = false,
        priceDisplay = "FREE"
    ),
    VIOLET_ECLIPSE(
        title = "Violet Eclipse",
        subtitle = "Electronic Warfare Violet (Unlocked)",
        primaryHex = 0xFFA855F7,
        glowHex = 0xFFC084FC,
        darkHex = 0xFF3B0764,
        borderHex = 0xFF6B21A8,
        isPremium = false,
        priceDisplay = "FREE"
    ),
    DESERT_STORM_GOLD(
        title = "Desert Storm Gold",
        subtitle = "Tactical Sand & Gold Bevel (Unlocked)",
        primaryHex = 0xFFEAB308,
        glowHex = 0xFFFACC15,
        darkHex = 0xFF422006,
        borderHex = 0xFF713F12,
        isPremium = false,
        priceDisplay = "FREE"
    ),
    ARCTIC_ICE_BLUE(
        title = "Arctic Ice Glacier",
        subtitle = "Sub-Zero Titanium Cyan (Unlocked)",
        primaryHex = 0xFF06B6D4,
        glowHex = 0xFF67E8F9,
        darkHex = 0xFF083344,
        borderHex = 0xFF0E7490,
        isPremium = false,
        priceDisplay = "FREE"
    ),
    SOLAR_FLARE_ORANGE(
        title = "Solar Flare Blaze",
        subtitle = "High-Intensity Fusion (Unlocked)",
        primaryHex = 0xFFFF5722,
        glowHex = 0xFFFF8A65,
        darkHex = 0xFF3E1107,
        borderHex = 0xFFB71C1C,
        isPremium = false,
        priceDisplay = "FREE"
    ),
    NIGHT_VISION_MONO(
        title = "Night Vision Mono",
        subtitle = "Phosphor Green Monochromatic (Unlocked)",
        primaryHex = 0xFF00FF66,
        glowHex = 0xFF80FFB2,
        darkHex = 0xFF00290A,
        borderHex = 0xFF005C17,
        isPremium = false,
        priceDisplay = "FREE"
    )
}

enum class PttSoundProfile(
    val title: String,
    val description: String,
    val pressFrequencies: List<Int>,
    val pressDurationMs: Int,
    val releaseFrequencies: List<Int>,
    val releaseDurationMs: Int,
    val isPremium: Boolean = false,
    val priceDisplay: String = "FREE"
) {
    NEXTEL_TACTICAL(
        title = "Nextel Direct Chirp",
        description = "Classic iDEN triple chirp & military roger beep",
        pressFrequencies = listOf(1200, 1800, 2400),
        pressDurationMs = 28,
        releaseFrequencies = listOf(1400, 1000),
        releaseDurationMs = 45,
        isPremium = false
    ),
    RETRO_VHF(
        title = "VHF Squelch Burst",
        description = "Analog radio squelch burst & dual-tone ack",
        pressFrequencies = listOf(800, 1600),
        pressDurationMs = 35,
        releaseFrequencies = listOf(1600, 1200, 800),
        releaseDurationMs = 30,
        isPremium = false
    ),
    CYBER_SYNTH(
        title = "Cyber Uplink",
        description = "Futuristic high-tech uplink tone & downlink pulse",
        pressFrequencies = listOf(1500, 2200, 3000),
        pressDurationMs = 22,
        releaseFrequencies = listOf(2800, 2100),
        releaseDurationMs = 40,
        isPremium = false
    ),
    STEALTH_PULSE(
        title = "Stealth Muted",
        description = "Low-frequency sub-bass pulse & minimal soft click",
        pressFrequencies = listOf(440, 660),
        pressDurationMs = 25,
        releaseFrequencies = listOf(550),
        releaseDurationMs = 30,
        isPremium = false
    ),
    POLICE_SCANNER(
        title = "Public Safety Scanner",
        description = "Trunked radio key-up burst & triple end tone",
        pressFrequencies = listOf(900, 1350, 1800),
        pressDurationMs = 30,
        releaseFrequencies = listOf(1800, 1500, 1200),
        releaseDurationMs = 35,
        isPremium = false,
        priceDisplay = "FREE"
    ),
    MIL_SPEC_BEEPER(
        title = "Mil-Spec Combat Beeper",
        description = "NATO command frequency sync pulse & combat roger chime",
        pressFrequencies = listOf(1050, 2100, 1050),
        pressDurationMs = 25,
        releaseFrequencies = listOf(2400, 1800, 1200),
        releaseDurationMs = 35,
        isPremium = false,
        priceDisplay = "FREE"
    ),
    SONAR_ACOUSTIC(
        title = "Submarine Sonar Ping",
        description = "Deep acoustic acoustic resonance ping & echo discharge",
        pressFrequencies = listOf(600, 1200, 2400),
        pressDurationMs = 30,
        releaseFrequencies = listOf(2000, 1000, 500),
        releaseDurationMs = 50,
        isPremium = false,
        priceDisplay = "FREE"
    )
}

enum class SubscriptionTier(
    val title: String,
    val priceDisplay: String,
    val monthlyPriceUsd: Double,
    val badgeLabel: String,
    val tagline: String,
    val maxTransmissionSec: Int,
    val burnerLinesLimit: Int,
    val burnerSelectionPoolSize: Int,
    val hasE2EE: Boolean,
    val hasNoiseCancellation: Boolean,
    val hasUnlimitedBurners: Boolean,
    val hasPriorityMesh: Boolean,
    val hasQuantumTunnel: Boolean,
    val supportLevel: String
) {
    FREE(
        title = "Basic Walkie",
        priceDisplay = "FREE",
        monthlyPriceUsd = 0.0,
        badgeLabel = "FREE BASIC",
        tagline = "Standard Walkie Talkie with 256-Bit Encrypted Communications",
        maxTransmissionSec = 30,
        burnerLinesLimit = 0,
        burnerSelectionPoolSize = 1,
        hasE2EE = true,
        hasNoiseCancellation = false,
        hasUnlimitedBurners = false,
        hasPriorityMesh = false,
        hasQuantumTunnel = false,
        supportLevel = "Automated Tactical AI Assistant"
    ),
    PRO(
        title = "Tactical Pro",
        priceDisplay = "$1.99 / mo",
        monthlyPriceUsd = 1.99,
        badgeLabel = "PRO TIER 1",
        tagline = "AI Studio Noise Cancellation & 1 Cloud Burner Line",
        maxTransmissionSec = 60,
        burnerLinesLimit = 1,
        burnerSelectionPoolSize = 3,
        hasE2EE = true,
        hasNoiseCancellation = true,
        hasUnlimitedBurners = false,
        hasPriorityMesh = false,
        hasQuantumTunnel = false,
        supportLevel = "Live Specialist Support Queue (PTT)"
    ),
    BLACK_OPS(
        title = "Black Ops Elite",
        priceDisplay = "$4.99 / mo",
        monthlyPriceUsd = 4.99,
        badgeLabel = "ELITE TIER 2",
        tagline = "5 Burner Lines, VHF Bandpass DSP & Priority Mesh",
        maxTransmissionSec = 180,
        burnerLinesLimit = 5,
        burnerSelectionPoolSize = 8,
        hasE2EE = true,
        hasNoiseCancellation = true,
        hasUnlimitedBurners = false,
        hasPriorityMesh = true,
        hasQuantumTunnel = false,
        supportLevel = "Priority 1 Live Specialist Dispatch (PTT)"
    ),
    GHOST_SENTINEL(
        title = "Ghost Sentinel",
        priceDisplay = "$9.99 / mo",
        monthlyPriceUsd = 9.99,
        badgeLabel = "VIP TIER 3",
        tagline = "Post-Quantum Cryptography & Ephemeral Self-Destruct Lines",
        maxTransmissionSec = 9999,
        burnerLinesLimit = 9999,
        burnerSelectionPoolSize = 16,
        hasE2EE = true,
        hasNoiseCancellation = true,
        hasUnlimitedBurners = true,
        hasPriorityMesh = true,
        hasQuantumTunnel = true,
        supportLevel = "Instant Dedicated Cryptographic Dispatch (PTT)"
    )
}

@Entity(tableName = "user_identity")
data class UserIdentity(
    @PrimaryKey val id: Int = 1,
    val phoneNumber: String = "+1 (555) 839-2041",
    val isPhoneVerified: Boolean = true,
    val isHumanVerified: Boolean = true,
    val hasAgreedToTerms: Boolean = true,
    val termsAgreedTimestamp: Long = 1700000000000L,
    val burnerNumber: String = "+1 (888) WIRE-7734",
    val hasBurnerSubscription: Boolean = true,
    val subscriptionTier: SubscriptionTier = SubscriptionTier.FREE,
    val activeNumberType: NumberType = NumberType.PHONE,
    val callsign: String = "User",
    val noiseFilterEnabled: Boolean = true,
    val noiseFilterMode: NoiseFilterMode = NoiseFilterMode.STUDIO_CLEAR,
    val layoutType: WalkieLayoutType = WalkieLayoutType.CLASSIC_TACTICAL,
    val themeScheme: AppThemeScheme = AppThemeScheme.TACTICAL_GREEN,
    val soundProfile: PttSoundProfile = PttSoundProfile.NEXTEL_TACTICAL,
    val unlockedLayouts: String = "CLASSIC_TACTICAL,MIL_SPEC_COCKPIT,CYBER_SYNTH_WAVE,MINIMALIST_STEALTH,RETRO_VINTAGE_CB",
    val hasPurchasedThemePack: Boolean = true,
    val chirpSoundEnabled: Boolean = true,
    val rogerBeepEnabled: Boolean = true,
    val hardwareVolumePttEnabled: Boolean = true,
    val hardwareVolumePttToggleMode: Boolean = false,
    val backgroundMonitoringEnabled: Boolean = true,
    val backgroundAudioBeepEnabled: Boolean = true,
    val zeroLogsEnabled: Boolean = true,
    val ephemeralTimeoutSeconds: Int = 60,
    val volumeLevel: Float = 0.85f,
    val squelchLevel: Float = 0.40f,
    val hapticFeedbackEnabled: Boolean = true,
    val audioRoutingToEarpiece: Boolean = false,
    val sleepModeBackgroundListeningEnabled: Boolean = true,
    val disclaimerAcknowledged: Boolean = true
) {
    val activeDisplayNumber: String
        get() = phoneNumber

    val isE2eeActive: Boolean
        get() = true

    fun isLayoutUnlocked(layout: WalkieLayoutType): Boolean = true

    fun isThemeUnlocked(theme: AppThemeScheme): Boolean = true

    fun isSoundUnlocked(sound: PttSoundProfile): Boolean = true
}

@Entity(tableName = "burner_lines")
data class BurnerLine(
    @PrimaryKey val number: String,
    val label: String,
    val areaCode: String,
    val cityRegion: String,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null,
    val isEphemeral: Boolean = false
)

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val number: String,
    val callsign: String,
    val isBurner: Boolean = false,
    val isOnline: Boolean = true,
    val isKeyVerified: Boolean = false,
    val safetyFingerprint: String = "E2EE-89A4-F291-C841",
    val safetyKeyBlocks: String = "48192 73910 82941 02948 19284 72910 84019 28401 92847 10293 84719 20491",
    val lastTransmissionTime: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

@Entity(tableName = "channels")
data class Channel(
    @PrimaryKey val id: String,
    val name: String,
    val frequency: String,
    val description: String,
    val activeMembersCount: Int,
    val isEncrypted: Boolean = true,
    val safetyFingerprint: String = "MIL-256-AES-GCM",
    val safetyKeyBlocks: String = "73910 82941 02948 48192 19284 72910 92847 10293 84719 84019 28401 20491",
    val isSystemChannel: Boolean = false,
    val isEmergency: Boolean = false,
    val channelCategory: String = "Custom",
    val frequencyCode: String = ""
) {
    val displayFrequencyCode: String
        get() = if (frequencyCode.isNotBlank()) frequencyCode else {
            val num = kotlin.math.abs((name + frequency + id).hashCode() % 9000) + 1000
            "FRQ-$num"
        }
}

enum class ScannerCategory(val title: String, val badgeIcon: String) {
    POLICE("Police & Sheriff", "🚓"),
    FIRE_RESCUE("Fire & Rescue", "🚒"),
    EMS_MEDICAL("EMS & Paramedics", "🚑"),
    MARINE_COAST_GUARD("Marine & Coast Guard", "⚓"),
    AVIATION_TOWER("Airport Tower & Air", "✈️"),
    ALL_HAZARDS("Public Safety Multi-Agency", "🚨")
}

data class PublicScannerFeed(
    val id: String,
    val name: String,
    val agency: String,
    val location: String,
    val stateCode: String,
    val category: ScannerCategory,
    val frequency: String,
    val activeListeners: Int,
    val streamUrl: String,
    val description: String,
    val tag: String,
    val isLive: Boolean = true
)

enum class WeatherAlertLevel(val label: String, val colorHex: Long) {
    NORMAL("NO SEVERE ALERTS", 0xFF10B981),
    ADVISORY("SPECIAL WEATHER ADVISORY", 0xFFF59E0B),
    WATCH("SEVERE THUNDERSTORM WATCH", 0xFFF97316),
    WARNING("TORNADO / FLASH FLOOD WARNING", 0xFFEF4444)
}

data class NoaaWeatherStation(
    val id: String,
    val callsign: String,
    val locationName: String,
    val state: String,
    val frequencyMhz: String,
    val streamUrl: String,
    val currentTempF: Int,
    val condition: String,
    val humidityPct: Int,
    val windMph: Int,
    val windDir: String,
    val baroInHg: Double,
    val alertLevel: WeatherAlertLevel,
    val activeAlertText: String
)

enum class DistressType(val title: String, val iconEmoji: String, val code: String) {
    LIFE_THREATENING_MEDICAL("Medical Emergency / Trauma", "🚨", "MED-01"),
    FIRE_EVACUATION("Structure / Wildfire Evac", "🔥", "FIRE-02"),
    MARINE_MAYDAY("Vessel Sinking / Maritime Mayday", "⛵", "MAYDAY-USCG"),
    SEARCH_AND_RESCUE("Lost Person / Wilderness SAR", "🌲", "SAR-04"),
    CRIME_IN_PROGRESS("Active Threat / Hostile Crime", "⚠️", "CRIME-05")
}

data class EmergencyAgency(
    val id: String,
    val name: String,
    val description: String,
    val phoneNumber: String,
    val dialActionUrl: String,
    val is24x7: Boolean = true,
    val badge: String,
    val iconType: String // "911", "COAST_GUARD", "CRISIS_988", "POISON", "SAR"
)

@Entity(tableName = "transmissions")
data class Transmission(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderName: String,
    val senderNumber: String,
    val senderCallsign: String,
    val targetType: String, // "CHANNEL" or "DIRECT"
    val targetId: String,
    val durationSeconds: Float,
    val timestamp: Long = System.currentTimeMillis(),
    val isEncrypted: Boolean = true,
    val isKeyVerified: Boolean = true,
    val waveAmplitudes: String = "15,35,70,85,60,95,80,45,20,10",
    val audioEffect: String = "CRYSTAL_CLEAR"
)

enum class AppMode {
    TACTICAL,
    WORLDWIDE
}

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val displayName: String = "Alex Vance",
    val callsign: String = "VIPER-7",
    val country: String = "United States",
    val countryCode: String = "US",
    val countryFlag: String = "🇺🇸",
    val city: String = "New York",
    val bio: String = "Global radio enthusiast & audio hacker. Always down to talk tech and travel!",
    val spokenLanguages: String = "English, Spanish",
    val avatarIcon: String = "radio_operator",
    val coinsBalance: Int = 350,
    val totalGiftsReceived: Int = 12,
    val totalGiftsSent: Int = 8,
    val reputationLevel: Int = 5,
    val badges: String = "GLOBETROTTER,EARLY_ADOPTER,VIP_SUPPORTER"
)

data class CallsignConflict(
    val isTaken: Boolean,
    val takenBy: String? = null
)

@Entity(tableName = "worldwide_rooms")
data class WorldwideRoom(
    @PrimaryKey val id: String,
    val name: String,
    val country: String,
    val countryCode: String,
    val countryFlag: String,
    val city: String,
    val region: String, // "Americas", "Europe", "Asia-Pacific", "Middle East", "Africa"
    val category: String, // "General", "Language Exchange", "Music & Jam", "Travel & Meet", "Night Owls", "Tech & Gaming"
    val activeListeners: Int,
    val activeSpeakersCount: Int,
    val language: String,
    val description: String,
    val isOfficial: Boolean = true,
    val isLive: Boolean = true,
    val tags: String = "VOICE,LIVE,GLOBAL",
    val createdAt: Long = System.currentTimeMillis()
) {
    val listenersCount: Int get() = activeListeners
}

data class WorldwideSpeaker(
    val id: String,
    val username: String,
    val callsign: String,
    val country: String,
    val countryFlag: String,
    val city: String,
    val bio: String,
    val isSpeaking: Boolean = false,
    val isHost: Boolean = false,
    val reputationScore: Int = 100,
    val badges: String = "ACTIVE_SPEAKER",
    val receivedGiftsCount: Int = 5
)

@Entity(tableName = "friends")
data class FriendUser(
    @PrimaryKey val id: String,
    val username: String,
    val callsign: String,
    val country: String,
    val countryFlag: String,
    val city: String,
    val bio: String = "",
    val isOnline: Boolean = true,
    val statusText: String = "Listening in Tokyo Lounge",
    val avatarIcon: String = "user_default",
    val mutualFriendsCount: Int = 2,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "blocked_users")
data class BlockedUser(
    @PrimaryKey val id: String,
    val username: String,
    val callsign: String,
    val country: String,
    val countryFlag: String,
    val reason: String = "User requested block",
    val blockedAt: Long = System.currentTimeMillis()
)

data class PaidGiftItem(
    val id: String,
    val name: String,
    val coinsCost: Int,
    val priceUsd: Double,
    val priceDisplay: String,
    val emoji: String,
    val badgeLabel: String,
    val description: String,
    val soundEffect: String
)

@Entity(tableName = "gift_transactions")
data class GiftTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val giftId: String,
    val giftName: String,
    val giftEmoji: String,
    val coinsSpent: Int,
    val senderUsername: String,
    val recipientId: String,
    val recipientUsername: String,
    val roomName: String,
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "room_messages")
data class LiveRoomMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomId: String,
    val senderId: String,
    val senderUsername: String,
    val senderCallsign: String,
    val senderCountryFlag: String,
    val senderCity: String,
    val text: String,
    val isGiftNotification: Boolean = false,
    val giftEmoji: String = "",
    val isVoiceSnippet: Boolean = false,
    val voiceDurationSeconds: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

object WorldwideGiftCatalog {
    val GIFTS = listOf(
        PaidGiftItem(
            id = "gift_coffee",
            name = "Tactical Coffee",
            coinsCost = 0,
            priceUsd = 0.0,
            priceDisplay = "FREE",
            emoji = "☕",
            badgeLabel = "CHEERS",
            description = "Send tactical coffee to cheer on the speaker",
            soundEffect = "COFFEE_CHIME"
        ),
        PaidGiftItem(
            id = "gift_rocket",
            name = "Rocket Boost",
            coinsCost = 0,
            priceUsd = 0.0,
            priceDisplay = "FREE",
            emoji = "🚀",
            badgeLabel = "BOOST",
            description = "Boost the speaker's frequency with a rocket animation",
            soundEffect = "ROCKET_BOOST"
        ),
        PaidGiftItem(
            id = "gift_walkie",
            name = "Radio Roger",
            coinsCost = 0,
            priceUsd = 0.0,
            priceDisplay = "FREE",
            emoji = "📻",
            badgeLabel = "TACTICAL",
            description = "Transmit a tactical roger salute to the channel",
            soundEffect = "GOLD_CHIRP"
        ),
        PaidGiftItem(
            id = "gift_crown",
            name = "Squad Crown",
            coinsCost = 0,
            priceUsd = 0.0,
            priceDisplay = "FREE",
            emoji = "👑",
            badgeLabel = "HONOR",
            description = "Honor the speaker with room-wide cheer banners",
            soundEffect = "ROYAL_FANFARE"
        ),
        PaidGiftItem(
            id = "gift_satellite",
            name = "Orbital Ping",
            coinsCost = 0,
            priceUsd = 0.0,
            priceDisplay = "FREE",
            emoji = "📡",
            badgeLabel = "ORBITAL",
            description = "Deploy an orbital satellite broadcast effect across the room",
            soundEffect = "SATELLITE_SWEEP"
        ),
        PaidGiftItem(
            id = "gift_diamond",
            name = "Encrypted Salute",
            coinsCost = 0,
            priceUsd = 0.0,
            priceDisplay = "FREE",
            emoji = "💎",
            badgeLabel = "LEGENDARY",
            description = "Ultimate encrypted voice salute with celebratory fanfare",
            soundEffect = "DIAMOND_EXPLOSION"
        )
    )
}

enum class CashoutMethod(
    val title: String,
    val badge: String,
    val feeDescription: String,
    val placeholder: String,
    val speed: String,
    val minCoins: Int = 100
) {
    PAYPAL(
        title = "PayPal Direct Transfer",
        badge = "INSTANT",
        feeDescription = "0% Platform Fee",
        placeholder = "Enter PayPal email address (e.g. user@gmail.com)",
        speed = "Instant (~60s)",
        minCoins = 100
    ),
    BANK_TRANSFER(
        title = "Direct Bank Wire / ACH",
        badge = "1-2 DAYS",
        feeDescription = "$0.00 Free ACH / Wire",
        placeholder = "Enter Account # & Routing # or IBAN",
        speed = "1 - 2 Business Days",
        minCoins = 200
    ),
    CRYPTO_USDT(
        title = "USDT / USDC Crypto (TRC-20 / SOL)",
        badge = "BLOCKCHAIN",
        feeDescription = "Network gas covered by InstaWire",
        placeholder = "Enter USDT (TRC-20) or Solana wallet address",
        speed = "5 - 10 Minutes",
        minCoins = 100
    ),
    CASH_APP(
        title = "Cash App Direct Payout",
        badge = "INSTANT",
        feeDescription = "Instant 0% Fee",
        placeholder = "Enter \$Cashtag (e.g. \$AlexVance)",
        speed = "Instant (~30s)",
        minCoins = 100
    ),
    STRIPE_DEBIT(
        title = "Visa / Mastercard Debit Payout",
        badge = "CARD DIRECT",
        feeDescription = "Instant card network payout",
        placeholder = "Enter Debit card number or linked billing email",
        speed = "Under 30 Minutes",
        minCoins = 150
    )
}

@Entity(tableName = "coin_cashouts")
data class CoinCashoutTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val coinsAmount: Int,
    val usdAmount: Double,
    val method: String,
    val destinationAccount: String,
    val accountHolderName: String,
    val status: String = "COMPLETED", // "COMPLETED", "PROCESSING"
    val referenceId: String,
    val feeUsd: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

enum class CoinStoreCategory(val title: String) {
    VIP_SUBSCRIPTION("VIP & Subscriptions"),
    CUSTOMIZATION("Themes & Layouts"),
    BURNER_NUMBERS("Burner Phone Lines"),
    AUDIO_SFX("PTT Soundboard & Beeps"),
    TACTICAL_TOOLS("Military DSP & Badges")
}

data class CoinInAppItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val coinCost: Int,
    val usdValueDisplay: String,
    val category: CoinStoreCategory,
    val emoji: String,
    val badge: String,
    val isFeatured: Boolean = false
)

object CoinInAppCatalog {
    val ITEMS = listOf(
        CoinInAppItem(
            id = "coin_item_pro_month",
            title = "Tactical Pro (1 Month VIP)",
            subtitle = "Full AI Studio DSP & 1 Burner Line",
            description = "Unlock 60s max transmission, AI noise cancellation, cloud burner line and priority queue.",
            coinCost = 199,
            usdValueDisplay = "$1.99 value",
            category = CoinStoreCategory.VIP_SUBSCRIPTION,
            emoji = "⚡",
            badge = "POPULAR",
            isFeatured = true
        ),
        CoinInAppItem(
            id = "coin_item_black_ops_month",
            title = "Black Ops Elite (1 Month VIP)",
            subtitle = "5 Burner Lines & Priority Mesh",
            description = "Unlock 180s transmission, 5 disposable burner numbers, VHF Bandpass DSP and priority mesh sync.",
            coinCost = 499,
            usdValueDisplay = "$4.99 value",
            category = CoinStoreCategory.VIP_SUBSCRIPTION,
            emoji = "🎖️",
            badge = "BEST VALUE",
            isFeatured = true
        ),
        CoinInAppItem(
            id = "coin_item_ghost_sentinel",
            title = "Ghost Sentinel (1 Month VIP)",
            subtitle = "Unlimited Burners & Quantum Tunnel",
            description = "Ultimate military clearance: unlimited burner lines, post-quantum crypto and zero transmission time limits.",
            coinCost = 999,
            usdValueDisplay = "$9.99 value",
            category = CoinStoreCategory.VIP_SUBSCRIPTION,
            emoji = "👑",
            badge = "ULTIMATE",
            isFeatured = true
        ),
        CoinInAppItem(
            id = "coin_item_master_theme_pack",
            title = "Master Themes & Layouts Pack",
            subtitle = "Unlock All 10 Themes & 5 Chassis",
            description = "Lifetime unlock for Cyberpunk Matrix, Retro CB Radio, Mil-Spec Cockpit HUD, Violet Eclipse and all color schemes.",
            coinCost = 594,
            usdValueDisplay = "$5.94 value",
            category = CoinStoreCategory.CUSTOMIZATION,
            emoji = "🎨",
            badge = "LIFETIME",
            isFeatured = true
        ),
        CoinInAppItem(
            id = "coin_item_soundboard_pack",
            title = "Tactical PTT Soundboard Pack",
            subtitle = "All 7 Pro Chirps & Roger Beeps",
            description = "Unlock Police Scanner, Mil-Spec Combat Beeper, Submarine Sonar Ping and retro squelch packs.",
            coinCost = 299,
            usdValueDisplay = "$2.99 value",
            category = CoinStoreCategory.AUDIO_SFX,
            emoji = "🔊",
            badge = "PRO SOUND",
            isFeatured = false
        ),
        CoinInAppItem(
            id = "coin_item_extra_burners",
            title = "3 Extra Cloud Burner Lines",
            subtitle = "Disposable Anonymized Numbers",
            description = "Instantly provisions 3 extra US/UK/International caller-ID masked burner phone numbers.",
            coinCost = 299,
            usdValueDisplay = "$2.99 value",
            category = CoinStoreCategory.BURNER_NUMBERS,
            emoji = "🔥",
            badge = "ANONYMOUS",
            isFeatured = false
        ),
        CoinInAppItem(
            id = "coin_item_dsp_extreme_filter",
            title = "Extreme Wind & Noise DSP Engine",
            subtitle = "-38dB Deep Voice Isolation",
            description = "Studio acoustic isolation algorithms for crystal clear voice transmissions in helicopters or extreme storms.",
            coinCost = 150,
            usdValueDisplay = "$1.50 value",
            category = CoinStoreCategory.TACTICAL_TOOLS,
            emoji = "🎙️",
            badge = "DSP ISOLATION",
            isFeatured = false
        ),
        CoinInAppItem(
            id = "coin_item_verified_gold_badge",
            title = "Gold Verified Radio Operator Badge",
            subtitle = "Worldwide Stage Verification ✨",
            description = "Showcases a gleaming golden operator badge next to your callsign in all worldwide rooms.",
            coinCost = 250,
            usdValueDisplay = "$2.50 value",
            category = CoinStoreCategory.TACTICAL_TOOLS,
            emoji = "⭐",
            badge = "GLOBAL BADGE",
            isFeatured = false
        ),
        CoinInAppItem(
            id = "coin_item_stage_megaphone",
            title = "Worldwide Stage Megaphone Boost",
            subtitle = "Room-Wide Broadcast Priority",
            description = "Grants 1 hour of top-stage speaking highlight and increased listener audio clarity in any worldwide room.",
            coinCost = 100,
            usdValueDisplay = "$1.00 value",
            category = CoinStoreCategory.TACTICAL_TOOLS,
            emoji = "📢",
            badge = "BOOST",
            isFeatured = false
        )
    )
}

/**
 * Encrypted Local Message Record for offline access and local message history.
 * Stores AES-GCM encrypted audio payloads or encrypted text bursts with cryptographic fingerprints.
 */
@Entity(tableName = "encrypted_messages")
data class EncryptedMessageRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val messageId: String = java.util.UUID.randomUUID().toString(),
    val conversationId: String, // Channel ID or Contact Number
    val senderNumber: String,
    val senderCallsign: String,
    val encryptedPayloadBase64: String, // AES-256 GCM encrypted audio ciphertext
    val encryptionIv: String, // 12-byte cryptographic IV (Hex/Base64)
    val keyFingerprint: String = "MIL-256-AES-GCM",
    val messageType: String = "VOICE_PTT", // "VOICE_PTT", "TEXT_DISPATCH", "EMERGENCY_BURST"
    val audioDurationMs: Long = 0L,
    val waveAmplitudes: String = "15,35,70,85,60,95,80,45,20,10",
    val timestamp: Long = System.currentTimeMillis(),
    val isOutgoing: Boolean = false,
    val deliveryStatus: String = "ENCRYPTED_LOCAL", // "ENCRYPTED_LOCAL", "TRANSMITTING", "SENT", "DELIVERED", "FAILED"
    val isOfflineAccessible: Boolean = true
)

/**
 * Metadata for burner phone lines for encrypted offline access and lifecycle management.
 * Tracks Firebase Auth identity linkage, verification status, cryptographic key aliases, and expiration rules.
 */
@Entity(tableName = "burner_number_metadata")
data class BurnerNumberMetadata(
    @PrimaryKey val phoneNumber: String,
    val label: String,
    val areaCode: String,
    val cityRegion: String,
    val countryCode: String = "US",
    val firebaseUid: String? = null,
    val verificationStatus: String = "ACTIVE", // "PENDING_SMS", "VERIFYING", "ACTIVE", "EXPIRED", "BURNED"
    val allocatedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000L),
    val keyStoreAlias: String = "burner_key_${System.currentTimeMillis()}",
    val encryptedMetadataBlob: String = "", // Encrypted SIP/VoIP credentials for offline caching
    val isOfflineVaultEnabled: Boolean = true,
    val transmissionLimit: Int = 250,
    val transmissionsUsed: Int = 0,
    val autoBurnOnExpire: Boolean = true
) {
    val remainingHours: Long
        get() = ((expiresAt - System.currentTimeMillis()).coerceAtLeast(0)) / (1000 * 60 * 60)

    val isExpired: Boolean
        get() = System.currentTimeMillis() >= expiresAt
}

