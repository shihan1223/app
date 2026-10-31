package com.example.ui.screens.racetrack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CompletionStatus
import com.example.data.model.RacetrackCheckpoint
import com.example.ui.components.RacetrackView
import com.example.ui.components.formatTime12h
import com.example.ui.theme.AccentRed
import com.example.ui.theme.DarkWineRed
import com.example.ui.theme.DeepBurgundy
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RacetrackDetailScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val checkpoints by viewModel.racetrackCheckpoints.collectAsStateWithLifecycle()
    var selectedCheckpoint by remember { mutableStateOf<RacetrackCheckpoint?>(null) }

    val completed = checkpoints.count { it.isCompleted }
    val total = checkpoints.size
    val percent = if (total > 0) (completed.toFloat() / total * 100).toInt() else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Racetrack Daily Journey",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Status Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepBurgundy)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TODAY'S LAP PROGRESS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SoftRose,
                                    letterSpacing = 1.2.sp
                                )
                                Text(
                                    text = "$percent% Complete",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$completed/$total",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = (completed.toFloat() / total.coerceAtLeast(1)))
                                    .height(8.dp)
                                    .background(SuccessGreen, RoundedCornerShape(4.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "10 meals spaced at 2-hour intervals starting 05:00 AM. Tap any checkpoint below to complete or view linked medicines.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftRose.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Detailed Racetrack View
            item {
                RacetrackView(
                    checkpoints = checkpoints,
                    onCheckpointClick = { cp -> selectedCheckpoint = cp }
                )
            }
        }
    }

    selectedCheckpoint?.let { cp ->
        AlertDialog(
            onDismissRequest = { selectedCheckpoint = null },
            title = {
                Text("Checkpoint #${cp.mealNumber}: ${cp.mealName}", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Scheduled Time: ${formatTime12h(cp.scheduledTime)}")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Status: ${if (cp.isCompleted) "Completed" else if (cp.isSkipped) "Skipped" else "Pending"}")

                    if (cp.associatedMedicines.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Attached Prescribed Medicines:", fontWeight = FontWeight.Bold, color = DeepBurgundy)
                        Spacer(modifier = Modifier.height(6.dp))
                        cp.associatedMedicines.forEach { med ->
                            Surface(
                                color = if (med.isTaken) SuccessGreenContainer else RoseContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "${med.name} (${med.dosage})",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = "${med.timingInstruction.labelEn} • Scheduled at ${formatTime12h(med.scheduledTime)}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.markMealStatus(
                            cp.mealNumber,
                            cp.mealName,
                            cp.scheduledTime,
                            CompletionStatus.COMPLETED
                        )
                        selectedCheckpoint = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Text("Mark Completed")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCheckpoint = null }) {
                    Text("Close")
                }
            }
        )
    }
}
