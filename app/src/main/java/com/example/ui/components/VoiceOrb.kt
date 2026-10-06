package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LunaAmberTertiary
import com.example.ui.theme.LunaCyanPrimary
import com.example.ui.theme.LunaPurpleSecondary

enum class AssistantOrbState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING
}

@Composable
fun VoiceOrb(
    state: AssistantOrbState,
    audioRms: Float = 0f,
    size: Dp = 160.dp,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "OrbPulse")

    // Breathing pulse
    val idleScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IdlePulse"
    )

    // Rotation for thinking state
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbRotation"
    )

    // Wave ripple
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaveRipple"
    )

    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaveAlpha"
    )

    val primaryColor = when (state) {
        AssistantOrbState.IDLE -> LunaCyanPrimary
        AssistantOrbState.LISTENING -> LunaCyanPrimary
        AssistantOrbState.THINKING -> LunaAmberTertiary
        AssistantOrbState.SPEAKING -> LunaPurpleSecondary
    }

    val secondaryColor = when (state) {
        AssistantOrbState.IDLE -> LunaPurpleSecondary
        AssistantOrbState.LISTENING -> Color(0xFF00B0FF)
        AssistantOrbState.THINKING -> Color(0xFFFF9100)
        AssistantOrbState.SPEAKING -> Color(0xFFE040FB)
    }

    val dynamicAudioScale = if (state == AssistantOrbState.LISTENING) {
        1f + (audioRms * 0.35f)
    } else if (state == AssistantOrbState.SPEAKING) {
        1.05f + (idleScale - 1f) * 1.5f
    } else {
        idleScale
    }

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val centerOffset = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = (this.size.minDimension / 2.6f) * dynamicAudioScale

            // Outer ripple ring when active
            if (state == AssistantOrbState.LISTENING || state == AssistantOrbState.SPEAKING) {
                drawCircle(
                    color = primaryColor.copy(alpha = rippleAlpha),
                    radius = baseRadius * rippleScale,
                    center = centerOffset,
                    style = Stroke(width = 3.dp.toPx())
                )
                drawCircle(
                    color = secondaryColor.copy(alpha = rippleAlpha * 0.7f),
                    radius = baseRadius * (rippleScale * 1.15f),
                    center = centerOffset,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Glow Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.45f),
                        secondaryColor.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = centerOffset,
                    radius = baseRadius * 1.3f
                ),
                radius = baseRadius * 1.3f,
                center = centerOffset
            )

            // Inner Core Spherical Orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.9f),
                        primaryColor,
                        secondaryColor,
                        Color(0xFF0A0F1D)
                    ),
                    center = Offset(centerOffset.x - (baseRadius * 0.25f), centerOffset.y - (baseRadius * 0.25f)),
                    radius = baseRadius
                ),
                radius = baseRadius,
                center = centerOffset
            )

            // Dynamic Core Rim Stroke
            drawCircle(
                color = primaryColor.copy(alpha = 0.7f),
                radius = baseRadius,
                center = centerOffset,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}
