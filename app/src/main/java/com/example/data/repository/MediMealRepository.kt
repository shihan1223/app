package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.CompletionStatus
import com.example.data.model.DailyTimelineItem
import com.example.data.model.FoodSchedule
import com.example.data.model.MealLog
import com.example.data.model.Medicine
import com.example.data.model.MedicineCheckpointSummary
import com.example.data.model.MedicineLog
import com.example.data.model.RacetrackCheckpoint
import com.example.data.model.TimelineItemType
import com.example.data.model.TimingInstruction
import com.example.data.model.UserProfile
import com.example.data.model.WaterLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MediMealRepository(private val database: AppDatabase) {

    private val foodScheduleDao = database.foodScheduleDao()
    private val medicineDao = database.medicineDao()
    private val mealLogDao = database.mealLogDao()
    private val medicineLogDao = database.medicineLogDao()
    private val userProfileDao = database.userProfileDao()
    private val waterLogDao = database.waterLogDao()

    val allFoodSchedules: Flow<List<FoodSchedule>> = foodScheduleDao.getAllFoodSchedules()
    val activeMedicines: Flow<List<Medicine>> = medicineDao.getActiveMedicines()
    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile()

    fun getMealLogsForDate(date: String): Flow<List<MealLog>> = mealLogDao.getMealLogsForDate(date)
    fun getMedicineLogsForDate(date: String): Flow<List<MedicineLog>> = medicineLogDao.getMedicineLogsForDate(date)
    fun getWaterLogForDate(date: String): Flow<WaterLog?> = waterLogDao.getWaterLogForDate(date)

    val allMealHistory: Flow<List<MealLog>> = mealLogDao.getAllHistory()
    val allMedicineHistory: Flow<List<MedicineLog>> = medicineLogDao.getAllHistory()

    suspend fun checkAndSeedDatabase() {
        if (foodScheduleDao.getCount() == 0) {
            AppDatabase.populateDefaultData(database)
        }
    }

    suspend fun updateFoodSchedule(schedule: FoodSchedule) {
        foodScheduleDao.update(schedule)
    }

    suspend fun regenerateFoodSchedule(startTimeStr: String, intervalMinutes: Int, totalMeals: Int) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        var currentTime = LocalTime.parse(startTimeStr, formatter)
        val defaultNames = listOf(
            "Early Morning", "Breakfast", "Mid-Morning", "Pre-Lunch", "Lunch",
            "Afternoon Snack", "Late Afternoon", "Dinner", "Post-Dinner", "Night Snack",
            "Midnight Fuel", "Pre-Dawn Bite"
        )

        val newSchedules = mutableListOf<FoodSchedule>()
        for (i in 1..totalMeals) {
            val nameIndex = (i - 1).coerceAtMost(defaultNames.size - 1)
            val defName = "Meal $i – ${defaultNames[nameIndex]}"
            val timeStr = currentTime.format(formatter)
            newSchedules.add(
                FoodSchedule(
                    mealNumber = i,
                    defaultName = defName,
                    customName = null,
                    scheduledTime = timeStr,
                    isEnabled = true,
                    notes = null
                )
            )
            currentTime = currentTime.plusMinutes(intervalMinutes.toLong())
        }

        foodScheduleDao.deleteAll()
        foodScheduleDao.insertAll(newSchedules)
    }

    suspend fun addMedicine(medicine: Medicine): Long {
        return medicineDao.insert(medicine)
    }

    suspend fun updateMedicine(medicine: Medicine) {
        medicineDao.update(medicine)
    }

    suspend fun deleteMedicine(medicine: Medicine) {
        medicineDao.delete(medicine)
    }

    suspend fun getMedicineById(id: Long): Medicine? {
        return medicineDao.getMedicineById(id)
    }

    suspend fun logMealAction(
        date: String,
        mealNumber: Int,
        mealName: String,
        scheduledTime: String,
        status: CompletionStatus,
        notes: String? = null
    ) {
        val existing = mealLogDao.getMealLog(date, mealNumber)
        val log = existing?.copy(
            status = status,
            actionTimestamp = System.currentTimeMillis(),
            notes = notes ?: existing.notes
        ) ?: MealLog(
            date = date,
            mealNumber = mealNumber,
            mealName = mealName,
            scheduledTime = scheduledTime,
            status = status,
            actionTimestamp = System.currentTimeMillis(),
            notes = notes
        )
        mealLogDao.insertOrUpdate(log)
    }

    suspend fun logMedicineAction(
        date: String,
        medicineId: Long,
        medicineName: String,
        scheduledTime: String,
        dosageInfo: String,
        timingInstruction: TimingInstruction,
        associatedMealNumber: Int?,
        status: CompletionStatus,
        notes: String? = null
    ) {
        val existing = medicineLogDao.getMedicineLog(date, medicineId, scheduledTime)
        val log = existing?.copy(
            status = status,
            actionTimestamp = System.currentTimeMillis(),
            notes = notes ?: existing.notes
        ) ?: MedicineLog(
            medicineId = medicineId,
            medicineName = medicineName,
            scheduledDate = date,
            scheduledTime = scheduledTime,
            dosageInfo = dosageInfo,
            timingInstruction = timingInstruction,
            associatedMealNumber = associatedMealNumber,
            status = status,
            actionTimestamp = System.currentTimeMillis(),
            notes = notes
        )
        medicineLogDao.insertOrUpdate(log)
    }

    suspend fun updateWaterIntake(date: String, delta: Int) {
        val current = waterLogDao.getWaterLogForDateDirect(date)
        val newGlasses = ((current?.glassesDrank ?: 0) + delta).coerceAtLeast(0)
        val log = WaterLog(
            date = date,
            glassesDrank = newGlasses,
            goalGlasses = current?.goalGlasses ?: 8
        )
        waterLogDao.insertOrUpdate(log)
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        userProfileDao.update(profile)
    }

    // Combines Meals + Medicines into the signature Racetrack Checkpoints
    fun getRacetrackCheckpointsFlow(date: String): Flow<List<RacetrackCheckpoint>> {
        return combine(
            allFoodSchedules,
            activeMedicines,
            getMealLogsForDate(date),
            getMedicineLogsForDate(date)
        ) { meals, meds, mealLogs, medLogs ->
            val now = LocalTime.now()
            val mealLogMap = mealLogs.associateBy { it.mealNumber }
            val medLogMap = medLogs.associateBy { "${it.medicineId}_${it.scheduledTime}" }

            var nextUpcomingFound = false

            meals.map { meal ->
                val log = mealLogMap[meal.mealNumber]
                val isCompleted = log?.status == CompletionStatus.COMPLETED
                val isSkipped = log?.status == CompletionStatus.SKIPPED

                val mealTime = try {
                    LocalTime.parse(meal.scheduledTime)
                } catch (e: Exception) {
                    LocalTime.of(8, 0)
                }

                val isPassed = now.isAfter(mealTime)

                var isNext = false
                if (!nextUpcomingFound && !isCompleted && !isSkipped && !isPassed) {
                    isNext = true
                    nextUpcomingFound = true
                }

                // Medicines associated with this meal
                val attachedMeds = meds.filter { it.associatedMealNumber == meal.mealNumber }
                    .map { med ->
                        val scheduledMedTime = calculateMedicineTime(meal.scheduledTime, med)
                        val medLog = medLogMap["${med.id}_$scheduledMedTime"]
                        val isTaken = medLog?.status == CompletionStatus.COMPLETED

                        MedicineCheckpointSummary(
                            medicineId = med.id,
                            name = med.name,
                            dosage = med.dosageDescription,
                            timingInstruction = med.timingInstruction,
                            offsetMinutes = med.mealOffsetMinutes,
                            scheduledTime = scheduledMedTime,
                            isTaken = isTaken
                        )
                    }

                RacetrackCheckpoint(
                    mealNumber = meal.mealNumber,
                    mealName = meal.displayName,
                    scheduledTime = meal.scheduledTime,
                    isCompleted = isCompleted,
                    isSkipped = isSkipped,
                    isNextUpcoming = isNext,
                    isPassed = isPassed,
                    associatedMedicines = attachedMeds
                )
            }
        }
    }

    // Combined Chronological Timeline for today
    fun getDailyTimelineFlow(date: String): Flow<List<DailyTimelineItem>> {
        return combine(
            allFoodSchedules,
            activeMedicines,
            getMealLogsForDate(date),
            getMedicineLogsForDate(date)
        ) { meals, meds, mealLogs, medLogs ->
            val now = LocalTime.now()
            val mealLogMap = mealLogs.associateBy { it.mealNumber }
            val medLogMap = medLogs.associateBy { "${it.medicineId}_${it.scheduledTime}" }

            val items = mutableListOf<DailyTimelineItem>()

            // Add all enabled meals
            meals.filter { it.isEnabled }.forEach { meal ->
                val log = mealLogMap[meal.mealNumber]
                items.add(
                    DailyTimelineItem(
                        id = "meal_${meal.mealNumber}",
                        time = meal.scheduledTime,
                        title = meal.displayName,
                        subtitle = "Checkpoint #${meal.mealNumber} • Scheduled Meal",
                        type = TimelineItemType.MEAL,
                        status = log?.status ?: CompletionStatus.PENDING,
                        mealNumber = meal.mealNumber
                    )
                )
            }

            // Add all active medicines (both meal-coordinated and independent)
            val mealMap = meals.associateBy { it.mealNumber }
            meds.forEach { med ->
                if (med.associatedMealNumber != null && mealMap.containsKey(med.associatedMealNumber)) {
                    val associatedMeal = mealMap[med.associatedMealNumber]!!
                    val calcTime = calculateMedicineTime(associatedMeal.scheduledTime, med)
                    val log = medLogMap["${med.id}_$calcTime"]

                    val relationText = when (med.timingInstruction) {
                        TimingInstruction.BEFORE_FOOD -> "${Math.abs(med.mealOffsetMinutes)}m before ${associatedMeal.displayName}"
                        TimingInstruction.AFTER_FOOD -> "${Math.abs(med.mealOffsetMinutes)}m after ${associatedMeal.displayName}"
                        TimingInstruction.WITH_FOOD -> "With ${associatedMeal.displayName}"
                        TimingInstruction.EMPTY_STOMACH -> "Empty stomach (linked to ${associatedMeal.displayName})"
                        TimingInstruction.ANYTIME -> "Independent / Any time"
                    }

                    items.add(
                        DailyTimelineItem(
                            id = "med_${med.id}_$calcTime",
                            time = calcTime,
                            title = med.name,
                            subtitle = "${med.dosageDescription} • $relationText",
                            type = TimelineItemType.MEDICINE,
                            status = log?.status ?: CompletionStatus.PENDING,
                            medicineId = med.id,
                            mealNumber = med.associatedMealNumber,
                            timingInstruction = med.timingInstruction,
                            dosageInfo = med.dosageDescription
                        )
                    )
                } else {
                    // Independent times
                    med.reminderTimesList.forEach { timeStr ->
                        val log = medLogMap["${med.id}_$timeStr"]
                        items.add(
                            DailyTimelineItem(
                                id = "med_${med.id}_$timeStr",
                                time = timeStr,
                                title = med.name,
                                subtitle = "${med.dosageDescription} • ${med.timingInstruction.labelEn}",
                                type = TimelineItemType.MEDICINE,
                                status = log?.status ?: CompletionStatus.PENDING,
                                medicineId = med.id,
                                timingInstruction = med.timingInstruction,
                                dosageInfo = med.dosageDescription
                            )
                        )
                    }
                }
            }

            // Sort chronologically by time
            items.sortBy { it.time }

            // Determine next upcoming
            var nextFound = false
            items.map { item ->
                val itemTime = try {
                    LocalTime.parse(item.time)
                } catch (e: Exception) {
                    LocalTime.of(8, 0)
                }
                val isPassed = now.isAfter(itemTime)
                val isNext = !nextFound && item.status == CompletionStatus.PENDING && !isPassed
                if (isNext) nextFound = true

                item.copy(
                    isNextUpcoming = isNext,
                    isCurrentActive = isPassed && item.status == CompletionStatus.PENDING
                )
            }
        }
    }

    private fun calculateMedicineTime(mealScheduledTime: String, medicine: Medicine): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val baseTime = LocalTime.parse(mealScheduledTime, formatter)
            val resultTime = when (medicine.timingInstruction) {
                TimingInstruction.BEFORE_FOOD -> baseTime.minusMinutes(Math.abs(medicine.mealOffsetMinutes).toLong())
                TimingInstruction.AFTER_FOOD -> baseTime.plusMinutes(Math.abs(medicine.mealOffsetMinutes).toLong())
                TimingInstruction.WITH_FOOD -> baseTime
                else -> baseTime
            }
            resultTime.format(formatter)
        } catch (e: Exception) {
            mealScheduledTime
        }
    }
}
