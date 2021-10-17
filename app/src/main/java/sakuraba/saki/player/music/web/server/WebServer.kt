package sakuraba.saki.player.music.web.server

import android.content.Context
import android.graphics.Bitmap.CompressFormat.JPEG
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response.Status.OK
import lib.github1552980358.ktExtension.android.graphics.getByteArray
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import sakuraba.saki.player.music.database.AudioDatabaseHelper
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArt
import sakuraba.saki.player.music.web.util.AudioInfoUtil.convertIntoJson
import java.io.ByteArrayInputStream
import java.io.File.separator

class WebServer(port: Int, private val context: Context): NanoHTTPD(port) {

    companion object {
        private const val SLASH = "/"
        private const val EMPTY = ""
        private const val URL_GET_MUSIC_LIST = "${SLASH}getMusicList"
        private const val UTL_GET_ALBUM_ART = "${SLASH}getAlbumArt"
        private const val URL_GET_DEFAULT_ALBUM_ART = "${SLASH}getDefaultAlbumArt"
    }

    override fun serve(session: IHTTPSession?): Response {

        return when (session?.uri) {
            EMPTY, SLASH -> {
                val indexStream = context.assets.open("web${separator}index.html")
                newFixedLengthResponse(OK, MIME_HTML, indexStream, indexStream.available().toLong())
            }
            URL_GET_MUSIC_LIST -> musicListJsonResponse(session)
            UTL_GET_ALBUM_ART -> albumArtResponse(session)
            URL_GET_DEFAULT_ALBUM_ART -> defaultAlbumArtResponse
            else -> newFixedLengthResponse(session?.uri)
        }
    }

    private fun musicListJsonResponse(session: IHTTPSession?): Response {
        val arrayList = arrayListOf<AudioInfo>()
        AudioDatabaseHelper(context).queryAllAudio(arrayList)
        return newFixedLengthResponse(arrayList.convertIntoJson).apply {
            addHeader("Access-Control-Allow-Origin", session?.headers?.get("Access-Control-Allow-Origin") ?: "*")
            addHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS")
            addHeader("Access-Control-Allow-Headers", session?.headers?.get("Access-Control-Allow-Headers") ?: "*")
            addHeader("Access-Control-Allow-Credentials", "true")
            addHeader("Access-Control-Max-Age", "0")
            addHeader("Content-Type", "application/json")
        }
    }

    private fun albumArtResponse(session: IHTTPSession?): Response {
        val id = session?.parameters?.get("albumId")?.first()?.toLong() ?: return newFixedLengthResponse("")
        val byteArray = tryRun { context.loadAlbumArt(id)?.getByteArray(format = JPEG) } ?: context.assets.open("mipmap_music.png").readBytes()
        val bufferedInputStream = ByteArrayInputStream(byteArray)
        return newFixedLengthResponse(OK, "image/jpeg", bufferedInputStream, byteArray.size.toLong())
    }

    private val defaultAlbumArtResponse get() =
        context.assets.open("mipmap_music.png").readBytes().run {
            newFixedLengthResponse(OK, "image/png", ByteArrayInputStream(this), size.toLong())
        }

}