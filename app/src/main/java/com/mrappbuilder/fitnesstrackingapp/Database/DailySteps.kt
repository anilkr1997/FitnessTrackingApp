package com.mrappbuilder.fitnesstrackingapp.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_steps")
data class DailySteps(
    @PrimaryKey val date: String,
    val steps: Int
)