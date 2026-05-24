package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.SecondaryAccent

@Composable
fun GlassmorphicBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize().background(Color(0xFF09090B))) {
        // Draw colorful glowing neon visual orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            if (w > 0f && h > 0f) {
                val r1 = w * 0.7f
                val r2 = w * 0.8f
                val r3 = w * 0.6f

                if (r1 > 0f && r2 > 0f && r3 > 0f) {
                    // Violet main orb at top right
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                PrimaryAccent.copy(alpha = 0.20f),
                                PrimaryAccent.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.85f, h * 0.22f),
                            radius = r1
                        ),
                        center = Offset(w * 0.85f, h * 0.22f),
                        radius = r1
                    )

                    // Fuchsia core orb at mid left
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                SecondaryAccent.copy(alpha = 0.16f),
                                SecondaryAccent.copy(alpha = 0.04f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.12f, h * 0.58f),
                            radius = r2
                        ),
                        center = Offset(w * 0.12f, h * 0.58f),
                        radius = r2
                    )

                    // Light cyan highlights at bottom center to give variety and premium vibe
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF06B6D4).copy(alpha = 0.10f),
                                Color(0xFF06B6D4).copy(alpha = 0.02f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.5f, h * 0.92f),
                            radius = r3
                        ),
                        center = Offset(w * 0.5f, h * 0.92f),
                        radius = r3
                    )
                }
            }
        }

        // Tint overlay to blend glass orbs together beautifully
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x6609090B),
                            Color(0xBF09090B),
                            Color(0xF509090B)
                        )
                    )
                )
        )

        content()
    }
}

// Transparent cards with translucent white reflection and premium glass glow
fun Modifier.glassCard(
    cornerRadius: Dp = 16.dp,
    backgroundColor: Color = Color(0x0CFFFFFF)
): Modifier {
    return this
        .clip(RoundedCornerShape(cornerRadius))
        .background(backgroundColor)
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.12f),
                    Color.White.copy(alpha = 0.02f),
                    Color.Black.copy(alpha = 0.25f)
                )
            ),
            shape = RoundedCornerShape(cornerRadius)
        )
}
