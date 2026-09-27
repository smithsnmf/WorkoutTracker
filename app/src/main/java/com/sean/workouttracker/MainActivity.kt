package com.sean.workouttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.sean.workouttracker.model.WorkoutRepository
import com.sean.workouttracker.ui.theme.WorkoutTrackerTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkoutTrackerTheme {
                WorkoutApp()
            }
        }
    }
}

@Composable
fun WorkoutApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val startDate = WorkoutRepository.startDate

    if (selectedDate != null) {
        WorkoutDetailScreen(
            date = selectedDate!!,
            startDate = startDate,
            onBack = { selectedDate = null }
        )
        BackHandler { selectedDate = null }
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        label = { Text("Today") },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        label = { Text("Calendar") },
                        icon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                    )
                }
            }
        ) { innerPadding ->
            Surface(modifier = Modifier.padding(innerPadding)) {
                when (selectedTab) {
                    0 -> TodayWorkoutScreen()
                    1 -> CalendarScreen(startDate) { date ->
                        selectedDate = date
                    }
                }
            }
        }
    }
}
