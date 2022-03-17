package projekt.cloud.piece.music.player

import android.content.ComponentName
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.SubscriptionCallback
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.room.Room
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.database.AudioDatabase
import projekt.cloud.piece.music.player.database.AudioDatabase.Companion.DATABASE_NAME
import projekt.cloud.piece.music.player.databinding.ActivityMainBinding
import projekt.cloud.piece.music.player.service.PlayService
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_PLAY_CONFIG
import projekt.cloud.piece.music.player.util.Constant.DELAY_MILLIS

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val mediaBrowserCompat get() = viewModel.mediaBrowserCompat
    private val subscriptionCallback get() = viewModel.subscriptionCallback
    private val mediaControllerCallback get() = viewModel.mediaControllerCallback
    private val mediaControllerCompat get() = viewModel.mediaControllerCompat

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private var progressJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.installViewFactory()
        delegate.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navController = binding.fragmentContainer.getFragment<NavHostFragment>().navController

        if (!viewModel.isLoaded) {

            viewModel.database = Room.databaseBuilder(this, AudioDatabase::class.java, DATABASE_NAME).build()

            viewModel.subscriptionCallback = object : SubscriptionCallback() { }
            viewModel.mediaBrowserCompat = MediaBrowserCompat(
                this,
                ComponentName(this, PlayService::class.java),
                object : MediaBrowserCompat.ConnectionCallback() {
                    override fun onConnected() {
                        mediaBrowserCompat.unsubscribe(TAG)
                        mediaBrowserCompat.subscribe(TAG, subscriptionCallback)

                        viewModel.mediaControllerCompat = MediaControllerCompat(this@MainActivity, mediaBrowserCompat.sessionToken)
                        MediaControllerCompat.setMediaController(this@MainActivity, mediaControllerCompat)

                        viewModel.mediaControllerCallback = object : MediaControllerCompat.Callback() {
                            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                                state?.let { this@MainActivity.onPlaybackStateChanged(it) }
                            }
                            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                                metadata?.let { this@MainActivity.onMetadataChanged(it) }
                            }
                        }
                        mediaControllerCompat.registerCallback(mediaControllerCallback)
                    }
                    override fun onConnectionSuspended() = Unit
                    override fun onConnectionFailed() = Unit
                }, null
            )
        }

    }

    override fun onStart() {
        super.onStart()
        if (!mediaBrowserCompat.isConnected) {
            mediaBrowserCompat.connect()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun onPlaybackStateChanged(state: PlaybackStateCompat) {
        @Suppress("SwitchIntDef")
        when (state.state) {
            STATE_PLAYING -> {
                progressJob?.cancel()
                progressJob = startPlaying(state.position)
            }
            STATE_BUFFERING -> {
                progressJob?.cancel()
            }
            STATE_PAUSED -> {
                viewModel.isPlaying = false
            }
        }
        state.extras?.getInt(EXTRA_PLAY_CONFIG)?.let { viewModel.playConfig = it }
    }

    private fun onMetadataChanged(metadata: MediaMetadataCompat) {
        viewModel.audioList.find { it.id == metadata.getString(METADATA_KEY_MEDIA_ID) }?.let {
            viewModel.audioItem = it
        }
    }

    private fun startPlaying(progress: Long) =  io {
        ui { viewModel.isPlaying = true }
        var currentProgress = progress.correctTime()
        do {
            ui { viewModel.progress = currentProgress }
            delay(DELAY_MILLIS)
            currentProgress += DELAY_MILLIS
        } while (viewModel.isPlaying)
    }

    private suspend fun Long.correctTime() =
        this + (this % DELAY_MILLIS).apply { delay(this) }

}