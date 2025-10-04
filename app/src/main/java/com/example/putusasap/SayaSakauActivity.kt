package com.example.putusasap

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext

class SayaSakauActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SayasakauScreen()
        }
    }
}

@Composable
fun SayasakauScreen() {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFFE4E1), Color(0xFFFFFFFF))
                )
            )
            .padding(16.dp)
    ) {
        // Tombol Back
        IconButton(
            onClick = {
                context.startActivity(Intent(context, MainActivity::class.java))
            },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFFC15F56) // merah
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Judul
            Text(
                text = "Lakukan Teknik\nPernapasan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp),
                lineHeight = 32.sp
            )

            // Gambar karakter
            Image(
                painter = painterResource(id = R.drawable.img_pernapasan), // ganti sesuai asset kamu
                contentDescription = "Ilustrasi Pernapasan",
                modifier = Modifier
                    .height(220.dp)
                    .padding(bottom = 16.dp)
            )

            // Subjudul
            Text(
                text = "Langkah-langkah",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Langkah-langkah
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.Start
            ) {
                StepText("Tarik napas dalam lewat hidung selama 4 detik.")
                StepText("Tahan selama 4 detik.")
                StepText("Hembuskan perlahan lewat mulut selama 6 detik.")
                StepText("Lakukan selama 2 menit")
            }

            Spacer(modifier = Modifier.weight(1f))

            // Tombol Selanjutnya
            Button(
                onClick = { /* TODO: pindah ke step berikutnya */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC15F56), // merah
                    contentColor = Color.White
                )
            ) {
                Text("Selanjutnya", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun StepText(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text("• ", fontSize = 14.sp, color = Color.Black)
        Text(text, fontSize = 14.sp, color = Color.Black)
    }
}
