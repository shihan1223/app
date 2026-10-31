package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String = "Tablet", // Tablet, Capsule, Syrup, Drops, Inhaler, Injection
    val dosageAmount: Double = 1.0,
    val dosageUnit: String = "tablet", // tablet, capsule, ml, drops, puff, unit
    val timingInstruction: TimingInstruction = TimingInstruction.BEFORE_FOOD,
    val associatedMealNumber: Int? = null, // 1 to 10 if linked to a meal
    val mealOffsetMinutes: Int = -30, // -30 = 30m before food, +30 = 30m after food, 0 = with food
    val timesPerDay: Int = 3, // Default 3 times per day
    val reminderTimesString: String = "08:00,13:00,20:00", // Comma separated HH:mm
    val startDate: String = "", // YYYY-MM-DD
    val endDate: String? = null, // YYYY-MM-DD
    val instructions: String? = null, // e.g. "Take with a full glass of water"
    val notes: String? = null,
    val isActive: Boolean = true,
    val colorHex: String = "#570D1D"
) {
    val reminderTimesList: List<String>
        get() = reminderTimesString.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    val dosageDescription: String
        get() = "${if (dosageAmount % 1.0 == 0.0) dosageAmount.toInt().toString() else dosageAmount.toString()} $dosageUnit"
}
