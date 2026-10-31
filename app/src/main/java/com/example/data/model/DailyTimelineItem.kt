package com.example.data.model

enum class TimelineItemType {
    MEAL,
    MEDICINE,
    WATER
}

data class DailyTimelineItem(
    val id: String,
    val time: String, // "HH:mm"
    val title: String,
    val subtitle: String,
    val type: TimelineItemType,
    val status: CompletionStatus = CompletionStatus.PENDING,
    val mealNumber: Int? = null,
    val medicineId: Long? = null,
    val timingInstruction: TimingInstruction? = null,
    val dosageInfo: String? = null,
    val isNextUpcoming: Boolean = false,
    val isCurrentActive: Boolean = false
) {
    val formatted12Hour: String
        get() = try {
            val parts = time.split(":")
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
            time
        }
}

data class RacetrackCheckpoint(
    val mealNumber: Int,
    val mealName: String,
    val scheduledTime: String,
    val isCompleted: Boolean,
    val isSkipped: Boolean,
    val isNextUpcoming: Boolean,
    val isPassed: Boolean,
    val associatedMedicines: List<MedicineCheckpointSummary> = emptyList()
)

data class MedicineCheckpointSummary(
    val medicineId: Long,
    val name: String,
    val dosage: String,
    val timingInstruction: TimingInstruction,
    val offsetMinutes: Int,
    val scheduledTime: String,
    val isTaken: Boolean
)
