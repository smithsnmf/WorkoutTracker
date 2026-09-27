package com.sean.workouttracker.model

fun getTemplate(type: WorkoutType): WorkoutTemplate {
    return when (type) {
        WorkoutType.STRENGTH_A -> strengthA
        WorkoutType.STRENGTH_B -> strengthB
        WorkoutType.CARDIO -> cardio
        WorkoutType.YOGA -> yoga
        WorkoutType.STRENGTH_A_CARDIO -> strengthA.copy(
            exercises = strengthA.exercises + cardio.exercises
        )
        WorkoutType.STRENGTH_B_CARDIO -> strengthB.copy(
            exercises = strengthB.exercises + cardio.exercises
        )
        WorkoutType.RECOVERY -> recovery
    }
}
