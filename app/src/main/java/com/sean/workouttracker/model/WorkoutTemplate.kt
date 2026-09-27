package com.sean.workouttracker.model

data class WorkoutTemplate(
    val type: WorkoutType,
    val exercises: List<Exercise>
)