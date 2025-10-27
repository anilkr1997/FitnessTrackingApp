package com.mrappbuilder.fitnesstrackingapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrappbuilder.fitnesstrackingapp.ViewModel.StepViewModelFactory
import com.mrappbuilder.fitnesstrackingapp.ViewModel.StepsViewModel

import com.mrappbuilder.fitnesstrackingapp.ui.Screen.AppNav
import com.mrappbuilder.fitnesstrackingapp.ui.theme.FitnessTrackingAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()


        setContent {
            val viewModel: StepsViewModel = viewModel(
                factory = StepViewModelFactory(application)
            )
            FitnessTrackingAppTheme {
                Surface { AppNav(viewModel) }

            }
        }
    }
}

