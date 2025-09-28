package com.example.putusasap

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
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

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 🔹 Load & Simpan konsumsi lokal pakai DataStore
    val dataStore = remember { UserPreferences(context) }
    var konsumsi by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        dataStore.getWater().collect { saved ->
            konsumsi = saved
        }
    }

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
            // 🔙 Tombol back di pojok kiri atas tanpa background
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onBackClick() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Pantauan Konsumsi Air Harian",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Ilustrasi Air lebih besar + turun sedikit
            Box(contentAlignment = Alignment.Center) {
                WaterWave(progress = progress, containerSize = 300.dp)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${konsumsi.toInt()}ml",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0x66000000), shape = MaterialTheme.shapes.medium)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Target: ${target.toInt()}ml", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "${(progress * 100).toInt()}% dari target tercapai",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol tambah air
            Button(
                onClick = {
                    konsumsi += 100f
                    scope.launch { dataStore.saveWater(konsumsi) }

                    // 🔹 Jika sudah mencapai target, simpan ke Firestore otomatis
                    if (konsumsi >= target) {
                        saveMissionToFirestore()
                    }
                },
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5AA3E8))
            ) {
                Text("+ 100ML", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun WaterWave(progress: Float, containerSize: Dp = 220.dp) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    val waveShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveShift"
    )

    Canvas(modifier = Modifier.size(containerSize)) {
        val canvasSize = this.size // ✅ Size dari CanvasScope
        val radius = canvasSize.minDimension / 2
        val center = Offset(canvasSize.width / 2, canvasSize.height / 2)

        val circlePath = Path().apply {
            addOval(Rect(center = center, radius = radius))
        }

        clipPath(circlePath) {
            // background dasar air
            drawRect(
                color = Color(0xFF90CAF9),
                size = canvasSize
            )

            val waterHeight = canvasSize.height * (1 - progress)

            val path = Path().apply {
                moveTo(0f, waterHeight)
                val waveLength = canvasSize.width / 1.5f
                val amplitude = 12f

                for (x in 0..canvasSize.width.toInt()) {
                    val y = (waterHeight + amplitude *
                            sin((x / waveLength) * 2 * Math.PI + waveShift)).toFloat()
                    lineTo(x.toFloat(), y)
                }
                lineTo(canvasSize.width, canvasSize.height)
                lineTo(0f, canvasSize.height)
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

fun saveMissionToFirestore() {
    val firestore = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    if (user == null) {
        Log.e("FirestoreSave", "Gagal simpan: User belum login, uid = null")
        return
    }

    val uid = user.uid
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val docRef = firestore.collection("misi").document("${uid}_$today")

    val data = mapOf(
        "air" to true,
        "tanggal" to today,
        "uid" to uid
    )

    Log.d("FirestoreSave", "Mencoba simpan data: $data ke doc: ${docRef.path}")

    docRef.set(data, SetOptions.merge())
        .addOnSuccessListener {
            Log.d("FirestoreSave", "Berhasil simpan data untuk user $uid")
        }
        .addOnFailureListener { e ->
            Log.e("FirestoreSave", "Gagal simpan data: ${e.message}", e)
        }
}