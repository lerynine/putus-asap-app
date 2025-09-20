package com.example.putusasap

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
            MaterialTheme {
                Surface {
                    RiwayatScreen()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RiwayatScreen() {
    val today = LocalDate.now()
    var selesaiDates by remember { mutableStateOf(setOf<LocalDate>()) }

    // Ambil data Firestore
    LaunchedEffect(Unit) {
        val snapshot = FirebaseFirestore.getInstance().collection("misi").get().await()
        val list = snapshot.documents.mapNotNull { doc ->
            val tanggal = doc.getString("tanggal")
            val status = doc.getString("status")
            if (tanggal != null && status == "selesai") {
                LocalDate.parse(tanggal, DateTimeFormatter.ISO_DATE)
            } else null
        }
        selesaiDates = list.toSet()
    }

    // bulan yang sedang ditampilkan
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                fontWeight = FontWeight.Bold
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
                                            isToday -> Color.LightGray
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
