package com.example.putusasap

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ArtikelJantungActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtikelJantungScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtikelJantungScreen() {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    val tabs = listOf("Tentang", "Gejala", "Faktor Risiko")

    Column(modifier = Modifier.fillMaxSize()) {
        // 🔹 Header merah (judul + gambar kanan)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFC15F56))
                .padding(top = 24.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            // Tombol back
            IconButton(
                onClick = { (context as? ComponentActivity)?.finish() },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Judul + gambar di kanan
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mengenal\nPenyakit Kardiovaskular",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp) // 🔹 padding kiri tambahan
                )

                Spacer(modifier = Modifier.width(8.dp))

                Image(
                    painter = painterResource(id = R.drawable.img_jantung),
                    contentDescription = "Penyakit Jantung",
                    modifier = Modifier
                        .size(130.dp)
                        .padding(end = 8.dp)
                )
            }
        }

        // 🔹 Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = Color(0xFFC15F56),
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) Color.White else Color(0xFFC15F56),
                            modifier = if (selectedTab == index) {
                                Modifier
                                    .background(
                                        Color(0xFFC15F56),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            } else Modifier
                        )
                    }
                )
            }
        }

        // 🔹 Isi Artikel (background putih)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            when (selectedTab) {
                0 -> TentangJantungSection()
                1 -> GejalaJantungSection()
                2 -> RisikoJantungSection()
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🔹 Sumber
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.who.int/news-room/fact-sheets/detail/cardiovascular-diseases-(cvds)")
                        )
                        context.startActivity(intent)
                    }
                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp) // 🔹 Tambah paddingStart
            ) {
                Text(
                    text = "Sumber: ",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "World Health Organization (WHO), 2024",
                    color = Color(0xFF1A73E8),
                    fontSize = 14.sp,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}

@Composable
fun TentangJantungSection() {
    Text(
        text = "Penyakit kardiovaskular adalah sekumpulan gangguan yang memengaruhi jantung dan pembuluh darah. Penyakit ini mencakup berbagai kondisi, antara lain penyakit jantung koroner, penyakit serebrovaskular, penyakit arteri perifer, penyakit jantung rematik, penyakit jantung bawaan, serta trombosis vena dalam dan emboli paru.\n\n" +
                "Serangan jantung dan stroke biasanya merupakan kejadian akut yang terjadi akibat penyumbatan aliran darah ke jantung atau otak, seringkali karena penumpukan lemak di dinding pembuluh darah.\n\n" +
                "Penyakit kardiovaskular umumnya disebabkan oleh kombinasi faktor risiko yang dapat memicu tekanan darah tinggi, kadar gula darah dan kolesterol yang tinggi, serta obesitas. Kondisi ini secara perlahan merusak dinding pembuluh darah dan memperbesar risiko serangan jantung atau stroke.",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(horizontal = 16.dp) // 🔹 Tambah padding kiri-kanan
    )
}

@Composable
fun GejalaJantungSection() {
    val gejala = listOf(
        "Nyeri dada atau rasa tidak nyaman di dada",
        "Sesak napas",
        "Kelelahan yang tidak biasa",
        "Detak jantung tidak teratur",
        "Pembengkakan di kaki, pergelangan kaki, atau perut",
        "Pusing atau pingsan mendadak",
        "Nyeri di leher, rahang, bahu, punggung, atau lengan",
        "Mati rasa atau lemah pada wajah, lengan, atau kaki"
    )
    Column(modifier = Modifier.padding(horizontal = 16.dp)) { // 🔹 Tambah padding horizontal
        gejala.forEach {
            Text("• $it", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 2.dp))
        }
    }
}

@Composable
fun RisikoJantungSection() {
    val risiko = listOf(
        "Merokok",
        "Pola makan tidak sehat (tinggi lemak jenuh, garam)",
        "Kurang aktivitas fisik",
        "Konsumsi alkohol berlebihan",
        "Tekanan darah tinggi",
        "Kadar kolesterol tinggi",
        "Diabetes atau kadar gula darah tinggi",
        "Kelebihan berat badan atau obesitas",
        "Stres kronis",
        "Riwayat keluarga dengan penyakit jantung",
        "Usia lanjut",
        "Paparan polusi udara dalam jangka panjang"
    )
    Column(modifier = Modifier.padding(horizontal = 16.dp)) { // 🔹 Tambah padding horizontal
        risiko.forEach {
            Text("• $it", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 2.dp))
        }
    }
}