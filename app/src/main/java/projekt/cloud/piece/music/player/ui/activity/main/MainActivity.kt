package projekt.cloud.piece.music.player.ui.activity.main

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.ConnectionCallback
import android.support.v4.media.session.MediaControllerCompat
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import projekt.cloud.piece.music.player.databinding.ActivityMainBinding
import projekt.cloud.piece.music.player.service.playback.PlaybackService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private lateinit var mediaBrowserCompat: MediaBrowserCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        val mainViewModel: MainViewModel by viewModels()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = binding.fragmentContainerView
            .getFragment<NavHostFragment>()
            .navController

        mediaBrowserCompat = MediaBrowserCompat(
            this,
            ComponentName(this, PlaybackService::class.java),
            object: ConnectionCallback() {
                override fun onConnected() {
                    // Set to Activity
                    MediaControllerCompat.setMediaController(
                        this@MainActivity,
                        MediaControllerCompat(this@MainActivity, mediaBrowserCompat.sessionToken)
                    )
                    // Notify connected
                    mainViewModel.setIsMediaBrowserCompatConnected(true)
                }
                override fun onConnectionFailed() {
                    connectMediaBrowserCompat()
                }
                override fun onConnectionSuspended() {
                    connectMediaBrowserCompat()
                }
            },
            null
        )
    }

    override fun onResume() {
        super.onResume()
        connectMediaBrowserCompat()
    }

    private fun connectMediaBrowserCompat() {
        if (!mediaBrowserCompat.isConnected) {
            mediaBrowserCompat.connect()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}