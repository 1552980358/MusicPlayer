package sakuraba.saki.player.music.web.util

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import sakuraba.saki.player.music.service.util.AudioInfo

object AudioInfoUtil {

    val ArrayList<AudioInfo>.convertIntoJson get() = JsonArray().let { jsonArray ->
        forEach { audioInfo ->
            jsonArray.add(
                JsonObject().apply {
                    addProperty("id", audioInfo.audioId)
                    addProperty("title", audioInfo.audioTitle)
                    addProperty("artist", audioInfo.audioArtist)
                    addProperty("album", audioInfo.audioAlbum)
                    addProperty("albumId", audioInfo.audioAlbumId.toString())
                    addProperty("duration", audioInfo.audioDuration.toString())
                }
            )
        }
        JsonObject().also { jsonObject -> jsonObject.add("list", jsonArray) }.toString()
    }

}