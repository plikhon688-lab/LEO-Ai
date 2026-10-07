package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CharacterStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("LEO AI", appName)
  }

  @Test
  fun `verify character stats tier ranking`() {
    val stats = CharacterStats(
      strength = 90,
      speed = 92,
      defense = 88,
      intelligence = 95,
      magic = 96,
      combat = 90,
      agility = 91,
      endurance = 90
    )
    assertTrue(stats.overallRating >= 90)
    assertEquals("EX-Rank (Transcendent)", stats.rankTier)
  }
}
