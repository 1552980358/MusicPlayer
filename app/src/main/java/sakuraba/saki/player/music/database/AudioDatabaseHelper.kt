package sakuraba.saki.player.music.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly
import sakuraba.saki.player.music.service.util.AudioInfo

class AudioDatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    
    companion object {
        private const val DATABASE_NAME = "AudioDatabase.db"
        private const val DATABASE_VERSION = 1
        
        const val TABLE_AUDIO = "AudioTable"
        
        private const val KEY_AUDIO_ID = "audio_id"
        private const val KEY_AUDIO_TITLE = "audio_title"
        private const val KEY_AUDIO_TITLE_PINYIN = "audio_title_pinyin"
        private const val KEY_AUDIO_ARTIST = "audio_artist"
        private const val KEY_AUDIO_ARTIST_PINYIN = "audio_artist_pinyin"
        private const val KEY_AUDIO_ALBUM = "audio_album"
        private const val KEY_AUDIO_ALBUM_PINYIN = "audio_album_pinyin"
        private const val KEY_AUDIO_ALBUM_ID = "audio_album_id"
        private const val KEY_AUDIO_DURATION = "audio_duration"
        
        private const val KEY_ARRANGEMENT = "arrangement"
        
    }
    
    override fun onCreate(sqLiteDatabase: SQLiteDatabase?) {
        sqLiteDatabase?.execSQL(
            "create table if not exists $TABLE_AUDIO(" +
                "$KEY_ARRANGEMENT integer not null, " +
                "$KEY_AUDIO_ID int not null, " +
                "$KEY_AUDIO_TITLE text, " +
                "$KEY_AUDIO_TITLE_PINYIN text, " +
                "$KEY_AUDIO_ARTIST text, " +
                "$KEY_AUDIO_ARTIST_PINYIN text, " +
                "$KEY_AUDIO_ALBUM text, " +
                "$KEY_AUDIO_ALBUM_PINYIN text, " +
                "$KEY_AUDIO_ALBUM_ID text, " +
                "$KEY_AUDIO_DURATION long," +
                "primary key ( $KEY_ARRANGEMENT, $KEY_AUDIO_ID )" +
                ")"
        )
    }
    
    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase?, oldVersion: Int, newVersion: Int) = Unit
    
    fun insertAudio(table: String, audioInfoList: ArrayList<AudioInfo>) =
        writableDatabase.apply {
            audioInfoList.forEachIndexed { index, audioInfo ->
                insert(table, null, ContentValues().apply {
                    put(KEY_ARRANGEMENT, index)
                    put(KEY_AUDIO_ID, audioInfo.audioId)
                    put(KEY_AUDIO_TITLE, audioInfo.audioTitle)
                    put(KEY_AUDIO_TITLE_PINYIN, audioInfo.audioTitlePinyin)
                    put(KEY_AUDIO_ARTIST, audioInfo.audioArtist)
                    put(KEY_AUDIO_ARTIST_PINYIN, audioInfo.audioArtistPinyin)
                    put(KEY_AUDIO_ALBUM, audioInfo.audioAlbum)
                    put(KEY_AUDIO_ALBUM_PINYIN, audioInfo.audioAlbumPinyin)
                    put(KEY_AUDIO_ALBUM_ID, audioInfo.audioAlbumId)
                    put(KEY_AUDIO_DURATION, audioInfo.audioDuration)
                })
            }
        }.close()
    
    fun clearTable(table: String) = writableDatabase.apply { delete(table, null, null) }.close()
    
    fun queryAll(arrayList: ArrayList<AudioInfo>) = readableDatabase.rawQuery("select * from $TABLE_AUDIO", null)?.apply {
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
                    getLong(getColumnIndexOrThrow(KEY_AUDIO_DURATION))
                ))
            }
        } while (moveToNext())
    }?.close()
    
}