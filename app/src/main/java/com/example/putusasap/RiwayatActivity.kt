package com.example.putusasap

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class RiwayatActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RiwayatScreen()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RiwayatScreen() {
    val today = LocalDate.now()
    var selesaiDates by remember { mutableStateOf(setOf<LocalDate>()) }

    var riwayat by remember { mutableStateOf(listOf<RiwayatDeteksi>()) }
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid

    // ✅ Ambil data misi untuk kalender (biarkan tetap)
    LaunchedEffect(Unit) {
        val snapshot = FirebaseFirestore.getInstance().collection("misi").get().await()
        val list = snapshot.documents.mapNotNull { doc ->
            val tanggal = doc.getString("tanggal")
            val m1 = doc.getBoolean("tidur") ?: false
            val m2 = doc.getBoolean("misi_rokok") ?: false
            val m3 = doc.getBoolean("aktivitas_fisik") ?: false
            val m4 = doc.getBoolean("air") ?: false

            if (tanggal != null && m1 && m2 && m3 && m4) {
                LocalDate.parse(tanggal, DateTimeFormatter.ISO_DATE)
            } else null
        }
        selesaiDates = list.toSet()
    }

    // ✅ Ambil riwayat deteksi dari Firestore
    LaunchedEffect(Unit) {
        if (uid != null) {
            try {
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("riwayat_deteksi")
                    .whereEqualTo("user_id", uid)
                    .get()
                    .await()

                Log.d("RiwayatScreen", "Jumlah dokumen: ${snapshot.size()}")

                riwayat = snapshot.documents.mapNotNull { doc ->
                    val ts = doc.getTimestamp("created_at")
                    val formattedDate = ts?.toDate()?.let { date ->
                        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")
                        date.toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDateTime()
                            .format(formatter)
                    } ?: "-"

                    val lung = doc.getString("resiko_lung") ?: "-"
                    val asthma = doc.getString("resiko_asthma") ?: "-"
                    val cardio = doc.getString("resiko_cardio") ?: "-"

                    RiwayatDeteksi(
                        tanggal = formattedDate,
                        resikoLung = lung,
                        resikoAsthma = asthma,
                        resikoCardio = cardio
                    )
                }

            } catch (e: Exception) {
                Log.e("RiwayatScreen", "Error ambil riwayat: ${e.message}", e)
            }
        } else {
            Log.w("RiwayatScreen", "UID null, user belum login?")
        }
    }

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val scrollState = rememberScrollState()
    Scaffold(
        bottomBar = { BottomNavigationBarHistory() },
        containerColor = Color.White
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),   // ✅ bikin halaman bisa discroll
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header dengan notifikasi
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Riwayat Misi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC15F56)
                )
                Box(
                    modifier = Modifier
                        .size(75.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_notification),
                        contentDescription = "Notifikasi",
                        tint = Color.Unspecified,
                        modifier = Modifier.fillMaxSize() // isi box
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Header bulan
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Text("<")
                }
                Text(
                    text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("id"))} ${currentMonth.year}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                TextButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Text(">")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nama hari
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab").forEach {
                    Text(text = it, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Grid tanggal
            val firstDay = currentMonth.atDay(1)
            val lastDay = currentMonth.atEndOfMonth()
            val startIndex = firstDay.dayOfWeek.value % 7
            val totalDays = lastDay.dayOfMonth

            Column {
                var day = 1
                for (week in 0..5) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        for (d in 0..6) {
                            if ((week == 0 && d < startIndex) || day > totalDays) {
                                Box(modifier = Modifier.size(40.dp)) // kosong
                            } else {
                                val date = currentMonth.atDay(day)
                                val isToday = date == today
                                val isSelesai = selesaiDates.contains(date)

                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = when {
                                                isSelesai -> Color(0xFF4CAF50)
                                                isToday -> Color(0xFFC15F56)
                                                else -> Color.Transparent
                                            },
                                            shape = CircleShape
                                        )
                                        .border(1.dp, Color.Gray.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        color = if (isSelesai || isToday) Color.White else Color.Black,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                                day++
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Riwayat Deteksi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC15F56),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                // ✅ Tampilkan data riwayat lengkap
                riwayat.forEach { data ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0EE)),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Tanggal: ${data.tanggal}",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("Hasil Deteksi", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Resiko Kanker Paru: ${data.resikoLung}", fontSize = 13.sp)
                            Text("Resiko Asma: ${data.resikoAsthma}", fontSize = 13.sp)
                            Text("Resiko Penyakit Jantung: ${data.resikoCardio}", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBarHistory() {
    var selectedIndex by remember { mutableStateOf(1) } // default = Riwayat
    val context = LocalContext.current

    NavigationBar(containerColor = Color.White) {
        listOf("Home", "Riwayat", "Profile").forEachIndexed { index, label ->
            val isSelected = selectedIndex == index
            val iconRes = when (index) {
                0 -> if (isSelected) R.drawable.ic_home_selected else R.drawable.ic_home_unselected
                1 -> if (isSelected) R.drawable.ic_mission_selected else R.drawable.ic_mission_unselected
                2 -> if (isSelected) R.drawable.ic_profile_selected else R.drawable.ic_profile_unselected
                else -> R.drawable.ic_home_unselected
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    selectedIndex = index
                    when (index) {
                        0 -> context.startActivity(Intent(context, MainActivity::class.java))
                        1 -> context.startActivity(Intent(context, RiwayatActivity::class.java))
                        2 -> context.startActivity(Intent(context, ProfileActivity::class.java))
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = label,
                        tint = if (isSelected) Color.White else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        label,
                        fontSize = 10.sp,
                        color = Color.Gray // ✅ selalu abu-abu
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFC15F56), // background merah saat dipilih
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.Gray,     // ✅ tetap abu-abu walau selected
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

data class RiwayatDeteksi(
    val tanggal: String,
    val resikoLung: String,
    val resikoAsthma: String,
    val resikoCardio: String
)