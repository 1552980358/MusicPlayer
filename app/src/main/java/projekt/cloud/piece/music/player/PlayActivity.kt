package projekt.cloud.piece.music.player

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.room.Room
import androidx.viewpager2.adapter.FragmentStateAdapter
import lib.github1552980358.ktExtension.android.content.getSerializableOf
import lib.github1552980358.ktExtension.android.os.bundle
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import mkaflowski.mediastylepalette.MediaNotificationProcessor
import projekt.cloud.piece.music.player.base.BaseMediaControlActivity
import projekt.cloud.piece.music.player.base.BasePlayFragment
import projekt.cloud.piece.music.player.database.AudioDatabase
import projekt.cloud.piece.music.player.database.AudioDatabase.Companion.DATABASE_NAME
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.ActivityPlayBinding
import projekt.cloud.piece.music.player.service.play.Action.ACTION_PLAY_CONFIG_CHANGED
import projekt.cloud.piece.music.player.service.play.Action.ACTION_SYNC_SERVICE
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_PLAY_CONFIG
import projekt.cloud.piece.music.player.ui.play.PlayFragment
import projekt.cloud.piece.music.player.util.ColorUtil.isLight
import projekt.cloud.piece.music.player.util.Extra.EXTRA_AUDIO_ITEM
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArtRaw
import projekt.cloud.piece.music.player.util.ImageUtil.loadAudioArtRaw
import projekt.cloud.piece.music.player.util.PlayActivityInterface

class PlayActivity: BaseMediaControlActivity() {
    
    companion object {
        const val TAG = "PlayActivity"
    }
    
    private lateinit var binding: ActivityPlayBinding
    
    private lateinit var audioItem: AudioItem
    
    private val fragmentLifecycleCallbacks =  object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
            Log.e(f::class.java.simpleName, "onFragmentAttached")
        }
        override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
            if (f is BasePlayFragment) {
                f.setInterface(activityInterface)
            }
        }
        override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
            Log.e(f::class.java.simpleName, "onFragmentCreated")
        }
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            Log.e(f::class.java.simpleName, "onFragmentViewCreated")
        }
        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentStarted")
        }
        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentResumed")
        }
        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentPaused")
        }
        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentStopped")
        }
        override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentViewDestroyed")
        }
        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentDestroyed")
        }
        override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentDetached")
        }
    }
    
    private lateinit var activityInterface: PlayActivityInterface
    
    private lateinit var defaultCoverBitmap: Bitmap
    private lateinit var currentCoverBitmap: Bitmap
    
    private lateinit var audioDatabase: AudioDatabase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    
        audioItem = intent.getSerializableOf(EXTRA_AUDIO_ITEM)!!
        
        activityInterface = PlayActivityInterface(
            requestMetadata = { audioItem },
            changePlayConfig = { config ->
                mediaBrowserCompat.sendCustomAction(ACTION_PLAY_CONFIG_CHANGED, bundleOf(EXTRA_PLAY_CONFIG to config), null)
            }
        )
        
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
        
        super.onCreate(savedInstanceState)
    
        audioDatabase = Room.databaseBuilder(this, AudioDatabase::class.java, DATABASE_NAME).build()
        
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play)
        
        val fragmentList = listOf(
            PlayFragment().apply { arguments = bundle { putSerializable(EXTRA_AUDIO_ITEM, audioItem) } }
        )
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragmentList.size
            override fun createFragment(position: Int) = fragmentList[position]
        }
        
        io {
            defaultCoverBitmap = getDrawable(resources, R.drawable.ic_music_big, null)!!.toBitmap()
            currentCoverBitmap =
                loadAudioArtRaw(audioItem.id) ?: loadAlbumArtRaw(audioItem.album) ?: defaultCoverBitmap
            updateMetadata(audioItem)
        }
        
    }
    
    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        metadata?.apply {
            io {
                audioItem = audioDatabase.audio.query(getString(METADATA_KEY_MEDIA_ID)).apply {
                    albumItem = audioDatabase.album.query(album)
                    artistItem = audioDatabase.artist.query(artist)
                    activityInterface.loadMetadata(audioItem)
                }
                ui { updateMetadata(audioItem) }
            }
        }
    }
    
    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        state?.state?.also { playbackState ->
            when (playbackState) {
                STATE_PLAYING -> {}
                STATE_PAUSED -> {}
                STATE_BUFFERING -> {}
            }
            state.extras?.getInt(EXTRA_PLAY_CONFIG)?.let {
                activityInterface.updatePlayConfig(it)
            }
        }
    }
    
    override fun onConnected() {
        registerMediaController()
        activityInterface.transportControls = mediaControllerCompat.transportControls
        mediaBrowserCompat.sendCustomAction(ACTION_SYNC_SERVICE, null, null)
    }
    
    private fun updateMetadata(audioItem: AudioItem) {
        currentCoverBitmap =
            loadAudioArtRaw(audioItem.id) ?: loadAlbumArtRaw(audioItem.album) ?: defaultCoverBitmap
        ui { activityInterface.loadBitmap(currentCoverBitmap) }
        MediaNotificationProcessor(this@PlayActivity, currentCoverBitmap).apply {
            ui {
                binding.backgroundColor = backgroundColor
                activityInterface.loadIsLight(backgroundColor.isLight)
            }
        }
    }
    
    override fun getParentID() = TAG
    
    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
        super.onDestroy()
    }
    
}