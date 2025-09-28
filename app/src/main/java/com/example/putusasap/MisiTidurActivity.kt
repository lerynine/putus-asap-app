package com.example.putusasap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class MisiTidurActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MisiTidurScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun MisiTidurScreen(
    onBackClick: () -> Unit
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFC5C0), Color(0xFFFFF5F5)) // 🔴 gradasi merah
    )

    // State
    var step by remember { mutableStateOf(1) }
    var jamTidur by remember { mutableStateOf("") }
    var durasiTidur by remember { mutableStateOf("") }
    var selesaiMsg by remember { mutableStateOf("") }
    var sudahIsiHariIni by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val uid = auth.currentUser?.uid ?: ""
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // 🔄 Cek apakah sudah isi hari ini
    LaunchedEffect(Unit) {
        if (uid.isNotEmpty()) {
            firestore.collection("misi")
                .document(uid + "_" + today)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        sudahIsiHariIni = true
                        selesaiMsg = if (doc.getBoolean("tidur") == true) {
                            "Selamat, kamu sudah memiliki tidur yang cukup ✅"
                        } else {
                            "Tidur kamu belum cukup hari ini 😴"
                        }
                        step = 3
                    }
                }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(16.dp)
    ) {
        when {
            sudahIsiHariIni -> {
                FinishScreen(onBackClick, selesaiMsg)
            }

            step == 1 -> {
                QuestionScreen(
                    question = "Jam berapa kamu tidur tadi malam?",
                    options = listOf("Sebelum jam 10 malam", "Antara jam 10-11 malam", "Setelah jam 11 malam"),
                    onBackClick = onBackClick
                ) { answer ->
                    jamTidur = answer
                    step = 2
                }
            }

            step == 2 -> {
                QuestionScreen(
                    question = "Berapa jam kamu tidur tadi malam?",
                    options = listOf("Kurang dari 8 jam", "Sekitar 8 Jam", "Lebih dari 8 jam"),
                    onBackClick = { step = 1 }
                ) { answer ->
                    durasiTidur = answer

                    val tidurCukup = (jamTidur == "Sebelum jam 10 malam") &&
                            (durasiTidur == "Sekitar 8 Jam" || durasiTidur == "Lebih dari 8 jam")

                    selesaiMsg = if (tidurCukup) {
                        "Selamat, kamu sudah memiliki tidur yang cukup ✅"
                    } else {
                        "Tidur kamu belum cukup hari ini 😴"
                    }

                    // Simpan ke Firestore
                    if (uid.isNotEmpty()) {
                        val data = hashMapOf(
                            "uid" to uid,
                            "tanggal" to today,
                            "tidur" to tidurCukup
                        )
                        firestore.collection("misi")
                            .document(uid + "_" + today)
                            .set(data)
                    }

                    step = 3
                }
            }

            step == 3 -> {
                FinishScreen(onBackClick, selesaiMsg)
            }
        }
    }
}

@Composable
fun QuestionScreen(
    question: String,
    options: List<String>,
    onBackClick: () -> Unit,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔙 Tombol back
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Pertanyaan
        Text(
            text = question,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 🔘 Opsi jawaban
        options.forEach { option ->
            AnswerOption(option) { onSelect(option) }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AnswerOption(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

@Composable
fun FinishScreen(onBackClick: () -> Unit, message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        AnswerOption("Kembali") { onBackClick() }
    }
}
