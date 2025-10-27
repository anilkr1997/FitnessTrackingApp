package com.mrappbuilder.fitnesstrackingapp.Database

import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    val goalFlow: Flow<Int>
    suspend fun setGoal(goal: Int)
    suspend fun getGoal(): Int
}