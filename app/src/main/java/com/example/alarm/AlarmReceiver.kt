package com.example.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.model.CompletionStatus
import com.example.data.model.TimingInstruction
import com.example.data.repository.MediMealRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TRIGGER_ALARM = "com.example.medimeal.TRIGGER_ALARM"
        private const val TAG = "AlarmReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        Log.d(TAG, "onReceive action: $action")

        val notificationHelper = NotificationHelper(context)
        notificationHelper.createNotificationChannels()

        val itemType = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_TYPE) ?: "MEAL"
        val todayStr = LocalDate.now().toString()

        val repository = MediMealRepository(AppDatabase.getDatabase(context))

        when (action) {
            ACTION_TRIGGER_ALARM -> {
                triggerVibration(context)

                if (itemType == "MEDICINE") {
                    val medId = intent.getLongExtra(NotificationHelper.EXTRA_ITEM_ID, 0L)
                    val name = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_NAME) ?: "Medicine"
                    val time = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_TIME) ?: "08:00"
                    val dosage = intent.getStringExtra(NotificationHelper.EXTRA_DOSAGE) ?: "1 dose"
                    val timingInstrStr = intent.getStringExtra(NotificationHelper.EXTRA_TIMING_INSTR) ?: TimingInstruction.BEFORE_FOOD.name
                    val timingInstr = try {
                        TimingInstruction.valueOf(timingInstrStr)
                    } catch (e: Exception) {
                        TimingInstruction.BEFORE_FOOD
                    }
                    val mealNum = if (intent.hasExtra(NotificationHelper.EXTRA_MEAL_NUMBER)) {
                        intent.getIntExtra(NotificationHelper.EXTRA_MEAL_NUMBER, 0)
                    } else null

                    val notificationId = 10000 + (medId.toInt() * 10) + (time.hashCode() % 10)
                    notificationHelper.showMedicineNotification(
                        notificationId = notificationId,
                        medicineId = medId,
                        medicineName = name,
                        scheduledTime = time,
                        dosageInfo = dosage,
                        timingInstruction = timingInstr,
                        associatedMealNumber = mealNum
                    )

                    // Reschedule for tomorrow
                    CoroutineScope(Dispatchers.IO).launch {
                        val med = repository.getMedicineById(medId)
                        if (med != null && med.isActive) {
                            AlarmScheduler.scheduleMedicineAlarm(context, med, time, 0)
                        }
                    }
                } else {
                    // Meal
                    val mealNumber = intent.getIntExtra(NotificationHelper.EXTRA_MEAL_NUMBER, 1)
                    val mealName = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_NAME) ?: "Meal $mealNumber"
                    val time = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_TIME) ?: "05:00"

                    val notificationId = 2000 + mealNumber
                    notificationHelper.showMealNotification(
                        notificationId = notificationId,
                        mealNumber = mealNumber,
                        mealName = mealName,
                        scheduledTime = time
                    )

                    // Reschedule for tomorrow
                    CoroutineScope(Dispatchers.IO).launch {
                        val schedule = AppDatabase.getDatabase(context).foodScheduleDao().getFoodScheduleByMealNumber(mealNumber)
                        if (schedule != null && schedule.isEnabled) {
                            AlarmScheduler.scheduleMealAlarm(context, schedule)
                        }
                    }
                }
            }

            NotificationHelper.ACTION_MARK_COMPLETED -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        if (itemType == "MEDICINE") {
                            val medId = intent.getLongExtra(NotificationHelper.EXTRA_ITEM_ID, 0L)
                            val name = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_NAME) ?: "Medicine"
                            val time = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_TIME) ?: ""
                            val dosage = intent.getStringExtra(NotificationHelper.EXTRA_DOSAGE) ?: ""
                            val timingInstrStr = intent.getStringExtra(NotificationHelper.EXTRA_TIMING_INSTR) ?: TimingInstruction.BEFORE_FOOD.name
                            val timingInstr = try { TimingInstruction.valueOf(timingInstrStr) } catch (e: Exception) { TimingInstruction.BEFORE_FOOD }
                            val mealNum = if (intent.hasExtra(NotificationHelper.EXTRA_MEAL_NUMBER)) intent.getIntExtra(NotificationHelper.EXTRA_MEAL_NUMBER, 0) else null

                            repository.logMedicineAction(
                                date = todayStr,
                                medicineId = medId,
                                medicineName = name,
                                scheduledTime = time,
                                dosageInfo = dosage,
                                timingInstruction = timingInstr,
                                associatedMealNumber = mealNum,
                                status = CompletionStatus.COMPLETED
                            )
                            val notificationId = 10000 + (medId.toInt() * 10) + (time.hashCode() % 10)
                            notificationHelper.cancelNotification(notificationId)
                        } else {
                            val mealNum = intent.getIntExtra(NotificationHelper.EXTRA_MEAL_NUMBER, 1)
                            val name = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_NAME) ?: "Meal $mealNum"
                            val time = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_TIME) ?: ""

                            repository.logMealAction(
                                date = todayStr,
                                mealNumber = mealNum,
                                mealName = name,
                                scheduledTime = time,
                                status = CompletionStatus.COMPLETED
                            )
                            val notificationId = 2000 + mealNum
                            notificationHelper.cancelNotification(notificationId)
                        }
                    } finally {
                        pendingResult.finish()
                    }
                }
            }

            NotificationHelper.ACTION_SNOOZE -> {
                if (itemType == "MEDICINE") {
                    val medId = intent.getLongExtra(NotificationHelper.EXTRA_ITEM_ID, 0L)
                    val name = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_NAME) ?: "Medicine"
                    val time = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_TIME) ?: ""
                    AlarmScheduler.snoozeAlarm(context, "MEDICINE", medId, name, time, 10)
                    val notificationId = 10000 + (medId.toInt() * 10) + (time.hashCode() % 10)
                    notificationHelper.cancelNotification(notificationId)
                } else {
                    val mealNum = intent.getIntExtra(NotificationHelper.EXTRA_MEAL_NUMBER, 1)
                    val name = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_NAME) ?: "Meal $mealNum"
                    val time = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_TIME) ?: ""
                    AlarmScheduler.snoozeAlarm(context, "MEAL", mealNum.toLong(), name, time, 10)
                    val notificationId = 2000 + mealNum
                    notificationHelper.cancelNotification(notificationId)
                }
            }

            NotificationHelper.ACTION_SKIP -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        if (itemType == "MEDICINE") {
                            val medId = intent.getLongExtra(NotificationHelper.EXTRA_ITEM_ID, 0L)
                            val name = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_NAME) ?: "Medicine"
                            val time = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_TIME) ?: ""
                            repository.logMedicineAction(
                                date = todayStr,
                                medicineId = medId,
                                medicineName = name,
                                scheduledTime = time,
                                dosageInfo = "",
                                timingInstruction = TimingInstruction.ANYTIME,
                                associatedMealNumber = null,
                                status = CompletionStatus.SKIPPED
                            )
                            val notificationId = 10000 + (medId.toInt() * 10) + (time.hashCode() % 10)
                            notificationHelper.cancelNotification(notificationId)
                        } else {
                            val mealNum = intent.getIntExtra(NotificationHelper.EXTRA_MEAL_NUMBER, 1)
                            val name = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_NAME) ?: "Meal $mealNum"
                            val time = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_TIME) ?: ""
                            repository.logMealAction(
                                date = todayStr,
                                mealNumber = mealNum,
                                mealName = name,
                                scheduledTime = time,
                                status = CompletionStatus.SKIPPED
                            )
                            val notificationId = 2000 + mealNum
                            notificationHelper.cancelNotification(notificationId)
                        }
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }

    private fun triggerVibration(context: Context) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 400, 200, 400), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 400, 200, 400), -1)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Vibration failed", e)
        }
    }
}
