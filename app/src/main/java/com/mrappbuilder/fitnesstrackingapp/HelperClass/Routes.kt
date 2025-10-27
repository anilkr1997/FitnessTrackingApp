package com.mrappbuilder.fitnesstrackingapp.HelperClass

sealed class Routes(val route: String) {
    data object start: Routes("start")
    data object Intro: Routes("intro")
    data object Dashboard: Routes("dashboard")
    data object History: Routes("history")
    data object Goal: Routes("goal")
}