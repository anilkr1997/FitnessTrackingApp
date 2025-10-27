package com.mrappbuilder.fitnesstrackingapp.ui.Screen

import android.Manifest
import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mrappbuilder.fitnesstrackingapp.HelperClass.Routes
import com.mrappbuilder.fitnesstrackingapp.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SplashScreen(
    nav: NavController
) {
    val ctx = LocalContext.current

    val activityPerm = rememberPermissionState(Manifest.permission.ACTIVITY_RECOGNITION)
    val notifPerm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS) else null

    // Animation states
    var logoScale by remember { mutableStateOf(0.8f) }
    var logoAlpha by remember { mutableStateOf(0f) }
    var textAlpha by remember { mutableStateOf(0f) }

    // Background gradient animation
    val infiniteTransition = rememberInfiniteTransition()
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Logo entrance animation
    LaunchedEffect(Unit) {
        // Animate logo entrance
        logoScale = 1.2f
        logoAlpha = 1f
        delay(500)
        logoScale = 1f
        delay(500)

        // Show app name
        textAlpha = 1f
        delay(500)

        // Continue with original logic
        val allGranted = activityPerm.status.isGranted &&
                (notifPerm?.status?.isGranted != false)

        if (allGranted) {
            nav.navigate(Routes.Dashboard.route) {
                popUpTo(Routes.start.route) { inclusive = true }
            }
        } else {
            nav.navigate(Routes.Intro.route) {
                popUpTo(Routes.start.route) { inclusive = true }
            }
        }
    }

    // Animated background gradient
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.onPrimary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary
        ),
        start = androidx.compose.ui.geometry.Offset(
            x = -100f * gradientOffset,
            y = -100f * gradientOffset
        ),
        end = androidx.compose.ui.geometry.Offset(
            x = 500f * gradientOffset,
            y = 500f * gradientOffset
        )
    )

    Surface(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient),
            contentAlignment = Alignment.Center
        ) {
            // Background decorative elements
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .blur(20.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Animated Logo
                Image(
                    painter = painterResource(id = R.drawable.outline_sports_gymnastics_24),
                    contentDescription = "Fitness Tracker Logo",
                    modifier = Modifier
                        .size(140.dp)
                        .scale(logoScale)
                        .alpha(logoAlpha),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                )

                // App Name with fade-in animation
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Fitness Tracker",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.alpha(textAlpha)
                    )

                    Text(
                        text = "Track your steps, achieve your goals",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.alpha(textAlpha)
                    )
                }

                // Loading indicator
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(30.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onPrimary,
                                shape = MaterialTheme.shapes.small
                            )
                            .offset(x = (50f * gradientOffset).dp)
                    )
                }
            }
        }
    }
}