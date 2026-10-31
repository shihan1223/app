package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medicine_logs",
    indices = [Index(value = ["scheduledDate", "medicineId", "scheduledTime"], unique = true)]
)
data class MedicineLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val medicineId: Long,
    val medicineName: String,
    val scheduledDate: String, // "YYYY-MM-DD"
    val scheduledTime: String, // "HH:mm"
    val dosageInfo: String,
    val timingInstruction: TimingInstruction,
    val associatedMealNumber: Int? = null,
    val status: CompletionStatus = CompletionStatus.PENDING,
    val actionTimestamp: Long = 0L,
    val notes: String? = null
)
