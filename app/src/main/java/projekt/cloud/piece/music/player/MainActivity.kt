package projekt.cloud.piece.music.player

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import projekt.cloud.piece.music.player.databinding.ActivityMainBinding
import projekt.cloud.piece.music.player.room.AudioDatabase
import projekt.cloud.piece.music.player.service.PlayService

class MainActivity: AppCompatActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    private lateinit var appBarConfiguration: AppBarConfiguration
    
    private lateinit var binding: ActivityMainBinding
    
    private val root get() = binding.root

    private val fragmentContainerView get() = binding.fragmentContainerView
    
    private lateinit var navController: NavController
    
    private lateinit var mediaBrowserCompat: MediaBrowserCompat
    
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        
        AudioDatabase.initial(this)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(root)
        
        navController = fragmentContainerView.getFragment<NavHostFragment>().navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
    
        mediaBrowserCompat = MediaBrowserCompat(
            this,
            ComponentName(this, PlayService::class.java),
            object: MediaBrowserCompat.ConnectionCallback() {
                override fun onConnected() {
                    mediaBrowserCompat.subscribe(TAG, object: MediaBrowserCompat.SubscriptionCallback() {})
                    
                    val mediaControllerCompat = MediaControllerCompat(this@MainActivity, mediaBrowserCompat.sessionToken)
                    mediaControllerCompat.registerCallback(object : MediaControllerCompat.Callback() {
                        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                        }
                        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                        }
                    })
                    
                    MediaControllerCompat.setMediaController(this@MainActivity, mediaControllerCompat)
                }
                override fun onConnectionFailed() {
                    mediaBrowserCompat.connect()
                }
                override fun onConnectionSuspended() {
                    mediaBrowserCompat.connect()
                }
            },
            null
        )
    }
    
    override fun onResume() {
        super.onResume()
        if (!mediaBrowserCompat.isConnected) {
            mediaBrowserCompat.connect()
        }
    }
    
    override fun onSupportNavigateUp() =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    
}