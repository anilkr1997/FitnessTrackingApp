package com.mrappbuilder.fitnesstrackingapp.RepImplementation


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.mrappbuilder.fitnesstrackingapp.Database.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GoalRepositoryImpl(private val store: DataStore<Preferences>) : GoalRepository {
    private val KEY_GOAL = intPreferencesKey("daily_goal")

    override val goalFlow: Flow<Int> =
        store.data.map { it[KEY_GOAL] ?: 6000 }

    override suspend fun setGoal(goal: Int) {
        store.edit { it[KEY_GOAL] = goal }
    }

    override suspend fun getGoal(): Int = goalFlow.first()
}
