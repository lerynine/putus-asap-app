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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
    var step by remember { mutableStateOf(1) }
    var jamTidur by remember { mutableStateOf("") }
    var durasiTidur by remember { mutableStateOf("") }
    var tidurCukup by remember { mutableStateOf<Boolean?>(null) }
    var sudahIsiHariIni by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val uid = auth.currentUser?.uid ?: ""
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // ✅ Cek apakah sudah isi hari ini
    LaunchedEffect(Unit) {
        if (uid.isNotEmpty()) {
            firestore.collection("misi")
                .whereEqualTo("uid", uid)
                .whereEqualTo("tanggal", today)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val doc = snapshot.documents[0]
                        if (doc.contains("tidur")) {
                            sudahIsiHariIni = true
                            tidurCukup = doc.getBoolean("tidur")
                            step = 3
                        }
                    }
                }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // 🔹 putih polos
            .padding(16.dp)
    ) {
        when {
            sudahIsiHariIni -> {
                FinishScreen(onBackClick, tidurCukup == true)
            }

            step == 1 -> {
                QuestionScreen(
                    question = "Jam berapa kamu tidur tadi malam?",
                    options = listOf(
                        "Sebelum jam 10 malam",
                        "Antara jam 10-11 malam",
                        "Setelah jam 11 malam"
                    ),
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

                    val hasil = (jamTidur == "Sebelum jam 10 malam") &&
                            (durasiTidur == "Sekitar 8 Jam" || durasiTidur == "Lebih dari 8 jam")
                    tidurCukup = hasil

                    // ✅ Simpan ke Firestore tanpa duplikat
                    if (uid.isNotEmpty()) {
                        val data = hashMapOf(
                            "uid" to uid,
                            "tanggal" to today,
                            "tidur" to hasil
                        )

                        firestore.collection("misi")
                            .whereEqualTo("uid", uid)
                            .whereEqualTo("tanggal", today)
                            .get()
                            .addOnSuccessListener { snapshot ->
                                if (!snapshot.isEmpty) {
                                    val docId = snapshot.documents[0].id
                                    firestore.collection("misi").document(docId)
                                        .set(data, SetOptions.merge())
                                } else {
                                    firestore.collection("misi").add(data)
                                }
                            }
                    }

                    step = 3
                }
            }

            step == 3 -> {
                FinishScreen(onBackClick, tidurCukup == true)
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // 🔙 Tombol back di pojok kiri atas
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable { onBackClick() }
                .align(Alignment.TopStart),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        // Isi pertanyaan dan opsi di tengah
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = question,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            options.forEach { option ->
                AnswerOption(option) { onSelect(option) }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Composable
fun AnswerOption(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5)) // sedikit abu biar tombolnya beda
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
fun FinishScreen(onBackClick: () -> Unit, tidurCukup: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // 🔹 putih polos
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable { onBackClick() }
                .align(Alignment.TopStart),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val imageRes = if (tidurCukup)
                R.drawable.tidur_cukup
            else
                R.drawable.tidur_tidak_cukup

            androidx.compose.foundation.Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Hasil Tidur",
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        }
    }
}
