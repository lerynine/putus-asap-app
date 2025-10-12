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

class ArtikelPpokActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtikelPpokScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtikelPpokScreen() {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    val tabs = listOf("Tentang", "Gejala", "Faktor Risiko")

    Column(modifier = Modifier.fillMaxSize()) {
        // 🔹 Header merah sama seperti Jantung
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFC15F56))
                .padding(top = 24.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
        ) {
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mengenal\nPenyakit PPOK",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Image(
                    painter = painterResource(id = R.drawable.img_ppok),
                    contentDescription = "PPOK",
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
                0 -> TentangPpokSection()
                1 -> GejalaPpokSection()
                2 -> RisikoPpokSection()
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🔹 Sumber
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.alodokter.com/penyakit-paru-obstruktif-kronis")
                        )
                        context.startActivity(intent)
                    }
                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
            ) {
                Text(
                    text = "Referensi: ",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Alodokter (2024)",
                    color = Color(0xFF1A73E8),
                    fontSize = 14.sp,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}

// 🔹 Bagian isi artikel — disesuaikan gaya Jantung
@Composable
fun TentangPpokSection() {
    Text(
        text = "Penyakit Paru Obstruktif Kronik (PPOK) adalah gangguan pernapasan jangka panjang yang ditandai oleh hambatan aliran udara di paru-paru. Dua kondisi utama penyebab PPOK adalah bronkitis kronis dan emfisema.\n\n" +
                "Bronkitis kronis menyebabkan peradangan pada saluran napas dan produksi lendir berlebih, sementara emfisema menyebabkan kerusakan pada kantung udara paru (alveolus) yang menurunkan kemampuan paru menyerap oksigen.\n\n" +
                "Penyebab utama PPOK adalah kebiasaan merokok, baik aktif maupun pasif. Faktor lain meliputi paparan polusi udara, asap industri, debu, serta faktor genetik seperti kekurangan protein alpha-1-antitrypsin.",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
fun GejalaPpokSection() {
    val gejala = listOf(
        "Kesulitan bernapas, terutama saat beraktivitas",
        "Suara mengi atau napas berbunyi",
        "Sesak di dada",
        "Batuk kronis disertai dahak",
        "Perubahan warna bibir atau kuku menjadi kebiruan",
        "Sering mengalami infeksi saluran napas",
        "Kelelahan tanpa sebab jelas",
        "Penurunan berat badan tanpa sebab",
        "Pembengkakan pada kaki atau pergelangan",
        "Linglung atau sulit berkonsentrasi"
    )
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        gejala.forEach {
            Text("• $it", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 2.dp))
        }
    }
}

@Composable
fun RisikoPpokSection() {
    val risiko = listOf(
        "Paparan jangka panjang terhadap asap rokok",
        "Perokok aktif atau pasif",
        "Paparan debu industri atau bahan kimia",
        "Paparan asap dari pembakaran bahan bakar padat",
        "Riwayat asma atau tuberkulosis",
        "Faktor genetik (kekurangan alpha-1-antitrypsin)",
        "Usia lanjut"
    )
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        risiko.forEach {
            Text("• $it", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 2.dp))
        }
    }
}
