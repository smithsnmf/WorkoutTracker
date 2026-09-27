package com.sean.workouttracker.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate

object WorkoutRepository {
    private val logs = mutableListOf<ExerciseLog>()
    private val completedWorkouts = mutableSetOf<LocalDate>()
    var currentScheduleMode by mutableStateOf(ScheduleMode.SUMMER)
        private set
    private lateinit var dataFile: File
    
    var startDate by mutableStateOf(LocalDate.of(2026, 9, 27))
        private set

    var currentZombiesRun by mutableStateOf(1)
        private set

    fun init(context: Context) {
        dataFile = File(context.filesDir, "workout_data.json")
        loadData()
    }

    private fun loadData() {
        if (dataFile.exists()) {
            try {
                val jsonString = dataFile.readText()
                val data = Json.decodeFromString<WorkoutData>(jsonString)
                logs.clear()
                logs.addAll(data.logs)
                completedWorkouts.clear()
                completedWorkouts.addAll(data.completedWorkouts)
                currentScheduleMode = data.scheduleMode
                startDate = data.startDate
                currentZombiesRun = data.currentZombiesRun
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveData() {
        try {
            val data = WorkoutData(logs, completedWorkouts.toList(), currentScheduleMode, startDate, currentZombiesRun)
            val jsonString = Json.encodeToString(data)
            dataFile.writeText(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getScheduleMode(): ScheduleMode = currentScheduleMode

    fun setScheduleMode(mode: ScheduleMode) {
        currentScheduleMode = mode
        saveData()
    }

    fun updateStartDate(date: LocalDate) {
        startDate = date
        saveData()
    }

    fun updateZombiesRun(run: Int) {
        currentZombiesRun = maxOf(1, run)
        saveData()
    }

    fun incrementZombiesRun() {
        currentZombiesRun++
        saveData()
    }

    fun addSet(exerciseId: String, date: LocalDate, set: SetLog) {
        val existingLog = logs.find { it.exerciseId == exerciseId && it.date == date }
        if (existingLog != null) {
            val updatedSets = existingLog.sets + set
            logs.remove(existingLog)
            logs.add(existingLog.copy(sets = updatedSets))
        } else {
            logs.add(ExerciseLog(exerciseId, date, listOf(set)))
        }
        saveData()
    }

    fun getLog(exerciseId: String, date: LocalDate): ExerciseLog? {
        return logs.find { it.exerciseId == exerciseId && it.date == date }
    }

    fun getLastLog(exerciseId: String, beforeDate: LocalDate): ExerciseLog? {
        return logs.filter { it.exerciseId == exerciseId && it.date.isBefore(beforeDate) }
            .maxByOrNull { it.date }
    }

    fun markWorkoutCompleted(date: LocalDate) {
        if (!completedWorkouts.contains(date)) {
            completedWorkouts.add(date)
            val workoutType = getWorkoutTypeForDate(startDate, date, currentScheduleMode)
            if (workoutType != null) {
                val template = getTemplate(workoutType)
                var hasZrx = false
                for (ex in template.exercises) {
                    if (ex.id == "zrx_run") {
                        hasZrx = true
                        break
                    }
                }
                if (hasZrx) {
                    incrementZombiesRun()
                }
            }
            saveData()
        }
    }

    fun isWorkoutCompleted(date: LocalDate): Boolean {
        return completedWorkouts.contains(date)
    }
}
