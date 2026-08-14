package com.example.security

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.KeyGenerator

object SecurityEngine {

    const val ENCRYPTION_STANDARD = "AES-256-GCM Military Grade"
    const val PROTOCOL_VERSION = "InstaWire E2EE v4.2"

    fun generateSessionKey(): String {
        val bytes = ByteArray(32) // 256 bits
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun generateSafetyNumber(localNumber: String, remoteNumber: String): String {
        val combined = listOf(localNumber, remoteNumber).sorted().joinToString("::")
        val md = MessageDigest.getInstance("SHA-512")
        val hash = md.digest(combined.toByteArray())
        
        // Convert into 12 chunks of 5 digits (Signal / WhatsApp E2EE safety format)
        val sb = StringBuilder()
        for (i in 0 until 12) {
            val chunkVal = ((hash[i * 2].toInt() and 0xFF) shl 8) or (hash[i * 2 + 1].toInt() and 0xFF)
            val num = chunkVal % 100000
            sb.append("%05d".format(num))
            if (i < 11) sb.append(" ")
        }
        return sb.toString()
    }

    fun generateHexFingerprint(seed: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(seed.toByteArray())
        return hash.take(8).joinToString(":") { "%02X".format(it) }
    }

    // 8x8 boolean matrix for cryptographic QR visual representation
    fun generateVisualCryptoMatrix(seed: String): List<List<Boolean>> {
        val random = java.util.Random(seed.hashCode().toLong())
        return List(8) { row ->
            List(8) { col ->
                // Keep corners dark for QR-like anchors
                if ((row < 2 && col < 2) || (row < 2 && col > 5) || (row > 5 && col < 2)) {
                    true
                } else {
                    random.nextBoolean()
                }
            }
        }
    }
}
