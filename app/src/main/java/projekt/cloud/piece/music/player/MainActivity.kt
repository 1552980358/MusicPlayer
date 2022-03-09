package projekt.cloud.piece.music.player

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Matrix
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.ArtistColumns.ARTIST
import android.provider.MediaStore.Audio.AudioColumns._ID
import android.provider.MediaStore.Audio.AudioColumns.ALBUM_ID
import android.provider.MediaStore.Audio.AudioColumns.ARTIST_ID
import android.provider.MediaStore.Audio.AudioColumns.DATA
import android.provider.MediaStore.Audio.AudioColumns.IS_MUSIC
import android.provider.MediaStore.Audio.AudioColumns.SIZE
import android.provider.MediaStore.Audio.AudioColumns.TITLE
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.MediaColumns.DURATION
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.room.Room.databaseBuilder
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lib.github1552980358.ktExtension.android.content.intent
import lib.github1552980358.ktExtension.android.content.isSystemDarkMode
import lib.github1552980358.ktExtension.android.graphics.heightF
import lib.github1552980358.ktExtension.android.graphics.widthF
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.ThemeTransitionActivity.Companion.EXTRA_IS_NIGHT
import projekt.cloud.piece.music.player.ThemeTransitionActivity.Companion.setScreenshot
import projekt.cloud.piece.music.player.base.BaseMainFragment
import projekt.cloud.piece.music.player.base.BaseThemeActivity
import projekt.cloud.piece.music.player.database.AudioDatabase
import projekt.cloud.piece.music.player.database.AudioDatabase.Companion.DATABASE_NAME
import projekt.cloud.piece.music.player.database.item.AlbumItem
import projekt.cloud.piece.music.player.database.item.ArtistItem
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.ActivityMainBinding
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArt
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArts40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.loadAudioArt40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.loadPlaylist40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.writeAlbumArt40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.writeAlbumArtRaw
import projekt.cloud.piece.music.player.util.MainActivityInterface
import projekt.cloud.piece.music.player.util.ViewUtil.screenshot

class MainActivity : BaseThemeActivity() {
    
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
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val fragmentContainerView get() = binding.appBarMain.contentMain.fragmentContainerView
    private lateinit var navController: NavController

    private lateinit var activityInterface: MainActivityInterface
    private val albumBitmap40DpMap get() = activityInterface.albumBitmap40DpMap
    private val audioBitmap40DpMap get() = activityInterface.audioBitmap40DpMap
    private val playlistBitmap40DpMap get() = activityInterface.playlistBitmap40DpMap

    private lateinit var audioDatabase: AudioDatabase

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

    override fun onCreate(savedInstanceState: Bundle?) {

        activityInterface = MainActivityInterface(
            itemClick = {

            },
            requestRefresh = {

            }
        )
    
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
            launch { activityInterface.audioList = audioDatabase.audio.query() }
            launch { activityInterface.albumList = audioDatabase.album.query() }
            launch { activityInterface.artistList = audioDatabase.artist.query() }
        }
        ui { activityInterface.refreshStageChanged() }
        runBlocking {
            launch { loadBitmap() }
            launch { activityInterface.playlistList = audioDatabase.playlist.query() }
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

    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
        super.onDestroy()
    }

}