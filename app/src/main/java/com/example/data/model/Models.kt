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
        subtitle = "Combat Pilot Telemetry ($0.99)",
        description = "Dual TX/RX decibel meters, live audio waterfall spectrogram, target crosshair, and combat flip controls.",
        isPremium = true,
        priceDisplay = "$0.99",
        iconName = "Flight"
    ),
    CYBER_SYNTH_WAVE(
        title = "Cyberpunk Matrix HUD",
        subtitle = "Neon Terminal Uplink ($0.99)",
        description = "Glowing neon ring PTT trigger, digital hex oscilloscope, high-tech waveform analyzer, and matrix telemetry.",
        isPremium = true,
        priceDisplay = "$0.99",
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
        subtitle = "Analog Chrome Transceiver ($0.99)",
        description = "Vintage backlit needle VU meter, rotary channel selector knob, chrome bezel rivets, and CB mic clip.",
        isPremium = true,
        priceDisplay = "$0.99",
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
        subtitle = "Deep Terminal Matrix ($0.99)",
        primaryHex = 0xFF10B981,
        glowHex = 0xFF34D399,
        darkHex = 0xFF064E3B,
        borderHex = 0xFF065F46,
        isPremium = true,
        priceDisplay = "$0.99"
    ),
    VIOLET_ECLIPSE(
        title = "Violet Eclipse",
        subtitle = "Electronic Warfare Violet ($0.99)",
        primaryHex = 0xFFA855F7,
        glowHex = 0xFFC084FC,
        darkHex = 0xFF3B0764,
        borderHex = 0xFF6B21A8,
        isPremium = true,
        priceDisplay = "$0.99"
    ),
    DESERT_STORM_GOLD(
        title = "Desert Storm Gold",
        subtitle = "Tactical Sand & Gold Bevel ($0.99)",
        primaryHex = 0xFFEAB308,
        glowHex = 0xFFFACC15,
        darkHex = 0xFF422006,
        borderHex = 0xFF713F12,
        isPremium = true,
        priceDisplay = "$0.99"
    ),
    ARCTIC_ICE_BLUE(
        title = "Arctic Ice Glacier",
        subtitle = "Sub-Zero Titanium Cyan ($0.99)",
        primaryHex = 0xFF06B6D4,
        glowHex = 0xFF67E8F9,
        darkHex = 0xFF083344,
        borderHex = 0xFF0E7490,
        isPremium = true,
        priceDisplay = "$0.99"
    ),
    SOLAR_FLARE_ORANGE(
        title = "Solar Flare Blaze",
        subtitle = "High-Intensity Fusion ($0.99)",
        primaryHex = 0xFFFF5722,
        glowHex = 0xFFFF8A65,
        darkHex = 0xFF3E1107,
        borderHex = 0xFFB71C1C,
        isPremium = true,
        priceDisplay = "$0.99"
    ),
    NIGHT_VISION_MONO(
        title = "Night Vision Mono",
        subtitle = "Phosphor Green Monochromatic ($0.99)",
        primaryHex = 0xFF00FF66,
        glowHex = 0xFF80FFB2,
        darkHex = 0xFF00290A,
        borderHex = 0xFF005C17,
        isPremium = true,
        priceDisplay = "$0.99"
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
        isPremium = true,
        priceDisplay = "$0.99"
    ),
    MIL_SPEC_BEEPER(
        title = "Mil-Spec Combat Beeper",
        description = "NATO command frequency sync pulse & combat roger chime",
        pressFrequencies = listOf(1050, 2100, 1050),
        pressDurationMs = 25,
        releaseFrequencies = listOf(2400, 1800, 1200),
        releaseDurationMs = 35,
        isPremium = true,
        priceDisplay = "$0.99"
    ),
    SONAR_ACOUSTIC(
        title = "Submarine Sonar Ping",
        description = "Deep acoustic acoustic resonance ping & echo discharge",
        pressFrequencies = listOf(600, 1200, 2400),
        pressDurationMs = 30,
        releaseFrequencies = listOf(2000, 1000, 500),
        releaseDurationMs = 50,
        isPremium = true,
        priceDisplay = "$0.99"
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
    val isPhoneVerified: Boolean = false,
    val isHumanVerified: Boolean = false,
    val hasAgreedToTerms: Boolean = false,
    val termsAgreedTimestamp: Long = 0L,
    val burnerNumber: String = "+1 (888) WIRE-7734",
    val hasBurnerSubscription: Boolean = true,
    val subscriptionTier: SubscriptionTier = SubscriptionTier.PRO,
    val activeNumberType: NumberType = NumberType.PHONE,
    val callsign: String = "VIPER-7",
    val noiseFilterEnabled: Boolean = true,
    val noiseFilterMode: NoiseFilterMode = NoiseFilterMode.STUDIO_CLEAR,
    val layoutType: WalkieLayoutType = WalkieLayoutType.CLASSIC_TACTICAL,
    val themeScheme: AppThemeScheme = AppThemeScheme.TACTICAL_GREEN,
    val soundProfile: PttSoundProfile = PttSoundProfile.NEXTEL_TACTICAL,
    val unlockedLayouts: String = "CLASSIC_TACTICAL,MINIMALIST_STEALTH,TACTICAL_GREEN,CYBER_AMBER,STEALTH_CYAN,CRIMSON_ALERT",
    val hasPurchasedThemePack: Boolean = false,
    val chirpSoundEnabled: Boolean = true,
    val rogerBeepEnabled: Boolean = true,
    val hardwareVolumePttEnabled: Boolean = true,
    val backgroundMonitoringEnabled: Boolean = true,
    val backgroundAudioBeepEnabled: Boolean = true,
    val zeroLogsEnabled: Boolean = true,
    val ephemeralTimeoutSeconds: Int = 60,
    val volumeLevel: Float = 0.85f,
    val squelchLevel: Float = 0.40f
) {
    val activeDisplayNumber: String
        get() = if (activeNumberType == NumberType.BURNER) burnerNumber else phoneNumber

    val isE2eeActive: Boolean
        get() = subscriptionTier.hasE2EE

    fun isLayoutUnlocked(layout: WalkieLayoutType): Boolean {
        if (!layout.isPremium || hasPurchasedThemePack) return true
        return unlockedLayouts.contains(layout.name)
    }

    fun isThemeUnlocked(theme: AppThemeScheme): Boolean {
        if (!theme.isPremium || hasPurchasedThemePack) return true
        return unlockedLayouts.contains(theme.name)
    }

    fun isSoundUnlocked(sound: PttSoundProfile): Boolean {
        if (!sound.isPremium || hasPurchasedThemePack) return true
        return unlockedLayouts.contains(sound.name)
    }
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
    val isSystemChannel: Boolean = true
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


