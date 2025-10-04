package com.example.putusasap

import android.content.Intent
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.putusasap.ui.theme.PutusAsapTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PutusAsapTheme {
                MainScreen(userName = "Leryna")
            }
        }
    }
}

@Composable
fun MainScreen(userName: String) {
    val context = LocalContext.current
    Scaffold(
        bottomBar = { BottomNavigationBar() },
        containerColor = Color.White // ✅ pastikan scaffold sendiri juga putih
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // ✅ biar tidak ikut tema
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_home),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(35.dp)   // ✅ lebih panjang ke bawah
                            .width(110.dp)    // ✅ lebih lebar ke samping (kalau perlu)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Hello, $userName",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC15F56) // ✅ pakai warna merah khas aplikasi
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.ic_notification),
                    contentDescription = "Notifikasi",
                    modifier = Modifier.size(75.dp) // sedikit lebih besar
                )
            }

            Spacer(Modifier.height(16.dp))

            // Card Misi Harian
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(470.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.bg_card),
                        contentDescription = "Card Background",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Ayo selesaikan misi harian!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(16.dp))

                        DailyMissionProgressScreen()

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val intent = Intent(context, MisiHarianActivity::class.java)
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFC15F56)
                            )
                        ) {
                            Text("Kerjakan sekarang", color = Color.White)
                        }

                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // di MainScreen
                            InfoMiniCard(
                                title = "Deteksi Risiko Penyakit",
                                icon = R.drawable.ic_health,
                                onClick = {
                                    context.startActivity(Intent(context, com.example.putusasap.com.example.putusasap.FormDeteksiActivity::class.java))
                                }
                            )

                            InfoMiniCard(
                                title = "Saya Sakau!",
                                icon = R.drawable.ic_sakau,
                                onClick = {
                                    val intent = Intent(context, SayaSakauActivity::class.java)
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Informasi
            Text("Informasi:", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            // ✅ Satu gambar satu row, lebar penuh, background putih
            InfoImage(image = R.drawable.img_info1)
            InfoImage(image = R.drawable.img_info2)
            InfoImage(image = R.drawable.img_info3)
        }
    }
}

@Composable
fun InfoImage(image: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 0.dp)
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = "Info",
            modifier = Modifier
                .fillMaxWidth()
                .height(155.dp), // tinggi seragam
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ImageButton(image: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(120.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = "Info Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun DailyMissionProgressScreen() {
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: return
    val db = FirebaseFirestore.getInstance()

    var progress by remember { mutableStateOf(0f) }
    var isLoading by remember { mutableStateOf(true) }

    // ✅ Cek misi dari Firestore
    LaunchedEffect(Unit) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())

        val snapshot = db.collection("misi")
            .whereEqualTo("uid", uid)
            .whereEqualTo("tanggal", today)
            .get()
            .await()

        if (snapshot.isEmpty) {
            // Belum ada dokumen hari ini
            progress = 0f
        } else {
            val doc = snapshot.documents[0]

            // daftar field misi
            val fields = listOf("misi_rokok", "misi_aktivitas", "misi_tidur", "misi_air")

            // hitung berapa true
            val completed = fields.count { doc.getBoolean(it) == true }

            // 1 field = 25%
            progress = completed * 0.25f
        }
        isLoading = false
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFC15F56))
        }
    } else {
        DailyMissionProgress(progress)
    }
}

@Composable
fun DailyMissionProgress(progress: Float) {
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = progress,
            strokeWidth = 12.dp,
            color = Color(0xFFC15F56),
            modifier = Modifier.size(150.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.img_person_mission),
            contentDescription = "Mission Person",
            modifier = Modifier.size(150.dp)
        )
    }
}

@Composable
fun BottomNavigationBar() {
    var selectedIndex by remember { mutableStateOf(0) }
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
                        tint = if (isSelected) Color.White else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        label,
                        fontSize = 10.sp,
                        color = Color.Gray // ✅ selalu abu-abu
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFC15F56), // background merah saat dipilih
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.Gray,     // ✅ tetap abu-abu walau selected
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

@Composable
fun InfoMiniCard(title: String, icon: Int,  onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent // ✅ transparan
        ),
        elevation = CardDefaults.cardElevation(0.dp) // ✅ hilangkan bayangan
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // 🔥 icon lurus mepet atas
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier.size(120.dp) // ✅ icon besar
            )
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun InfoCard(title: String, image: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = title,
                modifier = Modifier
                    .height(100.dp)
                    .width(120.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}