package projekt.cloud.piece.music.player

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Matrix
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.room.Room
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lib.github1552980358.ktExtension.android.content.isSystemDarkMode
import lib.github1552980358.ktExtension.android.graphics.heightF
import lib.github1552980358.ktExtension.android.graphics.widthF
import lib.github1552980358.ktExtension.android.os.bundle
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import mkaflowski.mediastylepalette.MediaNotificationProcessor
import projekt.cloud.piece.music.player.base.BaseMainFragment
import projekt.cloud.piece.music.player.base.BaseMediaControlActivity
import projekt.cloud.piece.music.player.database.AudioDatabase
import projekt.cloud.piece.music.player.database.AudioDatabase.Companion.DATABASE_NAME
import projekt.cloud.piece.music.player.database.item.AlbumItem
import projekt.cloud.piece.music.player.database.item.ArtistItem
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.database.item.ColorItem
import projekt.cloud.piece.music.player.databinding.ActivityMainBinding
import projekt.cloud.piece.music.player.service.play.Extra
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArt
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArts40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.loadAudioArt40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.loadPlaylist40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.writeAlbumArt40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.writeAlbumArtRaw
import projekt.cloud.piece.music.player.util.MainActivityInterface
import java.io.Serializable

class MainActivity : BaseMediaControlActivity() {

    companion object {
        private var isNightModeStatic: Boolean? = null
        @JvmStatic
        private fun initialIsNightMode(isNightMode: Boolean) {
            if (isNightModeStatic == null) {
                isNightModeStatic = isNightMode
            }
        }
        @JvmStatic
        private fun updateNightMode() {
            isNightModeStatic = !isNightMode
        }
        private val isNightMode get() = isNightModeStatic!!

        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var activityInterface: MainActivityInterface
    private val audioDatabase get() =  activityInterface.audioDatabase
    private val audioList get() = activityInterface.audioList
    private val albumBitmap40DpMap get() = activityInterface.albumBitmap40DpMap
    private val audioBitmap40DpMap get() = activityInterface.audioBitmap40DpMap
    private val playlistBitmap40DpMap get() = activityInterface.playlistBitmap40DpMap
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
            when (f) {
                is BaseMainFragment -> activityInterface
            }
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

    private var audioItem: AudioItem? = null

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {

        activityInterface = MainActivityInterface()
        activityInterface.setFragmentCallRequest(
            itemClick = { index ->
                val audioItem = audioList[index]
                transportControls.playFromMediaId(
                    audioItem.id,
                    bundle {
                        putSerializable(Extra.EXTRA_LIST, audioList as Serializable)
                        putInt(Extra.EXTRA_INDEX, index)
                    }
                )
            },
            requestRefresh = {

            },
            requestAudioItem = { audioItem }
        )

        activityInterface.defaultAudioImage = getDrawable(resources, R.drawable.ic_music, null)!!.toBitmap()

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        navController = binding.fragmentContainerView.getFragment<NavHostFragment>().navController

        activityInterface.audioDatabase = Room.databaseBuilder(this, AudioDatabase::class.java, DATABASE_NAME).build()

        initialIsNightMode(isSystemDarkMode)

        val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            when {
                it -> initialApplication()
            }
        }

        when (checkSelfPermission(this, READ_EXTERNAL_STORAGE)) {
            PERMISSION_GRANTED -> launchApplication()
            else -> requestPermission.launch(READ_EXTERNAL_STORAGE)
        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onSupportNavigateUp() =
        navController.navigateUp() || super.onSupportNavigateUp()

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
            bitmap = tryRun { loadAlbumArt(albumItem.id) }
            if (bitmap == null) {
                audioDatabase.color.insert(ColorItem(albumItem.id, ColorItem.TYPE_ALBUM))
                continue
            }
            writeAlbumArtRaw(albumItem.id, bitmap)
            matrix.apply { setScale(bitmapSize / bitmap.widthF, bitmapSize / bitmap.heightF) }
            createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false).apply {
                writeAlbumArt40Dp(albumItem.id, this)
                albumBitmap40DpMap[albumItem.id] = this
            }
            MediaNotificationProcessor(this, bitmap).apply {
                audioDatabase.color.insert(ColorItem(albumItem.id, ColorItem.TYPE_ALBUM, backgroundColor, primaryTextColor, secondaryTextColor))
            }
            bitmap.recycle()
        }
    }

    private fun querySystemDatabase(audioList: ArrayList<AudioItem>, albumList: ArrayList<AlbumItem>, artistList: ArrayList<ArtistItem>) {
        arrayListOf<AudioItem>().apply {
            contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.AudioColumns.IS_MUSIC
            )?.apply {
                var artist: String
                var album: String
                while (moveToNext()) {
                    artist = getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST_ID))
                    if (artistList.find { it.id == artist } == null) {
                        artistList.add(ArtistItem(artist, getString(getColumnIndexOrThrow(MediaStore.Audio.ArtistColumns.ARTIST))))
                    }
                    album = getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID))
                    if (albumList.find { it.id == album} == null) {
                        albumList.add(AlbumItem(album, getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM))))
                    }
                    @Suppress("InlinedApi")
                    audioList.add(
                        AudioItem(
                            id = getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)),
                            title = getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)),
                            artist = artist,
                            album = album,
                            duration = getLong(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)),
                            size = getLong(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.SIZE)),
                            path = getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA))
                        )
                    )
                }
                close()
            }
        }
    }

    private fun loadContent() {
        runBlocking {
            activityInterface.audioList = audioDatabase.audio.query()
            audioList.apply {
                sortedBy { it.pinyin }
                forEachIndexed { index, audioItem -> audioItem.index = index }
            }
        }
        audioList.forEach { audioItem ->
            audioItem.artistItem = audioDatabase.artist.query(audioItem.artist)
            audioItem.albumItem = audioDatabase.album.query(audioItem.album)
        }
        ui { activityInterface.refreshStageChanged() }
        runBlocking {
            launch { activityInterface.playlistList = audioDatabase.playlist.query() }
            loadBitmap()
        }
        ui { activityInterface.refreshCompleted() }
        activityInterface.isInitialized = true
    }

    private fun loadBitmap() {
        io { loadAudioArt40Dp(audioBitmap40DpMap) }
        io { loadAlbumArts40Dp(albumBitmap40DpMap) }
        loadPlaylist40Dp(playlistBitmap40DpMap)
    }

    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        metadata?.let { mediaMetadataCompat ->
            activityInterface.audioItem = audioList.find { it.id == mediaMetadataCompat.getString(METADATA_KEY_MEDIA_ID) }
        }
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        state?.state?.also { playbackState ->
            when (playbackState) {
                STATE_PLAYING -> {
                    startPlaying(state.position)
                    activityInterface.isPlaying = true
                }
                STATE_PAUSED -> {
                    updateTime(state.position)
                    isPlaying = false
                    activityInterface.isPlaying = false
                }
                STATE_BUFFERING -> isPlaying = false
            }
            state.extras?.getInt(Extra.EXTRA_PLAY_CONFIG)?.let {
                activityInterface.playConfig = it
            }
        }
    }

    override fun onConnected() {
        super.onConnected()
        activityInterface.setController(mediaBrowserCompat, transportControls)
    }

    override fun getParentID() = TAG

    override fun updateTime(currentProgress: Long) {
        activityInterface.progress = currentProgress
    }

}