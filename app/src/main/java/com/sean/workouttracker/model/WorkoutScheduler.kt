package com.sean.workouttracker.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private fun isWeekend(date: LocalDate): Boolean {
    return date.dayOfWeek == DayOfWeek.SATURDAY ||
            date.dayOfWeek == DayOfWeek.SUNDAY
}



fun getRecommendedSets(baseSets: Int, phase: Int): Int {
    return if (phase >= 2 && baseSets == 2) 3 else baseSets
}

private val NEW_SETUP_START_DATE = LocalDate.of(2026, 9, 28)

fun getPhase(startDate: LocalDate, today: LocalDate): Int {
    val weeksBetween = ChronoUnit.WEEKS.between(NEW_SETUP_START_DATE, today)
    return when {
        weeksBetween < 4 -> 1
        weeksBetween < 8 -> 2
        else -> 3
    }
}

fun getWorkoutTypeForDate(
    startDate: LocalDate,
    today: LocalDate,
    scheduleMode: ScheduleMode = ScheduleMode.NORMAL
): WorkoutType? {
    if (isWeekend(today)) return null
    
    // Use new setup from Sep 28, 2026 onwards
    if (!today.isBefore(NEW_SETUP_START_DATE)) {
        val todayMonday = today.minusDays((today.dayOfWeek.value - 1).toLong())
        val weeksBetween = ChronoUnit.WEEKS.between(NEW_SETUP_START_DATE, todayMonday)
        val isBWeek = weeksBetween % 2 != 0L
        val dayInWeek = today.dayOfWeek.value

        return if (scheduleMode == ScheduleMode.SUMMER) {
            when (dayInWeek) {
                1, 3, 5 -> { // Mon, Wed, Fri: Strength rotation
                    val isA = if (isBWeek) {
                        dayInWeek == 3 // B Week: B, A, B
                    } else {
                        dayInWeek == 1 || dayInWeek == 5 // A Week: A, B, A
                    }
                    if (isA) WorkoutType.STRENGTH_A else WorkoutType.STRENGTH_B
                }
                2, 4 -> WorkoutType.RECOVERY
                else -> null
            }
        } else {
            when (dayInWeek) {
                1, 3, 5 -> { // Mon, Wed, Fri: Strength + Cardio
                    val isA = if (isBWeek) {
                        dayInWeek == 3 // B Week: B, A, B
                    } else {
                        dayInWeek == 1 || dayInWeek == 5 // A Week: A, B, A
                    }
                    if (isA) WorkoutType.STRENGTH_A_CARDIO else WorkoutType.STRENGTH_B_CARDIO
                }
                2, 4 -> WorkoutType.YOGA
                else -> null
            }
        }
    }

    // OLD SETUP (Before May 25, 2026)
    if (today.isBefore(startDate)) return null

    var effectiveStart = startDate
    while (isWeekend(effectiveStart)) {
        effectiveStart = effectiveStart.plusDays(1)
    }

    if (today.isBefore(effectiveStart)) return null

    val startMonday = effectiveStart.minusDays((effectiveStart.dayOfWeek.value - 1).toLong())
    val todayMonday = today.minusDays((today.dayOfWeek.value - 1).toLong())
    val weeksBetween = ChronoUnit.WEEKS.between(startMonday, todayMonday)

    if (weeksBetween == 0L) {
        val dayIndex = today.dayOfWeek.value - effectiveStart.dayOfWeek.value
        return when (dayIndex) {
            0 -> WorkoutType.STRENGTH_A
            1 -> WorkoutType.CARDIO
            2 -> WorkoutType.STRENGTH_B
            3 -> WorkoutType.CARDIO
            4 -> WorkoutType.STRENGTH_A
            else -> null
        }
    }

    val isBWeek = weeksBetween % 2 != 0L
    val dayInWeek = today.dayOfWeek.value

    return if (isBWeek) {
        when (dayInWeek) {
            1 -> WorkoutType.STRENGTH_B
            2 -> WorkoutType.CARDIO
            3 -> WorkoutType.STRENGTH_A
            4 -> WorkoutType.CARDIO
            5 -> WorkoutType.STRENGTH_B
            else -> null
        }
    } else {
        when (dayInWeek) {
            1 -> WorkoutType.STRENGTH_A
            2 -> WorkoutType.CARDIO
            3 -> WorkoutType.STRENGTH_B
            4 -> WorkoutType.CARDIO
            5 -> WorkoutType.STRENGTH_A
            else -> null
        }
    }
}
