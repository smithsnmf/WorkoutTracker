package com.sean.workouttracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sean.workouttracker.model.*
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    date: LocalDate,
    startDate: LocalDate,
    onBack: () -> Unit
) {
    val scheduleMode = WorkoutRepository.getScheduleMode()
    val workoutType = getWorkoutTypeForDate(startDate, date, scheduleMode)
    val phase = getPhase(startDate, date)
    val isFuture = date.isAfter(LocalDate.now())
    val isCompleted = WorkoutRepository.isWorkoutCompleted(date)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = date.toString()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isCompleted) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (workoutType == null) {
                RestDayView(date)
            } else {
                WorkoutDetailContent(date, workoutType, phase, isFuture, isCompleted)
            }
        }
    }
}

@Composable
fun WorkoutDetailContent(
    date: LocalDate,
    type: WorkoutType,
    phase: Int,
    isFuture: Boolean,
    isCompleted: Boolean
) {
    val template = getTemplate(type)
    var refreshTrigger by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Header(date, type, phase)
        }

        items(template.exercises) { exercise ->
            if (isFuture) {
                ReadOnlyExerciseCard(exercise, phase)
            } else {
                key(refreshTrigger) {
                    ExerciseCard(exercise, date, phase)
                }
            }
        }

        if (!isFuture && !isCompleted) {
            item {
                Button(
                    onClick = {
                        WorkoutRepository.markWorkoutCompleted(date)
                        refreshTrigger++
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    Text("Mark Workout as Complete")
                }
            }
        }
    }
}

@Composable
fun ReadOnlyExerciseCard(exercise: Exercise, phase: Int) {
    val recSets = getRecommendedSets(exercise.baseRecommendedSets, phase)
    val unitStr = when(exercise.unit) {
        ExerciseUnit.REPS -> "reps"
        ExerciseUnit.SECONDS -> "sec"
        ExerciseUnit.MINUTES -> "min"
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = exercise.name, style = MaterialTheme.typography.titleLarge)
            Text(
                text = "Target: $recSets sets of ${exercise.baseRecommendedReps} $unitStr",
                style = MaterialTheme.typography.bodyMedium
            )
            if (exercise.baseRecommendedWeight != null) {
                Text(text = "Suggested Weight: ${exercise.baseRecommendedWeight}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
