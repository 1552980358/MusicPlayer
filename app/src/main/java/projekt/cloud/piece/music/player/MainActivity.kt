package projekt.cloud.piece.music.player

import android.content.ComponentName
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.SubscriptionCallback
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.room.Room
import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.AudioDatabase
import projekt.cloud.piece.music.player.database.AudioDatabase.Companion.DATABASE_NAME
import projekt.cloud.piece.music.player.database.item.ColorItem
import projekt.cloud.piece.music.player.database.item.ColorItem.Companion.TYPE_PLAYLIST
import projekt.cloud.piece.music.player.database.item.PlaylistItem
import projekt.cloud.piece.music.player.databinding.ActivityMainBinding
import projekt.cloud.piece.music.player.service.PlayService
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_CONFIGS
import projekt.cloud.piece.music.player.util.Constant.DELAY_MILLIS
import projekt.cloud.piece.music.player.util.Constant.PLAYLIST_LIKES

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

    private var fragmentLifecycleCallbacks = object : FragmentLifecycleCallbacks() {
        override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
            Log.e(f::class.simpleName, "onFragmentCreated")
        }
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            Log.e(f::class.simpleName, "onFragmentViewCreated")
        }
        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.simpleName, "onFragmentStarted")
        }
        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.simpleName, "onFragmentResumed")
        }
        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.simpleName, "onFragmentPaused")
        }
        override fun onFragmentSaveInstanceState(fm: FragmentManager, f: Fragment, outState: Bundle) {
            Log.e(f::class.simpleName, "onFragmentSaveInstanceState")
        }
        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.simpleName, "onFragmentStopped")
        }
        override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.simpleName, "onFragmentViewDestroyed")
        }
        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.simpleName, "onFragmentDestroyed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.installViewFactory()
        delegate.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navController = binding.fragmentContainer.getFragment<NavHostFragment>().navController

        viewModel.registerGetContent(this)

        if (!viewModel.isLoaded) {

            viewModel.database = Room.databaseBuilder(this, AudioDatabase::class.java, DATABASE_NAME)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        io {
                            viewModel.database.playlist.insert(PlaylistItem(PLAYLIST_LIKES, PLAYLIST_LIKES, PLAYLIST_LIKES))
                            viewModel.database.color.insert(
                                ColorItem(
                                    PLAYLIST_LIKES,
                                    TYPE_PLAYLIST,
                                    primaryColor = getColor(this@MainActivity, R.color.red),
                                    secondaryColor = getColor(this@MainActivity, R.color.red)
                                )
                            )
                        }
                    }
                })
                .build()

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

                        if (viewModel.requireSyncPlaylist) {
                            viewModel.requireSyncPlaylist = false
                        }
                    }
                    override fun onConnectionSuspended() = Unit
                    override fun onConnectionFailed() = Unit
                }, null
            )
        }
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
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

    override fun onBackPressed() {
        when (val currentFragment = supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.first()) {
            is BaseFragment -> if (currentFragment.canBackStack) {
                super.onBackPressed()
            }
            else -> super.onBackPressed()
        }
    }

    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
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
        state.extras?.getInt(EXTRA_CONFIGS)?.let { viewModel.playConfig = it }
    }

    private fun onMetadataChanged(metadata: MediaMetadataCompat) {
        viewModel.audioList.find { it.id == metadata.getString(METADATA_KEY_MEDIA_ID) }?.let {
            viewModel.audioItem = it
        }
        viewModel.coverArtBitmap = metadata.getBitmap(METADATA_KEY_ALBUM_ART)
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