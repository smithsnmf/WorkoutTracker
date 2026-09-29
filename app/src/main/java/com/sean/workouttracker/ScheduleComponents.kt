package com.sean.workouttracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sean.workouttracker.model.ScheduleMode
import com.sean.workouttracker.model.WorkoutRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun ScheduleModeToggle() {
    val currentMode = WorkoutRepository.getScheduleMode()
    val startDate = WorkoutRepository.startDate
    val zombiesRun = WorkoutRepository.currentZombiesRun
    var showSettingsDialog by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Start: $startDate | ZRX Run #$zombiesRun",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                OutlinedButton(
                    onClick = { showSettingsDialog = true },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Settings", style = MaterialTheme.typography.labelMedium)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Schedule: ", style = MaterialTheme.typography.labelMedium)
                FilterChip(
                    selected = currentMode == ScheduleMode.SUMMER,
                    onClick = { WorkoutRepository.setScheduleMode(ScheduleMode.SUMMER) },
                    label = { Text("Summer") },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                FilterChip(
                    selected = currentMode == ScheduleMode.NORMAL,
                    onClick = { WorkoutRepository.setScheduleMode(ScheduleMode.NORMAL) },
                    label = { Text("Fall/Winter") },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }

    if (showSettingsDialog) {
        ProgramSettingsDialog(onDismiss = { showSettingsDialog = false })
    }
}

@Composable
fun ProgramSettingsDialog(onDismiss: () -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    val startDate = WorkoutRepository.startDate
    var zrxRunInput by remember { mutableStateOf(WorkoutRepository.currentZombiesRun.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Program Settings") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Start Date Section
                Column {
                    Text("Program Start Date", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(startDate.toString(), style = MaterialTheme.typography.bodyMedium)
                        OutlinedButton(onClick = { showDatePicker = true }) {
                            Text("Change Start Date")
                        }
                    }
                }

                HorizontalDivider()

                // Zombies Run Section
                Column {
                    Text("Zombies, Run! Training Run Tracker", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = zrxRunInput,
                            onValueChange = { zrxRunInput = it },
                            label = { Text("Current Run #") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Button(
                            onClick = {
                                val runNum: Int? = zrxRunInput.toIntOrNull()
                                if (runNum != null) {
                                    WorkoutRepository.updateZombiesRun(runNum)
                                }
                            }
                        ) {
                            Text("Set")
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                WorkoutRepository.updateZombiesRun(1)
                                zrxRunInput = "1"
                            }
                        ) {
                            Text("Reset to Run 1")
                        }
                        OutlinedButton(
                            onClick = {
                                val current = WorkoutRepository.currentZombiesRun
                                WorkoutRepository.updateZombiesRun(current + 1)
                                zrxRunInput = WorkoutRepository.currentZombiesRun.toString()
                            }
                        ) {
                            Text("+1 Run")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )

    if (showDatePicker) {
        StartDateDialog(
            initialDate = startDate,
            onDismiss = { showDatePicker = false },
            onDateSelected = { newDate ->
                WorkoutRepository.updateStartDate(newDate)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartDateDialog(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val millis = initialDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = millis)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val millisVal = datePickerState.selectedDateMillis
                if (millisVal != null) {
                    val selectedLocalDate = Instant.ofEpochMilli(millisVal)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate()
                    onDateSelected(selectedLocalDate)
                }
                onDismiss()
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
