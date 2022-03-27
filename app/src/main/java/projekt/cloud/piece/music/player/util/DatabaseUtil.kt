package projekt.cloud.piece.music.player.util

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Matrix
import android.provider.MediaStore
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lib.github1552980358.ktExtension.android.graphics.heightF
import lib.github1552980358.ktExtension.android.graphics.widthF
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import mkaflowski.mediastylepalette.MediaNotificationProcessor
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.AudioDatabase
import projekt.cloud.piece.music.player.database.base.BaseItem
import projekt.cloud.piece.music.player.database.item.AlbumItem
import projekt.cloud.piece.music.player.database.item.ArtistItem
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.database.item.ColorItem
import projekt.cloud.piece.music.player.database.item.ColorItem.Companion.TYPE_ARTIST
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArt
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArts40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.loadAudioArt40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.writeAlbumArt40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.writeAlbumArtRaw
import projekt.cloud.piece.music.player.util.SharedPreferencesUtil.SP_FILE_FILTER_DURATION_DEFAULT
import projekt.cloud.piece.music.player.util.SharedPreferencesUtil.SP_FILE_FILTER_DURATION_INITIAL

object DatabaseUtil {

    fun initializeApp(context: Context,
                      sharedPreferences: SharedPreferences,
                      database: AudioDatabase,
                      audioArtMap: MutableMap<String, Bitmap>,
                      albumArtMap: MutableMap<String, Bitmap>,
                      callback: (List<AudioItem>) -> Unit) = io {
        val audioList = arrayListOf<AudioItem>()
        val albumList = arrayListOf<AlbumItem>()
        val artistList = arrayListOf<ArtistItem>()
        querySystemDatabase(context, audioList, albumList, artistList)
        storeToDatabase(database, audioList, albumList, artistList)
        initialImage(context, database, albumList)
        launchApp(context, sharedPreferences, database, audioArtMap, albumArtMap, callback)
    }

    fun launchAppCoroutine(context: Context,
                           sharedPreferences: SharedPreferences,
                           database: AudioDatabase,
                           audioArtMap: MutableMap<String, Bitmap>,
                           albumArtMap: MutableMap<String, Bitmap>,
                           callback: (List<AudioItem>) -> Unit) = io {
        launchApp(context, sharedPreferences, database, audioArtMap, albumArtMap, callback)
    }

    fun AudioDatabase.syncAudioList(context: Context, completeCallback: (List<AudioItem>, List<AlbumItem>, List<ArtistItem>) -> Unit) {
        val systemAudioList = arrayListOf<AudioItem>()
        val systemAlbumList = arrayListOf<AlbumItem>()
        val systemArtistList = arrayListOf<ArtistItem>()
        querySystemDatabase(context, systemAudioList, systemAlbumList, systemArtistList)

        compareAudioList(this, systemAudioList)
        compareAlbumList(context, this, systemAlbumList)
        compareArtistList(this, systemArtistList)

        completeCallback(loadDatabase(context, getDefaultSharedPreferences(context), this), systemAlbumList, systemArtistList)
    }

    private fun launchApp(context: Context,
                          sharedPreferences: SharedPreferences,
                          database: AudioDatabase,
                          audioArtMap: MutableMap<String, Bitmap>,
                          albumArtMap: MutableMap<String, Bitmap>,
                          callback: (List<AudioItem>) -> Unit) = runBlocking {
        launch { context.loadAudioArt40Dp(audioArtMap) }
        launch { context.loadAlbumArts40Dp(albumArtMap) }
        callback(loadDatabase(context, sharedPreferences, database))
    }

    fun loadDatabase(context: Context,
                     sharedPreferences: SharedPreferences,
                     database: AudioDatabase): List<AudioItem> {
        var filterDuration = SP_FILE_FILTER_DURATION_INITIAL
        if (sharedPreferences.getBoolean(context.getString(R.string.key_setting_file_filter_duration_enable), true)) {
            filterDuration = sharedPreferences.getString(
                context.getString(R.string.key_setting_file_filter_duration_set),
                SP_FILE_FILTER_DURATION_DEFAULT
            ) ?: SP_FILE_FILTER_DURATION_DEFAULT
        }
        var filterFileSize = "1"
        return database.audio.query(filterDuration, filterDuration).toMutableList().apply {
            this.sortBy { it.pinyin }
            forEachIndexed { index, audioItem ->
                audioItem.index = index
                audioItem.albumItem = database.album.query(audioItem.album)
                audioItem.artistItem = database.artist.query(audioItem.artist)
            }
        }
    }

