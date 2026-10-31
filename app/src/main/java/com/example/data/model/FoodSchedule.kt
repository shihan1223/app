package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_schedules")
data class FoodSchedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mealNumber: Int,
    val defaultName: String,
    val customName: String? = null,
    val scheduledTime: String, // "HH:mm" (24-hour format)
    val isEnabled: Boolean = true,
    val notes: String? = null
) {
    val displayName: String
        get() = if (!customName.isNullOrBlank()) customName else defaultName

    val formatted12Hour: String
        get() = try {
            val parts = scheduledTime.split(":")
            val hour = parts[0].toInt()
            val min = parts[1].toInt()
            val amPm = if (hour < 12) "AM" else "PM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            String.format("%02d:%02d %s", displayHour, min, amPm)
        } catch (e: Exception) {
            scheduledTime
        }
}
