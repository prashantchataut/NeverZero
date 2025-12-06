package com.productivitystreak.ui.animation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

/**
 * Particle Effects System for NeverZero
 * Creates celebratory and atmospheric visual effects
 */

data class Particle(
    val position: Offset,
    val velocity: Offset,
    val size: Float,
    val color: Color,
    val alpha: Float,
    val lifetime: Float,
    val maxLifetime: Float,
    val rotation: Float = 0f,
    val rotationSpeed: Float = 0f
) {
    fun update(deltaTime: Float, gravity: Float = 9.8f): Particle {
        val newVelocity = velocity.copy(y = velocity.y + gravity * deltaTime)
        val newPosition = position + newVelocity * deltaTime
        val newAlpha = (alpha * (lifetime / maxLifetime)).coerceIn(0f, 1f)
        val newRotation = rotation + rotationSpeed * deltaTime
        
        return copy(
            position = newPosition,
            velocity = newVelocity,
            alpha = newAlpha,
            lifetime = lifetime - deltaTime,
            rotation = newRotation
        )
    }
    
    fun isAlive(): Boolean = lifetime > 0 && alpha > 0.01f
}

data class ParticleConfig(
    val count: Int = 50,
    colors: List<Color> = listOf(Color.Yellow, Color.Magenta, Color.Cyan),
    val sizeRange: ClosedFloatingPointRange<Float> = 2f..8f,
    val velocityRange: ClosedFloatingPointRange<Float> = -200f..200f,
    val lifetimeRange: ClosedFloatingPointRange<Float> = 1f..3f,
    val gravity: Float = 100f,
    val fadeOut: Boolean = true,
    val rotation: Boolean = false
)

class ParticleSystem {
    private val particles = mutableListOf<Particle>()
    private var isActive = false
    
    fun emit(config: ParticleConfig, origin: Offset) {
        repeat(config.count) {
            val angle = Random.nextFloat() * 2 * PI
            val speed = Random.nextFloat() * (config.velocityRange.endInclusive - config.velocityRange.start) + config.velocityRange.start
            val velocity = Offset(
                cos(angle).toFloat() * speed,
                sin(angle).toFloat() * speed - 100f // Initial upward burst
            )
            
            val particle = Particle(
                position = origin,
                velocity = velocity,
                size = Random.nextFloat() * (config.sizeRange.endInclusive - config.sizeRange.start) + config.sizeRange.start,
                color = config.colors.random(),
                alpha = 1f,
                lifetime = Random.nextFloat() * (config.lifetimeRange.endInclusive - config.lifetimeRange.start) + config.lifetimeRange.start,
                maxLifetime = Random.nextFloat() * (config.lifetimeRange.endInclusive - config.lifetimeRange.start) + config.lifetimeRange.start,
                rotationSpeed = if (config.rotation) Random.nextFloat() * 360f - 180f else 0f
            )
            
            particles.add(particle)
        }
        isActive = true
    }
    
    fun update(deltaTime: Float) {
        particles.removeAll { !it.isAlive() }
        particles.forEach { particle ->
            particles[particles.indexOf(particle)] = particle.update(deltaTime, 100f)
        }
        
        if (particles.isEmpty()) {
            isActive = false
        }
    }
    
    fun getParticles(): List<Particle> = particles.toList()
    
    fun isRunning(): Boolean = isActive
    
    fun clear() {
        particles.clear()
        isActive = false
    }
}

@Composable
fun rememberParticleSystem(): ParticleSystem = remember { ParticleSystem() }

/**
 * Confetti celebration effect
 */
