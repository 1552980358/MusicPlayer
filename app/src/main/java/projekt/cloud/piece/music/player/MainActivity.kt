package projekt.cloud.piece.music.player

import android.content.ComponentName
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import projekt.cloud.piece.music.player.databinding.ActivityMainBinding
import projekt.cloud.piece.music.player.service.PlayService

/**
 * Class [MainActivity], inherit to [AppCompatActivity]
 *
 * Variables:
 *   [binding]
 *   [navController]
 *
 * Methods:
 *   [onCreate]
 *
 **/
class MainActivity : AppCompatActivity() {
    
    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private lateinit var viewModel: MainActivityViewModel
    
    private lateinit var mediaBrowserCompat: MediaBrowserCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.installViewFactory()
        delegate.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        viewModel.registerForActivityResult(this)

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        navController = binding.fragmentContainerView.getFragment<NavHostFragment>().navController
        
        mediaBrowserCompat = MediaBrowserCompat(
            this,
            ComponentName(this, PlayService::class.java),
            object: MediaBrowserCompat.ConnectionCallback() {
                override fun onConnected() {
                    mediaBrowserCompat.subscribe(TAG, object : MediaBrowserCompat.SubscriptionCallback() { })
    
                    val mediaControllerCompat = MediaControllerCompat(this@MainActivity, mediaBrowserCompat.sessionToken)
                    mediaControllerCompat.registerCallback(object : MediaControllerCompat.Callback() {
                        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                            state?.let { playbackStateChanged(it) }
                        }
    
                        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                            metadata?.let { metadataChanged(it) }
                        }
                    })
                    
                    MediaControllerCompat.setMediaController(this@MainActivity, mediaControllerCompat)
                }
            },
            null
        )
    }
    
    override fun onStart() {
        super.onStart()
        if (!mediaBrowserCompat.isConnected) {
            mediaBrowserCompat.connect()
        }
    }
    
    private fun playbackStateChanged(state: PlaybackStateCompat) {
    }
    
    private fun metadataChanged(metadata: MediaMetadataCompat) {
    }

}