package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.FoodSchedule
import com.example.data.model.Medicine
import com.example.data.model.TimingInstruction
import com.example.ui.components.formatTime12h
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MediMeal", appName)
    }

    @Test
    fun `format 24-hour time to 12-hour AM PM correctly`() {
        assertEquals("05:00 AM", formatTime12h("05:00"))
        assertEquals("12:00 PM", formatTime12h("12:00"))
        assertEquals("01:00 PM", formatTime12h("13:00"))
        assertEquals("11:00 PM", formatTime12h("23:00"))
    }

    @Test
    fun `generate 10-meal schedule at 2-hour interval starting at 05 00 AM`() {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        var currentTime = LocalTime.parse("05:00", formatter)
        val times = mutableListOf<String>()

        for (i in 1..10) {
            times.add(currentTime.format(formatter))
            currentTime = currentTime.plusMinutes(120)
        }

        assertEquals(10, times.size)
        assertEquals("05:00", times[0])
        assertEquals("07:00", times[1])
        assertEquals("09:00", times[2])
        assertEquals("11:00", times[3])
        assertEquals("13:00", times[4])
        assertEquals("15:00", times[5])
        assertEquals("17:00", times[6])
        assertEquals("19:00", times[7])
        assertEquals("21:00", times[8])
        assertEquals("23:00", times[9])
    }

    @Test
    fun `medicine timing before food calculates correct lead time`() {
        val mealTime = LocalTime.parse("07:00") // Meal 2 Breakfast
        val offsetMinutes = 30
        val medTime = mealTime.minusMinutes(offsetMinutes.toLong())
        assertEquals("06:30", medTime.format(DateTimeFormatter.ofPattern("HH:mm")))
    }

    @Test
    fun `medicine timing after food calculates correct delay time`() {
        val mealTime = LocalTime.parse("13:00") // Meal 5 Lunch
        val offsetMinutes = 30
        val medTime = mealTime.plusMinutes(offsetMinutes.toLong())
        assertEquals("13:30", medTime.format(DateTimeFormatter.ofPattern("HH:mm")))
    }
}
