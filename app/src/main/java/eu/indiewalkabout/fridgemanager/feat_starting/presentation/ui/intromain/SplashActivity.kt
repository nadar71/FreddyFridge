package eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.components.BackgroundPattern
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import kotlinx.coroutines.delay
import kotlin.random.Random

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FreddyFridgeTheme {
                SplashScreen {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val messages = stringArrayResource(id = R.array.splash_messages)
    var currentMessage by remember { mutableStateOf(messages.random()) }

    LaunchedEffect(Unit) {
        val random = Random(System.currentTimeMillis())
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < 3000) {
            delay(random.nextLong(500L, 1000L))
            currentMessage = messages.random()
        }
        onTimeout()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BackgroundPattern()
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.shopping),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = currentMessage,
                style = text_16(colorText,true),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}