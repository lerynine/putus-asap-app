package com.example.putusasap

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.putusasap.ui.theme.PutusAsapTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SmokeInfoActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setContent {
            PutusAsapTheme {
                SmokeInfoScreen(
                    onSubmitClick = { price, sticksPerPack, sticksPerDay ->
                        saveSmokeInfo(price, sticksPerPack, sticksPerDay)
                    }
                )
            }
        }
    }

    private fun saveSmokeInfo(price: String, sticksPerPack: String, sticksPerDay: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ Konversi String ke Number (Double / Long)
        val cigarettePrice = price.toDoubleOrNull() ?: 0.0
        val sticksPack = sticksPerPack.toIntOrNull() ?: 0
        val sticksDay = sticksPerDay.toIntOrNull() ?: 0

        val updates = mapOf(
            "cigarettePrice" to cigarettePrice, // disimpan sebagai Number
            "sticksPerPack" to sticksPack,      // disimpan sebagai Number
            "sticksPerDay" to sticksDay         // disimpan sebagai Number
        )

        firestore.collection("users")
            .document(userId)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, WelcomeScreenActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal simpan data: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}

private val GradientRed = Brush.verticalGradient(
    colors = listOf(Color(0xFFFFDCDC), Color(0xFFFDF8F8))
)

@Composable
fun SmokeInfoScreen(onSubmitClick: (String, String, String) -> Unit) {
    var price by remember { mutableStateOf("") }
    var sticksPerPack by remember { mutableStateOf("") }
    var sticksPerDay by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientRed)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(24.dp))

            // Pertanyaan 1
            Text(
                text = "Berapa harga rokok yang biasanya kamu beli?",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 16.sp
            )
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                placeholder = { Text("Contoh: 20.000") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // Pertanyaan 2
            Text(
                text = "Berapa jumlah batang rokok dalam satu pack yang kamu beli?",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 16.sp
            )
            OutlinedTextField(
                value = sticksPerPack,
                onValueChange = { sticksPerPack = it },
                placeholder = { Text("Contoh: 16") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // Pertanyaan 3
            Text(
                text = "Berapa batang rokok yang kamu konsumsi setiap hari?",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 16.sp
            )
            OutlinedTextField(
                value = sticksPerDay,
                onValueChange = { sticksPerDay = it },
                placeholder = { Text("Contoh: 10") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            Spacer(Modifier.height(32.dp))

            // Tombol
            Button(
                onClick = {
                    if (price.isNotBlank() && sticksPerPack.isNotBlank() && sticksPerDay.isNotBlank()) {
                        onSubmitClick(price, sticksPerPack, sticksPerDay)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFC15F56), Color(0xFFD77467))
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Selanjutnya", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
