package projekt.cloud.piece.music.player

import android.app.Instrumentation
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.CustomActionCallback
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.util.Log
import android.view.View
import android.view.View.OVER_SCROLL_NEVER
import android.view.ViewAnimationUtils.createCircularReveal
import android.view.Window.FEATURE_ACTIVITY_TRANSITIONS
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.room.Room
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_SETTLING
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import lib.github1552980358.ktExtension.android.content.getSerializableOf
import lib.github1552980358.ktExtension.android.os.bundle
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import okhttp3.internal.format
import projekt.cloud.piece.music.player.base.BaseMediaControlActivity
import projekt.cloud.piece.music.player.base.BasePlayFragment
import projekt.cloud.piece.music.player.database.AudioDatabase
import projekt.cloud.piece.music.player.database.AudioDatabase.Companion.DATABASE_NAME
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.ActivityPlayBinding
import projekt.cloud.piece.music.player.service.play.Action.ACTION_PLAY_CONFIG_CHANGED
import projekt.cloud.piece.music.player.service.play.Action.ACTION_REQUEST_LIST
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_LIST
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_PLAY_CONFIG
import projekt.cloud.piece.music.player.ui.lyricPlay.LyricPlayFragment
import projekt.cloud.piece.music.player.ui.play.PlayFragment
import projekt.cloud.piece.music.player.util.ColorUtil.isLight
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION_LONG
import projekt.cloud.piece.music.player.util.Extra.EXTRA_AUDIO_ITEM
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArtRaw
import projekt.cloud.piece.music.player.util.ImageUtil.loadAudioArtRaw
import projekt.cloud.piece.music.player.util.PlayActivityInterface
import kotlin.math.hypot

class PlayActivity: BaseMediaControlActivity() {
    
    companion object {
        const val TAG = "PlayActivity"
        private const val TRANSFORM_ANIMATION_DURATION = 300L
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
    
    private var isLaunched = false
    
    private val circularStartPoint = Point()
    
    private var isScrolling = false
    private var scrollOffsetPixel = 0
    
    private var colors: String? = null
    private var audioList: List<AudioItem>? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    
        audioItem = intent.getSerializableOf(EXTRA_AUDIO_ITEM)!!
        
        activityInterface = PlayActivityInterface(
            this,
            requestMetadata = { audioItem },
            changePlayConfig = { config ->
                mediaBrowserCompat.sendCustomAction(ACTION_PLAY_CONFIG_CHANGED, bundleOf(EXTRA_PLAY_CONFIG to config), object : CustomActionCallback() {
                    override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                        @Suppress("UNCHECKED_CAST")
                        (resultData?.getSerializable(EXTRA_LIST) as List<AudioItem>?)?.let { list ->
                            activityInterface.updateAudioList(list)
                        }
                    }
                })
            },
            requestColors = { colors }
        )
        
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
    
