package com.mrappbuilder.fitnesstrackingapp.Database

import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    val goalFlow: Flow<Int>
    suspend fun setGoal(goal: Int)
    suspend fun getGoal(): Int

    val askedBatteryOpt: Flow<Boolean>
    suspend fun setAskedBatteryOpt(asked: Boolean)
    val askedAutoStart: Flow<Boolean>
    suspend fun setAskedAutoStart(asked: Boolean)


}