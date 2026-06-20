package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberAccent
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import kotlin.random.Random

// Cyber Grid, Scanline, and Floating Particle Backdrop
@Composable
fun CyberpunkBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    // Holographic animations
    val infiniteTransition = rememberInfiniteTransition(label = "cyber_grid")
    
    // Scanline vertical sweep
    val scanlineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteTransitionSpec(),
        label = "scanline_y"
    )

    // Grid glowing opacity pulse
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteTransitionSpec(duration = 2000),
        label = "grid_pulse"
    )

    // Simple reactive local particles
    val particles = remember {
        List(15) {
            ParticleState(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = Random.nextFloat() * 0.02f + 0.01f,
                alpha = Random.nextFloat() * 0.4f + 0.2f,
                size = Random.nextFloat() * 4f + 2f
            )
        }
    }

    // Dynamic anim frame tick for particles
    var tick by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(32) // ~30 fps
            tick++
            particles.forEach { p ->
                p.y -= p.speed
                if (p.y < 0) {
                    p.y = 1f
                    p.x = Random.nextFloat()
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
            .drawBehind {
                val width = size.width
                val height = size.height

                // 1. Draw Sci-Fi Hex/Grid Background
                val gridSpacing = 60f
                val gridStroke = 1.5f
                val gridColor = CyberPrimary.copy(alpha = 0.08f * pulseAlpha)

                // Vertical grid lines
                var x = 0f
                while (x < width) {
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, height),
                        strokeWidth = gridStroke
                    )
                    x += gridSpacing
                }

                // Horizontal grid lines
                var y = 0f
                while (y < height) {
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = gridStroke
                    )
                    y += gridSpacing
                }

                // 2. Draw Floating Cyber Particles
                particles.forEach { p ->
                    drawCircle(
                        color = CyberPrimary.copy(alpha = p.alpha),
                        radius = p.size,
                        center = Offset(p.x * width, p.y * height)
                    )
                }

                // 3. Draw Scanline line sweep
                val sweepY = scanlineY * height
                drawLine(
                    color = CyberPrimary.copy(alpha = 0.25f),
                    start = Offset(0f, sweepY),
                    end = Offset(width, sweepY),
                    strokeWidth = 3f
                )

                // Draw secondary glowing gradient aura at the top and bottom edge
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(CyberAccent.copy(alpha = 0.08f), Color.Transparent, CyberPrimary.copy(alpha = 0.08f))
                    )
                )
            }
    ) {
        content()
    }
}

class ParticleState(
    var x: Float,
    var y: Float,
    var speed: Float,
    var alpha: Float,
    var size: Float
)

@Composable
fun infiniteTransitionSpec(duration: Int = 4000): InfiniteRepeatableSpec<Float> {
    return infiniteRepeatable(
        animation = tween(duration, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    )
}

// Glassmorphic neon-card
@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    borderColor: Color = CyberPrimary,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val mod = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    Card(
        modifier = mod
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(listOf(borderColor.copy(alpha = 0.8f), borderColor.copy(alpha = 0.2f))),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = CyberCardBg
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

// Premium Holographic Button
@Composable
fun CyberButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = CyberPrimary,
    glowColor: Color = CyberSecondary,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_button")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .padding(vertical = 4.dp)
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(listOf(color, glowColor)),
                shape = RoundedCornerShape(8.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 2.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )
    }
}

// Neon Loading Effect
@Composable
fun CyberLoadingView(
    modifier: Modifier = Modifier,
    statusText: String = "INITIALIZING SECURE SOCKETS..."
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loader")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot"
    )

    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink"
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier.size(80.dp)) {
            drawArc(
                color = CyberPrimary.copy(alpha = 0.2f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 6f)
            )

            drawArc(
                color = CyberPrimary,
                startAngle = angle,
                sweepAngle = 90f,
                useCenter = false,
                style = Stroke(width = 6.5f)
            )

            drawArc(
                color = CyberAccent,
                startAngle = -angle,
                sweepAngle = 60f,
                useCenter = false,
                style = Stroke(width = 6.5f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = statusText,
            color = CyberPrimary.copy(alpha = blinkAlpha),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
    }
}

// Interactive digital delay for simulation ticker
suspend fun delay(ms: Long) {
    kotlinx.coroutines.delay(ms)
}
