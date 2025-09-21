package com.example.putusasap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

class AirActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AirScreen(onBackClick = { finish() })
        }
    }
}

@Composable
fun AirScreen(onBackClick: () -> Unit) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFC5C0), Color(0xFFFFF5F5))
    )

    var konsumsi by remember { mutableStateOf(2400f) }
    val target = 2500f

    val progress = (konsumsi / target).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, CircleShape)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Pantauan Konsumsi Air Harian",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Wadah Air
            Box(contentAlignment = Alignment.Center) {
                WaterWave(progress = progress)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${konsumsi.toInt()}ml",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0x66000000), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Target: ${target.toInt()}ml", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "${(progress * 100).toInt()}% dari target tercapai",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { konsumsi += 100f },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5AA3E8))
            ) {
                Text("+ 100ML", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { /* Simpan ke Firestore */ },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC15F56)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Selesai", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun WaterWave(progress: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    val waveShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveShift"
    )

    Canvas(modifier = Modifier.size(220.dp)) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)
        val circlePath = Path().apply {
            addOval(Rect(center = center, radius = radius))
        }

        clipPath(circlePath) {
            drawRect(
                color = Color(0xFF90CAF9),
                size = size
            )

            val waterHeight = size.height * (1 - progress)

            val path = Path().apply {
                moveTo(0f, waterHeight)
                val waveLength = size.width / 1.5f
                val amplitude = 12f

                for (x in 0..size.width.toInt()) {
                    val y = (waterHeight + amplitude * sin((x / waveLength) * 2 * Math.PI + waveShift)).toFloat()
                    lineTo(x.toFloat(), y)
                }
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF64B5F6), Color(0xFF1976D2))
                )
            )
        }
    }
}
