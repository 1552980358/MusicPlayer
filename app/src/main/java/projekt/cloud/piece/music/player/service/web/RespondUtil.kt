package projekt.cloud.piece.music.player.service.web

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.database.audio.item.PlaylistItem

object RespondUtil {
    
    const val RESPOND_STATUS = "status"
    
    private const val PLAYLIST_ID = "id"
    private const val PLAYLIST_TITLE = "title"
    private const val PLAYLIST_DESCRIPTION = "description"
    
    const val PLAYLIST_RESPOND_URI = "uri"
    const val PLAYLIST_RESPOND_LIST = "list"
    
    private val PlaylistItem.jsonObject get() = JsonObject().also {
        it.addProperty(PLAYLIST_ID, id)
        it.addProperty(PLAYLIST_TITLE, title)
        it.addProperty(PLAYLIST_DESCRIPTION, description)
    }
    
    @JvmStatic
    val List<PlaylistItem>.playlistJsonArray get() = JsonArray().also { jsonArray ->
        forEach { jsonArray.add(it.jsonObject) }
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
    val AudioItem.jsonObject get() = JsonObject().also {
        it.addProperty(AUDIO_ID, id)
        it.addProperty(AUDIO_TITLE, title)
        it.addProperty(AUDIO_ARTIST, artist)
        it.addProperty(AUDIO_ARTIST_NAME, artistName)
        it.addProperty(AUDIO_ALBUM, album)
        it.addProperty(AUDIO_ALBUM_TITLE, albumTitle)
        it.addProperty(AUDIO_DURATION, duration)
    }
    
    @JvmStatic
    val List<AudioItem>.audioJsonArray get() = JsonArray().also { jsonArray ->
        forEach { jsonArray.add(it.jsonObject) }
    }
    
}