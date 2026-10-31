package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CompletionStatus
import com.example.data.model.DailyTimelineItem
import com.example.data.model.RacetrackCheckpoint
import com.example.data.model.TimelineItemType
import com.example.data.model.TimingInstruction
import com.example.ui.components.LanguageSupport
import com.example.ui.components.RacetrackView
import com.example.ui.components.formatTime12h
import com.example.ui.theme.AccentRed
import com.example.ui.theme.DarkWineRed
import com.example.ui.theme.DeepBurgundy
import com.example.ui.theme.RichCrimson
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer
import com.example.ui.theme.WaterBlue
import com.example.ui.theme.WaterBlueContainer
import com.example.ui.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToRacetrack: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToEditFood: () -> Unit,
    onNavigateToAddMed: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val isBangla by viewModel.isBangla.collectAsStateWithLifecycle()
    val checkpoints by viewModel.racetrackCheckpoints.collectAsStateWithLifecycle()
    val timelineItems by viewModel.dailyTimeline.collectAsStateWithLifecycle()
    val todayMealLogs by viewModel.todayMealLogs.collectAsStateWithLifecycle()
    val todayMedLogs by viewModel.todayMedicineLogs.collectAsStateWithLifecycle()
    val waterLog by viewModel.waterLog.collectAsStateWithLifecycle()

    var selectedCheckpointForDialog by remember { mutableStateOf<RacetrackCheckpoint?>(null) }
    var selectedTimelineItemForDialog by remember { mutableStateOf<DailyTimelineItem?>(null) }

    val today = LocalDate.now()
    val dayOfWeek = today.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
    val formattedDate = today.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))

    val hour = LocalTime.now().hour
    val greeting = when (hour) {
        in 5..11 -> if (isBangla) "শুভ সকাল" else "Good Morning"
        in 12..16 -> if (isBangla) "শুভ দুপুর" else "Good Afternoon"
        in 17..21 -> if (isBangla) "শুভ সন্ধ্যা" else "Good Evening"
        else -> if (isBangla) "শুভ রাত্রি" else "Good Night"
    }
    val userName = userProfile?.name ?: "Alex"

    // Counts
    val totalMeals = checkpoints.size
    val completedMeals = checkpoints.count { it.isCompleted }
    val remainingMeals = (totalMeals - completedMeals).coerceAtLeast(0)

    val medItems = timelineItems.filter { it.type == TimelineItemType.MEDICINE }
    val totalMeds = medItems.size.coerceAtLeast(userProfile?.targetMedsPerDay ?: 3)
    val completedMeds = medItems.count { it.status == CompletionStatus.COMPLETED }

    val nextEvent = timelineItems.firstOrNull { it.isNextUpcoming }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddMed,
                containerColor = DeepBurgundy,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Medicine")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Header: Greeting, Date, Profile Avatar & Language toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$dayOfWeek, $formattedDate",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$greeting, $userName",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Language Switcher Badge
                        Surface(
                            onClick = { viewModel.toggleLanguage() },
                            color = SoftRose,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = DeepBurgundy,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isBangla) "বাং" else "EN",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DeepBurgundy,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Profile Avatar
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(DeepBurgundy)
                                .clickable { onNavigateToProfile() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // 2. Next Upcoming Hero Card
            if (nextEvent != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(DarkWineRed, DeepBurgundy)
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = SoftRose,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isBangla) "পরবর্তী ইভেন্ট" else "NEXT UPCOMING EVENT",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SoftRose,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.2.sp
                                        )
                                    }

                                    Surface(
                                        color = Color.White.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = formatTime12h(nextEvent.time),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = nextEvent.title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = nextEvent.subtitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SoftRose.copy(alpha = 0.85f)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Button(
                                        onClick = {
                                            if (nextEvent.type == TimelineItemType.MEAL) {
                                                viewModel.markMealStatus(
                                                    nextEvent.mealNumber ?: 1,
                                                    nextEvent.title,
                                                    nextEvent.time,
                                                    CompletionStatus.COMPLETED
                                                )
                                            } else {
                                                viewModel.markMedicineStatus(
                                                    nextEvent.medicineId ?: 0L,
                                                    nextEvent.title,
                                                    nextEvent.time,
                                                    nextEvent.dosageInfo ?: "",
                                                    nextEvent.timingInstruction ?: TimingInstruction.ANYTIME,
                                                    nextEvent.mealNumber,
                                                    CompletionStatus.COMPLETED
                                                )
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SuccessGreen,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (nextEvent.type == TimelineItemType.MEAL) "Mark Eaten" else "Mark Taken")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            if (nextEvent.type == TimelineItemType.MEAL) {
                                                viewModel.markMealStatus(
                                                    nextEvent.mealNumber ?: 1,
                                                    nextEvent.title,
                                                    nextEvent.time,
                                                    CompletionStatus.SKIPPED
                                                )
                                            } else {
                                                viewModel.markMedicineStatus(
                                                    nextEvent.medicineId ?: 0L,
                                                    nextEvent.title,
                                                    nextEvent.time,
                                                    nextEvent.dosageInfo ?: "",
                                                    nextEvent.timingInstruction ?: TimingInstruction.ANYTIME,
                                                    nextEvent.mealNumber,
                                                    CompletionStatus.SKIPPED
                                                )
                                            }
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Text("Skip")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Daily Summary 2x2 Grid (10 Total Meals, Completed Meals, 3 Med Slots, Completed Meds)
            item {
                Column {
                    Text(
                        text = if (isBangla) "দৈনিক সারসংক্ষেপ" else "Daily Target Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Meals Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Fastfood,
                                        contentDescription = null,
                                        tint = DeepBurgundy,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Meals",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$completedMeals",
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = DeepBurgundy
                                    )
                                    Text(
                                        text = "/$totalMeals",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                                    )
                                }
                                Text(
                                    text = "$remainingMeals remaining today",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Medicines Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Medication,
                                        contentDescription = null,
                                        tint = AccentRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Medicines",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$completedMeds",
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = AccentRed
                                    )
                                    Text(
                                        text = "/$totalMeds",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                                    )
                                }
                                Text(
                                    text = "3 daily prescribed slots",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 4. Hydration Water Tracker Quick Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = WaterBlueContainer.copy(alpha = 0.45f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WaterBlue.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(WaterBlue, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocalDrink, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Hydration Tracker",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = WaterBlue
                                )
                                Text(
                                    text = "${waterLog?.glassesDrank ?: 0} of ${waterLog?.goalGlasses ?: 8} glasses drunk",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.updateWaterIntake(-1) },
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color.White, CircleShape)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease water", tint = WaterBlue)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { viewModel.updateWaterIntake(+1) },
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(WaterBlue, CircleShape)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase water", tint = Color.White)
                            }
                        }
                    }
                }
            }

            // 5. Signature Feature: Racetrack Daily Timeline
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBangla) "রেসট্র্যাক টাইমলাইন" else "Racetrack Timeline",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        TextButton(onClick = onNavigateToRacetrack) {
                            Text("Full View", color = DeepBurgundy, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    RacetrackView(
                        checkpoints = checkpoints,
                        onCheckpointClick = { cp ->
                            selectedCheckpointForDialog = cp
                        }
                    )
                }
            }

            // 6. Quick Action Buttons Row
            item {
                Column {
                    Text(
                        text = if (isBangla) "দ্রুত অ্যাকশন" else "Quick Actions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ActionChip(
                            icon = Icons.Default.Schedule,
                            label = if (isBangla) "খাবার সময়" else "Food Times",
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToEditFood
                        )
                        ActionChip(
                            icon = Icons.Default.Timeline,
                            label = if (isBangla) "পূর্ণ সূচি" else "Full Day",
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToSchedule
                        )
                        ActionChip(
                            icon = Icons.Default.History,
                            label = if (isBangla) "ইতিহাস" else "History",
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToHistory
                        )
                    }
                }
            }

            // 7. Today's Full Schedule List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBangla) "আজকের সময়সূচি" else "Today's Schedule (${timelineItems.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = onNavigateToSchedule) {
                        Text("See All", color = DeepBurgundy)
                    }
                }
            }

            items(timelineItems.take(6)) { item ->
                TimelineItemCard(
                    item = item,
                    onClick = { selectedTimelineItemForDialog = item },
                    onMarkComplete = {
                        if (item.type == TimelineItemType.MEAL) {
                            viewModel.markMealStatus(
                                item.mealNumber ?: 1,
                                item.title,
                                item.time,
                                CompletionStatus.COMPLETED
                            )
                        } else {
                            viewModel.markMedicineStatus(
                                item.medicineId ?: 0L,
                                item.title,
                                item.time,
                                item.dosageInfo ?: "",
                                item.timingInstruction ?: TimingInstruction.ANYTIME,
                                item.mealNumber,
                                CompletionStatus.COMPLETED
                            )
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Dialog for Checkpoint Details
    selectedCheckpointForDialog?.let { cp ->
        AlertDialog(
            onDismissRequest = { selectedCheckpointForDialog = null },
            title = {
                Text(text = cp.mealName, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Scheduled: ${formatTime12h(cp.scheduledTime)}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Status: ${if (cp.isCompleted) "Completed" else if (cp.isSkipped) "Skipped" else "Pending"}")
                    if (cp.associatedMedicines.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Associated Prescriptions:", fontWeight = FontWeight.Bold)
                        cp.associatedMedicines.forEach { med ->
                            Text("• ${med.name} (${med.dosage}) - ${med.timingInstruction.labelEn}")
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
                        selectedCheckpointForDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Text("Mark Completed")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCheckpointForDialog = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Dialog for Timeline Item Details
    selectedTimelineItemForDialog?.let { item ->
        AlertDialog(
            onDismissRequest = { selectedTimelineItemForDialog = null },
            title = {
                Text(text = item.title, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Time: ${formatTime12h(item.time)}")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Details: ${item.subtitle}")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Status: ${item.status.name}")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (item.type == TimelineItemType.MEAL) {
                            viewModel.markMealStatus(
                                item.mealNumber ?: 1,
                                item.title,
                                item.time,
                                CompletionStatus.COMPLETED
                            )
                        } else {
                            viewModel.markMedicineStatus(
                                item.medicineId ?: 0L,
                                item.title,
                                item.time,
                                item.dosageInfo ?: "",
                                item.timingInstruction ?: TimingInstruction.ANYTIME,
                                item.mealNumber,
                                CompletionStatus.COMPLETED
                            )
                        }
                        selectedTimelineItemForDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Text("Mark Completed")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTimelineItemForDialog = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun ActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DeepBurgundy,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun TimelineItemCard(
    item: DailyTimelineItem,
    onClick: () -> Unit,
    onMarkComplete: () -> Unit
) {
    val isMed = item.type == TimelineItemType.MEDICINE
    val isCompleted = item.status == CompletionStatus.COMPLETED

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) SuccessGreenContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCompleted) SuccessGreen.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            if (isCompleted) SuccessGreen else if (isMed) AccentRed else DeepBurgundy,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.Check else if (isMed) Icons.Default.Medication else Icons.Default.Fastfood,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = DeepBurgundy.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = formatTime12h(item.time),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = DeepBurgundy
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!isCompleted) {
                IconButton(
                    onClick = onMarkComplete,
                    modifier = Modifier
                        .size(36.dp)
                        .background(SuccessGreen.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Mark done",
                        tint = SuccessGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
