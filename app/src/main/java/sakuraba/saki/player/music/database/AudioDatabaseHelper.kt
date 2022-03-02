package sakuraba.saki.player.music.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly
import lib.github1552980358.ktExtension.jvm.util.addInstance
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.util.MediaAlbum
import sakuraba.saki.player.music.util.Playlist

class AudioDatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    
    companion object {
        private const val DATABASE_NAME = "AudioDatabase.db"
        private const val DATABASE_VERSION = 1

        private const val COUNT_SIZE = "size"
        
        const val TABLE_AUDIO = "AudioTable"
        const val TABLE_ALBUM = "AlbumTable"
        const val TABLE_PLAYLIST = "PlaylistTable"
        const val TABLE_PLAYLIST_CONTENT = "PlaylistContentTable"
        
        private const val KEY_AUDIO_ID = "audio_id"
        private const val KEY_AUDIO_TITLE = "audio_title"
        private const val KEY_AUDIO_TITLE_PINYIN = "audio_title_pinyin"
        private const val KEY_AUDIO_ARTIST = "audio_artist"
        private const val KEY_AUDIO_ARTIST_PINYIN = "audio_artist_pinyin"
        private const val KEY_AUDIO_ALBUM = "audio_album"
        private const val KEY_AUDIO_ALBUM_PINYIN = "audio_album_pinyin"
        private const val KEY_AUDIO_ALBUM_ID = "audio_album_id"
        private const val KEY_AUDIO_DURATION = "audio_duration"
        private const val KEY_AUDIO_SIZE = "audio_size"
        private const val KEY_AUDIO_PATH = "audio_path"
        
        private const val KEY_ALBUM_ID = "album_id"
        private const val KEY_ALBUM_TITLE = "album_title"
        private const val KEY_ALBUM_TITLE_PINYIN = "album_title_pinyin"
        private const val KEY_ALBUM_NUMBER_OF_AUDIO = "album_number_of_audio"

        private const val KEY_PLAYLIST_TITLE = "playlist_title"
        private const val KEY_PLAYLIST_TITLE_PINYIN = "playlist_title_pinyin"
        private const val KEY_PLAYLIST_DESCRIPTION = "playlist_description"

        private const val KEY_PLAYLIST_CONTENT_PLAYLIST_TITLE = "playlist_title"
        private const val KEY_PLAYLIST_CONTENT_AUDIO_ID = "audio_id"

    }

    @Volatile
    private var hasTaskWorking = false
    
    override fun onCreate(sqLiteDatabase: SQLiteDatabase?) {
        sqLiteDatabase?.execSQL(
            "create table if not exists $TABLE_AUDIO(" +
                "$KEY_AUDIO_ID text not null, " +
                "$KEY_AUDIO_TITLE text, " +
                "$KEY_AUDIO_TITLE_PINYIN text, " +
                "$KEY_AUDIO_ARTIST text, " +
                "$KEY_AUDIO_ARTIST_PINYIN text, " +
                "$KEY_AUDIO_ALBUM text, " +
                "$KEY_AUDIO_ALBUM_PINYIN text, " +
                "$KEY_AUDIO_ALBUM_ID text, " +
                "$KEY_AUDIO_DURATION long," +
                "$KEY_AUDIO_SIZE long," +
                "$KEY_AUDIO_PATH text," +
                "primary key ( $KEY_AUDIO_ID )" +
                ")"
        )
        sqLiteDatabase?.execSQL(
            "create table if not exists $TABLE_ALBUM(" +
                "$KEY_ALBUM_ID text not null, " +
                "$KEY_ALBUM_TITLE text, " +
                "$KEY_ALBUM_TITLE_PINYIN text, " +
                "$KEY_ALBUM_NUMBER_OF_AUDIO int," +
                "primary key ( $KEY_ALBUM_ID )" +
                ")"
        )
        sqLiteDatabase?.execSQL(
            "create table if not exists $TABLE_PLAYLIST (" +
                "$KEY_PLAYLIST_TITLE text not null primary key, " +
                "$KEY_PLAYLIST_TITLE_PINYIN text not null, " +
                "$KEY_PLAYLIST_DESCRIPTION text" +
                ") ;"
        )
        sqLiteDatabase?.execSQL(
            "create table if not exists $TABLE_PLAYLIST_CONTENT ( " +
                "$KEY_PLAYLIST_CONTENT_PLAYLIST_TITLE text not null, " +
                "$KEY_PLAYLIST_CONTENT_AUDIO_ID text not null " +
                ") ;"
        )
    }
    
    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase?, oldVersion: Int, newVersion: Int) = Unit
    
    fun insertAudio(audioInfoList: ArrayList<AudioInfo>) {
        hasTaskWorking = true
        writableDatabase.apply {
            audioInfoList.forEach { audioInfo ->
                insert(TABLE_AUDIO, null, ContentValues().apply {
                    put(KEY_AUDIO_ID, audioInfo.audioId)
                    put(KEY_AUDIO_TITLE, audioInfo.audioTitle)
                    put(KEY_AUDIO_TITLE_PINYIN, audioInfo.audioTitlePinyin)
                    put(KEY_AUDIO_ARTIST, audioInfo.audioArtist)
                    put(KEY_AUDIO_ARTIST_PINYIN, audioInfo.audioArtistPinyin)
                    put(KEY_AUDIO_ALBUM, audioInfo.audioAlbum)
                    put(KEY_AUDIO_ALBUM_PINYIN, audioInfo.audioAlbumPinyin)
                    put(KEY_AUDIO_ALBUM_ID, audioInfo.audioAlbumId)
                    put(KEY_AUDIO_DURATION, audioInfo.audioDuration)
                    put(KEY_AUDIO_SIZE, audioInfo.audioSize)
                    put(KEY_AUDIO_PATH, audioInfo.audioPath)
                })
            }
        }
    }

    fun insertMediaAlbum(mediaAlbumList: ArrayList<MediaAlbum>) {
        hasTaskWorking = true
        writableDatabase.apply {
            mediaAlbumList.forEach { mediaAlbum ->
                insert(TABLE_ALBUM, null, ContentValues().apply {
                    put(KEY_ALBUM_ID, mediaAlbum.albumId)
                    put(KEY_ALBUM_TITLE, mediaAlbum.title)
                    put(KEY_ALBUM_TITLE_PINYIN, mediaAlbum.titlePinyin)
                    put(KEY_ALBUM_NUMBER_OF_AUDIO, mediaAlbum.numberOfAudio)
                })
            }
        }
    }

    fun removeAudio(audioInfoList: ArrayList<AudioInfo>) {
        hasTaskWorking = true
        writableDatabase.apply {
            audioInfoList.forEach { audioInfo ->
                delete(TABLE_AUDIO, "${KEY_AUDIO_ID}=?", arrayOf(audioInfo.audioId))
            }
        }
    }

    fun removeMediaAlbum(mediaAlbumList: ArrayList<MediaAlbum>) {
        hasTaskWorking = true
        writableDatabase.apply {
            mediaAlbumList.forEach { mediaAlbum ->
                delete(TABLE_ALBUM, "${KEY_ALBUM_ID}=?", arrayOf(mediaAlbum.albumId.toString()))
            }
        }
    }

    fun updateAudio(audioInfoList: ArrayList<AudioInfo>) {
        hasTaskWorking = true
        writableDatabase.apply {
            audioInfoList.forEach { audioInfo ->
                update(TABLE_AUDIO, ContentValues().apply {
                    put(KEY_AUDIO_TITLE, audioInfo.audioTitle)
                    put(KEY_AUDIO_TITLE_PINYIN, audioInfo.audioTitlePinyin)
                    put(KEY_AUDIO_ARTIST, audioInfo.audioArtist)
                    put(KEY_AUDIO_ARTIST_PINYIN, audioInfo.audioArtistPinyin)
                    put(KEY_AUDIO_ALBUM, audioInfo.audioAlbum)
                    put(KEY_AUDIO_ALBUM_PINYIN, audioInfo.audioAlbumPinyin)
                    put(KEY_AUDIO_ALBUM_ID, audioInfo.audioAlbumId)
                    put(KEY_AUDIO_DURATION, audioInfo.audioDuration)
                    put(KEY_AUDIO_SIZE, audioInfo.audioSize)
                    put(KEY_AUDIO_PATH, audioInfo.audioPath)
                }, "${KEY_AUDIO_ID}=?", arrayOf(audioInfo.audioId))
            }
        }
    }

    fun updateAlbum(mediaAlbumList: ArrayList<MediaAlbum>) {
        hasTaskWorking = true
        writableDatabase.apply {
            mediaAlbumList.forEach { mediaAlbum ->
                update(TABLE_ALBUM, ContentValues().apply {
                    put(KEY_ALBUM_TITLE, mediaAlbum.title)
                    put(KEY_ALBUM_TITLE_PINYIN, mediaAlbum.titlePinyin)
                    put(KEY_ALBUM_NUMBER_OF_AUDIO, mediaAlbum.numberOfAudio)
                }, "${KEY_ALBUM_ID}=?", arrayOf(mediaAlbum.albumId.toString()))
            }
        }
    }

    fun queryAllAudio(arrayList: ArrayList<AudioInfo>) {
        readableDatabase.rawQuery("select * from $TABLE_AUDIO", null)?.apply {
            if (!moveToFirst()) {
                return@apply
            }
            do {
                tryOnly {
                    arrayList.add(AudioInfo(
                        getString(getColumnIndexOrThrow(KEY_AUDIO_ID)),
                        getString(getColumnIndexOrThrow(KEY_AUDIO_TITLE)),
                        getString(getColumnIndexOrThrow(KEY_AUDIO_TITLE_PINYIN)),
                        getString(getColumnIndexOrThrow(KEY_AUDIO_ARTIST)),
                        getString(getColumnIndexOrThrow(KEY_AUDIO_ARTIST_PINYIN)),
                        getString(getColumnIndexOrThrow(KEY_AUDIO_ALBUM)),
                        getString(getColumnIndexOrThrow(KEY_AUDIO_ALBUM_PINYIN)),
                        getLong(getColumnIndexOrThrow(KEY_AUDIO_ALBUM_ID)),
                        getLong(getColumnIndexOrThrow(KEY_AUDIO_DURATION)),
                        getLong(getColumnIndexOrThrow(KEY_AUDIO_SIZE)),
                        getString(getColumnIndexOrThrow(KEY_AUDIO_PATH))
                    ))
                }
            } while (moveToNext())
            close()
        }
    }

    fun queryAudioInfo(audioId: String): AudioInfo? {
        var audioInfo: AudioInfo? = null
        readableDatabase.rawQuery("select * from $TABLE_AUDIO where $KEY_AUDIO_ID=?", arrayOf(audioId))?.apply {
            if (moveToFirst()) {
                audioInfo = AudioInfo(
                    getString(getColumnIndexOrThrow(KEY_AUDIO_ID)),
                    getString(getColumnIndexOrThrow(KEY_AUDIO_TITLE)),
                    getString(getColumnIndexOrThrow(KEY_AUDIO_TITLE_PINYIN)),
                    getString(getColumnIndexOrThrow(KEY_AUDIO_ARTIST)),
                    getString(getColumnIndexOrThrow(KEY_AUDIO_ARTIST_PINYIN)),
                    getString(getColumnIndexOrThrow(KEY_AUDIO_ALBUM)),
                    getString(getColumnIndexOrThrow(KEY_AUDIO_ALBUM_PINYIN)),
                    getLong(getColumnIndexOrThrow(KEY_AUDIO_ALBUM_ID)),
                    getLong(getColumnIndexOrThrow(KEY_AUDIO_DURATION)),
                    getLong(getColumnIndexOrThrow(KEY_AUDIO_SIZE)),
                    getString(getColumnIndexOrThrow(KEY_AUDIO_PATH))
                )
            }
            close()
        }
        return audioInfo
    }
    
    fun queryMediaAlbum(arrayList: ArrayList<MediaAlbum>) {
        readableDatabase.rawQuery("select * from $TABLE_ALBUM", null)?.apply {
            if (!moveToFirst()) {
                return@apply
            }
            do {
                tryOnly {
                    arrayList.add(
                        MediaAlbum(
                            getLong(getColumnIndexOrThrow(KEY_ALBUM_ID)),
                            getString(getColumnIndexOrThrow(KEY_ALBUM_TITLE)),
                            getString(getColumnIndexOrThrow(KEY_ALBUM_TITLE_PINYIN)),
                            getInt(getColumnIndexOrThrow(KEY_ALBUM_NUMBER_OF_AUDIO))
                        )
                    )
                }
            } while (moveToNext())
            close()
        }
    }
    
    fun queryAudioForMediaAlbum(arrayList: ArrayList<AudioInfo>, albumId: Long) =
        readableDatabase.rawQuery("select * from $TABLE_AUDIO where $KEY_AUDIO_ALBUM_ID=?", arrayOf(albumId.toString())).apply {
            if (!moveToFirst()) {
                return@apply
            }
            do {
                arrayList.add(AudioInfo(
                    getString(getColumnIndexOrThrow(KEY_AUDIO_ID)),
                    getString(getColumnIndexOrThrow(KEY_AUDIO_TITLE)),
                    getString(getColumnIndexOrThrow(KEY_AUDIO_TITLE_PINYIN)),
                    getString(getColumnIndexOrThrow(KEY_AUDIO_ARTIST)),
                    getString(getColumnIndexOrThrow(KEY_AUDIO_ARTIST_PINYIN)),
                    getString(getColumnIndexOrThrow(KEY_AUDIO_ALBUM)),
                    getString(getColumnIndexOrThrow(KEY_AUDIO_ALBUM_PINYIN)),
                    getLong(getColumnIndexOrThrow(KEY_AUDIO_ALBUM_ID)),
                    getLong(getColumnIndexOrThrow(KEY_AUDIO_DURATION)),
                    getLong(getColumnIndexOrThrow(KEY_AUDIO_SIZE)),
                    getString(getColumnIndexOrThrow(KEY_AUDIO_PATH))
                ))
            } while (moveToNext())
        }.close()

    val hasTask get() = hasTaskWorking

    fun writeComplete() {
        writableDatabase.close()
        hasTaskWorking = false
    }

    fun readComplete() = readableDatabase.close()

    fun createPlaylist(playlist: Playlist) {
        hasTaskWorking = true
        writableDatabase.execSQL("insert into $TABLE_PLAYLIST values ( '${playlist.title}' , '${playlist.titlePinyin}' , \"\" ) ;")
    }

    fun updatePlaylist(originTitle: String, playlist: Playlist) {
        hasTaskWorking = true
        StringBuilder("update $TABLE_PLAYLIST set").apply {
            if (originTitle != playlist.title) {
                append("$KEY_PLAYLIST_TITLE=${playlist.title} , ")
                append("$KEY_PLAYLIST_TITLE_PINYIN=${playlist.titlePinyin} , ")
            }
            append("$KEY_PLAYLIST_DESCRIPTION=${playlist.description}")
            append("where $KEY_PLAYLIST_TITLE=$originTitle ; ")

            writableDatabase.execSQL(toString())
        }
    }

    fun removePlaylist(playlist: Playlist) {
        hasTaskWorking = true
        writableDatabase.execSQL("delete from $TABLE_PLAYLIST_CONTENT where $KEY_PLAYLIST_CONTENT_PLAYLIST_TITLE='${playlist.title}' ;")
        writableDatabase.execSQL("delete from $TABLE_PLAYLIST where $KEY_PLAYLIST_TITLE='${playlist.title}' ;")
    }

    fun addPlaylistContent(playlist: Playlist, audioInfo: AudioInfo) {
        hasTaskWorking = true
        writableDatabase.execSQL("insert into $TABLE_PLAYLIST_CONTENT values ( '${playlist.title}' , '${audioInfo.audioId}' ) ;")
    }

    fun removePlaylistContent(playlist: Playlist, audioId: String) {
        hasTaskWorking = true
        writableDatabase.execSQL("delete from $TABLE_PLAYLIST_CONTENT where $KEY_PLAYLIST_CONTENT_PLAYLIST_TITLE='${playlist.title}' and $KEY_PLAYLIST_CONTENT_AUDIO_ID='$audioId' ;")
    }

    fun queryAllPlaylist(playlists: ArrayList<Playlist>) {
        readableDatabase.rawQuery("select * from $TABLE_PLAYLIST", null).apply {
            if (!moveToFirst()) {
                return@apply
            }
            do {
                playlists.addInstance(
                    getString(getColumnIndexOrThrow(KEY_PLAYLIST_TITLE)),
                    getString(getColumnIndexOrThrow(KEY_PLAYLIST_TITLE_PINYIN)),
                    getString(getColumnIndexOrThrow(KEY_PLAYLIST_DESCRIPTION))
                )
            } while (moveToNext())
        }.close()
    }

    fun queryPlaylistContent(playlist: Playlist, audioInfoList: ArrayList<AudioInfo>) {
        readableDatabase.rawQuery("select * from $TABLE_PLAYLIST_CONTENT where $KEY_PLAYLIST_CONTENT_PLAYLIST_TITLE=?", arrayOf(playlist.titlePinyin)).apply {
            if (!moveToFirst()) {
                return@apply
            }
            do {
                getString(getColumnIndexOrThrow(KEY_PLAYLIST_CONTENT_AUDIO_ID)).also { id ->
                    audioInfoList.find { it.audioId == id }?.let {
                        if (!playlist.contains(it)) {
                            playlist += it
                        }
                    }
                }
            } while (moveToNext())
        }.close()
    }

}