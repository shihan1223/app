package com.example.alarm

import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppDatabase
import com.example.data.model.CompletionStatus
import com.example.data.model.TimingInstruction
import com.example.data.repository.MediMealRepository
import com.example.ui.theme.AccentRed
import com.example.ui.theme.DarkWineRed
import com.example.ui.theme.DeepBurgundy
import com.example.ui.theme.MediMealTheme
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.launch
import java.time.LocalDate

class AlarmActivity : ComponentActivity() {

    private var ringtone: Ringtone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        // Start playing ringtone
        try {
            val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ringtone = RingtoneManager.getRingtone(applicationContext, alertUri)
            ringtone?.play()
        } catch (e: Exception) {
            // Ignore ringtone errors
        }

        val itemType = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_TYPE) ?: "MEAL"
        val itemId = intent.getLongExtra(NotificationHelper.EXTRA_ITEM_ID, 0L)
        val mealNumber = intent.getIntExtra(NotificationHelper.EXTRA_MEAL_NUMBER, 1)
        val itemName = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_NAME) ?: "MediMeal Reminder"
        val itemTime = intent.getStringExtra(NotificationHelper.EXTRA_ITEM_TIME) ?: "08:00"
        val dosage = intent.getStringExtra(NotificationHelper.EXTRA_DOSAGE) ?: ""
        val timingInstrStr = intent.getStringExtra(NotificationHelper.EXTRA_TIMING_INSTR) ?: TimingInstruction.BEFORE_FOOD.name
        val timingInstr = try { TimingInstruction.valueOf(timingInstrStr) } catch (e: Exception) { TimingInstruction.BEFORE_FOOD }

        val repository = MediMealRepository(AppDatabase.getDatabase(this))

        setContent {
            MediMealTheme(darkTheme = true) {
                AlarmRingingContent(
                    itemType = itemType,
                    itemId = itemId,
                    mealNumber = mealNumber,
                    itemName = itemName,
                    itemTime = itemTime,
                    dosage = dosage,
                    timingInstruction = timingInstr,
                    onMarkCompleted = {
                        stopRingtone()
                        val today = LocalDate.now().toString()
                        if (itemType == "MEDICINE") {
                            repository.logMedicineAction(
                                date = today,
                                medicineId = itemId,
                                medicineName = itemName,
                                scheduledTime = itemTime,
                                dosageInfo = dosage,
                                timingInstruction = timingInstr,
                                associatedMealNumber = mealNumber,
                                status = CompletionStatus.COMPLETED
                            )
                        } else {
                            repository.logMealAction(
                                date = today,
                                mealNumber = mealNumber,
                                mealName = itemName,
                                scheduledTime = itemTime,
                                status = CompletionStatus.COMPLETED
                            )
                        }
                        finish()
                    },
                    onSnooze = {
                        stopRingtone()
                        AlarmScheduler.snoozeAlarm(
                            context = this@AlarmActivity,
                            itemType = itemType,
                            idOrNumber = if (itemType == "MEAL") mealNumber.toLong() else itemId,
                            name = itemName,
                            originalTime = itemTime,
                            snoozeMinutes = 10
                        )
                        finish()
                    },
                    onSkip = {
                        stopRingtone()
                        val today = LocalDate.now().toString()
                        if (itemType == "MEDICINE") {
                            repository.logMedicineAction(
                                date = today,
                                medicineId = itemId,
                                medicineName = itemName,
                                scheduledTime = itemTime,
                                dosageInfo = dosage,
                                timingInstruction = timingInstr,
                                associatedMealNumber = mealNumber,
                                status = CompletionStatus.SKIPPED
                            )
                        } else {
                            repository.logMealAction(
                                date = today,
                                mealNumber = mealNumber,
                                mealName = itemName,
                                scheduledTime = itemTime,
                                status = CompletionStatus.SKIPPED
                            )
                        }
                        finish()
                    }
                )
            }
        }
    }

    private fun stopRingtone() {
        try {
            if (ringtone?.isPlaying == true) {
                ringtone?.stop()
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRingtone()
    }
}

@Composable
fun AlarmRingingContent(
    itemType: String,
    itemId: Long,
    mealNumber: Int,
    itemName: String,
    itemTime: String,
    dosage: String,
    timingInstruction: TimingInstruction,
    onMarkCompleted: suspend () -> Unit,
    onSnooze: () -> Unit,
    onSkip: suspend () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val isMedicine = itemType == "MEDICINE"

    // Pulsing circle animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF28060F),
                        DeepBurgundy,
                        DarkWineRed,
                        Color(0xFF151113)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Badge
            Surface(
                color = AccentRed.copy(alpha = 0.25f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentRed)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = null,
                        tint = SoftRose,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isMedicine) "MEDICATION ALERT" else "FOOD TIMELINE ALERT",
                        color = SoftRose,
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            // Center Info Card with glowing pulsing icon
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(pulseScale)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(AccentRed, DarkWineRed)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isMedicine) Icons.Default.Medication else Icons.Default.Fastfood,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = itemTime,
                    color = SoftRose,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = itemName,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isMedicine) {
                    Surface(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Dosage: $dosage",
                                color = SoftRose,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = timingInstruction.labelEn,
                                color = Color.White.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Checkpoint #$mealNumber • 10-Meal Daily Rhythm",
                        color = SoftRose,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // Bottom Actions (Mark Taken/Eaten, Snooze, Skip)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Primary Action: Mark Completed
                Button(
                    onClick = {
                        coroutineScope.launch {
                            onMarkCompleted()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SuccessGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isMedicine) "MARK AS TAKEN" else "MARK AS EATEN",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Snooze Button
                    Button(
                        onClick = onSnooze,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.15f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Icon(Icons.Default.Snooze, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Snooze 10m",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Skip Button
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                onSkip()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White.copy(alpha = 0.8f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Skip",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
