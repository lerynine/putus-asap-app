package com.example.putusasap

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextDecoration
import android.net.Uri

class ArtikelParuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtikelParuScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtikelParuScreen() {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()

    val tabs = listOf("Tentang", "Gejala", "Faktor Risiko")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 🔹 Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFC15F56))
                .padding(16.dp)
        ) {
            IconButton(
                onClick = {
                    context.startActivity(Intent(context, MainActivity::class.java))
                },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Mengenal\nKanker Paru-Paru",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Image(
                    painter = painterResource(id = R.drawable.img_paru), // ganti dengan asset paru
                    contentDescription = "Kanker Paru",
                    modifier = Modifier
                        .height(120.dp)
                )
            }
        }

        // 🔹 Tab
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

        // 🔹 Isi Artikel
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> TentangSection()
                1 -> GejalaSection()
                2 -> RisikoSection()
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🔹 Sumber
            Text(
                text = "Sumber: https://www.rspondokindah.co.id/id/news/kanker-paru-gejala-penyebab-penanganan",
                color = Color(0xFF1A73E8),
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.rspondokindah.co.id/id/news/kanker-paru-gejala-penyebab-penanganan"))
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun TentangSection() {
    Column {
        Text(
            text = "Kanker paru-paru adalah kondisi ketika sel-sel dalam jaringan paru tumbuh secara tidak normal dan tidak terkendali. Pertumbuhan sel yang abnormal ini dapat mengganggu fungsi paru sebagai organ utama dalam sistem pernapasan manusia, dan dalam banyak kasus, berujung pada kematian.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Surface(
            color = Color(0xFFFFF3CD),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text(
                text = "⚠️ Menurut WHO lebih dari 80% kasus kanker paru-paru disebabkan oleh kebiasaan merokok",
                color = Color(0xFF856404),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.dp)
            )
        }

        Text(
            text = "❗ Penyebab Utama",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC15F56),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            text = "Paparan zat karsinogenik, terutama yang berasal dari asap rokok. Merokok, baik dalam bentuk rokok konvensional, rokok kretek, maupun rokok elektrik (vape), merupakan faktor risiko terbesar. Kandungan zat berbahaya dalam rokok dapat merusak lapisan paru-paru dan memicu mutasi sel normal menjadi sel kanker.\n\n" +
                    "Pada awalnya, tubuh memang memiliki mekanisme untuk memperbaiki kerusakan akibat paparan racun. Namun, jika paparan tersebut terus berulang atau, kemampuan tubuh untuk memperbaiki kerusakan menurun, hingga akhirnya memicu terbentuknya kanker. Semakin lama dan banyak jumlah rokok yang dihisap, semakin besar pula risiko terjadinya kanker paru-paru. Tidak hanya perokok aktif yang berisiko, tetapi juga perokok pasif. Selain rokok, faktor lain seperti paparan polusi udara dan bekerja atau tinggal di lingkungan yang tercemar bahan kimia beracun juga dapat meningkatkan risiko.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun GejalaSection() {
    Column {
        Text("Meskipun gejala kanker paru-paru pada stadium awal sering kali tidak tampak jelas, berikut ini merupakan beberapa gejala yang seringkali muncul:", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        val gejala = listOf(
            "Sakit kepala terus-menerus.",
            "Berat badan menurun.",
            "Kehilangan selera makan.",
            "Suara menjadi serak.",
            "Perubahan pada bentuk jari, yaitu ujung jari menjadi cembung.",
            "Pembengkakan pada muka atau leher.",
            "Batuk berkelanjutan dan bertambah parah, hingga akhirnya mengalami batuk darah.",
            "Mengalami sesak napas dan rasa nyeri di dada."
        )
        gejala.forEach {
            Text("• $it", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun RisikoSection() {
    Column {
        val risiko = listOf(
            "Merupakan perokok pasif atau sering terpapar asap rokok.",
            "Pernah melakukan radioterapi pada daerah dada untuk kanker lain.",
            "Terpapar dengan zat karsinogenik dalam jumlah tinggi, termasuk tinggal di lingkungan dengan tingkat polusi udara yang tinggi, kadar radon tinggi, maupun bekerja di pabrik asbes.",
            "Memiliki keluarga yang juga menderita kanker paru-paru.",
            "Penyakit seperti PPOK (penyakit paru obstruktif kronis) atau tuberkulosis dapat meningkatkan risiko terkena kanker paru-paru."
        )
        risiko.forEach {
            Text("• $it", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 2.dp))
        }
    }
}
