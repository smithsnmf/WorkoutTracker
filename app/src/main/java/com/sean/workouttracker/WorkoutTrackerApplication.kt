package com.sean.workouttracker

import android.app.Application
import com.sean.workouttracker.model.WorkoutRepository

class WorkoutTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        WorkoutRepository.init(this)
    }
}
