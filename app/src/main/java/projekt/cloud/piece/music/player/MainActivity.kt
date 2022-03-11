package projekt.cloud.piece.music.player

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Matrix
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.ArtistColumns.ARTIST
import android.provider.MediaStore.Audio.AudioColumns._ID
import android.provider.MediaStore.Audio.AudioColumns.ALBUM_ID
import android.provider.MediaStore.Audio.AudioColumns.ARTIST_ID
import android.provider.MediaStore.Audio.AudioColumns.DATA
import android.provider.MediaStore.Audio.AudioColumns.DURATION
import android.provider.MediaStore.Audio.AudioColumns.IS_MUSIC
import android.provider.MediaStore.Audio.AudioColumns.SIZE
import android.provider.MediaStore.Audio.AudioColumns.TITLE
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.util.Log
import android.view.Menu
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.room.Room.databaseBuilder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lib.github1552980358.ktExtension.android.content.intent
import lib.github1552980358.ktExtension.android.content.isSystemDarkMode
import lib.github1552980358.ktExtension.android.graphics.heightF
import lib.github1552980358.ktExtension.android.graphics.widthF
import lib.github1552980358.ktExtension.android.os.bundle
import lib.github1552980358.ktExtension.android.view.getDimensionPixelSize
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.ThemeTransitionActivity.Companion.EXTRA_IS_NIGHT
import projekt.cloud.piece.music.player.ThemeTransitionActivity.Companion.setScreenshot
import projekt.cloud.piece.music.player.base.BaseMainFragment
import projekt.cloud.piece.music.player.base.BaseMediaControlActivity
import projekt.cloud.piece.music.player.database.AudioDatabase
import projekt.cloud.piece.music.player.database.AudioDatabase.Companion.DATABASE_NAME
import projekt.cloud.piece.music.player.database.item.AlbumItem
import projekt.cloud.piece.music.player.database.item.ArtistItem
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.ActivityMainBinding
import projekt.cloud.piece.music.player.service.play.Action.ACTION_SYNC_SERVICE
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_INDEX
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_LIST
import projekt.cloud.piece.music.player.util.Constant.DELAY_MILS
import projekt.cloud.piece.music.player.util.Extra.EXTRA_AUDIO_ITEM
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArt
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArts40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.loadAudioArt40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.loadPlaylist40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.writeAlbumArt40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.writeAlbumArtRaw
import projekt.cloud.piece.music.player.util.MainActivityInterface
import projekt.cloud.piece.music.player.util.ViewUtil.screenshot
import java.io.Serializable

class MainActivity : BaseMediaControlActivity() {
    
    companion object {
        private var _isNightMode: Boolean? = null
        private fun initialIsNightMode(isNightMode: Boolean) {
            if (_isNightMode == null) {
                _isNightMode = isNightMode
            }
        }
        private fun updateNightMode() {
            _isNightMode = !isNightMode
        }
        private val isNightMode get() = _isNightMode!!
        
        private const val TAG = "MainActivity"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    
    private val appBarMain get() = binding.appBarMain
    private val contentMain get() = appBarMain.contentMain
    private val contentBottomSheetMain get() = appBarMain.contentBottomSheetMain

    private val fragmentContainerView get() = contentMain.fragmentContainerView
    private lateinit var navController: NavController

    private lateinit var activityInterface: MainActivityInterface
    private val audioList get() = activityInterface.audioList
    private val albumBitmap40DpMap get() = activityInterface.albumBitmap40DpMap
    private val audioBitmap40DpMap get() = activityInterface.audioBitmap40DpMap
    private val playlistBitmap40DpMap get() = activityInterface.playlistBitmap40DpMap
    
    private lateinit var audioDatabase: AudioDatabase
    
    private lateinit var behavior: BottomSheetBehavior<RelativeLayout>
    
    private val transportControls get() = mediaControllerCompat.transportControls

    private val fragmentLifecycleCallbacks =  object : FragmentLifecycleCallbacks() {
        override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
            Log.e(f::class.java.simpleName, "onFragmentAttached")
        }
        override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
            when (f) {
                is BaseMainFragment -> f.setInterface(activityInterface)
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
    
    private lateinit var audioItem: AudioItem

    override fun onCreate(savedInstanceState: Bundle?) {

        activityInterface = MainActivityInterface(
            itemClick = { index ->
                val audioItem = audioList[index]
                transportControls.playFromMediaId(
                    audioItem.id,
                    bundle {
                        putSerializable(EXTRA_LIST, audioList as Serializable)
                        putInt(EXTRA_INDEX, index)
                    }
                )
            },
            requestRefresh = {

            }
        )
        
        activityInterface.defaultAudioImage = getDrawable(resources, R.drawable.ic_music, null)!!.toBitmap()
    
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
    
        setDecorFitsSystemWindows(window, false)
        
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.appBarMain.toolbar)

        navController = fragmentContainerView.getFragment<NavHostFragment>().navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home), binding.drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        audioDatabase = databaseBuilder(this, AudioDatabase::class.java, DATABASE_NAME).build()
    
        behavior = BottomSheetBehavior.from(contentBottomSheetMain.relativeLayout)
        behavior.peekHeight = 0
        behavior.state = STATE_COLLAPSED
        
        initialIsNightMode(isSystemDarkMode)
        
        binding.navView.getHeaderView(0).findViewById<RelativeLayout>(R.id.relative_layout).setOnClickListener {
            binding.root.screenshot(window) { bitmap ->
                setScreenshot(bitmap)
                updateNightMode()
                startActivity(intent(this, ThemeTransitionActivity::class.java) {
                    putExtra(EXTRA_IS_NIGHT, isNightMode)
                })
                overridePendingTransition(0, 0)
            }
        }

        val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            when {
                it -> initialApplication()
            }
        }

