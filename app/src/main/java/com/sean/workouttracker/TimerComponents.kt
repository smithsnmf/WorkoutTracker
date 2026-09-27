package com.sean.workouttracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CompactWorkoutTimer(
    initialSeconds: Int,
    label: String,
    onTimerFinished: () -> Unit = {}
) {
    var timeLeft by remember { mutableIntStateOf(initialSeconds) }
    var isRunning by remember { mutableStateOf(false) }
    var totalTime by remember { mutableIntStateOf(initialSeconds) }

    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft--
            if (timeLeft == 0) {
                isRunning = false
                onTimerFinished()
            }
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = formatTime(timeLeft),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Adjustment buttons
                Text(
                    text = "-10s",
                    modifier = Modifier
                        .clickable { 
                            totalTime = (totalTime - 10).coerceAtLeast(10)
                            timeLeft = totalTime
                        }
                        .padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "+10s",
                    modifier = Modifier
                        .clickable { 
                            totalTime = (totalTime + 10)
                            timeLeft = totalTime
                        }
                        .padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.labelSmall
                )
                
                VerticalDivider(modifier = Modifier.height(16.dp).padding(horizontal = 4.dp))

                IconButton(
                    onClick = { isRunning = !isRunning },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                IconButton(
                    onClick = {
                        timeLeft = totalTime
                        isRunning = false
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}
