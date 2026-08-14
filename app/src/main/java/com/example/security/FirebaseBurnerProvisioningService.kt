package com.example.security

import kotlinx.coroutines.delay
import java.security.MessageDigest
import kotlin.random.Random

data class FirebaseProvisioningResponse(
    val burnerNumber: String,
    val functionName: String = "provisionBurnerNumber",
    val region: String = "us-central1",
    val executionTimeMs: Long,
    val requestId: String,
    val sessionAuthToken: String,
    val ttlSeconds: Long = 86400 // 24 hours
)

object FirebaseBurnerProvisioningService {

    /**
     * Simulates a secure Firebase Cloud Functions call to provision an anonymous VoIP/SIP burner line.
     */
    suspend fun callProvisionBurnerFunction(
        callsign: String,
        tierName: String
    ): FirebaseProvisioningResponse {
        val startTime = System.currentTimeMillis()
        // Simulate network call to Firebase Cloud Functions
        delay(Random.nextLong(600, 1100))

        val prefixes = when (tierName) {
            "GHOST_SENTINEL" -> listOf("888", "800", "VIP")
            "BLACK_OPS" -> listOf("877", "866", "855")
            else -> listOf("888", "800", "877", "866")
        }
        val prefix = prefixes.random()
        val num = Random.nextInt(1000, 9999)
        val generatedNumber = if (prefix == "VIP") "+1 (VIP) GHOST-$num" else "+1 ($prefix) WIRE-$num"

        val requestId = "req_fb_${System.currentTimeMillis().toString(16)}_${Random.nextInt(1000, 9999)}"
        val tokenRaw = "$callsign:$generatedNumber:${System.currentTimeMillis()}"
        val tokenMd5 = MessageDigest.getInstance("SHA-256").digest(tokenRaw.toByteArray())
            .take(8).joinToString("") { "%02x".format(it) }

        val executionTime = System.currentTimeMillis() - startTime

        return FirebaseProvisioningResponse(
            burnerNumber = generatedNumber,
            executionTimeMs = executionTime,
            requestId = requestId,
            sessionAuthToken = "fb-token-$tokenMd5",
            ttlSeconds = if (tierName == "GHOST_SENTINEL") 3600 else 86400
        )
    }
}
