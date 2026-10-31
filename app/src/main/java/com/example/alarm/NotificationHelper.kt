package com.example.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.TimingInstruction

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_MEDICINE_ID = "medimeal_medicines_channel"
        const val CHANNEL_MEAL_ID = "medimeal_meals_channel"
        const val CHANNEL_WATER_ID = "medimeal_water_channel"

        const val EXTRA_ACTION_TYPE = "extra_action_type"
        const val EXTRA_ITEM_TYPE = "extra_item_type" // "MEAL" or "MEDICINE"
        const val EXTRA_ITEM_ID = "extra_item_id"
        const val EXTRA_ITEM_NAME = "extra_item_name"
        const val EXTRA_ITEM_TIME = "extra_item_time"
        const val EXTRA_DOSAGE = "extra_dosage"
        const val EXTRA_TIMING_INSTR = "extra_timing_instr"
        const val EXTRA_MEAL_NUMBER = "extra_meal_number"

        const val ACTION_MARK_COMPLETED = "com.example.medimeal.ACTION_MARK_COMPLETED"
        const val ACTION_SNOOZE = "com.example.medimeal.ACTION_SNOOZE"
        const val ACTION_SKIP = "com.example.medimeal.ACTION_SKIP"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            // Medicine Channel
            val medChannel = NotificationChannel(
                CHANNEL_MEDICINE_ID,
                "Medicine Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High-priority alarms for prescribed medicines"
                enableLights(true)
                lightColor = Color.parseColor("#75172B")
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                setSound(alarmSound, audioAttributes)
            }

            // Meal Channel
            val mealChannel = NotificationChannel(
                CHANNEL_MEAL_ID,
                "Food & Meal Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for scheduled daily meals"
                enableLights(true)
                lightColor = Color.parseColor("#570D1D")
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 400, 200, 400)
                setSound(alarmSound, audioAttributes)
            }

            // Water Channel
            val waterChannel = NotificationChannel(
                CHANNEL_WATER_ID,
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily water drinking reminders"
            }

            notificationManager.createNotificationChannel(medChannel)
            notificationManager.createNotificationChannel(mealChannel)
            notificationManager.createNotificationChannel(waterChannel)
        }
    }

    fun showMedicineNotification(
        notificationId: Int,
        medicineId: Long,
        medicineName: String,
        scheduledTime: String,
        dosageInfo: String,
        timingInstruction: TimingInstruction,
        associatedMealNumber: Int?
    ) {
        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra(EXTRA_ITEM_TYPE, "MEDICINE")
            putExtra(EXTRA_ITEM_ID, medicineId)
            putExtra(EXTRA_ITEM_NAME, medicineName)
            putExtra(EXTRA_ITEM_TIME, scheduledTime)
            putExtra(EXTRA_DOSAGE, dosageInfo)
            putExtra(EXTRA_TIMING_INSTR, timingInstruction.name)
            if (associatedMealNumber != null) putExtra(EXTRA_MEAL_NUMBER, associatedMealNumber)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Mark as Taken
        val takeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_MARK_COMPLETED
            putExtra(EXTRA_ITEM_TYPE, "MEDICINE")
            putExtra(EXTRA_ITEM_ID, medicineId)
            putExtra(EXTRA_ITEM_NAME, medicineName)
            putExtra(EXTRA_ITEM_TIME, scheduledTime)
            putExtra(EXTRA_DOSAGE, dosageInfo)
            putExtra(EXTRA_TIMING_INSTR, timingInstruction.name)
            if (associatedMealNumber != null) putExtra(EXTRA_MEAL_NUMBER, associatedMealNumber)
        }
        val takePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 1000,
            takeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Snooze
        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_ITEM_TYPE, "MEDICINE")
            putExtra(EXTRA_ITEM_ID, medicineId)
            putExtra(EXTRA_ITEM_NAME, medicineName)
            putExtra(EXTRA_ITEM_TIME, scheduledTime)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 2000,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Skip
        val skipIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_SKIP
            putExtra(EXTRA_ITEM_TYPE, "MEDICINE")
            putExtra(EXTRA_ITEM_ID, medicineId)
            putExtra(EXTRA_ITEM_NAME, medicineName)
            putExtra(EXTRA_ITEM_TIME, scheduledTime)
        }
        val skipPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 3000,
            skipIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val instructionText = "${timingInstruction.labelEn} • $dosageInfo"

        val notification = NotificationCompat.Builder(context, CHANNEL_MEDICINE_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Medicine Reminder: $medicineName")
            .setContentText(instructionText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$instructionText\nScheduled for $scheduledTime. Please take as prescribed.")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .setFullScreenIntent(contentPendingIntent, true)
            .addAction(0, "Mark Taken", takePendingIntent)
            .addAction(0, "Snooze 10m", snoozePendingIntent)
            .addAction(0, "Skip", skipPendingIntent)
            .setColor(Color.parseColor("#75172B"))
            .build()

        notificationManager.notify(notificationId, notification)
    }

    fun showMealNotification(
        notificationId: Int,
        mealNumber: Int,
        mealName: String,
        scheduledTime: String
    ) {
        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra(EXTRA_ITEM_TYPE, "MEAL")
            putExtra(EXTRA_MEAL_NUMBER, mealNumber)
            putExtra(EXTRA_ITEM_NAME, mealName)
            putExtra(EXTRA_ITEM_TIME, scheduledTime)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Mark as Eaten
        val eatIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_MARK_COMPLETED
            putExtra(EXTRA_ITEM_TYPE, "MEAL")
            putExtra(EXTRA_MEAL_NUMBER, mealNumber)
            putExtra(EXTRA_ITEM_NAME, mealName)
            putExtra(EXTRA_ITEM_TIME, scheduledTime)
        }
        val eatPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 4000,
            eatIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Snooze
        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_ITEM_TYPE, "MEAL")
            putExtra(EXTRA_MEAL_NUMBER, mealNumber)
            putExtra(EXTRA_ITEM_NAME, mealName)
            putExtra(EXTRA_ITEM_TIME, scheduledTime)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 5000,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_MEAL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Food Reminder: $mealName")
            .setContentText("Scheduled at $scheduledTime • Keep your 10-meal rhythm!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Checkpoint #$mealNumber • Scheduled at $scheduledTime.\nNourish your body according to your daily plan.")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .setFullScreenIntent(contentPendingIntent, true)
            .addAction(0, "Mark Eaten", eatPendingIntent)
            .addAction(0, "Snooze 10m", snoozePendingIntent)
            .setColor(Color.parseColor("#570D1D"))
            .build()

        notificationManager.notify(notificationId, notification)
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
}
