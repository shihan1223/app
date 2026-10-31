package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RacetrackCheckpoint
import com.example.data.model.TimingInstruction
import com.example.ui.theme.AccentRed
import com.example.ui.theme.DarkWineRed
import com.example.ui.theme.DeepBurgundy
import com.example.ui.theme.RichCrimson
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer
import java.time.LocalTime

@Composable
fun RacetrackView(
    checkpoints: List<RacetrackCheckpoint>,
    modifier: Modifier = Modifier,
    onCheckpointClick: (RacetrackCheckpoint) -> Unit = {}
) {
    if (checkpoints.isEmpty()) return

    // Pulse animation for the active/next checkpoint
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                )
            )
            .padding(16.dp)
    ) {
        // Track Header with Live Progress
        val completedCount = checkpoints.count { it.isCompleted }
        val totalCount = checkpoints.size
        val progressPercent = if (totalCount > 0) (completedCount.toFloat() / totalCount * 100).toInt() else 0

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(AccentRed, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "10-CHECKPOINT RACETRACK",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }
                Text(
                    text = "Daily Eating & Medicine Rhythm",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                color = if (progressPercent == 100) SuccessGreenContainer else RoseContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "$completedCount/$totalCount ($progressPercent%)",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (progressPercent == 100) SuccessGreen else DeepBurgundy,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Checkpoints vertical track with racing curves
        checkpoints.forEachIndexed { index, checkpoint ->
            val isEven = index % 2 == 0
            val isLast = index == checkpoints.size - 1

            RacetrackCheckpointRow(
                checkpoint = checkpoint,
                isEven = isEven,
                isLast = isLast,
                pulseScale = if (checkpoint.isNextUpcoming) pulseScale else 1f,
                onClick = { onCheckpointClick(checkpoint) }
            )
        }
    }
}

@Composable
fun RacetrackCheckpointRow(
    checkpoint: RacetrackCheckpoint,
    isEven: Boolean,
    isLast: Boolean,
    pulseScale: Float,
    onClick: () -> Unit
) {
    val nodeColor = when {
        checkpoint.isCompleted -> SuccessGreen
        checkpoint.isNextUpcoming -> AccentRed
        checkpoint.isSkipped -> Color.Gray
        checkpoint.isPassed -> DarkWineRed.copy(alpha = 0.7f)
        else -> DeepBurgundy
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Track Node Indicator Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            // Checkpoint Circle / Badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(if (checkpoint.isNextUpcoming) 38.dp else 32.dp)
                    .scale(pulseScale)
                    .background(nodeColor, CircleShape)
                    .shadow(
                        elevation = if (checkpoint.isNextUpcoming) 6.dp else 2.dp,
                        shape = CircleShape
                    )
            ) {
                if (checkpoint.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        text = "${checkpoint.mealNumber}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Connecting Track Conduit Line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(36.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    nodeColor.copy(alpha = 0.8f),
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            )
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Content Card for Checkpoint
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    checkpoint.isNextUpcoming -> SoftRose.copy(alpha = 0.45f)
                    checkpoint.isCompleted -> SuccessGreenContainer.copy(alpha = 0.35f)
                    else -> MaterialTheme.colorScheme.surface
                }
            ),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(
                width = if (checkpoint.isNextUpcoming) 1.5.dp else 1.dp,
                color = when {
                    checkpoint.isNextUpcoming -> AccentRed
                    checkpoint.isCompleted -> SuccessGreen.copy(alpha = 0.5f)
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                }
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = checkpoint.mealName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Scheduled Time Badge
                    Surface(
                        color = DeepBurgundy.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = formatTime12h(checkpoint.scheduledTime),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepBurgundy
                        )
                    }
                }

                // Status or Next Badge
                if (checkpoint.isNextUpcoming) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = AccentRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Next Upcoming Meal",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Attached Medicines Pills
                if (checkpoint.associatedMedicines.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        checkpoint.associatedMedicines.forEach { med ->
                            Surface(
                                color = if (med.isTaken) SuccessGreenContainer else RoseContainer,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 0.8.dp,
                                    color = if (med.isTaken) SuccessGreen.copy(alpha = 0.5f) else RichCrimson.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Medication,
                                        contentDescription = null,
                                        tint = if (med.isTaken) SuccessGreen else DeepBurgundy,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${med.name} (${med.dosage})",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (med.isTaken) SuccessGreen else DeepBurgundy
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${formatMedInstruction(med.timingInstruction, med.offsetMinutes)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatMedInstruction(instruction: TimingInstruction, offsetMinutes: Int): String {
    return when (instruction) {
        TimingInstruction.BEFORE_FOOD -> "${Math.abs(offsetMinutes)}m before"
        TimingInstruction.AFTER_FOOD -> "${Math.abs(offsetMinutes)}m after"
        TimingInstruction.WITH_FOOD -> "With food"
        TimingInstruction.EMPTY_STOMACH -> "Empty stomach"
        TimingInstruction.ANYTIME -> "Anytime"
    }
}

fun formatTime12h(timeStr: String): String {
    return try {
        val parts = timeStr.split(":")
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
        timeStr
    }
}
