package projekt.cloud.piece.music.player

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
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
import android.view.Menu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.room.Room.databaseBuilder
import lib.github1552980358.ktExtension.android.graphics.heightF
import lib.github1552980358.ktExtension.android.graphics.widthF
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import projekt.cloud.piece.music.player.database.AudioDatabase
import projekt.cloud.piece.music.player.database.AudioDatabase.Companion.DATABASE_NAME
import projekt.cloud.piece.music.player.database.item.AlbumItem
import projekt.cloud.piece.music.player.database.item.ArtistItem
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.ActivityMainBinding
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArt
import projekt.cloud.piece.music.player.util.ImageUtil.writeAlbumArt40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.writeAlbumArtRaw
import projekt.cloud.piece.music.player.util.MainActivityInterface

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val fragmentContainerView get() = binding.appBarMain.contentMain.fragmentContainerView
    private lateinit var navController: NavController

    private lateinit var activityInterface: MainActivityInterface
    private val albumByteArrayRawMap get() = activityInterface.albumByteArrayRawMap
    private val albumBitmap40DpMap get() = activityInterface.albumBitmap40DpMap

    private lateinit var audioDatabase: AudioDatabase

    override fun onCreate(savedInstanceState: Bundle?) {

        activityInterface = MainActivityInterface()

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        navController = fragmentContainerView.getFragment<NavHostFragment>().navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home), binding.root)

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        audioDatabase = databaseBuilder(this, AudioDatabase::class.java, DATABASE_NAME).build()

        val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            when {
                it -> initialApplication()
            }
        }

        when (checkSelfPermission(this, READ_EXTERNAL_STORAGE)) {
            PERMISSION_GRANTED -> {}
            else -> requestPermission.launch(READ_EXTERNAL_STORAGE)
        }

    }

    private fun initialApplication() = io {
        val audioList = arrayListOf<AudioItem>()
        val albumList = arrayListOf<AlbumItem>()
        val artistList = arrayListOf<ArtistItem>()
        loadFromDatabase(audioList, albumList, artistList)
        initialImage(albumList)
    }

    private fun launchApplication() = io {
        loadDatabase()
    }

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
            albumByteArrayRawMap[albumItem.id] = writeAlbumArtRaw(albumItem.id, bitmap)
            matrix.apply { setScale(bitmapSize / bitmap.widthF, bitmapSize / bitmap.heightF) }
            writeAlbumArt40Dp(albumItem.id, bitmap)
            albumBitmap40DpMap[albumItem.id] = bitmap
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

    private fun loadDatabase() {
        activityInterface.audioList = audioDatabase.audio.query()
        // activityInterface.artistList = audioDatabase.artist.query()
        activityInterface.albumList = audioDatabase.album.query()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp() =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

}