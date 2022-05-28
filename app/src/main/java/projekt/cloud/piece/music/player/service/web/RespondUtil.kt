package projekt.cloud.piece.music.player.service.web

import org.json.JSONArray
import org.json.JSONObject
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.database.audio.item.PlaylistItem

object RespondUtil {
    
    const val RESPOND_STATUS = "status"
    
    private const val PLAYLIST_ID = "id"
    private const val PLAYLIST_TITLE = "title"
    private const val PLAYLIST_DESCRIPTION = "description"
    
    const val PLAYLIST_RESPOND_URI = "uri"
    const val PLAYLIST_RESPOND_LIST = "list"
    
    private val PlaylistItem.jsonObject get() = JSONObject().also {
        it.put(PLAYLIST_ID, id)
        it.put(PLAYLIST_TITLE, title)
        it.put(PLAYLIST_DESCRIPTION, description)
    }
    
    @JvmStatic
    val List<PlaylistItem>.playlistJsonArray get() = JSONArray().also { jsonArray ->
        forEach { jsonArray.put(it.jsonObject) }
    }
    
    const val AUDIO_RESPOND_URI = "uri"
    const val AUDIO_RESPOND_LIST = "list"
    private const val AUDIO_ID = "id"
    private const val AUDIO_TITLE = "title"
    private const val AUDIO_ALBUM = "album"
    private const val AUDIO_ALBUM_TITLE = "album_title"
    private const val AUDIO_ARTIST = "artist"
    private const val AUDIO_ARTIST_NAME = "artist_name"
    private const val AUDIO_DURATION = "duration"
    
    @JvmStatic
    val AudioItem.jsonObject get() = JSONObject().also {
        it.put(AUDIO_ID, id)
        it.put(AUDIO_TITLE, title)
        it.put(AUDIO_ARTIST, artist)
        it.put(AUDIO_ARTIST_NAME, artistName)
        it.put(AUDIO_ALBUM, album)
        it.put(AUDIO_ALBUM_TITLE, albumTitle)
        it.put(AUDIO_DURATION, duration)
    }
    
    @JvmStatic
    val List<AudioItem>.audioJsonArray get() = JSONArray().also { jsonArray ->
        forEach { jsonArray.put(it.jsonObject) }
    }
    
}