package com.example.putusasap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.putusasap.R

class HasilDeteksiActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ambil hasil dari Intent
        val resikoLung = intent.getStringExtra("resiko_lung")
        val resikoAsthma = intent.getStringExtra("resiko_asthma")
        val resikoCardio = intent.getStringExtra("resiko_cardio")

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFFFF1F1)) {
                    HasilDeteksiScreen(
                        resikoLung = resikoLung,
                        resikoAsthma = resikoAsthma,
                        resikoCardio = resikoCardio
                    )
                }
            }
        }
    }
}

@Composable
fun HasilDeteksiScreen(
    resikoLung: String?,
    resikoAsthma: String?,
    resikoCardio: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tingkat Risiko Penyakit",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Card Risiko Kanker Paru-Paru
        resikoLung?.let {
            RisikoCard(
                title = "Risiko Kanker Paru Paru",
                level = it,
                description = when (it) {
                    "Rendah" -> "Tetap jaga pola hidup sehat dan lanjutkan misi harian!"
                    "Sedang" -> "Ikuti misi harian dan pertimbangkan pemeriksaan dini."
                    else -> "Segera ikuti misi harian dan periksakan diri ke fasilitas kesehatan!"
                }
            )
        }

        // Card Risiko Penyakit Paru Obstruktif Kronik (Asthma)
        resikoAsthma?.let {
            RisikoCard(
                title = "Risiko Penyakit Paru Obstruktif Kronik",
                level = it,
                description = when (it) {
                    "Rendah" -> "Tetap jaga pola hidup sehat dan lanjutkan misi harian!"
                    "Sedang" -> "Ikuti misi harian dan pertimbangkan pemeriksaan dini."
                    else -> "Segera ikuti misi harian dan periksakan diri ke fasilitas kesehatan!"
                }
            )
        }

        // Card Risiko Penyakit Kardiovaskular
        resikoCardio?.let {
            RisikoCard(
                title = "Risiko Penyakit Kardiovaskular",
                level = it,
                description = when (it) {
                    "Rendah" -> "Tetap jaga pola hidup sehat dan lanjutkan misi harian!"
                    "Sedang" -> "Ikuti misi harian dan pertimbangkan pemeriksaan dini."
                    else -> "Segera ikuti misi harian dan periksakan diri ke fasilitas kesehatan!"
                }
            )
        }
    }
}

@Composable
fun RisikoCard(title: String, level: String, description: String) {
    val iconRes = when (level) {
        "Rendah" -> R.drawable.ic_smile_green
        "Sedang" -> R.drawable.ic_neutral_yellow
        else -> R.drawable.ic_sad_red
    }

    val bgColor = Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "$title: $level",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
