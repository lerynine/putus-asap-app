package com.example.putusasap

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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

    Scaffold(
        bottomBar = { BottomNavigationBarProfile() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔹 Header background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_header_profile), // gambar hiasanmu
                    contentDescription = "Header",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Profil",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 30.dp)
                )
            }

            // 🔹 Foto profil bulat
            Box(
                modifier = Modifier
                    .offset(y = (-40).dp)
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile_placeholder),
                    contentDescription = "Profile",
                    tint = Color(0xFFBDBDBD),
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🔹 Nama & email
            Text(
                text = "Leryna Ramadhani",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "lerynaramadhani@gmail.com",
                color = Color(0xFFB97169),
                fontSize = 14.sp,
                modifier = Modifier
                    .background(Color(0xFFFFE6E3), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Card menu
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFE6E3))
            ) {
                ProfileMenuItem(
                    icon = R.drawable.ic_edit,
                    title = "Edit Profil"
                ) {
                    // intent ke EditProfileActivity
                }

                Divider(color = Color.Gray.copy(alpha = 0.2f))

                ProfileMenuItem(
                    icon = R.drawable.ic_money,
                    title = "Total Penghematan Uang",
                    subtitle = "Rp.100.000,00"
                ) { }

                Divider(color = Color.Gray.copy(alpha = 0.2f))

                ProfileMenuItem(
                    icon = R.drawable.ic_calendar,
                    title = "Tanggal Bergabung",
                    subtitle = "19 April 2025"
                ) { }

                Divider(color = Color.Gray.copy(alpha = 0.2f))

                ProfileMenuItem(
                    icon = R.drawable.ic_logout,
                    title = "Keluar",
                    titleColor = Color(0xFFC15F56)
                ) {
                    // logout action
                }
            }
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
    var selectedIndex by remember { mutableStateOf(2) } // default ke Profile

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
                onClick = { selectedIndex = index },
                icon = {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        label,
                        fontSize = 10.sp,
                        color = if (isSelected) Color(0xFFC15F56) else Color.Gray
                    )
                }
            )
        }
    }
}
