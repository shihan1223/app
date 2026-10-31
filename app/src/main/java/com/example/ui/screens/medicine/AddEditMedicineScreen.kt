package com.example.ui.screens.medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Medicine
import com.example.data.model.TimingInstruction
import com.example.ui.components.formatTime12h
import com.example.ui.theme.AccentRed
import com.example.ui.theme.DarkWineRed
import com.example.ui.theme.DeepBurgundy
import com.example.ui.theme.RichCrimson
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMedicineScreen(
    medicineId: Long,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val meals by viewModel.foodSchedules.collectAsStateWithLifecycle()
    val activeMeds by viewModel.activeMedicines.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Tablet") }
    var dosageAmountStr by remember { mutableStateOf("1.0") }
    var dosageUnit by remember { mutableStateOf("tablet") }
    var timingInstruction by remember { mutableStateOf(TimingInstruction.BEFORE_FOOD) }
    var selectedMealNumber by remember { mutableStateOf<Int?>(2) } // default Breakfast
    var offsetMinutes by remember { mutableIntStateOf(30) }
    var timesPerDay by remember { mutableIntStateOf(3) }
    var reminderTimesString by remember { mutableStateOf("08:00,13:00,20:00") }
    var instructions by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedColorHex by remember { mutableStateOf("#570D1D") }

    val isEditing = medicineId > 0

    LaunchedEffect(medicineId, activeMeds) {
        if (medicineId > 0) {
            val existing = activeMeds.firstOrNull { it.id == medicineId }
            if (existing != null) {
                name = existing.name
                category = existing.category
                dosageAmountStr = existing.dosageAmount.toString()
                dosageUnit = existing.dosageUnit
                timingInstruction = existing.timingInstruction
                selectedMealNumber = existing.associatedMealNumber
                offsetMinutes = Math.abs(existing.mealOffsetMinutes)
                timesPerDay = existing.timesPerDay
                reminderTimesString = existing.reminderTimesString
                instructions = existing.instructions ?: ""
                notes = existing.notes ?: ""
                selectedColorHex = existing.colorHex
            }
        }
    }

    val categories = listOf("Tablet", "Capsule", "Syrup", "Drops", "Inhaler", "Injection")
    val units = listOf("tablet", "capsule", "mg", "ml", "drops", "puff")
    val timingOptions = TimingInstruction.values().toList()
    val offsets = listOf(15, 30, 45, 60)
    val colorSwatches = listOf("#570D1D", "#75172B", "#A51F36", "#D63850", "#28845C", "#1976D2")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Medicine" else "Add New Medicine",
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
            // General Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Prescription Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Medicine Name (e.g., Metformin, Amoxicillin)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Category Chips
                        Text(
                            text = "Form / Category",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(categories) { cat ->
                                Surface(
                                    onClick = { category = cat },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (category == cat) DeepBurgundy else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = cat,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (category == cat) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Dosage Amount & Unit Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = dosageAmountStr,
                                onValueChange = { dosageAmountStr = it },
                                label = { Text("Dose Amount") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = dosageUnit,
                                onValueChange = { dosageUnit = it },
                                label = { Text("Unit (mg, tablet...)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            // Smart Food Coordination Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Medication, contentDescription = null, tint = AccentRed)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Timing & Food Coordination",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Timing Instruction Selection
                        timingOptions.forEach { opt ->
                            val isSelected = timingInstruction == opt
                            Surface(
                                onClick = { timingInstruction = opt },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) SoftRose else Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) DeepBurgundy else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) DeepBurgundy else Color.Transparent)
                                            .border(1.5.dp, DeepBurgundy, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(Color.White, CircleShape)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = opt.labelEn,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) DeepBurgundy else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // If linked to food (BEFORE, AFTER, WITH, or EMPTY STOMACH)
                        if (timingInstruction != TimingInstruction.ANYTIME) {
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Associate With Meal Checkpoint:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(meals) { meal ->
                                    val isMealSel = selectedMealNumber == meal.mealNumber
                                    Surface(
                                        onClick = { selectedMealNumber = meal.mealNumber },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isMealSel) DeepBurgundy else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Meal ${meal.mealNumber}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isMealSel) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = formatTime12h(meal.scheduledTime),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isMealSel) SoftRose else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            // Offset Minutes (for Before or After)
                            if (timingInstruction == TimingInstruction.BEFORE_FOOD || timingInstruction == TimingInstruction.AFTER_FOOD) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Offset: $offsetMinutes minutes ${if (timingInstruction == TimingInstruction.BEFORE_FOOD) "before" else "after"} meal",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    offsets.forEach { mins ->
                                        Surface(
                                            onClick = { offsetMinutes = mins },
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (offsetMinutes == mins) DeepBurgundy else MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "${mins}m",
                                                modifier = Modifier.padding(vertical = 8.dp),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (offsetMinutes == mins) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // Independent Times
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedTextField(
                                value = reminderTimesString,
                                onValueChange = { reminderTimesString = it },
                                label = { Text("Independent Times (comma separated e.g. 08:00,14:00,20:00)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Doctor Notes & Color Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Prescription Instructions & Notes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = instructions,
                            onValueChange = { instructions = it },
                            label = { Text("Doctor's Advice (e.g., Take with a full glass of water)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Personal Notes / Pharmacy info") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Pill Badge Accent Color",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            colorSwatches.forEach { hex ->
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(Color(android.graphics.Color.parseColor(hex)), CircleShape)
                                        .clickable { selectedColorHex = hex }
                                        .border(
                                            width = if (selectedColorHex == hex) 3.dp else 1.dp,
                                            color = if (selectedColorHex == hex) Color.White else Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selectedColorHex == hex) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Save CTA Button
            item {
                Button(
                    onClick = {
                        if (name.isBlank()) return@Button
                        val dose = dosageAmountStr.toDoubleOrNull() ?: 1.0
                        val finalOffset = if (timingInstruction == TimingInstruction.BEFORE_FOOD) {
                            -Math.abs(offsetMinutes)
                        } else if (timingInstruction == TimingInstruction.AFTER_FOOD) {
                            Math.abs(offsetMinutes)
                        } else {
                            0
                        }

                        val med = Medicine(
                            id = if (isEditing) medicineId else 0L,
                            name = name.trim(),
                            category = category,
                            dosageAmount = dose,
                            dosageUnit = dosageUnit.trim(),
                            timingInstruction = timingInstruction,
                            associatedMealNumber = if (timingInstruction == TimingInstruction.ANYTIME) null else selectedMealNumber,
                            mealOffsetMinutes = finalOffset,
                            timesPerDay = timesPerDay,
                            reminderTimesString = reminderTimesString,
                            instructions = instructions.ifBlank { null },
                            notes = notes.ifBlank { null },
                            colorHex = selectedColorHex,
                            isActive = true
                        )

                        if (isEditing) {
                            viewModel.updateMedicine(med)
                        } else {
                            viewModel.addMedicine(med)
                        }
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBurgundy)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEditing) "Update Medicine" else "Save Medicine",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
