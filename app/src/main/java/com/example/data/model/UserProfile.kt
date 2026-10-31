package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "Health Seeker",
    val email: String? = null,
    val targetMealsPerDay: Int = 10,
    val targetMedsPerDay: Int = 3,
    val languageCode: String = "en", // "en" or "bn"
    val isDarkMode: Boolean? = null, // null = system, true = dark, false = light
    val alarmSound: String = "default",
    val isVibrationEnabled: Boolean = true,
    val snoozeMinutes: Int = 10,
    val isWaterReminderEnabled: Boolean = true,
    val waterDailyGoalGlasses: Int = 8,
    val medicalNotes: String? = null,
    val doctorContact: String? = null
)

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey
    val date: String, // "YYYY-MM-DD"
    val glassesDrank: Int = 0,
    val goalGlasses: Int = 8
)
