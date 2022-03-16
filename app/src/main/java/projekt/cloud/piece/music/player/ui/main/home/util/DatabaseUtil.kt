package projekt.cloud.piece.music.player.ui.main.home.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Matrix
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lib.github1552980358.ktExtension.android.graphics.heightF
import lib.github1552980358.ktExtension.android.graphics.widthF
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import mkaflowski.mediastylepalette.MediaNotificationProcessor
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.AudioDatabase
import projekt.cloud.piece.music.player.database.item.AlbumItem
import projekt.cloud.piece.music.player.database.item.ArtistItem
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.database.item.ColorItem
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArt
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArts40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.loadAudioArt40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.writeAlbumArt40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.writeAlbumArtRaw

object DatabaseUtil {

    fun initializeApp(context: Context,
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
        launchApp(context, database, audioArtMap, albumArtMap, callback)
    }

    fun launchAppCoroutine(context: Context,
                           database: AudioDatabase,
                           audioArtMap: MutableMap<String, Bitmap>,
                           albumArtMap: MutableMap<String, Bitmap>,
                           callback: (List<AudioItem>) -> Unit) = io {
        launchApp(context, database, audioArtMap, albumArtMap, callback)
    }

    private fun launchApp(context: Context,
                          database: AudioDatabase,
                          audioArtMap: MutableMap<String, Bitmap>,
                          albumArtMap: MutableMap<String, Bitmap>,
                          callback: (List<AudioItem>) -> Unit) = runBlocking {
        launch { context.loadAudioArt40Dp(audioArtMap) }
        launch { context.loadAlbumArts40Dp(albumArtMap) }
        database.audio.query().apply {
            forEach {
                it.albumItem = database.album.query(it.album)
                it.artistItem = database.artist.query(it.artist)
            }
            callback(this)
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
                    Log.e("TAG", "${audioList.last().id} ${audioList.last().title}")
                }
                close()
            }
        }
    }

    private fun storeToDatabase(database: AudioDatabase, audioList: ArrayList<AudioItem>, albumList: ArrayList<AlbumItem>, artistList: ArrayList<ArtistItem>) {
        database.audio.insert(*audioList.toTypedArray())
        database.album.insert(*albumList.toTypedArray())
        database.artist.insert(*artistList.toTypedArray())
    }

    private fun initialImage(context: Context, database: AudioDatabase, albumList: ArrayList<AlbumItem>) {
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

}