@Composable
fun ConfettiEffect(
    trigger: Boolean,
    modifier: Modifier = Modifier,
    config: ParticleConfig = ParticleConfig(
        count = 100,
        colors = listOf(
            Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFF45B7D1),
            Color(0xFFFFA07A), Color(0xFF98D8C8), Color(0xFF6C5CE7),
            Color(0xFFFD79A8), Color(0xFFFDCB6E), Color(0xFF6C63FF)
        ),
        sizeRange = 4f..12f,
        velocityRange = -300f..300f,
        lifetimeRange = 2f..4f,
        rotation = true
    )
) {
    val particleSystem = rememberParticleSystem()
    val density = LocalDensity.current
    
    LaunchedEffect(trigger) {
        if (trigger) {
            val origin = Offset(density.density * 200f, density.density * 100f) // Center of screen
            particleSystem.emit(config, origin)
        }
    }
    
    LaunchedEffect(particleSystem.isRunning()) {
        while (particleSystem.isRunning()) {
            delay(16) // ~60fps
            particleSystem.update(0.016f)
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particleSystem.getParticles().forEach { particle ->
            drawParticle(particle)
        }
    }
}

/**
 * Star burst effect for achievements
 */
@Composable
fun StarBurstEffect(
    trigger: Boolean,
    modifier: Modifier = Modifier,
    config: ParticleConfig = ParticleConfig(
        count = 30,
        colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500), Color(0xFFFF8C00)),
        sizeRange = 2f..6f,
        velocityRange = -400f..400f,
        lifetimeRange = 1.5f..2.5f,
        rotation = false
    )
) {
    val particleSystem = rememberParticleSystem()
    val density = LocalDensity.current
    
    LaunchedEffect(trigger) {
        if (trigger) {
            val origin = Offset(density.density * 100f, density.density * 100f)
            particleSystem.emit(config, origin)
        }
    }
    
    LaunchedEffect(particleSystem.isRunning()) {
        while (particleSystem.isRunning()) {
            delay(16)
            particleSystem.update(0.016f)
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particleSystem.getParticles().forEach { particle ->
            drawStar(particle)
        }
    }
}

/**
 * Floating ambient particles for atmosphere
 */
@Composable
fun AmbientParticles(
    modifier: Modifier = Modifier,
    config: ParticleConfig = ParticleConfig(
        count = 20,
        colors = listOf(Color.White.copy(alpha = 0.3f), Color(0xFFE8F4F8).copy(alpha = 0.5f)),
        sizeRange = 1f..3f,
        velocityRange = -20f..20f,
        lifetimeRange = 10f..20f,
        gravity = 0f // No gravity for ambient particles
    )
) {
    val particleSystem = rememberParticleSystem()
    val density = LocalDensity.current
    
    // Continuously emit ambient particles
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000) // Emit every 2 seconds
            val origin = Offset(
                Random.nextFloat() * density.density * 400f,
                density.density * 400f
            )
            particleSystem.emit(config.copy(count = 5), origin)
        }
    }
    
    LaunchedEffect(particleSystem.isRunning()) {
        while (particleSystem.isRunning()) {
            delay(16)
            particleSystem.update(0.016f)
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particleSystem.getParticles().forEach { particle ->
            drawParticle(particle)
        }
    }
}

/**
 * Fire trail effect for streaks
 */
