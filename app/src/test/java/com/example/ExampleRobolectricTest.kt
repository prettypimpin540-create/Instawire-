package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.PttState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("InstaWire", appName)
  }

  @Test
  fun `ptt states verified`() {
    val idleState = PttState.IDLE
    val transmitState = PttState.TRANSMITTING
    val incomingState = PttState.INCOMING_TRANSMISSION

    assertEquals("IDLE", idleState.name)
    assertEquals("TRANSMITTING", transmitState.name)
    assertEquals("INCOMING_TRANSMISSION", incomingState.name)
  }

  @Test
  fun `verify subscription tier pricing and features`() {
    val freeTier = com.example.data.model.SubscriptionTier.FREE
    val proTier = com.example.data.model.SubscriptionTier.PRO
    val eliteTier = com.example.data.model.SubscriptionTier.BLACK_OPS
    val ghostTier = com.example.data.model.SubscriptionTier.GHOST_SENTINEL

    // Free tier includes encrypted communication
    assertEquals(0.0, freeTier.monthlyPriceUsd, 0.001)
    assertEquals(true, freeTier.hasE2EE)

    // Cheaper pricing with max payment of $9.99
    assertEquals(1.99, proTier.monthlyPriceUsd, 0.001)
    assertEquals(4.99, eliteTier.monthlyPriceUsd, 0.001)
    assertEquals(9.99, ghostTier.monthlyPriceUsd, 0.001)
    org.junit.Assert.assertTrue(ghostTier.monthlyPriceUsd <= 9.99)

    // Escalating features per tier
    org.junit.Assert.assertTrue(proTier.maxTransmissionSec > freeTier.maxTransmissionSec)
    org.junit.Assert.assertTrue(eliteTier.burnerLinesLimit > proTier.burnerLinesLimit)
    org.junit.Assert.assertTrue(ghostTier.hasQuantumTunnel)
  }
}

