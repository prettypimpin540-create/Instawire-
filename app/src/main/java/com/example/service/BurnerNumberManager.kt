package com.example.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import kotlin.random.Random

data class BurnerNumber(
    val id: String = UUID.randomUUID().toString(),
    val phoneNumber: String,
    val label: String,
    val areaCode: String,
    val cityRegion: String,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000L), // 24 hours default
    val isActive: Boolean = false
) {
    val formattedDisplay: String
        get() = phoneNumber

    val remainingHours: Long
        get() = ((expiresAt - System.currentTimeMillis()).coerceAtLeast(0)) / (1000 * 60 * 60)
}

/**
 * Service that simulates the generation and lifecycle of temporary burner phone numbers
 * and manages swapping the active burner line for anonymous communication.
 */
class BurnerNumberManager {

    private val sampleAreaCodes = listOf(
        Pair("212", "New York, NY"),
        Pair("310", "Los Angeles, CA"),
        Pair("415", "San Francisco, CA"),
        Pair("312", "Chicago, IL"),
        Pair("305", "Miami, FL"),
        Pair("512", "Austin, TX"),
        Pair("206", "Seattle, WA"),
        Pair("404", "Atlanta, GA"),
        Pair("617", "Boston, MA"),
        Pair("702", "Las Vegas, NV")
    )

    private val _burnerNumbers = MutableStateFlow<List<BurnerNumber>>(emptyList())
    val burnerNumbers: StateFlow<List<BurnerNumber>> = _burnerNumbers.asStateFlow()

    private val _activeBurnerNumber = MutableStateFlow<BurnerNumber?>(null)
    val activeBurnerNumber: StateFlow<BurnerNumber?> = _activeBurnerNumber.asStateFlow()

    init {
        // Seed with two realistic temporary burner numbers
        val initialOne = generateSimulatedNumber("Primary Burner", "415")
        val initialTwo = generateSimulatedNumber("Backup Ghost", "310")
        _burnerNumbers.value = listOf(
            initialOne.copy(isActive = true),
            initialTwo.copy(isActive = false)
        )
        _activeBurnerNumber.value = initialOne.copy(isActive = true)
    }

    /**
     * Generates a new simulated temporary phone number.
     */
    fun generateBurnerNumber(
        label: String? = null,
        preferredAreaCode: String? = null,
        ttlHours: Long = 24
    ): BurnerNumber {
        val area = sampleAreaCodes.find { it.first == preferredAreaCode } ?: sampleAreaCodes.random()
        val num = generateSimulatedNumber(
            customLabel = label ?: "Temp Line ${Random.nextInt(10, 99)}",
            areaCode = area.first,
            cityRegion = area.second,
            ttlHours = ttlHours
        )

        val updatedList = _burnerNumbers.value.toMutableList()
        // If it's the first number, make it active
        val shouldBeActive = updatedList.isEmpty()
        val finalNum = num.copy(isActive = shouldBeActive)
        updatedList.add(0, finalNum)
        _burnerNumbers.value = updatedList

        if (shouldBeActive) {
            _activeBurnerNumber.value = finalNum
        }

        return finalNum
    }

    /**
     * Swaps the active burner number to the specified [phoneNumber].
     */
    fun swapActiveBurner(phoneNumber: String): Boolean {
        val current = _burnerNumbers.value
        val target = current.find { it.phoneNumber == phoneNumber } ?: return false

        val updated = current.map { item ->
            item.copy(isActive = item.phoneNumber == phoneNumber)
        }
        _burnerNumbers.value = updated
        _activeBurnerNumber.value = target.copy(isActive = true)
        return true
    }

    /**
     * Deletes a burner number. If it was active, sets another available number as active.
     */
    fun removeBurner(phoneNumber: String) {
        val filtered = _burnerNumbers.value.filterNot { it.phoneNumber == phoneNumber }
        if (_activeBurnerNumber.value?.phoneNumber == phoneNumber) {
            val nextActive = filtered.firstOrNull()?.copy(isActive = true)
            _activeBurnerNumber.value = nextActive
            _burnerNumbers.value = filtered.mapIndexed { idx, item ->
                if (idx == 0 && nextActive != null) item.copy(isActive = true) else item.copy(isActive = false)
            }
        } else {
            _burnerNumbers.value = filtered
        }
    }

    private fun generateSimulatedNumber(
        customLabel: String,
        areaCode: String = "415",
        cityRegion: String? = null,
        ttlHours: Long = 24
    ): BurnerNumber {
        val region = cityRegion ?: (sampleAreaCodes.find { it.first == areaCode }?.second ?: "United States")
        val prefix = Random.nextInt(200, 899)
        val line = Random.nextInt(1000, 9999)
        val formatted = "+1 ($areaCode) $prefix-$line"
        val expiresAt = System.currentTimeMillis() + (ttlHours * 60 * 60 * 1000L)

        return BurnerNumber(
            phoneNumber = formatted,
            label = customLabel,
            areaCode = areaCode,
            cityRegion = region,
            expiresAt = expiresAt,
            isActive = false
        )
    }
}
