package com.sean.workouttracker.model

enum class ExerciseUnit {
    REPS, SECONDS, MINUTES
}

data class Exercise(
    val id: String,
    val name: String,
    val requiredEquipment: List<Equipment>,
    val optionalEquipment: List<Equipment> = emptyList(),
    val baseRecommendedSets: Int,
    val baseRecommendedReps: String,
    val baseRecommendedWeight: String? = null,
    val unit: ExerciseUnit = ExerciseUnit.REPS,
    val instructions: String? = null,
    val videoUrl: String? = null
) {
    val equipment: List<Equipment>
        get() = requiredEquipment + optionalEquipment
}
