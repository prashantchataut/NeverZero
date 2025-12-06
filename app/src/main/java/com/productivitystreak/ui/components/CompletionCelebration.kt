package com.productivitystreak.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.productivitystreak.ui.animation.ConfettiEffect
import com.productivitystreak.ui.animation.StarBurstEffect
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Completion celebration overlay with confetti and expanding ring effect.
 */
@Composable
fun CompletionCelebration(
    trigger: Boolean,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFFFD700) // Gold
) {
    var isPlaying by remember { mutableStateOf(false) }
    
    LaunchedEffect(trigger) {
        if (trigger && !isPlaying) {
            isPlaying = true
            delay(1500) // Animation duration
            isPlaying = false
            onComplete()
        }
    }

    if (isPlaying) {
        Box(modifier = modifier.fillMaxSize()) {
            // Gold ring expanding outward
            GoldRingEffect(color = color)
            // Enhanced particle effects
            ConfettiEffect(trigger = true)
            StarBurstEffect(trigger = true)
        }
    }
}

@Composable
private fun GoldRingEffect(color: Color) {
    val animatedRadius = remember { Animatable(0f) }
    val animatedAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        animatedRadius.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )
    }
    
    LaunchedEffect(Unit) {
        delay(400)
        animatedAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(400, easing = LinearEasing)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val maxRadius = size.minDimension / 2
        val currentRadius = maxRadius * animatedRadius.value
        
        drawCircle(
            color = color.copy(alpha = animatedAlpha.value * 0.8f),
            radius = currentRadius,
            style = Stroke(width = 6f)
        )
        
        drawCircle(
            color = color.copy(alpha = animatedAlpha.value * 0.3f),
            radius = currentRadius * 0.85f,
            style = Stroke(width = 12f)
        )
    }
}

@Composable
private fun ConfettiEffect() {
    val particles = remember {
        List(20) { ConfettiParticle() }
    }
    
    val animatedProgress = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        
        particles.forEach { particle ->
            val progress = animatedProgress.value
            val distance = particle.speed * progress * size.minDimension / 2
            val x = centerX + distance * cos(particle.angle)
            val y = centerY + distance * sin(particle.angle) + (progress * progress * 200 * particle.gravity)
            val alpha = (1f - progress).coerceIn(0f, 1f)
            val particleSize = particle.size * (1f - progress * 0.5f)
            
            drawCircle(
                color = particle.color.copy(alpha = alpha),
                radius = particleSize,
                center = Offset(x, y)
            )
        }
    }
}

private data class ConfettiParticle(
    val angle: Float = Random.nextFloat() * 2f * Math.PI.toFloat(),
    val speed: Float = 0.3f + Random.nextFloat() * 0.7f,
    val size: Float = 4f + Random.nextFloat() * 8f,
    val gravity: Float = 0.5f + Random.nextFloat() * 0.5f,
    val color: Color = confettiColors[Random.nextInt(confettiColors.size)]
)

private val confettiColors = listOf(
    Color(0xFFFFD700), // Gold
    Color(0xFFFF6B6B), // Coral
    Color(0xFF4ECDC4), // Teal
    Color(0xFF45B7D1), // Sky blue
    Color(0xFF96CEB4), // Sage
    Color(0xFFFFEEAD), // Cream
    Color(0xFFD4A5A5), // Dusty rose
)
