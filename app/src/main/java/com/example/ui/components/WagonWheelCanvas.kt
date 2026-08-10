package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BallEvent
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WagonWheelCanvas(
    ballEvents: List<BallEvent>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3D2B))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏏 Interactive Shot Map / Wagon Wheel",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(240.dp)
                    .background(Color(0xFF2E7D32), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.width / 2f - 12f

                    // Draw Boundary Circle
                    drawCircle(
                        color = Color.White.copy(alpha = 0.8f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 3f)
                    )

                    // Draw 30-yard circle
                    drawCircle(
                        color = Color.White.copy(alpha = 0.3f),
                        radius = radius * 0.55f,
                        center = center,
                        style = Stroke(width = 2f)
                    )

                    // Draw Pitch Rect in center
                    drawRect(
                        color = Color(0xFFD7CCC8),
                        topLeft = Offset(center.x - 10f, center.y - 25f),
                        size = androidx.compose.ui.geometry.Size(20f, 50f)
                    )

                    // Map shot directions to angles in degrees (0 = top / Long Off / Straight)
                    fun directionToAngle(dir: String): Double {
                        return when (dir) {
                            "Straight", "Long Off" -> 270.0
                            "Long On" -> 220.0
                            "Midwicket", "Leg" -> 180.0
                            "Fine Leg", "Square Leg" -> 135.0
                            "Third Man" -> 45.0
                            "Point", "Off" -> 0.0
                            "Cover" -> 315.0
                            else -> 270.0
                        }
                    }

                    // Draw shots
                    ballEvents.forEach { ball ->
                        if (ball.runsScored > 0 && ball.shotDirection.isNotEmpty()) {
                            val angleDeg = directionToAngle(ball.shotDirection)
                            val angleRad = Math.toRadians(angleDeg)

                            val distanceFactor = when (ball.runsScored) {
                                1 -> 0.4f
                                2 -> 0.65f
                                3 -> 0.8f
                                4 -> 0.95f
                                6 -> 1.05f
                                else -> 0.5f
                            }

                            val endX = center.x + (radius * distanceFactor * cos(angleRad)).toFloat()
                            val endY = center.y + (radius * distanceFactor * sin(angleRad)).toFloat()

                            val lineColor = when (ball.runsScored) {
                                4 -> Color(0xFF2196F3) // Blue for 4s
                                6 -> Color(0xFFFF9800) // Orange for 6s
                                else -> Color.White.copy(alpha = 0.7f)
                            }

                            drawLine(
                                color = lineColor,
                                start = center,
                                end = Offset(endX, endY),
                                strokeWidth = if (ball.runsScored >= 4) 4f else 2f
                            )

                            drawCircle(
                                color = lineColor,
                                radius = if (ball.runsScored >= 4) 6f else 4f,
                                center = Offset(endX, endY)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Shot Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(label = "Singles/Twos", color = Color.White)
                LegendItem(label = "FOURS (4)", color = Color(0xFF2196F3))
                LegendItem(label = "SIXES (6)", color = Color(0xFFFF9800))
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        ) { }
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, color = Color.White, fontSize = 11.sp)
    }
}
