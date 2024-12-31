package solid.javad.soundplayer.ui

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.koin.compose.viewmodel.koinViewModel
import solid.javad.soundplayer.ui.screen.AudiosScreen
import solid.javad.soundplayer.ui.theme.SoundPlayerTheme
import solid.javad.soundplayer.ui.viewmodel.AudiosScreenViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoundPlayerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AudiosScreen(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}