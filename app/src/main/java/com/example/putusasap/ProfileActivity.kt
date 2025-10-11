package com.example.putusasap

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.tasks.await

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileScreen()
        }
    }
}

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid
    val user = auth.currentUser

    var totalSaving by remember { mutableStateOf(0f) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var joinDate by remember { mutableStateOf("Tidak diketahui") }

    // 🔹 Ambil tanggal registrasi dari FirebaseAuth
    LaunchedEffect(user) {
        user?.let {
            val creationTime = it.metadata?.creationTimestamp
            if (creationTime != null) {
                val dateFormat = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale("id", "ID"))
                joinDate = dateFormat.format(java.util.Date(creationTime))
            }
        }
    }

    // 🔹 Ambil name & email dari Firestore
    LaunchedEffect(uid) {
        if (uid != null) {
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            try {
                val userDoc = db.collection("users").document(uid).get().await()
                name = userDoc.getString("name") ?: "Nama Tidak Diketahui"
                email = userDoc.getString("email") ?: "Email Tidak Diketahui"
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 🔹 Ambil total saving
    LaunchedEffect(uid) {
        if (uid != null) {
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            try {
                val userDoc = db.collection("users").document(uid).get().await()
                val cigarettePrice = userDoc.getDouble("cigarettePrice") ?: 0.0
                val sticksPerDay = userDoc.getDouble("sticksPerDay") ?: 0.0
                val sticksPerPack = userDoc.getDouble("sticksPerPack") ?: 1.0
                val pricePerStick = cigarettePrice / sticksPerPack

                val misiDocs = db.collection("misi").whereEqualTo("uid", uid).get().await()
                var savingAcc = 0.0
                for (doc in misiDocs.documents) {
                    val misiRokok = doc.getBoolean("misi_rokok") ?: false
                    if (misiRokok) {
                        val konsumsi = doc.getDouble("konsumsi_rokok") ?: sticksPerDay
                        if (konsumsi < sticksPerDay) {
                            savingAcc += (sticksPerDay - konsumsi) * pricePerStick
                        }
                    }
                }

                totalSaving = savingAcc.toFloat()
                db.collection("users").document(uid).update("totalSaving", savingAcc)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBarProfile() }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 🔹 Header Background
            Image(
                painter = painterResource(id = R.drawable.bg_header_profile),
                contentDescription = "Header",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 160.dp) // posisi konten mulai setelah header
            ) {
                // 🔹 Foto Profil Overlap
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_account),
                        contentDescription = "Profile",
                        modifier = Modifier.size(100.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = email,
                    color = Color(0xFFB97169),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .background(Color(0xFFFFE6E3), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 🔹 Box Informasi Akun
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFFE6E3))
                ) {
                    ProfileMenuItem(
                        icon = R.drawable.ic_edit,
                        title = "Ubah Password"
                    ) {
                        auth.currentUser?.email?.let { email ->
                            auth.sendPasswordResetEmail(email)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Email ubah password telah dikirim ke $email",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        context,
                                        "Gagal mengirim email: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }

                    Divider(color = Color.Gray.copy(alpha = 0.2f))

                    ProfileMenuItem(
                        icon = R.drawable.ic_money,
                        title = "Total Penghematan Uang",
                        subtitle = "Rp ${"%,.0f".format(totalSaving)}",
                        showArrow = false,
                        onClick = {}
                    )

                    Divider(color = Color.Gray.copy(alpha = 0.2f))

                    ProfileMenuItem(
                        icon = R.drawable.ic_calendar,
                        title = "Tanggal Bergabung",
                        subtitle = joinDate,
                        showArrow = false,
                        onClick = {}
                    )

                    Divider(color = Color.Gray.copy(alpha = 0.2f))

                    ProfileMenuItem(
                        icon = R.drawable.ic_logout,
                        title = "Keluar",
                        titleColor = Color(0xFFC15F56)
                    ) {
                        auth.signOut()
                        Toast.makeText(context, "Berhasil keluar", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                }
            }

            // 🔹 Teks "Profil" di atas header
            Text(
                text = "Profil",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp)
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: Int,
    title: String,
    subtitle: String? = null,
    titleColor: Color = Color.Black,
    showArrow: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = showArrow) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            modifier = Modifier.size(20.dp),
            tint = if (titleColor != Color.Black) titleColor else Color.Black
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium, color = titleColor)
            if (subtitle != null) {
                Text(subtitle, fontSize = 12.sp, color = Color.DarkGray)
            }
        }
        if (showArrow) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "Next",
                tint = if (titleColor != Color.Black) titleColor else Color.Black
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: Int,
    title: String,
    subtitle: String? = null,
    titleColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            modifier = Modifier.size(20.dp),
            tint = if (titleColor != Color.Black) titleColor else Color.Black
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium, color = titleColor)
            if (subtitle != null) {
                Text(subtitle, fontSize = 12.sp, color = Color.DarkGray)
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = "Next",
            tint = if (titleColor != Color.Black) titleColor else Color.Black
        )
    }
}

@Composable
fun BottomNavigationBarProfile() {
    var selectedIndex by remember { mutableStateOf(2) } // default = Riwayat
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