    private fun querySystemDatabase(context: Context, audioList: ArrayList<AudioItem>, albumList: ArrayList<AlbumItem>, artistList: ArrayList<ArtistItem>) {
        arrayListOf<AudioItem>().apply {
            context.contentResolver.query(
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

    private fun storeToDatabase(database: AudioDatabase, audioList: ArrayList<AudioItem>, albumList: ArrayList<AlbumItem>, artistList: ArrayList<ArtistItem>) {
        database.audio.insert(*audioList.toTypedArray())
        database.album.insert(*albumList.toTypedArray())
        database.artist.insert(*artistList.toTypedArray())
        artistList.forEach { database.color.insert(ColorItem(it.id, TYPE_ARTIST)) }
    }

    private fun initialImage(context: Context, database: AudioDatabase, albumList: List<AlbumItem>) {
        var bitmap: Bitmap?
        val matrix = Matrix()
        val bitmapSize = context.resources.getDimensionPixelSize(R.dimen.md_spec_list_image_size)
        for (albumItem in albumList) {
            bitmap = tryRun { context.loadAlbumArt(albumItem.id) }
            if (bitmap == null) {
                database.color.insert(ColorItem(albumItem.id, ColorItem.TYPE_ALBUM))
                continue
            }
            context.writeAlbumArtRaw(albumItem.id, bitmap)
            matrix.apply { setScale(bitmapSize / bitmap.widthF, bitmapSize / bitmap.heightF) }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false).apply {
                context.writeAlbumArt40Dp(albumItem.id, this)
            }
            MediaNotificationProcessor(context, bitmap).apply {
                database.color.insert(ColorItem(albumItem.id, ColorItem.TYPE_ALBUM, backgroundColor, primaryTextColor, secondaryTextColor))
            }
            bitmap.recycle()
        }
    }

    private fun compareAudioList(database: AudioDatabase, systemList: List<AudioItem>) {
        val databaseList = database.audio.query()

        @Suppress("UNCHECKED_CAST")
        val addList = compareItemList(systemList, databaseList) as List<AudioItem>
        @Suppress("UNCHECKED_CAST")
        val deleteList = compareItemList(databaseList, systemList) as List<AudioItem>
        database.audio.delete(*deleteList.toTypedArray())
        database.audio.insert(*addList.toTypedArray())
        // Update data info
        database.audio.update(systemList)
        // Update color
        deleteList.forEach {
            database.color.queryForAudio(it.id)?.let { colorItem -> database.color.delete(colorItem) }
        }
    }

    private fun compareAlbumList(context: Context, database: AudioDatabase, systemList: List<AlbumItem>) {
        val databaseList = database.album.query()

        @Suppress("UNCHECKED_CAST")
        val addList = compareItemList(systemList, databaseList) as List<AlbumItem>
        @Suppress("UNCHECKED_CAST")
        val deleteList = compareItemList(databaseList, systemList) as List<AlbumItem>
        database.album.delete(*deleteList.toTypedArray())
        database.album.insert(*addList.toTypedArray())
        // Update data info
        database.album.update(systemList)
        // Remove color
        deleteList.forEach { database.color.delete(database.color.query(it.id)) }
        initialImage(context, database, addList)
    }

    private fun compareArtistList(database: AudioDatabase, systemList: List<ArtistItem>) {
        val databaseList = database.artist.query()
        @Suppress("UNCHECKED_CAST")
        val addList = compareItemList(systemList, databaseList) as List<ArtistItem>
        @Suppress("UNCHECKED_CAST")
        val deleteList = compareItemList(databaseList, systemList) as List<ArtistItem>
        database.artist.delete(*deleteList.toTypedArray())
        database.artist.insert(*addList.toTypedArray())
        // Update database info
        database.artist.update(systemList)
        deleteList.forEach { database.color.delete(database.color.query(it.id)) }
        addList.forEach { database.color.insert(ColorItem(it.id, TYPE_ARTIST)) }
    }

    private fun compareItemList(itemListBased: List<BaseItem>, itemListCompared: List<BaseItem>) = arrayListOf<BaseItem>().apply {
        itemListBased.forEach { basedItem ->
            if (itemListCompared.find { comparedItem -> basedItem.id == comparedItem.id } == null) {
                add(basedItem)
            }
        }
    }

}