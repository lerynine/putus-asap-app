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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // tambahkan di atas dalam composable

    var konsumsiHariIni by remember { mutableStateOf(0) }
    var batasHarian by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var misiSelesai by remember { mutableStateOf(false) }

    // 🔄 Load data dari Firestore
    LaunchedEffect(Unit) {
        // Ambil cache lokal dulu
        konsumsiHariIni = RokokPreferences.loadKonsumsi(context)

        // Lalu ambil data batas harian dari Firestore
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
                Text(
                    "Pengisian konsumsi rokok telah dilakukan, \nkembali lagi besok!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onBackClick,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC15F56)),
                    modifier = Modifier
                        .width(140.dp)
                        .height(45.dp)
                ) {
                    Text(
                        "Tutup",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
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
                    // Counter konsumsi (versi ImageButton)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tombol Kurangi
                        Image(
                            painter = painterResource(id = R.drawable.ic_minus),
                            contentDescription = "Kurangi",
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { if (konsumsiHariIni > 0) konsumsiHariIni-- }
                                .padding(top = 19.dp, start = 8.dp, end = 8.dp)
                        )

                        val isOverLimit = konsumsiHariIni > batasHarian

                        Text(
                            text = "$konsumsiHariIni dari $batasHarian",
                            modifier = Modifier.padding(horizontal = 24.dp),
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOverLimit) Color.Red else Color.Black
                            )
                        )

                        // Tombol Tambah
                        Image(
                            painter = painterResource(id = R.drawable.ic_plus),
                            contentDescription = "Tambah",
                            modifier = Modifier
                                .size(48.dp)
                                .clickable {
                                    konsumsiHariIni++
                                    scope.launch {
                                        RokokPreferences.saveKonsumsi(context, konsumsiHariIni)
                                    }
                                }
                                .padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Tombol Selesai
                    Button(
                        onClick = {
                            saveKonsumsiHariIni(konsumsiHariIni)
                            misiSelesai = true
                            scope.launch {
                                RokokPreferences.saveKonsumsi(context, konsumsiHariIni)
                            }
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
    val TAG = "RokokDebug"
    val db = FirebaseFirestore.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    Log.d(TAG, "=== MULAI getBatasHarianDanStatus ===")
    Log.d(TAG, "UID: $uid | Tanggal: $today")

    try {
        // 🔹 Cari dokumen misi hari ini
        Log.d(TAG, "Mencari dokumen misi hari ini...")
        val todaySnapshot = db.collection("misi")
            .whereEqualTo("uid", uid)
            .whereEqualTo("tanggal", today)
            .get()
            .await()

        if (!todaySnapshot.isEmpty) {
            Log.d(TAG, "✅ Ditemukan dokumen hari ini.")
            val todayDoc = todaySnapshot.documents[0]
            val selesai = todayDoc.getBoolean("misi_rokok") ?: false

            val batas = if (todayDoc.contains("batas_harian")) {
                // Jika field batas_harian ada, langsung gunakan
                todayDoc.getLong("batas_harian")?.toInt() ?: 0
            } else {
                // Jika tidak ada, cari di dokumen sebelumnya
                Log.d(TAG, "⚠️ Field batas_harian tidak ditemukan, mencari dari dokumen sebelumnya...")
                val allDocs = db.collection("misi")
                    .whereEqualTo("uid", uid)
                    .orderBy("tanggal", Query.Direction.DESCENDING)
                    .get()
                    .await()

                var foundBatas: Int? = null
                for (doc in allDocs.documents) {
                    val prevBatas = doc.getLong("batas_harian")?.toInt()
                    if (prevBatas != null && prevBatas > 0) {
                        foundBatas = prevBatas
                        break
                    }
                }

                if (foundBatas != null) {
                    Log.d(TAG, "✅ Ditemukan batas dari dokumen sebelumnya: $foundBatas")
                    // Simpan kembali ke dokumen hari ini agar tidak kosong
                    db.collection("misi").document(todayDoc.id)
                        .set(mapOf("batas_harian" to foundBatas), SetOptions.merge())
                    foundBatas
                } else {
                    // Jika tetap tidak ada, ambil dari users.SticksPerDay
                    Log.d(TAG, "❌ Tidak ditemukan batas di dokumen sebelumnya, ambil dari users.SticksPerDay")
                    val userDoc = db.collection("users").document(uid).get().await()
                    (userDoc.getLong("sticksPerDay")?.toInt() ?: 0)
                }
            }

            Log.d(TAG, "Batas harian: $batas | Misi selesai: $selesai")
            return Pair(batas, selesai)
        }

        // 🔹 Jika tidak ada dokumen hari ini, pakai logika sebelumnya
        Log.d(TAG, "❌ Tidak ada dokumen misi untuk hari ini.")
        Log.d(TAG, "Mencari dokumen terakhir user...")

        val latestSnapshot = db.collection("misi")
            .whereEqualTo("uid", uid)
            .orderBy("tanggal", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()

        val batasHarian = if (!latestSnapshot.isEmpty) {
            Log.d(TAG, "✅ Ditemukan dokumen terakhir sebelumnya.")
            val lastDoc = latestSnapshot.documents[0]
            val lastBatas = lastDoc.getLong("batas_harian")?.toInt()
            val lastKonsumsi = lastDoc.getLong("konsumsi_rokok")?.toInt()
            Log.d(TAG, "Data terakhir → batas: $lastBatas | konsumsi: $lastKonsumsi")

            if (lastBatas != null) {
                if (lastKonsumsi != null) {
                    if (lastKonsumsi < lastBatas) {
                        Log.d(TAG, "Menurunkan batas karena konsumsi < batas")
                        lastBatas - 1
                    } else {
                        Log.d(TAG, "Konsumsi sudah sama/lebih, batas tetap")
                        lastBatas
                    }
                } else {
                    Log.d(TAG, "Konsumsi terakhir null, kurangi batas 1")
                    lastBatas - 1
                }
            } else {
                Log.d(TAG, "Batas harian null, ambil dari users.SticksPerDay")
                val userDoc = db.collection("users").document(uid).get().await()
                (userDoc.getLong("sticksPerDay")?.toInt() ?: 0) - 1
            }
        } else {
            Log.d(TAG, "❌ Tidak ada dokumen sebelumnya, ambil dari users.SticksPerDay")
            val userDoc = db.collection("users").document(uid).get().await()
            (userDoc.getLong("sticksPerDay")?.toInt() ?: 0) - 1
        }.coerceAtLeast(0)

        // 🔹 Simpan dokumen hari ini
        val data = mapOf(
            "uid" to uid,
            "tanggal" to today,
            "batas_harian" to batasHarian,
            "misi_rokok" to false
        )
        db.collection("misi").add(data).await()
        Log.d(TAG, "✅ Dokumen baru untuk hari ini disimpan: batas = $batasHarian")

        Log.d(TAG, "=== SELESAI getBatasHarianDanStatus ===")
        return Pair(batasHarian, false)

    } catch (e: Exception) {
        Log.e(TAG, "❌ Gagal ambil data Firestore: ${e.message}", e)
        return Pair(0, false)
    }
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
