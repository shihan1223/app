package com.example.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.model.FoodSchedule
import com.example.data.model.Medicine
import com.example.data.model.TimingInstruction
import com.example.data.repository.MediMealRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object AlarmScheduler {

    private const val TAG = "AlarmScheduler"

    fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    fun scheduleMealAlarm(context: Context, meal: FoodSchedule) {
        if (!meal.isEnabled) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerEpochMillis = calculateNextTriggerMillis(meal.scheduledTime)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_TRIGGER_ALARM
            putExtra(NotificationHelper.EXTRA_ITEM_TYPE, "MEAL")
            putExtra(NotificationHelper.EXTRA_MEAL_NUMBER, meal.mealNumber)
            putExtra(NotificationHelper.EXTRA_ITEM_NAME, meal.displayName)
            putExtra(NotificationHelper.EXTRA_ITEM_TIME, meal.scheduledTime)
        }

        val requestCode = 1000 + meal.mealNumber
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleExact(context, alarmManager, triggerEpochMillis, pendingIntent)
        Log.d(TAG, "Scheduled meal ${meal.displayName} at $triggerEpochMillis")
    }

    fun scheduleMedicineAlarm(
        context: Context,
        medicine: Medicine,
        scheduledTime: String,
        doseIndex: Int
    ) {
        if (!medicine.isActive) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerEpochMillis = calculateNextTriggerMillis(scheduledTime)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_TRIGGER_ALARM
            putExtra(NotificationHelper.EXTRA_ITEM_TYPE, "MEDICINE")
            putExtra(NotificationHelper.EXTRA_ITEM_ID, medicine.id)
            putExtra(NotificationHelper.EXTRA_ITEM_NAME, medicine.name)
            putExtra(NotificationHelper.EXTRA_ITEM_TIME, scheduledTime)
            putExtra(NotificationHelper.EXTRA_DOSAGE, medicine.dosageDescription)
            putExtra(NotificationHelper.EXTRA_TIMING_INSTR, medicine.timingInstruction.name)
            if (medicine.associatedMealNumber != null) {
                putExtra(NotificationHelper.EXTRA_MEAL_NUMBER, medicine.associatedMealNumber)
            }
        }

        val requestCode = 20000 + (medicine.id.toInt() * 10) + doseIndex
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleExact(context, alarmManager, triggerEpochMillis, pendingIntent)
        Log.d(TAG, "Scheduled medicine ${medicine.name} at $scheduledTime ($triggerEpochMillis)")
    }

    fun snoozeAlarm(
        context: Context,
        itemType: String,
        idOrNumber: Long,
        name: String,
        originalTime: String,
        snoozeMinutes: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerEpochMillis = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000L)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_TRIGGER_ALARM
            putExtra(NotificationHelper.EXTRA_ITEM_TYPE, itemType)
            if (itemType == "MEAL") {
                putExtra(NotificationHelper.EXTRA_MEAL_NUMBER, idOrNumber.toInt())
            } else {
                putExtra(NotificationHelper.EXTRA_ITEM_ID, idOrNumber)
            }
            putExtra(NotificationHelper.EXTRA_ITEM_NAME, name)
            putExtra(NotificationHelper.EXTRA_ITEM_TIME, originalTime)
        }

        val requestCode = 50000 + (idOrNumber.toInt() % 1000)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleExact(context, alarmManager, triggerEpochMillis, pendingIntent)
    }

    private fun scheduleExact(
        context: Context,
        alarmManager: AlarmManager,
        triggerMillis: Long,
        pendingIntent: PendingIntent
    ) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val showIntent = Intent(context, AlarmActivity::class.java)
                val showPendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    showIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerMillis, showPendingIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            }
        } catch (se: SecurityException) {
            Log.e(TAG, "Exact alarm permission not granted, falling back to inexact", se)
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling alarm", e)
        }
    }

    private fun calculateNextTriggerMillis(timeStr: String): Long {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val targetTime = try {
            LocalTime.parse(timeStr, formatter)
        } catch (e: Exception) {
            LocalTime.of(8, 0)
        }

        val now = LocalDateTime.now()
        var targetDateTime = LocalDateTime.of(LocalDate.now(), targetTime)

        // If target time has already passed today, schedule for tomorrow
        if (targetDateTime.isBefore(now)) {
            targetDateTime = targetDateTime.plusDays(1)
        }

        return targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    suspend fun rescheduleAll(context: Context, repository: MediMealRepository) {
        try {
            val meals = repository.allFoodSchedules.first()
            meals.forEach { meal ->
                scheduleMealAlarm(context, meal)
            }

            val meds = repository.activeMedicines.first()
            val mealMap = meals.associateBy { it.mealNumber }

            meds.forEach { med ->
                if (med.associatedMealNumber != null && mealMap.containsKey(med.associatedMealNumber)) {
                    val meal = mealMap[med.associatedMealNumber]!!
                    val baseTime = LocalTime.parse(meal.scheduledTime)
                    val medTime = when (med.timingInstruction) {
                        TimingInstruction.BEFORE_FOOD -> baseTime.minusMinutes(Math.abs(med.mealOffsetMinutes).toLong())
                        TimingInstruction.AFTER_FOOD -> baseTime.plusMinutes(Math.abs(med.mealOffsetMinutes).toLong())
                        TimingInstruction.WITH_FOOD -> baseTime
                        else -> baseTime
                    }
                    val timeStr = medTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                    scheduleMedicineAlarm(context, med, timeStr, 0)
                } else {
                    med.reminderTimesList.forEachIndexed { index, timeStr ->
                        scheduleMedicineAlarm(context, med, timeStr, index)
                    }
                }
            }
            Log.d(TAG, "Successfully rescheduled all alarms.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reschedule alarms", e)
        }
    }
}
