package com.sean.workouttracker

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.sean.workouttracker.model.*
import java.time.LocalDate

@Composable
fun TodayWorkoutScreen() {
    val startDate = WorkoutRepository.startDate
    val today = LocalDate.now()
    val scheduleMode = WorkoutRepository.getScheduleMode()

    val workoutType = getWorkoutTypeForDate(startDate, today, scheduleMode)
    val phase = getPhase(startDate, today)

    Column {
        ScheduleModeToggle()
        if (workoutType == null) {
            RestDayView(today)
        } else {
            WorkoutDayView(today, workoutType, phase)
        }
    }
}

@Composable
fun RestDayView(date: LocalDate) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = date.toString(), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Rest Day 😴",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Recovery is part of the plan. See you tomorrow!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun WorkoutDayView(today: LocalDate, type: WorkoutType, phase: Int) {
    val template = getTemplate(type)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Header(today, type, phase)
        }

        items(template.exercises) { exercise ->
            ExerciseCard(exercise, today, phase)
        }

        item {
            Button(
                onClick = {
                    WorkoutRepository.markWorkoutCompleted(today)
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Text("Finish Workout")
            }
        }
    }
}

@Composable
fun Header(date: LocalDate, type: WorkoutType, phase: Int) {
    val scheduleMode = WorkoutRepository.getScheduleMode()
    val phaseSuffix = if (scheduleMode == ScheduleMode.SUMMER) "A" else "B"
    val phaseName = if (scheduleMode == ScheduleMode.SUMMER) "Summer Foundation" else "Fall/Winter Cardio Build"

    Column {
        Text(text = date.toString(), style = MaterialTheme.typography.titleMedium)
        Text(
            text = type.name.replace("_", " "),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = "Phase $phase$phaseSuffix: $phaseName",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelLarge
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, today: LocalDate, phase: Int) {
    var refreshTrigger by remember { mutableIntStateOf(0) }
    val currentLog = remember(refreshTrigger) { WorkoutRepository.getLog(exercise.id, today) }
    val lastLog = remember { WorkoutRepository.getLastLog(exercise.id, today) }
    val recSets = getRecommendedSets(exercise.baseRecommendedSets, phase)
    val context = LocalContext.current

    // Weight input logic: use numeric part only
    val initialWeight = (currentLog?.sets?.lastOrNull()?.weight ?: lastLog?.sets?.lastOrNull()?.weight ?: exercise.baseRecommendedWeight ?: "")
        .filter { it.isDigit() || it == '.' }
    
    var weightInput by remember { mutableStateOf(initialWeight) }
    var valueInput by remember { mutableStateOf("") }
    var showInstructions by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (exercise.instructions != null || exercise.videoUrl != null) {
                    IconButton(onClick = { showInstructions = !showInstructions }) {
                        Icon(Icons.Default.Info, contentDescription = "Instructions", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Instructions Expandable Area
            AnimatedVisibility(visible = showInstructions) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        exercise.instructions?.let {
                            Text(text = it, style = MaterialTheme.typography.bodySmall)
                        }
                        exercise.videoUrl?.let { url ->
                            Row(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Watch Tutorial",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline
                                )
                            }
                        }
                    }
                }
            }

            // Equipment Tags
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                exercise.equipment.forEach { eq ->
                    AssistChip(
                        onClick = {},
                        label = { Text(eq.name.replace("_", " ").lowercase()) },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }

            // Zombies Run Tracker if zrx_run
            if (exercise.id == "zrx_run") {
                val currentRun = WorkoutRepository.currentZombiesRun
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🧟 Zombies, Run! Training Run #$currentRun",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Track your 5K training progress. Completing this run will automatically advance to Run #${currentRun + 1}.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { WorkoutRepository.updateZombiesRun(1) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Reset to Run 1", style = MaterialTheme.typography.labelSmall)
                            }
                            OutlinedButton(
                                onClick = { WorkoutRepository.updateZombiesRun(currentRun + 1) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Skip / +1 Run", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            // Recommendations
            val unitStr = when(exercise.unit) {
                ExerciseUnit.REPS -> "reps"
                ExerciseUnit.SECONDS -> "sec"
                ExerciseUnit.MINUTES -> "min"
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Target: $recSets sets of ${exercise.baseRecommendedReps} $unitStr",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Timers
            if (exercise.unit == ExerciseUnit.MINUTES) {
                val maxMinutes = exercise.baseRecommendedReps.split("–", "-").last().trim().toIntOrNull() ?: 10
                CompactWorkoutTimer(initialSeconds = maxMinutes * 60, label = "Cardio")
            } else if (recSets > 1 && (currentLog?.sets?.size ?: 0) < recSets && (currentLog?.sets?.size ?: 0) > 0) {
                CompactWorkoutTimer(initialSeconds = 30, label = "Rest")
            }

            // Last Time
            lastLog?.let {
                val lastSummary = it.sets.joinToString(", ") { s -> "${s.value}${if (s.weight != null) " @ ${s.weight}" else ""}" }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Last: $lastSummary",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Current Sets
            currentLog?.sets?.let { sets ->
                if (sets.isNotEmpty()) {
                    Text(
                        text = "Today's Sets:",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    sets.forEachIndexed { index, set ->
                        Text(
                            text = "Set ${index + 1}: ${set.value} $unitStr ${if (set.weight != null) "@ ${set.weight}" else ""}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input Fields
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = valueInput,
                    onValueChange = { valueInput = it },
                    label = { Text(unitStr.replaceFirstChar { it.uppercase() }) },
                    placeholder = { Text(exercise.baseRecommendedReps) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if (exercise.baseRecommendedWeight != null || weightInput.isNotEmpty()) {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("lb") },
                        modifier = Modifier.weight(0.8f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                
                Button(
                    onClick = {
                        val value = valueInput.toIntOrNull() ?: 0
                        if (value > 0) {
                            val weightToSave = if (weightInput.isNotEmpty()) "$weightInput lb" else null
                            WorkoutRepository.addSet(
                                exercise.id,
                                today,
                                SetLog(value, weightToSave)
                            )
                            valueInput = ""
                            refreshTrigger++
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Log")
                }
            }
        }
    }
}
