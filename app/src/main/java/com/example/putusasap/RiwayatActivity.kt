package com.example.putusasap

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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

    // Ambil data Firestore
    // Ambil data Firestore
    LaunchedEffect(Unit) {
        val snapshot = FirebaseFirestore.getInstance().collection("misi").get().await()
        val list = snapshot.documents.mapNotNull { doc ->
            val tanggal = doc.getString("tanggal")
            val uid = doc.getString("uid")

            // misal field true/false di doc
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


    // bulan yang sedang ditampilkan
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Scaffold(
        bottomBar = { BottomNavigationBarHistory() },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
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
                IconButton(onClick = { /* TODO: Notifikasi */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_notification),
                        contentDescription = "Notifikasi",
                        tint = Color.Unspecified, // biar sesuai warna asli icon
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Kata-kata semangat
            Text(
                text = "Tetap konsisten! Satu langkah kecil setiap hari = kemenangan besar 🚀",
                fontSize = 14.sp,
                color = Color(0xFF444444),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        label,
                        fontSize = 10.sp,
                        color = if (isSelected) Color(0xFFC15F56) else Color.Gray
                    )
                }
            )
        }
    }
}
