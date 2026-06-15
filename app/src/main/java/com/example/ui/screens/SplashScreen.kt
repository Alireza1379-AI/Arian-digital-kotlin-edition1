package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit) {
    val scale = remember { Animatable(0f) }
    val animatedAlpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        // Soft spring animation
        scale.animateTo(
            targetValue = 1.1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        scale.animateTo(1.0f)
        animatedAlpha.animateTo(1.0f, animationSpec = tween(1200))
        delay(2000)
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFEF7FF),
                        Color(0xFFF3EDF7),
                        Color(0xFFE8DEF8)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .scale(scale.value)
                    .clip(CircleShape)
                    .background(Color(0xFF6750A4).copy(alpha = 0.15f))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow effect
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF6750A4), Color(0xFFD0BCFF))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "آد",
                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "آرین دیجیتال",
                color = Color(0xFF6750A4),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.graphicsLayer { alpha = animatedAlpha.value }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "پلتفرم جامع سفارش و پرداخت خدمات دیجیتال",
                color = Color(0xFF49454F),
                fontSize = 14.sp,
                modifier = Modifier.graphicsLayer { alpha = animatedAlpha.value }
            )
        }

        Text(
            text = "نسخه ۱.۰ (توسعه نمونه)",
            color = Color(0xFFCAC4D0),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}