@Composable
fun FireTrailEffect(
    trigger: Boolean,
    modifier: Modifier = Modifier,
    config: ParticleConfig = ParticleConfig(
        count = 15,
        colors = listOf(
            Color(0xFFFF6B35), Color(0xFFF7931E), Color(0xFFFFC947),
            Color(0xFFFF5722), Color(0xFFFF9800), Color(0xFFFFEB3B)
        ),
        sizeRange = 3f..8f,
        velocityRange = -150f..150f,
        lifetimeRange = 0.5f..1.5f,
        gravity = -50f // Fire rises
    )
) {
    val particleSystem = rememberParticleSystem()
    val density = LocalDensity.current
    
    LaunchedEffect(trigger) {
        if (trigger) {
            val origin = Offset(density.density * 50f, density.density * 200f) // Side of screen
            particleSystem.emit(config, origin)
        }
    }
    
    LaunchedEffect(particleSystem.isRunning()) {
        while (particleSystem.isRunning()) {
            delay(16)
            particleSystem.update(0.016f)
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particleSystem.getParticles().forEach { particle ->
            drawFireParticle(particle)
        }
    }
}

/**
 * Magic sparkle effect for wisdom/insights
 */
@Composable
fun MagicSparkleEffect(
    trigger: Boolean,
    modifier: Modifier = Modifier,
    config: ParticleConfig = ParticleConfig(
        count = 25,
        colors = listOf(
            Color(0xFFE1BEE7), Color(0xFFCE93D8), Color(0xFFBA68C8),
            Color(0xFF9C27B0), Color(0xFF8E24AA), Color(0xFF7B1FA2)
        ),
        sizeRange = 2f..5f,
        velocityRange = -100f..100f,
        lifetimeRange = 2f..3f,
        gravity = -20f, // Gentle upward float
        rotation = true
    )
) {
    val particleSystem = rememberParticleSystem()
    val density = LocalDensity.current
    
    LaunchedEffect(trigger) {
        if (trigger) {
            val origin = Offset(density.density * 200f, density.density * 150f)
            particleSystem.emit(config, origin)
        }
    }
    
    LaunchedEffect(particleSystem.isRunning()) {
        while (particleSystem.isRunning()) {
            delay(16)
            particleSystem.update(0.016f)
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particleSystem.getParticles().forEach { particle ->
            drawSparkle(particle)
        }
    }
}

private fun DrawScope.drawParticle(particle: Particle) {
    drawCircle(
        color = particle.color.copy(alpha = particle.alpha),
        radius = particle.size,
        center = particle.position
    )
}

private fun DrawScope.drawStar(particle: Particle) {
    val size = particle.size
    val center = particle.position
    val color = particle.color.copy(alpha = particle.alpha)
    
    // Draw a simple 4-pointed star
    val points = listOf(
        Offset(center.x, center.y - size),
        Offset(center.x + size * 0.5f, center.y - size * 0.5f),
        Offset(center.x + size, center.y),
        Offset(center.x + size * 0.5f, center.y + size * 0.5f),
        Offset(center.x, center.y + size),
        Offset(center.x - size * 0.5f, center.y + size * 0.5f),
        Offset(center.x - size, center.y),
        Offset(center.x - size * 0.5f, center.y - size * 0.5f)
    )
    
    for (i in points.indices step 2) {
        if (i + 1 < points.size) {
            drawLine(
                color = color,
                start = points[i],
                end = points[i + 1],
                strokeWidth = size * 0.3f
            )
        }
    }
}

private fun DrawScope.drawFireParticle(particle: Particle) {
    val gradient = androidx.compose.ui.graphics.LinearGradient(
        colors = listOf(
            particle.color.copy(alpha = particle.alpha * 0.8f),
            particle.color.copy(alpha = particle.alpha * 0.4f),
            Color.Transparent
        ),
        start = particle.position - Offset(0f, particle.size),
        end = particle.position + Offset(0f, particle.size)
    )
    
    drawCircle(
        brush = gradient,
        radius = particle.size * 1.5f,
        center = particle.position
    )
}

private fun DrawScope.drawSparkle(particle: Particle) {
    val size = particle.size
    val center = particle.position
    val color = particle.color.copy(alpha = particle.alpha)
    
    // Draw sparkle shape (cross with diagonal lines)
    drawLine(
        color = color,
        start = center - Offset(size, 0f),
        end = center + Offset(size, 0f),
        strokeWidth = size * 0.3f
    )
    
    drawLine(
        color = color,
        start = center - Offset(0f, size),
        end = center + Offset(0f, size),
        strokeWidth = size * 0.3f
    )
    
    drawLine(
        color = color,
        start = center - Offset(size * 0.7f, size * 0.7f),
        end = center + Offset(size * 0.7f, size * 0.7f),
        strokeWidth = size * 0.2f
    )
    
    drawLine(
        color = color,
        start = center - Offset(size * 0.7f, -size * 0.7f),
        end = center + Offset(size * 0.7f, -size * 0.7f),
        strokeWidth = size * 0.2f
    )
}

/**
 * Weather effects for seasonal atmosphere
 */
@Composable
fun WeatherEffect(
    weatherType: WeatherType,
    modifier: Modifier = Modifier
) {
    when (weatherType) {
        WeatherType.SNOW -> SnowEffect(modifier)
        WeatherType.RAIN -> RainEffect(modifier)
        WeatherType.LEAVES -> FallingLeavesEffect(modifier)
        WeatherType.SUNSHINE -> SunshineEffect(modifier)
    }
}

enum class WeatherType {
    SNOW, RAIN, LEAVES, SUNSHINE
}

@Composable
private fun SnowEffect(modifier: Modifier = Modifier) {
    val config = ParticleConfig(
        count = 30,
        colors = listOf(Color.White, Color(0xFFF0F8FF), Color(0xFFE6E6FA)),
        sizeRange = 2f..6f,
        velocityRange = -30f..30f,
        lifetimeRange = 5f..10f,
        gravity = 20f
    )
    
    AmbientParticles(modifier, config)
}

@Composable
private fun RainEffect(modifier: Modifier = Modifier) {
    val config = ParticleConfig(
        count = 40,
        colors = listOf(Color(0xFF4FC3F7), Color(0xFF29B6F6), Color(0xFF03A9F4)),
        sizeRange = 1f..3f,
        velocityRange = 100f..200f,
        lifetimeRange = 1f..2f,
        gravity = 300f
    )
    
    AmbientParticles(modifier, config)
}

@Composable
private fun FallingLeavesEffect(modifier: Modifier = Modifier) {
    val config = ParticleConfig(
        count = 20,
        colors = listOf(
            Color(0xFFD2691E), Color(0xFFCD853F), Color(0xFFDEB887),
            Color(0xFFF4A460), Color(0xFFD2691E), Color(0xFF8B4513)
        ),
        sizeRange = 4f..10f,
        velocityRange = -50f..50f,
        lifetimeRange = 4f..8f,
        gravity = 30f,
        rotation = true
    )
    
    AmbientParticles(modifier, config)
}

@Composable
private fun SunshineEffect(modifier: Modifier = Modifier) {
    val config = ParticleConfig(
        count = 15,
        colors = listOf(
            Color(0xFFFFD700), Color(0xFFFFEA00), Color(0xFFFFF59D)
        ),
        sizeRange = 2f..4f,
        velocityRange = -10f..10f,
        lifetimeRange = 3f..6f,
        gravity = -10f
    )
    
    AmbientParticles(modifier, config)
}
