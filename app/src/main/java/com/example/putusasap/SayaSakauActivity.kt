package com.example.putusasap

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class SayaSakauActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SayasakauScreen()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SayasakauScreen(userName: String = "User") {
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(1) }

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
                if (currentStep == 1) {
                    context.startActivity(Intent(context, MainActivity::class.java))
                } else {
                    currentStep = currentStep - 1
                }
            },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFFC15F56)
            )
        }

        // Konten dengan animasi
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn(animationSpec = tween(500)) togetherWith
                        slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(animationSpec = tween(500))
            },
            modifier = Modifier.fillMaxSize()
        ) { step ->
            when (step) {
                1 -> BreathingStep(onNext = { currentStep = 2 })
                2 -> StretchingStep1(onNext = { currentStep = 3 })
                3 -> StretchingStep2(onNext = { currentStep = 4 })
                4 -> StretchingStep3(onNext = { currentStep = 5 })
                5 -> StretchingStep4(onNext = { currentStep = 6 })
                6 -> StretchingStep5(onNext = { currentStep = 7 })
                7 -> StretchingStep6(onNext = { currentStep = 8 })
                8 -> StretchingStep7(onNext = { currentStep = 9 })
                9 -> MinumAir(onNext = { currentStep = 10 })
                10 -> MakanPermen(onNext = { currentStep = 11 })
                11 -> ClosingStep(userName = userName, onFinish = {
                    context.startActivity(Intent(context, MainActivity::class.java))
                })
            }
        }
    }
}

