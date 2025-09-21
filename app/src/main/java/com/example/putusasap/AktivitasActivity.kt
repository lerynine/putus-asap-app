package com.example.putusasap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class AktivitasActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AktivitasScreen(onBackClick = { finish() })
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AktivitasScreen(onBackClick: () -> Unit) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFC5C0), Color(0xFFFFF5F5))
    )

    var isStarted by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(30 * 60) } // 30 menit = 1800 detik
    var isLoading by remember { mutableStateOf(true) }

    // ✅ Cek Firestore apakah sudah selesai
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        if (uid != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = dateFormat.format(Date())

            val snapshot = db.collection("misi")
                .whereEqualTo("uid", uid)
                .whereEqualTo("tanggal", today)
                .limit(1)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val doc = snapshot.documents[0]
                val aktivitasFisik = doc.getBoolean("aktivitas_fisik") ?: false
                if (aktivitasFisik) {
                    isFinished = true   // langsung selesai
                }
            }
        }
        isLoading = false
    }

    // Timer Coroutine
    LaunchedEffect(isStarted, isPaused) {
        if (isStarted && !isPaused && !isFinished) {
            while (timeLeft > 0 && !isPaused && !isFinished) {
                delay(1000L)
                timeLeft--
            }
            if (timeLeft == 0 && !isFinished) {
                // Timer habis otomatis simpan
                saveAktivitasToFirestore()
                isFinished = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
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
                text = "Lakukan Aktivitas Fisik 30 Menit",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedContent(
                targetState = when {
                    isFinished -> "finished"
                    isStarted -> "timer"
                    else -> "start"
                },
                transitionSpec = { fadeIn(tween(500)) with fadeOut(tween(500)) },
                modifier = Modifier.fillMaxWidth()
            ) { state ->
                when (state) {
                    "start" -> {
                        // Card Awal
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 12.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    ambientColor = Color.Black.copy(alpha = 0.08f),
                                    spotColor = Color.Black.copy(alpha = 0.12f)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Luangkan waktu 30 menit untuk aktivitas fisik hari ini. Lakukan aktivitas fisik seperti jalan cepat, bersepeda, berenang atau senam.",
                                    style = TextStyle(fontSize = 14.sp, color = Color.Black),
                                    modifier = Modifier.padding(bottom = 24.dp)
                                )

                                Button(
                                    onClick = { isStarted = true },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFC15F56)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth(0.5f)
                                        .height(45.dp)
                                ) {
                                    Text("Mulai", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    "timer" -> {
                        // Card Timer
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                                .shadow(
                                    elevation = 12.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    ambientColor = Color.Black.copy(alpha = 0.08f),
                                    spotColor = Color.Black.copy(alpha = 0.12f)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Lingkaran Timer
                                Box(contentAlignment = Alignment.Center) {
                                    val progress = timeLeft / (30f * 60f)
                                    Canvas(modifier = Modifier.size(200.dp)) {
                                        drawArc(
                                            color = Color.LightGray.copy(alpha = 0.3f),
                                            startAngle = -90f,
                                            sweepAngle = 360f,
                                            useCenter = false,
                                            style = Stroke(width = 12f)
                                        )
                                        drawArc(
                                            color = Color(0xFFC15F56),
                                            startAngle = -90f,
                                            sweepAngle = 360 * progress,
                                            useCenter = false,
                                            style = Stroke(width = 12f, cap = StrokeCap.Round)
                                        )
                                    }

                                    val minutes = timeLeft / 60
                                    val seconds = timeLeft % 60
                                    Text(
                                        text = String.format("%02d:%02d", minutes, seconds),
                                        style = TextStyle(
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    OutlinedButton(
                                        onClick = { isPaused = !isPaused },
                                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(if (isPaused) "Lanjut" else "Pause")
                                    }

                                    Button(
                                        onClick = {
                                            saveAktivitasToFirestore()
                                            isFinished = true
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFC15F56)
                                        ),
                                        modifier = Modifier.weight(1f).padding(start = 8.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Selesai", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                    "finished" -> {
                        // Card Selesai
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 12.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    ambientColor = Color.Black.copy(alpha = 0.08f),
                                    spotColor = Color.Black.copy(alpha = 0.12f)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "🎉 Selamat!",
                                    style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Aktivitas fisik hari ini selesai.\nAyo lanjutkan esok hari 💪",
                                    style = TextStyle(fontSize = 16.sp),
                                    modifier = Modifier.padding(bottom = 24.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )

                                Button(
                                    onClick = { onBackClick() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFC15F56)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Tutup", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


fun saveAktivitasToFirestore() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: return   // kalau belum login, langsung keluar

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    val data = hashMapOf(
        "uid" to uid,
        "tanggal" to today,
        "aktivitas_fisik" to true
    )

    db.collection("misi")
        .whereEqualTo("uid", uid)
        .whereEqualTo("tanggal", today)
        .limit(1)   // cukup satu saja
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // kalau dokumen sudah ada, update dokumen pertama
                val docId = querySnapshot.documents[0].id
                db.collection("misi")
                    .document(docId)
                    .set(data, SetOptions.merge()) // merge biar field lain tidak hilang
                    .addOnSuccessListener {
                        // sukses update
                    }
                    .addOnFailureListener {
                        // gagal update
                    }
            } else {
                // kalau belum ada, buat baru
                db.collection("misi")
                    .add(data)
                    .addOnSuccessListener {
                        // sukses tambah
                    }
                    .addOnFailureListener {
                        // gagal tambah
                    }
            }
        }
        .addOnFailureListener {
            // gagal query
        }
}
