package com.sean.workouttracker.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class SetLog(
    val value: Int,
    val weight: String? = null
)

@Serializable
data class ExerciseLog(
    val exerciseId: String,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    val sets: List<SetLog>
)

@Serializable
data class WorkoutData(
    val logs: List<ExerciseLog>,
    val completedWorkouts: List<@Serializable(with = LocalDateSerializer::class) LocalDate>,
    val scheduleMode: ScheduleMode = ScheduleMode.NORMAL,
    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate = LocalDate.of(2026, 9, 27),
    val currentZombiesRun: Int = 1
)