@Composable
fun StretchingStep1(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Lakukan Stretching\nSejenak",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp),
            lineHeight = 32.sp
        )

        Image(
            painter = painterResource(id = R.drawable.img_stretching),
            contentDescription = "Ilustrasi Stretching",
            modifier = Modifier
                .height(220.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Langkah-langkah",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            StepText("Berdiri tegak")
            StepText("Miringkan kepala ke kanan")
            StepText("Tahan 10 detik, lalu ke kiri. Bisa juga tundukkan dagu ke dada, lalu dongak perlahan ke atas.")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC15F56),
                contentColor = Color.White
            )
        ) {
            Text("Selanjutnya", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun StretchingStep2(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Lakukan Stretching\nSejenak",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp),
            lineHeight = 32.sp
        )

        Image(
            painter = painterResource(id = R.drawable.img_stretching), // pakai gambar bahu kalau ada
            contentDescription = "Ilustrasi Stretching Bahu",
            modifier = Modifier
                .height(220.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Langkah-langkah",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            StepText("Angkat bahu ke atas")
            StepText("Putar ke belakang, lalu ke bawah secara perlahan.")
            StepText("Lakukan 5 kali ke belakang, 5 kali ke depan.")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC15F56),
                contentColor = Color.White
            )
        ) {
            Text("Selanjutnya", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun StretchingStep3(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Lakukan Stretching\nSejenak",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp),
            lineHeight = 32.sp
        )

        Image(
            painter = painterResource(id = R.drawable.img_stretching), // ganti sesuai aset
            contentDescription = "Ilustrasi Stretching",
            modifier = Modifier
                .height(220.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Langkah-langkah",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            StepText("Rentangkan satu tangan lurus ke depan, tarik ke arah dada dengan tangan lainnya.")
            StepText("Tahan 10–15 detik, ganti sisi.")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC15F56),
                contentColor = Color.White
            )
        ) {
            Text("Selesai", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun StretchingStep4(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Lakukan Stretching\nSejenak",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp),
            lineHeight = 32.sp
        )

        Image(
            painter = painterResource(id = R.drawable.img_stretching), // ganti sesuai aset
            contentDescription = "Ilustrasi Stretching",
            modifier = Modifier
                .height(220.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Langkah-langkah",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            StepText("Angkat satu tangan ke atas kepala, miringkan tubuh ke sisi berlawanan.")
            StepText("Tahan 10 detik.")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC15F56),
                contentColor = Color.White
            )
        ) {
            Text("Selesai", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun StretchingStep5(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Lakukan Stretching\nSejenak",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp),
            lineHeight = 32.sp
        )

        Image(
            painter = painterResource(id = R.drawable.img_stretching), // ganti sesuai aset
            contentDescription = "Ilustrasi Stretching",
            modifier = Modifier
                .height(220.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Langkah-langkah",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            StepText("Duduk dengan satu kaki lurus ke depan, satu kaki ditekuk.")
            StepText("Condongkan tubuh ke arah kaki yang lurus sambil jaga punggung tetap lurus.")
            StepText("Tahan 15–20 detik.")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC15F56),
                contentColor = Color.White
            )
        ) {
            Text("Selesai", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun StretchingStep6(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Lakukan Stretching\nSejenak",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp),
            lineHeight = 32.sp
        )

        Image(
            painter = painterResource(id = R.drawable.img_stretching), // ganti sesuai aset
            contentDescription = "Ilustrasi Stretching",
            modifier = Modifier
                .height(220.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Langkah-langkah",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            StepText("Berdiri menghadap dinding, satu kaki di depan, satu di belakang.")
            StepText("Tekuk kaki depan, luruskan kaki belakang.")
            StepText("Dorong perlahan ke dinding.")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC15F56),
                contentColor = Color.White
            )
        ) {
            Text("Selesai", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun StretchingStep7(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Lakukan Stretching\nSejenak",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp),
            lineHeight = 32.sp
        )

        Image(
            painter = painterResource(id = R.drawable.img_stretching), // ganti sesuai aset
            contentDescription = "Ilustrasi Stretching",
            modifier = Modifier
                .height(220.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Langkah-langkah",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            StepText("Dari posisi berlutut, duduk di atas tumit, rentangkan tangan ke depan di lantai.")
            StepText("Biarkan dahi menyentuh lantai.")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC15F56),
                contentColor = Color.White
            )
        ) {
            Text("Selesai", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun MinumAir(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Minumlah 1 Gelas\nAir Mineral",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp),
            lineHeight = 32.sp
        )

        Image(
            painter = painterResource(id = R.drawable.img_air), // ganti sesuai aset
            contentDescription = "Ilustrasi Air",
            modifier = Modifier
                .height(220.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Langkah-langkah",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StepText("Untuk membantu tubuh tetap segar dan terhidrasi.")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC15F56),
                contentColor = Color.White
            )
        ) {
            Text("Selesai", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun MakanPermen(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Nikmatilah Permen\nMint",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp),
            lineHeight = 32.sp
        )

        Image(
            painter = painterResource(id = R.drawable.img_permen), // ganti sesuai aset
            contentDescription = "Ilustrasi Permen",
            modifier = Modifier
                .height(220.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Langkah-langkah",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StepText("Permen dapat membantu mengurangi sensasi\nmual akibat dari gejala sakau")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC15F56),
                contentColor = Color.White
            )
        ) {
            Text("Selesai", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ClosingStep(userName: String, onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            Text(
                text = "Hai, $userName!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            Text(
                text = "Semangat terus untuk hidup sehat",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.Black)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFC15F56)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Setiap isapan rokok mengurangi waktumu dengan orang yang kamu cintai. Berhenti sekarang, demi mereka dan dirimu.",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Image(
                        painter = painterResource(id = R.drawable.img_penutup), // ganti sesuai asset
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
        }

        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC15F56))
        ) {
            Text("Selesai", color = Color.White)
        }
    }
}

@Composable
fun BreathingStep(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Lakukan Teknik\nPernapasan",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp),
            lineHeight = 32.sp
        )

        Image(
            painter = painterResource(id = R.drawable.img_pernapasan),
            contentDescription = "Ilustrasi Pernapasan",
            modifier = Modifier
                .height(220.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Langkah-langkah",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

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

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC15F56),
                contentColor = Color.White
            )
        ) {
            Text("Selanjutnya", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
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
