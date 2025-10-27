package com.mrappbuilder.fitnesstrackingapp.ui.Screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrappbuilder.fitnesstrackingapp.HelperClass.openAutoStartSettings
import com.mrappbuilder.fitnesstrackingapp.R

import com.mrappbuilder.fitnesstrackingapp.ViewModel.StepsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: StepsViewModel, onHistory: () -> Unit, onEditGoal: () -> Unit) {
    val ctx = LocalContext.current
    val steps by viewModel.steps.collectAsStateWithLifecycle()
    val goal by viewModel.goal.collectAsStateWithLifecycle()
    val askedBattery by viewModel.askedBatteryOpt.collectAsState()
    var showBatteryDialog by remember { mutableStateOf(!askedBattery) }
    val askedAutoStart by viewModel.askedAutoStart.collectAsState(initial = false)
    var showAutoStartDialog by remember { mutableStateOf(!askedAutoStart) }


    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                ctx, Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.startAndBindService()
        }
    }


    val progressTarget = (steps.toFloat() / goal).coerceIn(0f, 1f)
    val progress by animateFloatAsState(targetValue = progressTarget, label = "progress")

    // Additional state for enhanced UI
    val remainingSteps = (goal - steps).coerceAtLeast(0)
    val percentage = (progressTarget * 100).toInt()
    val caloriesBurned = (steps * 0.04).toInt() // Approximate calories calculation
    if (showBatteryDialog) {
        AskBatteryOptimizationOnce(
            shouldAsk = showBatteryDialog,
            onAsked = {
                viewModel.markBatteryAsked()
                showBatteryDialog = false
            }
        )
    }
    if (showAutoStartDialog) {
        AutoStartDialog(
            onConfirm = {
                ctx.openAutoStartSettings()
                viewModel.markAutoStartAsked()
                showAutoStartDialog = false
            },
            onCancel = {
                viewModel.markAutoStartAsked()
                showAutoStartDialog = false
            }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Fitness Tracker",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .verticalScroll(rememberScrollState())
        ) {
            // Main Progress Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated Circular Progress with gradient
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(240.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = 1f,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            strokeWidth = 12.dp,
                            modifier = Modifier.size(240.dp)
                        )
                        CircularProgressIndicator(
                            progress = progress,
                            color = if (progress >= 1f) MaterialTheme.colorScheme.tertiary
                            else MaterialTheme.colorScheme.primary,
                            strokeWidth = 12.dp,
                            modifier = Modifier.size(240.dp)
                        )

                        // Center content
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "$percentage%",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Completed",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Steps information
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "$steps / $goal",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            if (steps >= goal) "🎉 Goal Achieved! Amazing work!"
                            else "$remainingSteps steps to go - You can do it! 💪",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Stats Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Calories Card
                StatCard(
                    title = "Calories",
                    value = "${caloriesBurned} kcal",
                    icon = painterResource(R.drawable.outline_local_fire_department_24),
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.tertiary
                )

                // Distance Card
                StatCard(
                    title = "Distance",
                    value = "${"%.1f".format(steps * 0.0008)} km",
                    icon = painterResource(R.drawable.outline_directions_walk_24),
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Action Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // History Card
                ActionCard(
                    title = "Activity History",
                    subtitle = "View your step history",
                    icon = painterResource(R.drawable.outline_history_24),
                    onClick = onHistory,
                    modifier = Modifier.weight(1f),
                    buttonText = "History"
                )

                // Goal Card
                ActionCard(
                    title = "Daily Goal",
                    subtitle = "Adjust your target",
                    icon =painterResource(R.drawable.outline_flag_24),
                    onClick = onEditGoal,
                    modifier = Modifier.weight(1f),
                    buttonText = "Edit Goal"
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: Painter,
    modifier: Modifier = Modifier,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    subtitle: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(buttonText)
            }
        }
    }
}