package com.example.putusasap

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class RokokActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RokokScreen(onBackClick = { finish() })
        }
    }
}

@Composable
fun RokokScreen(onBackClick: () -> Unit) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFC5C0), Color(0xFFFFF5F5))
    )

    var konsumsiHariIni by remember { mutableStateOf(0) }
    var batasHarian by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    // Load batas harian dari Firestore
    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        batasHarian = getBatasHarian(uid)
        isLoading = false
    }

    // Tentukan teks judul berdasarkan kondisi
    val judul = when {
        konsumsiHariIni >= batasHarian && batasHarian > 0 ->
            "Cukup!, konsumsi rokok sudah melebihi batas harian"
        konsumsiHariIni > 0 ->
            "Bagus sekali, kamu berhasil mengurangi konsumsi rokok hari ini!"
        else ->
            "Hari ini tanpa asap, kamu hebat!"
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
            // Back Button
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

            // Judul dinamis
            Text(
                text = judul,
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Gambar
            Image(
                painter = painterResource(id = R.drawable.ic_cigarette_box),
                contentDescription = "Cigarette Box",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(350.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                // Counter konsumsi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tombol Minus
                    OutlinedButton(
                        onClick = {
                            if (konsumsiHariIni > 0) konsumsiHariIni--
                        },
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_minus), // ganti dengan nama file drawable kamu
                            contentDescription = "Kurangi",
                            tint = Color(0xFFC15F56)
                        )
                    }

                    Text(
                        text = "$konsumsiHariIni dari $batasHarian",
                        modifier = Modifier.padding(horizontal = 24.dp),
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )

                    // Tombol Plus
                    OutlinedButton(
                        onClick = {
                            if (konsumsiHariIni < batasHarian) konsumsiHariIni++
                        },
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah",
                            tint = Color(0xFFC15F56)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Tombol Selesai
                Button(
                    onClick = {
                        saveKonsumsiHariIni(konsumsiHariIni)
                        onBackClick()
                    },
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
}

/**
 * Ambil batas harian dari Firestore:
 * 1. Cari konsumsi_rokok di misi kemarin
 * 2. Kalau tidak ada, ambil SticksPerDay dari users
 */
suspend fun getBatasHarian(uid: String): Int {
    val db = FirebaseFirestore.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val yesterday = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
    val yesterdayStr = dateFormat.format(yesterday.time)
    Log.d("RokokDebug", "Cek batas harian untuk UID=$uid, tanggal kemarin=$yesterdayStr")

    // Cari di collection misi
    val misiSnapshot = db.collection("misi")
        .whereEqualTo("uid", uid)
        .whereEqualTo("tanggal", yesterdayStr)
        .get()
        .await()

    Log.d("RokokDebug", "Jumlah dokumen misi kemarin: ${misiSnapshot.size()}")

    if (!misiSnapshot.isEmpty) {
        val konsumsiRokok = misiSnapshot.documents[0].getLong("konsumsi_rokok")
        Log.d("RokokDebug", "Field konsumsi_rokok ditemukan: $konsumsiRokok")

        if (konsumsiRokok != null) {
            val result = (konsumsiRokok.toInt() - 1).coerceAtLeast(0)
            Log.d("RokokDebug", "Batas harian dari misi kemarin = $result")
            return result
        }
    }

    // Kalau tidak ada -> ambil dari users
    val userDoc = db.collection("users").document(uid).get().await()
    Log.d("RokokDebug", "UserDoc data: ${userDoc.data}")

    val sticksPerDay = userDoc.getLong("SticksPerDay")?.toInt() ?: 0
    val result = (sticksPerDay - 1).coerceAtLeast(0)
    Log.d("RokokDebug", "Batas harian fallback dari users.SticksPerDay = $result")

    return result
}

fun saveKonsumsiHariIni(jumlah: Int) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: return
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    val data = hashMapOf(
        "uid" to uid,
        "tanggal" to today,
        "konsumsi_rokok" to jumlah
    )

    // Cek dulu apakah sudah ada dokumen dengan uid + tanggal hari ini
    db.collection("misi")
        .whereEqualTo("uid", uid)
        .whereEqualTo("tanggal", today)
        .get()
        .addOnSuccessListener { snapshot ->
            if (!snapshot.isEmpty) {
                // Update dokumen pertama yang ditemukan
                val docId = snapshot.documents[0].id
                db.collection("misi")
                    .document(docId)
                    .update(data as Map<String, Any>)
                    .addOnSuccessListener {
                        // sukses update
                    }
                    .addOnFailureListener {
                        // gagal update
                    }
            } else {
                // Kalau belum ada → buat baru
                db.collection("misi")
                    .add(data)
                    .addOnSuccessListener {
                        // sukses simpan baru
                    }
                    .addOnFailureListener {
                        // gagal simpan baru
                    }
            }
        }
        .addOnFailureListener {
            // gagal query
        }
}
