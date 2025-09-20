package com.example.putusasap

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.putusasap.R
import kotlin.jvm.java


class MisiHarianActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🔹 Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Kembali"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Misi Harian",
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.ic_notification), // ikon paru2 yg kamu siapkan
                contentDescription = "Lung Icon",
                modifier = Modifier.size(75.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val context = LocalContext.current

        Image(
            painter = painterResource(id = R.drawable.misi_aktivitas),
            contentDescription = "Aktivitas Fisik 30 Menit",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .aspectRatio(3f)
                .clickable {
                    context.startActivity(
                        Intent(context, AktivitasActivity::class.java)
                    )
                }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Image(
            painter = painterResource(id = R.drawable.misi_rokok),
            contentDescription = "Pelacakan Konsumsi Rokok Harian",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .aspectRatio(3f)
                .clickable {
                    context.startActivity(
                        Intent(context, RokokActivity::class.java)
                    )
                }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Image(
            painter = painterResource(id = R.drawable.misi_air),
            contentDescription = "Pelacakan Konsumsi Air Harian",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .aspectRatio(3f)
                .clickable { onClickAir() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Image(
            painter = painterResource(id = R.drawable.misi_istirahat),
            contentDescription = "Pemantauan Kualitas Istirahat",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .aspectRatio(3f)
                .clickable { onClickIstirahat() }
        )
    }
}