        when (checkSelfPermission(this, READ_EXTERNAL_STORAGE)) {
            PERMISSION_GRANTED -> launchApplication()
            else -> requestPermission.launch(READ_EXTERNAL_STORAGE)
        }
        
        @Suppress("ClickableViewAccessibility")
        contentBottomSheetMain.relativeLayout.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                ACTION_DOWN -> {
                    (view.background as RippleDrawable).setHotspot(motionEvent.x, motionEvent.y)
                    view.isPressed = true
                }
                ACTION_UP -> {
                    view.isPressed = false
                    @Suppress("SwitchIntDef")
                    when {
                        motionEvent.x > contentBottomSheetMain.textView.right -> when (mediaControllerCompat.playbackState.state) {
                            STATE_PLAYING -> mediaControllerCompat.transportControls.pause()
                            STATE_PAUSED -> mediaControllerCompat.transportControls.play()
                        }
                        else -> startActivity(intent(this, PlayActivity::class.java) {
                            putExtra(EXTRA_AUDIO_ITEM, audioItem)
                        })
                    }
                }
            }
            return@setOnTouchListener true
        }
        
    }

    private fun initialApplication() = io {
        val audioList = arrayListOf<AudioItem>()
        val albumList = arrayListOf<AlbumItem>()
        val artistList = arrayListOf<ArtistItem>()
        loadFromDatabase(audioList, albumList, artistList)
        initialImage(albumList)
        loadContent()
    }

    private fun launchApplication() = io { loadContent() }

    private fun loadFromDatabase(audioList: ArrayList<AudioItem>, albumList: ArrayList<AlbumItem>, artistList: ArrayList<ArtistItem>) {
        querySystemDatabase(audioList, albumList, artistList)
        audioDatabase.artist.insert(*artistList.toTypedArray())
        audioDatabase.album.insert(*albumList.toTypedArray())
        audioDatabase.audio.insert(*audioList.toTypedArray())
    }

    private fun initialImage(albumList: ArrayList<AlbumItem>) {
        var bitmap: Bitmap?
        val matrix = Matrix()
        val bitmapSize = resources.getDimensionPixelSize(R.dimen.dp_40)
        for (albumItem in albumList) {
            bitmap = tryRun { loadAlbumArt(albumItem.id) } ?: continue
            // albumByteArrayRawMap[albumItem.id] = writeAlbumArtRaw(albumItem.id, bitmap)
            writeAlbumArtRaw(albumItem.id, bitmap)
            matrix.apply { setScale(bitmapSize / bitmap.widthF, bitmapSize / bitmap.heightF) }
            createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false).apply {
                writeAlbumArt40Dp(albumItem.id, this)
                albumBitmap40DpMap[albumItem.id] = this
            }
            bitmap.recycle()
        }
    }

    private fun querySystemDatabase(audioList: ArrayList<AudioItem>, albumList: ArrayList<AlbumItem>, artistList: ArrayList<ArtistItem>) {
        arrayListOf<AudioItem>().apply {
            contentResolver.query(EXTERNAL_CONTENT_URI, null, null, null, IS_MUSIC)?.apply {
                var artist: String
                var album: String
                while (moveToNext()) {
                    artist = getString(getColumnIndexOrThrow(ARTIST_ID))
                    if (artistList.find { it.id == artist } == null) {
                        artistList.add(ArtistItem(artist, getString(getColumnIndexOrThrow(ARTIST))))
                    }
                    album = getString(getColumnIndexOrThrow(ALBUM_ID))
                    if (albumList.find { it.id == album} == null) {
                        albumList.add(AlbumItem(
                            album,
                            getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM))
                        ))
                    }
                    @Suppress("InlinedApi")
                    audioList.add(
                        AudioItem(
                            id = getString(getColumnIndexOrThrow(_ID)),
                            title = getString(getColumnIndexOrThrow(TITLE)),
                            artist = artist,
                            album = album,
                            duration = getLong(getColumnIndexOrThrow(DURATION)),
                            size = getLong(getColumnIndexOrThrow(SIZE)),
                            path = getString(getColumnIndexOrThrow(DATA))
                        )
                    )
                }
                close()
            }
        }
    }

    private fun loadContent() {
        runBlocking {
            launch { activityInterface.albumList = audioDatabase.album.query() }
            launch { activityInterface.artistList = audioDatabase.artist.query() }
            activityInterface.audioList = audioDatabase.audio.query()
        }
        activityInterface.audioList.forEach { audioItem ->
            activityInterface.albumList.find { it.id == audioItem.album }?.let {
                audioItem.albumItem = it
            }
            activityInterface.artistList.find { it.id == audioItem.artist }?.let {
                audioItem.artistItem = it
            }
        }
        ui { activityInterface.refreshStageChanged() }
        runBlocking {
            launch { activityInterface.playlistList = audioDatabase.playlist.query() }
            loadBitmap()
        }
        ui { activityInterface.refreshCompleted() }
    }

    private fun loadBitmap() {
        io { loadAudioArt40Dp(audioBitmap40DpMap) }
        io { loadAlbumArts40Dp(albumBitmap40DpMap) }
        loadPlaylist40Dp(playlistBitmap40DpMap)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp() =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    
    override fun onResume() {
        super.onResume()
        if (mediaBrowserCompat.isConnected) {
            requestSyncService()
        }
    }

    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
        super.onDestroy()
    }
    
    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        metadata?.apply {
            contentBottomSheetMain.title = getString(METADATA_KEY_TITLE)
            contentBottomSheetMain.bitmap =
                audioBitmap40DpMap[getString(METADATA_KEY_MEDIA_ID)]
                    ?: albumBitmap40DpMap[getString(METADATA_KEY_ALBUM_ART_URI)]
                        ?: activityInterface.defaultAudioImage
            contentBottomSheetMain.duration = getLong(METADATA_KEY_DURATION)
            audioList.find { it.id == getString(METADATA_KEY_MEDIA_ID) }?.let { audioItem = it }
        }
    }
    
    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        state?.state?.also { playbackState ->
            when (playbackState) {
                STATE_PLAYING -> {
                    startPlaying(state.position)
                    if (behavior.isDraggable) {
                        behavior.state = STATE_EXPANDED
                        behavior.isDraggable = false
                        contentMain.root.apply {
                            layoutParams = (layoutParams as CoordinatorLayout.LayoutParams).apply {
                                setMargins(0, 0, 0, getDimensionPixelSize(R.dimen.main_bottom_sheet_height))
                            }
                        }
                    }
                    if (contentBottomSheetMain.playbackState != R.drawable.ani_play_pause) {
                        contentBottomSheetMain.playbackState = R.drawable.ani_play_pause
                    }
                     when (contentBottomSheetMain.playbackState) {
                        null -> contentBottomSheetMain.playbackState = R.drawable.ic_pause
                        R.drawable.ani_pause_play -> contentBottomSheetMain.playbackState = R.drawable.ani_play_pause
                    }
                }
                STATE_PAUSED -> {
                    isPlaying = false
                    when (contentBottomSheetMain.playbackState) {
                        null -> contentBottomSheetMain.playbackState = R.drawable.ic_play
                        R.drawable.ani_play_pause -> contentBottomSheetMain.playbackState = R.drawable.ani_pause_play
                    }
                }
                STATE_BUFFERING -> {
                    isPlaying = false
                }
            }
        }
    }
    
    override fun updateTime(currentProgress: Long) {
        contentBottomSheetMain.progress = currentProgress
    }
    
    override fun onConnected() {
        registerMediaController()
        activityInterface.setMediaBrowserCompat(mediaBrowserCompat)
        requestSyncService()
    }
    
    private fun requestSyncService() =
        mediaBrowserCompat.sendCustomAction(ACTION_SYNC_SERVICE, null, null)
    
    override fun getParentID() = TAG
    
}