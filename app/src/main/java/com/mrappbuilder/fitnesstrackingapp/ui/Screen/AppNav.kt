package com.mrappbuilder.fitnesstrackingapp.ui.Screen

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.mrappbuilder.fitnesstrackingapp.FitApp
import com.mrappbuilder.fitnesstrackingapp.HelperClass.Routes
import com.mrappbuilder.fitnesstrackingapp.ViewModel.StepViewModelFactory
import com.mrappbuilder.fitnesstrackingapp.ViewModel.StepsViewModel

@Composable
fun AppNav( viewModel: StepsViewModel ,navController: NavHostController = rememberNavController()) {


    NavHost(navController, startDestination = Routes.start.route) {

        composable(Routes.start.route) { SplashScreen(navController) }

        composable(Routes.Intro.route) {
            IntroPermissionScreen(
                onContinue = {

                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Intro.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Dashboard.route) {
            DashboardScreen( viewModel,
                onHistory = { navController.navigate(Routes.History.route) },
                onEditGoal = { navController.navigate(Routes.Goal.route) }
            )
        }
        composable(Routes.History.route) { HistoryScreen(viewModel) }
        composable(Routes.Goal.route) { GoalEditScreen(viewModel,onDone = { navController.popBackStack() }) }
    }
}


