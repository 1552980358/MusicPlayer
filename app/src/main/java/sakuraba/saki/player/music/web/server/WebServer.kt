package sakuraba.saki.player.music.web.server

import android.content.Context
import android.graphics.Bitmap.CompressFormat.JPEG
import android.net.ConnectivityManager
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

class WebServer(private val port: Int, private val context: Context, private val connectivityManager: ConnectivityManager): NanoHTTPD(port) {

    companion object {
        private const val TAG = "WebServer"
        private const val SLASH = "/"
        private const val EMPTY = ""
        private const val URL_GET_INDEX = "${SLASH}index.html"
        private const val URL_GET_MUSIC_LIST = "${SLASH}getMusicList"
        private const val UTL_GET_ALBUM_ART = "${SLASH}getAlbumArt"
        private const val URL_GET_DEFAULT_ALBUM_ART = "${SLASH}getDefaultAlbumArt"
        private const val CONTENT_TYPE_CHARSET = ";charset=UTF-8"
    }

    override fun serve(session: IHTTPSession?): Response {
        val uri = session?.uri!!
        return when (session.uri) {
            EMPTY, SLASH, URL_GET_INDEX -> {
                val indexStream = context.assets.open("web${separator}index.html")
                newFixedLengthResponse(OK, MIME_HTML + CONTENT_TYPE_CHARSET, indexStream, indexStream.available().toLong())
            }
            URL_GET_MUSIC_LIST -> musicListJsonResponse(session)
            UTL_GET_ALBUM_ART -> albumArtResponse(session)
            URL_GET_DEFAULT_ALBUM_ART -> defaultAlbumArtResponse
            else -> {
                val indexStream = context.assets.open("web$uri")
                val mimeType = when {
                    uri.endsWith("css") -> "text/css$CONTENT_TYPE_CHARSET"
                    uri.endsWith("js") -> "text/javascript$CONTENT_TYPE_CHARSET"
                    uri.endsWith("ico") -> "image/x-icon"
                    else -> MIME_HTML
                }
                newFixedLengthResponse(OK, mimeType, indexStream, indexStream.available().toLong())
            }
        }
    }

    private fun musicListJsonResponse(session: IHTTPSession?): Response {
        val arrayList = arrayListOf<AudioInfo>()
        AudioDatabaseHelper(context).queryAllAudio(arrayList)
        arrayList.sortBy { audioInfo -> audioInfo.audioTitlePinyin }
        return newFixedLengthResponse(arrayList.convertIntoJson).apply {
            addHeader("Access-Control-Allow-Origin", session?.headers?.get("Access-Control-Allow-Origin") ?: "*")
            addHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS")
            addHeader("Access-Control-Allow-Headers", session?.headers?.get("Access-Control-Allow-Headers") ?: "*")
            addHeader("Access-Control-Allow-Credentials", "true")
            addHeader("Access-Control-Max-Age", "0")
            addHeader("Content-Type", "application/json$CONTENT_TYPE_CHARSET")
        }
    }

    private fun albumArtResponse(session: IHTTPSession?): Response {
        val id = session?.parameters?.get("albumId")?.first()?.toLong() ?: return defaultAlbumArtResponse
        val byteArray = tryRun { context.loadAlbumArt(id)?.getByteArray(format = JPEG) } ?: return defaultAlbumArtResponse
        return newFixedLengthResponse(OK, "image/x-icon", ByteArrayInputStream(byteArray), byteArray.size.toLong())
    }

    private val defaultAlbumArtResponse get() =
        context.assets.open("mipmap_music.png").readBytes().run {
            newFixedLengthResponse(OK, "image/png", ByteArrayInputStream(this), size.toLong())
        }

}