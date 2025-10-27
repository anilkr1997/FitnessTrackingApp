package com.mrappbuilder.fitnesstrackingapp.RepImplementation


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.mrappbuilder.fitnesstrackingapp.Database.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GoalRepositoryImpl(private val store: DataStore<Preferences>) : GoalRepository {
    private val KEY_GOAL = intPreferencesKey("daily_goal")
    private val KEY_ASKED_BATTERY = booleanPreferencesKey("asked_battery_opt")
    private val KEY_ASKED_AUTOSTART = booleanPreferencesKey("asked_autostart")


    override val goalFlow: Flow<Int> =
        store.data.map { it[KEY_GOAL] ?: 6000 }

    override suspend fun setGoal(goal: Int) {
        store.edit { it[KEY_GOAL] = goal }
    }

    override suspend fun getGoal(): Int = goalFlow.first()

    //  NEW — Read asked battery optimization only once
    override val askedBatteryOpt: Flow<Boolean> =
        store.data.map { it[KEY_ASKED_BATTERY] ?: false }

    //  NEW — Save value so we don't ask every time
    override suspend fun setAskedBatteryOpt(asked: Boolean) {
        store.edit { it[KEY_ASKED_BATTERY] = asked }
    }
    override val askedAutoStart: Flow<Boolean> =
        store.data.map { it[KEY_ASKED_AUTOSTART] ?: false }

    override suspend fun setAskedAutoStart(asked: Boolean) {
        store.edit { it[KEY_ASKED_AUTOSTART] = asked }
    }
}
