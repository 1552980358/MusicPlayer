package projekt.cloud.piece.music.player

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import projekt.cloud.piece.music.player.databinding.ActivityMainBinding
import projekt.cloud.piece.music.player.item.AudioMetadata
import projekt.cloud.piece.music.player.room.AudioDatabase
import projekt.cloud.piece.music.player.service.PlayService
import projekt.cloud.piece.music.player.service.play.ServiceConstants

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
    
        val viewModel: MainActivityViewModel by viewModels()
        
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
                        
                        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                            viewModel.setPlaybackState(state.state)
                            viewModel.setPosition(state.position)
                        }
                        
                        override fun onMetadataChanged(mediaMetadataCompat: MediaMetadataCompat) {
                            viewModel.setTitle(mediaMetadataCompat.getString(METADATA_KEY_TITLE))
                            viewModel.setArtist(mediaMetadataCompat.getString(METADATA_KEY_ARTIST))
                            viewModel.setAlbum(mediaMetadataCompat.getString(METADATA_KEY_ALBUM))
                            viewModel.setArtBitmap(mediaMetadataCompat.getBitmap(METADATA_KEY_ALBUM_ART))
                            viewModel.setDuration(mediaMetadataCompat.getLong(METADATA_KEY_DURATION))
                            viewModel.playingQueue.value
                                ?.indexOfFirst { it.id == mediaMetadataCompat.getString(METADATA_KEY_MEDIA_ID) }
                                ?.let { viewModel.setPlayingPosition(it) }
                        }
    
                        override fun onRepeatModeChanged(repeatMode: Int) {
                            viewModel.setRepeatMode(repeatMode)
                        }
    
                        override fun onShuffleModeChanged(shuffleMode: Int) {
                            viewModel.setShuffleMode(shuffleMode)
                        }
                        
                        override fun onExtrasChanged(extras: Bundle) {
                            @Suppress("UNCHECKED_CAST")
                            (extras.getSerializable(ServiceConstants.EXTRA_AUDIO_METADATA_LIST) as? ArrayList<AudioMetadata>)
                                ?.let { viewModel.setPlayingQueue(it) }
                        }
                        
                    })
                    
                    // Update repeat and shuffle mode
                    viewModel.setRepeatMode(mediaControllerCompat.repeatMode)
                    viewModel.setShuffleMode(mediaControllerCompat.shuffleMode)
                    
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