package com.sean.workouttracker.model

val strengthA = WorkoutTemplate(
    type = WorkoutType.STRENGTH_A,
    exercises = listOf(
        Exercise(
            id = "goblet_squat",
            name = "Goblet Squat",
            requiredEquipment = listOf(Equipment.KETTLEBELL),
            optionalEquipment = listOf(Equipment.MAT),
            baseRecommendedSets = 2,
            baseRecommendedReps = "8–12",
            baseRecommendedWeight = "45 lb",
            instructions = "Hold kettlebell at chest. Feet shoulder-width apart. Sit down like a chair. Keep chest up. Drive through heels to stand.",
            videoUrl = "https://www.youtube.com/watch?v=6xwGFn-J_Q4"
        ),
        Exercise(
            id = "db_row",
            name = "Dumbbell Row",
            requiredEquipment = listOf(Equipment.DUMBBELL),
            optionalEquipment = listOf(Equipment.BENCH),
            baseRecommendedSets = 2,
            baseRecommendedReps = "8–12",
            baseRecommendedWeight = "35 lb",
            instructions = "One hand supported on knee/chair. Other hand holds dumbbell. Pull weight toward your hip (not chest). Keep back flat. Lower slowly.",
            videoUrl = "https://www.youtube.com/watch?v=pYcpY20QaE8"
        ),
        Exercise(
            id = "pushups",
            name = "Push-ups",
            requiredEquipment = listOf(Equipment.BODYWEIGHT),
            optionalEquipment = listOf(Equipment.MAT, Equipment.RESISTANCE_BAND),
            baseRecommendedSets = 2,
            baseRecommendedReps = "8–12",
            instructions = "Hands under shoulders. Body in straight line. Lower chest to near floor. Push back up. Keep core tight.",
            videoUrl = "https://www.youtube.com/watch?v=IODxDxX7oi4"
        ),
        Exercise(
            id = "plank",
            name = "Plank",
            requiredEquipment = listOf(Equipment.BODYWEIGHT),
            optionalEquipment = listOf(Equipment.MAT),
            baseRecommendedSets = 2,
            baseRecommendedReps = "20–30",
            unit = ExerciseUnit.SECONDS,
            instructions = "Elbows under shoulders. Body straight line. Don’t let hips sag. Hold steady breathing.",
            videoUrl = "https://www.youtube.com/watch?v=pSHjTRCQxIw"
        )
    )
)

val strengthB = WorkoutTemplate(
    type = WorkoutType.STRENGTH_B,
    exercises = listOf(
        Exercise(
            id = "rdl",
            name = "Romanian Deadlift (RDL)",
            requiredEquipment = listOf(Equipment.DUMBBELL),
            optionalEquipment = listOf(Equipment.KETTLEBELL),
            baseRecommendedSets = 2,
            baseRecommendedReps = "8–12",
            baseRecommendedWeight = "10–35 lb",
            instructions = "Hold dumbbells in front of thighs. Slight knee bend (don’t squat). Push hips BACK (not down). Keep weights close to legs. Stop when hamstrings stretch. Stand up by squeezing glutes.",
            videoUrl = "https://www.youtube.com/watch?v=2SHsk9AzdjA"
        ),
        Exercise(
            id = "face_pull",
            name = "Band Face Pulls",
            requiredEquipment = listOf(Equipment.RESISTANCE_BAND),
            optionalEquipment = listOf(Equipment.BENCH, Equipment.DUMBBELL),
            baseRecommendedSets = 2,
            baseRecommendedReps = "10–15",
            baseRecommendedWeight = "10 lb",
            instructions = "Anchor band at face height. Pull toward your face. Elbows high and wide. Squeeze shoulder blades together. Control the return.",
            videoUrl = "https://www.youtube.com/watch?v=rep-qVOkqgk"
        ),
        Exercise(
            id = "floor_press",
            name = "Dumbbell Floor Press",
            requiredEquipment = listOf(Equipment.DUMBBELL),
            optionalEquipment = listOf(Equipment.MAT),
            baseRecommendedSets = 2,
            baseRecommendedReps = "8–12",
            baseRecommendedWeight = "10–35 lb",
            instructions = "Lie on floor. Dumbbells above chest. Lower until elbows touch floor. Press back up. Keep elbows controlled (don’t flare).",
            videoUrl = "https://www.youtube.com/watch?v=VmB1G1K7v94"
        ),
        Exercise(
            id = "core",
            name = "Core (Dead Bug)",
            requiredEquipment = listOf(Equipment.BODYWEIGHT),
            optionalEquipment = listOf(Equipment.MAT),
            baseRecommendedSets = 2,
            baseRecommendedReps = "10",
            instructions = "Lie on back. Opposite arm + leg extend slowly. Keep lower back flat on floor. Move slow and controlled.",
            videoUrl = "https://www.youtube.com/watch?v=8Q5z0v0qG5c"
        )
    )
)

val cardio = WorkoutTemplate(
    type = WorkoutType.CARDIO,
    exercises = listOf(
        Exercise(
            id = "zrx_run",
            name = "Run Zombies (ZRX)",
            requiredEquipment = listOf(Equipment.PHONE, Equipment.SHOES),
            optionalEquipment = listOf(Equipment.EARPHONES),
            baseRecommendedSets = 1,
            baseRecommendedReps = "20–30",
            unit = ExerciseUnit.MINUTES,
            instructions = "Open ZRX app and start a mission. Run or walk as directed."
        )
    )
)

val yoga = WorkoutTemplate(
    type = WorkoutType.YOGA,
    exercises = listOf(
        Exercise(
            id = "yoga_choice",
            name = "Yoga or Cardio Choice",
            requiredEquipment = listOf(Equipment.MAT),
            optionalEquipment = listOf(Equipment.JUMP_ROPE, Equipment.PHONE),
            baseRecommendedSets = 1,
            baseRecommendedReps = "15–30",
            unit = ExerciseUnit.MINUTES,
            instructions = "Choose between a yoga flow, light cardio, or active recovery. Optional rest day."
        )
    )
)

val recovery = WorkoutTemplate(
    type = WorkoutType.RECOVERY,
    exercises = listOf(
        Exercise(
            id = "family_walk",
            name = "Family Walk / Recovery",
            requiredEquipment = listOf(Equipment.SHOES),
            optionalEquipment = listOf(Equipment.PHONE),
            baseRecommendedSets = 1,
            baseRecommendedReps = "20–30",
            unit = ExerciseUnit.MINUTES,
            instructions = "Enjoy an evening family walk or light mobility work. No structured cardio scheduled due to summer heat."
        )
    )
)
