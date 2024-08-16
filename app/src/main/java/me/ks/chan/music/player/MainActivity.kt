package me.ks.chan.music.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import me.ks.chan.music.player.ui.theme.MusicPlayerTheme
import me.ks.chan.music.player.ui.theme.configureActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureActivity()
        setContent {
            MusicPlayerTheme {
            }
        }
    }
}