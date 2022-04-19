package projekt.cloud.piece.music.player

import android.content.ComponentName
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import projekt.cloud.piece.music.player.database.Database.audioRoom
import projekt.cloud.piece.music.player.databinding.ActivityMainBinding
import projekt.cloud.piece.music.player.service.PlayService
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui

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
        
        private const val DELAY_DIFF_MILLIS = 100L
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private lateinit var viewModel: MainActivityViewModel
    
    private lateinit var mediaBrowserCompat: MediaBrowserCompat
    
    private var countJob: Job? = null

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
        @Suppress("SwitchIntDef")
        when (state.state) {
            STATE_PLAYING -> {
                viewModel.isPlaying = false
                countJob?.cancel()
                countJob = startPlayback(state.position)
            }
            
            STATE_BUFFERING -> countJob?.cancel()
            
            STATE_PAUSED -> {
                viewModel.isPlaying = false
                countJob?.cancel()
            }
        }
    }
    
    private fun startPlayback(position: Long) = io {
        var current = position.correctPosition()
        do {
            ui { viewModel.position = current }
            current += DELAY_DIFF_MILLIS
            delay(DELAY_DIFF_MILLIS)
        } while (viewModel.isPlaying)
    }
    
    private suspend fun Long.correctPosition(): Long =
        this + (this % DELAY_DIFF_MILLIS).apply { delay(this) }
    
    private fun metadataChanged(metadata: MediaMetadataCompat) {
        io {
            val audioItem = audioRoom.queryAudio(metadata.getString(METADATA_KEY_MEDIA_ID))
            ui { viewModel.audioItem = audioItem }
        }
        viewModel.bitmapArt = metadata.getBitmap(METADATA_KEY_ALBUM_ART)
    }

}