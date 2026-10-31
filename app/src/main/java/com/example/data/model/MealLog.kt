package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meal_logs",
    indices = [Index(value = ["date", "mealNumber"], unique = true)]
)
data class MealLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // "YYYY-MM-DD"
    val mealNumber: Int,
    val mealName: String,
    val scheduledTime: String,
    val status: CompletionStatus = CompletionStatus.PENDING,
    val actionTimestamp: Long = 0L,
    val notes: String? = null
)
