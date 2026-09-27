package com.sean.workouttracker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sean.workouttracker.model.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun CalendarScreen(startDate: LocalDate, onDayClick: (LocalDate) -> Unit) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val daysInMonth = remember(currentMonth) { getDaysInMonth(currentMonth) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ScheduleModeToggle()

        Spacer(modifier = Modifier.height(8.dp))

        CalendarHeader(
            currentMonth = currentMonth,
            onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
            onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        DayOfWeekHeader()

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(daysInMonth) { date ->
                if (date != null) {
                    CalendarDayItem(date, startDate, onDayClick)
                } else {
                    Box(modifier = Modifier.aspectRatio(0.8f))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Legend()
    }
}

@Composable
fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Month")
        }
        Text(
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Month")
        }
    }
}

@Composable
fun DayOfWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        days.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun CalendarDayItem(date: LocalDate, startDate: LocalDate, onDayClick: (LocalDate) -> Unit) {
    val scheduleMode = WorkoutRepository.getScheduleMode()
    val workoutType = getWorkoutTypeForDate(startDate, date, scheduleMode)
    val phase = getPhase(startDate, date)
    val isToday = date == LocalDate.now()
    val isCompleted = WorkoutRepository.isWorkoutCompleted(date)

    val backgroundColor = when (workoutType) {
        WorkoutType.STRENGTH_A, WorkoutType.STRENGTH_A_CARDIO -> colorResource(R.color.strength_a_tag).copy(alpha = 0.8f)
        WorkoutType.STRENGTH_B, WorkoutType.STRENGTH_B_CARDIO -> colorResource(R.color.strength_b_tag).copy(alpha = 0.8f)
        WorkoutType.CARDIO -> colorResource(R.color.cardio_tag).copy(alpha = 0.8f)
        WorkoutType.YOGA, WorkoutType.RECOVERY -> colorResource(R.color.yoga_tag).copy(alpha = 0.8f)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .aspectRatio(0.8f)
            .padding(2.dp)
            .background(
                color = if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .clickable { onDayClick(date) },
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Normal,
                color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            
            if (workoutType != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    color = backgroundColor,
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = when(workoutType) {
                                    WorkoutType.STRENGTH_A -> "A"
                                    WorkoutType.STRENGTH_B -> "B"
                                    WorkoutType.CARDIO -> "C"
                                    WorkoutType.YOGA -> "Y"
                                    WorkoutType.STRENGTH_A_CARDIO -> "A+C"
                                    WorkoutType.STRENGTH_B_CARDIO -> "B+C"
                                    WorkoutType.RECOVERY -> "R"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "P$phase",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        
                        if (isCompleted) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                modifier = Modifier.size(16.dp).align(Alignment.TopEnd).padding(1.dp),
                                tint = colorResource(R.color.completed_green)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Legend() {
    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Legend:", style = MaterialTheme.typography.titleSmall)
        LegendItem("Strength A + Cardio", colorResource(R.color.strength_a_tag))
        LegendItem("Strength B + Cardio", colorResource(R.color.strength_b_tag))
        LegendItem("Yoga / Active Rest", colorResource(R.color.yoga_tag))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(12.dp), tint = colorResource(R.color.completed_green))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Completed", style = MaterialTheme.typography.labelMedium)
        }
        Text("Px = Phase Number", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, MaterialTheme.shapes.extraSmall))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}

private fun getDaysInMonth(month: YearMonth): List<LocalDate?> {
    val firstDayOfMonth = month.atDay(1)
    val dayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 
    val daysInMonth = month.lengthOfMonth()

    val days = mutableListOf<LocalDate?>()
    for (i in 0 until dayOfWeek) {
        days.add(null)
    }
    for (i in 1..daysInMonth) {
        days.add(month.atDay(i))
    }
    return days
}
