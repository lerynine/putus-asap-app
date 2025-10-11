package com.example.putusasap

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.putusasap.R

class MisiHarianActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // 🔹 Surface putih
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    MisiHarianScreen(
                        onBack = { finish() },
                        onClickAktivitas = {
                            Toast.makeText(this, "Aktivitas Fisik diklik", Toast.LENGTH_SHORT).show()
                        },
                        onClickRokok = {
                            Toast.makeText(this, "Pelacakan Rokok diklik", Toast.LENGTH_SHORT).show()
                        },
                        onClickAir = {
                            Toast.makeText(this, "Pelacakan Air diklik", Toast.LENGTH_SHORT).show()
                        },
                        onClickIstirahat = {
                            Toast.makeText(this, "Pemantauan Istirahat diklik", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MisiHarianScreen(
    onBack: () -> Unit,
    onClickAktivitas: () -> Unit,
    onClickRokok: () -> Unit,
    onClickAir: () -> Unit,
    onClickIstirahat: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // 🔹 background putih
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // 🔹 Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp)
        ) {
            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Misi Harian",
                fontSize = 20.sp,
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.ic_notification),
                contentDescription = "Lung Icon",
                modifier = Modifier.size(35.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Image Buttons Full Width
        val imageModifier = Modifier
            .fillMaxWidth()
            .height(120.dp) // bisa kamu atur sesuai tinggi yang diinginkan
            .clickable { }

        Image(
            painter = painterResource(id = R.drawable.misi_dua),
            contentDescription = "Aktivitas Fisik 30 Menit",
            modifier = imageModifier.clickable {
                context.startActivity(Intent(context, AktivitasActivity::class.java))
            },
            contentScale = ContentScale.FillWidth
        )

        Spacer(modifier = Modifier.height(12.dp))

        Image(
            painter = painterResource(id = R.drawable.misi_satu),
            contentDescription = "Pelacakan Konsumsi Rokok Harian",
            modifier = imageModifier.clickable {
                context.startActivity(Intent(context, RokokActivity::class.java))
            },
            contentScale = ContentScale.FillWidth
        )

        Spacer(modifier = Modifier.height(12.dp))

        Image(
            painter = painterResource(id = R.drawable.misi_tiga),
            contentDescription = "Pelacakan Konsumsi Air Harian",
            modifier = imageModifier.clickable {
                context.startActivity(Intent(context, AirActivity::class.java))
            },
            contentScale = ContentScale.FillWidth
        )

        Spacer(modifier = Modifier.height(12.dp))

        Image(
            painter = painterResource(id = R.drawable.misi_empat),
            contentDescription = "Pemantauan Kualitas Istirahat",
            modifier = imageModifier.clickable {
                context.startActivity(Intent(context, MisiTidurActivity::class.java))
            },
            contentScale = ContentScale.FillWidth
        )
    }
}
