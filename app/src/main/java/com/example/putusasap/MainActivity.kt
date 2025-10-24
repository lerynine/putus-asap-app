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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.compose.ui.platform.LocalLifecycleOwner

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PutusAsapTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val uid = auth.currentUser?.uid

    var userName by remember { mutableStateOf<String?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var refreshTrigger by remember { mutableStateOf(0) }

// 🔁 Jalankan saat Activity kembali ke foreground
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshTrigger++ // trigger naik setiap kali kembali ke layar ini
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


// 🔹 Ambil nama user dari Firestore
    LaunchedEffect(uid, refreshTrigger) {
        if (uid != null) {
            try {
                val userDoc = db.collection("users").document(uid).get().await()
                userName = userDoc.getString("name") ?: "Pengguna"
            } catch (e: Exception) {
                userName = "Pengguna"
            }
        } else {
            userName = "Pengguna"
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar() },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
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
                Column(horizontalAlignment = Alignment.Start) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_home),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(35.dp)
                            .width(110.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Halo, ${userName ?: "..." }",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC15F56)
                    )
                }
                NotificationWithBadge()
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

                        DailyMissionProgressScreen(refreshTrigger)


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
                            InfoMiniCard(
                                title = "Deteksi Risiko Penyakit",
                                icon = R.drawable.ic_health,
                                onClick = {
                                    context.startActivity(
                                        Intent(
                                            context,
                                            com.example.putusasap.com.example.putusasap.FormDeteksiActivity::class.java
                                        )
                                    )
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

            Text("Informasi:", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            InfoImage(
                image = R.drawable.img_info1,
                activity = ArtikelParuActivity::class.java
            )
            InfoImage(
                image = R.drawable.img_info3,
                activity = ArtikelPpokActivity::class.java
            )
            InfoImage(
                image = R.drawable.img_info2,
                activity = ArtikelJantungActivity::class.java
            )
        }
    }
}

@Composable
fun InfoImage(image: Int, activity: Class<*>) {
    val context = LocalContext.current

    Image(
        painter = painterResource(id = image),
        contentDescription = "Info",
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp) // tinggi tetap
            .clickable {
                val intent = Intent(context, activity)
                context.startActivity(intent)
            },
        contentScale = ContentScale.Fit // ubah dari Fit ke Crop
    )
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
fun DailyMissionProgressScreen(refreshTrigger: Int) {
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: return
    val db = FirebaseFirestore.getInstance()

    var progress by remember { mutableStateOf(0f) }
    var isLoading by remember { mutableStateOf(true) }

    // ✅ Ambil data ulang setiap kali refreshTrigger berubah
    LaunchedEffect(refreshTrigger) {
        isLoading = true
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())

        val snapshot = db.collection("misi")
            .whereEqualTo("uid", uid)
            .whereEqualTo("tanggal", today)
            .get()
            .await()

        if (snapshot.isEmpty) {
            progress = 0f
        } else {
            val doc = snapshot.documents[0]
            val fields = listOf("misi_rokok", "aktivitas_fisik", "tidur", "air")
            val completed = fields.count { doc.getBoolean(it) == true }
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
            progress = 1f, // selalu full
            strokeWidth = 12.dp,
            color = Color.LightGray.copy(alpha = 0.3f), // warna bayangan
            modifier = Modifier.size(150.dp)
        )

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

@Composable
fun NotificationWithBadge() {
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    var streak by remember { mutableStateOf(0) }

    LaunchedEffect(uid) {
        if (uid != null) {
            streak = calculateStreak(uid, db)
        }
    }

    Box {
        // Icon notifikasi
        Image(
            painter = painterResource(id = R.drawable.ic_notification),
            contentDescription = "Notifikasi",
            modifier = Modifier.size(35.dp)
        )

        if (streak > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp) // geser biar nempel pojok
                    .size(20.dp)
                    .background(Color.Red, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = streak.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// 🔹 Fungsi hitung streak
suspend fun calculateStreak(uid: String, db: FirebaseFirestore): Int {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var streak = 0

    // Loop mundur dari hari ini
    for (i in 0..30) { // max cek 30 hari ke belakang
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DAY_OF_YEAR, -i)
        val dateStr = dateFormat.format(cal.time)

        val snapshot = db.collection("misi")
            .whereEqualTo("uid", uid)
            .whereEqualTo("tanggal", dateStr)
            .get()
            .await()

        if (snapshot.isEmpty) {
            break
        } else {
            val doc = snapshot.documents[0]
            val allTrue = (doc.getBoolean("aktivitas_fisik") == true &&
                    doc.getBoolean("misi_rokok") == true &&
                    doc.getBoolean("tidur") == true &&
                    doc.getBoolean("air") == true)
            if (allTrue) {
                streak++
            } else {
                break
            }
        }
    }
    return streak
}
