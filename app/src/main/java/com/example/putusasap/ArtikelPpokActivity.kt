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
        // 🔹 Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFC15F56))
                .padding(16.dp)
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

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Mengenal\nPenyakit PPOK",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Image(
                    painter = painterResource(id = R.drawable.img_ppok), // ganti dengan gambar PPOK
                    contentDescription = "PPOK",
                    modifier = Modifier.height(120.dp)
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

        // 🔹 Isi Artikel
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> TentangPpokSection()
                1 -> GejalaPpokSection()
                2 -> RisikoPpokSection()
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🔹 Sumber
            Text(
                text = "Sumber: https://www.alodokter.com/penyakit-paru-obstruktif-kronis",
                color = Color(0xFF1A73E8),
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.alodokter.com/penyakit-paru-obstruktif-kronis")
                    )
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun TentangPpokSection() {
    Column {
        Text(
            text = "Penyakit Paru Obstruktif Kronik (PPOK) adalah kondisi gangguan pernapasan jangka panjang yang ditandai dengan aliran udara yang terhambat di paru-paru, sehingga menyebabkan kesulitan bernapas. Dua kondisi utama yang paling sering berkembang menjadi PPOK adalah bronkitis kronis dan emfisema.\n\n" +
                    "Pada bronkitis kronis, kerusakan terjadi pada saluran pernapasan atau bronkus yang menyebabkan peradangan dan produksi lendir berlebih. Sementara itu, pada emfisema, kerusakan terjadi pada kantung udara paru-paru atau alveolus, yang menyebabkan berkurangnya kemampuan paru-paru dalam menyerap oksigen.\n\n" +
                    "Kebiasaan merokok, baik sebagai perokok aktif maupun pasif, merupakan penyebab utama PPOK. Selain itu, paparan polusi udara seperti asap kendaraan, debu jalanan, dan emisi industri yang terjadi secara terus-menerus juga dapat memicu PPOK.\n\n" +
                    "Risiko PPOK juga meningkat pada orang yang memiliki riwayat asma, tuberkulosis, atau infeksi HIV. Faktor genetik seperti kekurangan protein alpha-1-antitrypsin (AAt), serta riwayat PPOK dalam keluarga, turut memperbesar kemungkinan seseorang terkena penyakit ini.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun GejalaPpokSection() {
    val gejala = listOf(
        "Mengalami kesulitan bernapas, terutama saat melakukan aktivitas fisik.",
        "Munculnya suara mengi atau napas berbunyi seperti siulan saat menghembuskan napas.",
        "Timbul rasa sesak di dada.",
        "Batuk kronis yang berlangsung lama, disertai dahak berwarna putih, kuning, hingga kehijauan.",
        "Tampak perubahan warna pada bibir atau ujung kuku menjadi kebiruan.",
        "Sering mengalami infeksi saluran pernapasan.",
        "Merasa kelelahan berlebihan meskipun tidak melakukan aktivitas berat.",
        "Mengalami penurunan berat badan drastis tanpa sebab.",
        "Terjadi pembengkakan pada kaki, pergelangan kaki, atau tungkai kaki.",
        "Linglung dan sulit berkonsentrasi."
    )
    Column {
        gejala.forEach {
            Text("• $it", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 2.dp))
        }
    }
}

@Composable
fun RisikoPpokSection() {
    val risiko = listOf(
        "Paparan jangka panjang terhadap asap rokok, baik sebagai perokok aktif maupun perokok pasif.",
        "Kebiasaan merokok pada individu yang memiliki riwayat penyakit asma.",
        "Terpapar debu industri atau bahan kimia berbahaya secara terus-menerus.",
        "Paparan asap dari pembakaran.",
        "Faktor usia lanjut.",
        "Faktor genetik."
    )
    Column {
        risiko.forEach {
            Text("• $it", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 2.dp))
        }
    }
}
