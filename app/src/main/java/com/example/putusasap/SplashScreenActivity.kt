package com.example.putusasap

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen(
                onFinished = {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            )
        }
    }
}

private val SplashRed = Color(0xFFC15F56)

@Composable
fun SplashScreen(onFinished: () -> Unit = {}) {
    val logoAlpha = remember { Animatable(0f) }
    val textProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Fade in logo
        logoAlpha.animateTo(1f, animationSpec = tween(800))
        // Munculkan tulisan setelah logo
        textProgress.animateTo(1f, animationSpec = tween(1200))
        delay(500)
        onFinished()
    }

    SplashContent(
        logoAlpha = logoAlpha.value,
        textRevealProgress = textProgress.value
    )
}

@Composable
private fun SplashContent(
    logoAlpha: Float,
    textRevealProgress: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashRed),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.padding(bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo paru-paru (image asset)
            Image(
                painter = painterResource(id = R.drawable.ic_lungs),
                contentDescription = "Logo Lungs",
                modifier = Modifier
                    .size(120.dp)
                    .alpha(logoAlpha)
            )

            Spacer(Modifier.height(16.dp))

            // Judul PutusAsap (image asset)
            Image(
                painter = painterResource(id = R.drawable.ic_putusasap),
                contentDescription = "Putus Asap Title",
                modifier = Modifier
                    .height(40.dp)
                    .wrapContentWidth()
                    .revealFromLeft(textRevealProgress)
            )
        }
    }
}

/**
 * Modifier untuk efek reveal dari kiri → kanan
 */
private fun Modifier.revealFromLeft(progress: Float) = this.then(
    Modifier.drawWithContent {
        val clipRight = size.width * progress.coerceIn(0f, 1f)
        clipRect(left = 0f, top = 0f, right = clipRight, bottom = size.height) {
            this@drawWithContent.drawContent()
        }
    }
)

@Preview(
    showBackground = true,
    backgroundColor = 0xFFC15F56,
    name = "Splash Preview"
)
@Composable
private fun SplashPreview() {
    SplashContent(logoAlpha = 1f, textRevealProgress = 1f)
}
