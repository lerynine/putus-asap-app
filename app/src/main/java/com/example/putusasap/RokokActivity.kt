package com.example.putusasap

import android.os.Bundle
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
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
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
    var misiSelesai by remember { mutableStateOf(false) }

    // 🔄 Load data dari Firestore
    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        val result = getBatasHarianDanStatus(uid)
        batasHarian = result.first
        misiSelesai = result.second
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        if (misiSelesai) {
            // ✅ Tampilan selesai
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Misi hari ini selesai 🎉", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBackClick) {
                    Text("Kembali")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Tombol Back
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
                val judul = when {
                    konsumsiHariIni >= batasHarian && batasHarian > 0 ->
                        "Cukup!, konsumsi rokok sudah melebihi batas harian"
                    konsumsiHariIni > 0 ->
                        "Bagus sekali, kamu berhasil mengurangi konsumsi rokok hari ini!"
                    else ->
                        "Hari ini tanpa asap, kamu hebat!"
                }

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
                        OutlinedButton(
                            onClick = { if (konsumsiHariIni > 0) konsumsiHariIni-- },
                            shape = CircleShape,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_minus),
                                contentDescription = "Kurangi",
                                tint = Color(0xFFC15F56)
                            )
                        }

                        Text(
                            text = "$konsumsiHariIni dari $batasHarian",
                            modifier = Modifier.padding(horizontal = 24.dp),
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        )

                        OutlinedButton(
                            onClick = { if (konsumsiHariIni < batasHarian) konsumsiHariIni++ },
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
                            misiSelesai = true
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
}

/**
 * Ambil batas harian + cek status misi hari ini
 */
suspend fun getBatasHarianDanStatus(uid: String): Pair<Int, Boolean> {
    val db = FirebaseFirestore.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    // Cari dokumen hari ini
    val todaySnapshot = db.collection("misi")
        .whereEqualTo("uid", uid)
        .whereEqualTo("tanggal", today)
        .get()
        .await()

    if (!todaySnapshot.isEmpty) {
        val todayDoc = todaySnapshot.documents[0]
        val batas = todayDoc.getLong("batas_harian")?.toInt() ?: 0
        val selesai = todayDoc.getBoolean("misi_rokok") ?: false
        return Pair(batas, selesai)
    }

    // Kalau belum ada dokumen hari ini → hitung dari dokumen terakhir
    val latestSnapshot = db.collection("misi")
        .whereEqualTo("uid", uid)
        .orderBy("tanggal", Query.Direction.DESCENDING)
        .limit(1)
        .get()
        .await()

    val batasHarian = if (!latestSnapshot.isEmpty) {
        val lastDoc = latestSnapshot.documents[0]
        val lastBatas = lastDoc.getLong("batas_harian")?.toInt()
        val lastKonsumsi = lastDoc.getLong("konsumsi_rokok")?.toInt()
        if (lastBatas != null) {
            if (lastKonsumsi != null) {
                if (lastKonsumsi < lastBatas) lastBatas - 1 else lastBatas
            } else lastBatas - 1
        } else {
            val userDoc = db.collection("users").document(uid).get().await()
            (userDoc.getLong("SticksPerDay")?.toInt() ?: 0) - 1
        }
    } else {
        val userDoc = db.collection("users").document(uid).get().await()
        (userDoc.getLong("SticksPerDay")?.toInt() ?: 0) - 1
    }.coerceAtLeast(0)

    // Simpan dokumen hari ini
    val data = mapOf(
        "uid" to uid,
        "tanggal" to today,
        "batas_harian" to batasHarian,
        "misi_rokok" to false
    )
    db.collection("misi").add(data).await()

    return Pair(batasHarian, false)
}

/**
 * Simpan konsumsi hari ini & tandai misi selesai
 */
fun saveKonsumsiHariIni(jumlah: Int) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: return
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    val data = mapOf(
        "uid" to uid,
        "tanggal" to today,
        "konsumsi_rokok" to jumlah,
        "misi_rokok" to true
    )

    db.collection("misi")
        .whereEqualTo("uid", uid)
        .whereEqualTo("tanggal", today)
        .get()
        .addOnSuccessListener { snapshot ->
            if (!snapshot.isEmpty) {
                val docId = snapshot.documents[0].id
                db.collection("misi").document(docId).set(data, SetOptions.merge())
            } else {
                db.collection("misi").add(data)
            }
        }
}