        // Token from
        // https://material.io/develop/android/theming/motion#material-container-transform
        window.requestFeature(FEATURE_ACTIVITY_TRANSITIONS)
        findViewById<View>(android.R.id.content).transitionName = "fab_trans"
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = TRANSFORM_ANIMATION_DURATION
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = TRANSFORM_ANIMATION_DURATION
        }
        
        super.onCreate(savedInstanceState)
    
        audioDatabase = Room.databaseBuilder(this, AudioDatabase::class.java, DATABASE_NAME).build()
        
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play)
        
        with(binding.viewPager) {
            /**
             * For ViewPager2, method of disabling over scroll is token from
             * https://stackoverflow.com/a/56942231/11685230
             **/
            binding.viewPager.getChildAt(0).overScrollMode = OVER_SCROLL_NEVER
    
            val fragmentList = listOf(
                PlayFragment().apply { arguments = bundle { putSerializable(EXTRA_AUDIO_ITEM, audioItem) } },
                LyricPlayFragment()
            )
    
            adapter = object : FragmentStateAdapter(this@PlayActivity) {
                override fun getItemCount() = fragmentList.size
                override fun createFragment(position: Int) = fragmentList[position]
            }
    
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    isScrolling = state == SCROLL_STATE_DRAGGING || state == SCROLL_STATE_SETTLING
                }
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    scrollOffsetPixel = positionOffsetPixels
                }
            })
        }
        
        io {
            defaultCoverBitmap = getDrawable(resources, R.drawable.ic_music_big, null)!!.toBitmap()
            updateMetadata(audioItem)
            
            circularStartPoint.apply {
                x = resources.displayMetrics.widthPixels / 2
                y = (resources.displayMetrics.heightPixels - resources.displayMetrics.widthPixels) * 2 / 5 -
                    resources.getDimensionPixelSize(R.dimen.fragment_play_content_buttons_seek_height) * 2 +
                    resources.displayMetrics.widthPixels
            }
            
        }
    }
    
    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        metadata?.apply {
            io {
                audioItem = audioDatabase.audio.query(getString(METADATA_KEY_MEDIA_ID)).apply {
                    albumItem = audioDatabase.album.query(album)
                    artistItem = audioDatabase.artist.query(artist)
                    activityInterface.updateAudioItem(this)
                }
                updateMetadata(audioItem)
            }
            requestSyncList()
        }
    }
    
    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        state?.state?.also { playbackState ->
            when (playbackState) {
                STATE_PLAYING -> {
                    startPlaying(state.position)
                    activityInterface.updatePlayState(true)
                }
                STATE_PAUSED -> {
                    updateTime(state.position)
                    isPlaying = false
                    activityInterface.updatePlayState(false)
                }
                STATE_BUFFERING -> isPlaying = false
            }
            state.extras?.getInt(EXTRA_PLAY_CONFIG)?.let {
                activityInterface.updatePlayConfig(it)
            }
        }
    }
    
    override fun onConnected() {
        super.onConnected()
        activityInterface.transportControls = mediaControllerCompat.transportControls
        requestSyncList()
    }
    
    private fun requestSyncList() {
        mediaBrowserCompat.sendCustomAction(ACTION_REQUEST_LIST, null, object : CustomActionCallback() {
            override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                @Suppress("UNCHECKED_CAST")
                (resultData?.getSerializable(EXTRA_LIST) as List<AudioItem>?)?.let { audioList ->
                    this@PlayActivity.audioList = audioList
                    activityInterface.updateAudioList(audioList)
                }
            }
        })
    }
    
    override fun updateTime(currentProgress: Long) =
        activityInterface.updateProgress(currentProgress)
    
    private fun updateMetadata(audioItem: AudioItem) {
        currentCoverBitmap =
            loadAudioArtRaw(audioItem.id) ?: loadAlbumArtRaw(audioItem.album) ?: defaultCoverBitmap
        ui { activityInterface.updateBitmap(currentCoverBitmap) }
        audioDatabase.color.query(audioItem.id, audioItem.album).apply {
            ui { binding.relativeLayout.setBackgroundColor(backgroundColor) }
            when {
                !isLaunched -> {
                    ui { binding.coordinatorLayout.setBackgroundColor(backgroundColor) }
                    isLaunched = true
                }
                else -> {
                    val startX = when {
                        isScrolling -> if (scrollOffsetPixel > circularStartPoint.x) 0 else circularStartPoint.x - scrollOffsetPixel
                        !activityInterface.isPlayFragment -> 0
                        else -> circularStartPoint.x
                    }
                    createCircularReveal(binding.relativeLayout, startX, circularStartPoint.y, 0F,
                        hypot(
                            when (startX) {
                                0 -> resources.displayMetrics.widthPixels
                                else -> resources.displayMetrics.widthPixels - startX
                            }.toFloat(),
                            circularStartPoint.y.toFloat())
                    ).apply {
                        duration = ANIMATION_DURATION_LONG
                        doOnEnd { binding.coordinatorLayout.setBackgroundColor(backgroundColor) }
                        ui { start() }
                    }
                }
            }
            ui {
                activityInterface.updateColor(primaryColor, secondaryColor)
                activityInterface.updateContrast(backgroundColor.isLight)
            }
            colors = format("#%08X #%08X", primaryColor, secondaryColor)
        }
    }
    
    override fun onStop() {
        Instrumentation().callActivityOnSaveInstanceState(this, Bundle())
        super.onStop()
    }
    
    override fun onBackPressed() = finishAfterTransition()
    
    override fun getParentID() = TAG
    
    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
        super.onDestroy()
    }
    
}