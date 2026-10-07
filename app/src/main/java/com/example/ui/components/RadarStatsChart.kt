package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CharacterStats
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonPurple
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarStatsChart(
    stats: CharacterStats,
    modifier: Modifier = Modifier
) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(stats) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(1000))
    }

    val labels = listOf("STR", "SPD", "DEF", "INT", "MAG", "CMB", "AGI", "END")
    val values = listOf(
        stats.strength,
        stats.speed,
        stats.defense,
        stats.intelligence,
        stats.magic,
        stats.combat,
        stats.agility,
        stats.endurance
    )
    val numSides = labels.size

    val cyanColor = ElectricCyan
    val purpleColor = NeonPurple

    Box(
        modifier = modifier
            .testTag("radar_stats_chart"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(28.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = (minOf(size.width, size.height) / 2f) * 0.9f

            // Draw concentric web polygons (25%, 50%, 75%, 100%)
            val levels = listOf(0.25f, 0.5f, 0.75f, 1.0f)
            for (level in levels) {
                val radius = maxRadius * level
                val gridPath = Path()
                for (i in 0 until numSides) {
                    val angle = (2 * PI / numSides * i) - (PI / 2)
                    val x = center.x + radius * cos(angle).toFloat()
                    val y = center.y + radius * sin(angle).toFloat()
                    if (i == 0) gridPath.moveTo(x, y) else gridPath.lineTo(x, y)
                }
                gridPath.close()
                drawPath(
                    path = gridPath,
                    color = Color(0x33B388FF),
                    style = Stroke(width = if (level == 1.0f) 2.5f else 1f)
                )
            }

            // Draw spoke axes and labels
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 28f
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val valPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#00E5FF")
                textSize = 24f
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
            }

            for (i in 0 until numSides) {
                val angle = (2 * PI / numSides * i) - (PI / 2)
                val endX = center.x + maxRadius * cos(angle).toFloat()
                val endY = center.y + maxRadius * sin(angle).toFloat()

                // Draw axis spoke
                drawLine(
                    color = Color(0x44B388FF),
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 1.5f
                )

                // Label positions slightly outside polygon
                val labelDistance = maxRadius + 28f
                val labelX = center.x + labelDistance * cos(angle).toFloat()
                val labelY = center.y + labelDistance * sin(angle).toFloat() + 10f

                drawContext.canvas.nativeCanvas.drawText(
                    labels[i],
                    labelX,
                    labelY,
                    textPaint
                )
            }

            // Draw user stats polygon with animation
            val statPath = Path()
            val points = mutableListOf<Offset>()

            for (i in 0 until numSides) {
                val angle = (2 * PI / numSides * i) - (PI / 2)
                val statRatio = (values[i] / 100f).coerceIn(0.1f, 1f) * animationProgress.value
                val r = maxRadius * statRatio
                val x = center.x + r * cos(angle).toFloat()
                val y = center.y + r * sin(angle).toFloat()
                val point = Offset(x, y)
                points.add(point)

                if (i == 0) statPath.moveTo(x, y) else statPath.lineTo(x, y)
            }
            statPath.close()

            // Fill with vibrant cyber gradient
            drawPath(
                path = statPath,
                brush = Brush.radialGradient(
                    colors = listOf(
                        cyanColor.copy(alpha = 0.55f),
                        purpleColor.copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxRadius
                )
            )

            // Draw glowing border stroke
            drawPath(
                path = statPath,
                brush = Brush.horizontalGradient(listOf(cyanColor, purpleColor)),
                style = Stroke(width = 3.5f)
            )

            // Draw glowing points on vertices
            for (p in points) {
                drawCircle(
                    color = Color.White,
                    radius = 5.5f,
                    center = p
                )
                drawCircle(
                    color = cyanColor,
                    radius = 3.5f,
                    center = p
                )
            }
        }
    }
}
