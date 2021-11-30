package sakuraba.saki.player.music.web.server

import android.content.Context
import android.graphics.Bitmap.CompressFormat.JPEG
import android.util.Log
import androidx.core.content.ContextCompat
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response.Status.OK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lib.github1552980358.ktExtension.android.graphics.getByteArray
import lib.github1552980358.ktExtension.android.graphics.toBitmap
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.database.AudioDatabaseHelper
import sakuraba.saki.player.music.service.PlayService
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.service.util.startService
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArt
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArtRaw
import sakuraba.saki.player.music.util.BitmapUtil.loadAudioArtRaw
import sakuraba.saki.player.music.util.Constants.ACTION_EXTRA
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_LIST
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_POS
import sakuraba.saki.player.music.util.Constants.EXTRAS_DATA
import sakuraba.saki.player.music.util.Constants.EXTRA_INIT
import sakuraba.saki.player.music.util.Constants.EXTRA_MEDIA_ID
import sakuraba.saki.player.music.web.util.AudioInfoUtil.convertIntoJson
import sakuraba.saki.player.music.web.util.WebControlUtil
import java.io.ByteArrayInputStream
import java.io.File.separator

class WebServer(port: Int, private val context: Context, private val webControlUtil: WebControlUtil): NanoHTTPD(port) {

    companion object {
        private const val TAG = "WebServer"
        private const val SLASH = "/"
        private const val EMPTY = ""
        private const val URL_GET_INDEX = "${SLASH}index.html"
        private const val URL_GET_MUSIC_LIST = "${SLASH}getMusicList"
        private const val UTL_GET_ALBUM_ART = "${SLASH}getAlbumArt"
        private const val URL_GET_DEFAULT_ALBUM_ART = "${SLASH}getDefaultAlbumArt"
        private const val CONTENT_TYPE_CHARSET = ";charset=UTF-8"
        private const val URI_PLAY_AUDIO = "${SLASH}playAudio"
    }

    override fun serve(session: IHTTPSession?): Response {
        val uri = session?.uri ?: return newFixedLengthResponse("404 Unknown Uri")
        return when (session.uri) {
            EMPTY, SLASH, URL_GET_INDEX -> {
                Log.e(TAG, "index")
                val indexStream = context.assets.open("web${separator}index.html")
                newFixedLengthResponse(OK, "$MIME_HTML$CONTENT_TYPE_CHARSET", indexStream, indexStream.available().toLong()).withHeaders(session)
            }
            URL_GET_MUSIC_LIST -> musicListJsonResponse(session)
            UTL_GET_ALBUM_ART -> albumArtResponse(session)
            URL_GET_DEFAULT_ALBUM_ART -> defaultAlbumArtResponse
            URI_PLAY_AUDIO -> playMusic(session)
            else -> {
                val indexStream = context.assets.open("web$uri")
                newFixedLengthResponse(OK, uri.mimeType, indexStream, indexStream.available().toLong())
            }
        }
    }

    private fun musicListJsonResponse(session: IHTTPSession): Response {
        Log.e(TAG, "musicListJsonResponse")
        val arrayList = arrayListOf<AudioInfo>()
        AudioDatabaseHelper(context).queryAllAudio(arrayList)
        arrayList.sortBy { audioInfo -> audioInfo.audioTitlePinyin }
        return newFixedLengthResponse(OK, "application/json$CONTENT_TYPE_CHARSET", arrayList.convertIntoJson).withHeaders(session)
    }

    private fun albumArtResponse(session: IHTTPSession): Response {
        val albumId = session.parameters["albumId"]?.first() ?: return defaultAlbumArtResponse
        val id = session.parameters["id"]?.first() ?: return defaultAlbumArtResponse
        val byteArray =
            (context.loadAudioArtRaw(id)
                ?: context.loadAlbumArtRaw(albumId)
                ?: ContextCompat.getDrawable(context, R.drawable.ic_music)!!.toBitmap()
                ?: return defaultAlbumArtResponse).getByteArray()!!
        return newFixedLengthResponse(OK, "image/x-icon", ByteArrayInputStream(byteArray), byteArray.size.toLong())
    }

    private val defaultAlbumArtResponse get() =
        context.assets.open("mipmap_music.png").readBytes().run {
            Log.e(TAG, "defaultAlbumArtResponse")
            newFixedLengthResponse(OK, "image/png", ByteArrayInputStream(this), size.toLong())
        }

    private fun playMusic(session: IHTTPSession): Response {
        Log.e(TAG, "playMusic")
        val id = session.parameters["id"]?.first()?.toString() ?: return newFixedLengthResponse("404 Unknown Uri")
        val pos = session.parameters["pos"]?.first()?.toInt() ?: return newFixedLengthResponse("404 Unknown Uri")
        Log.e(TAG, "id=$id pos=$pos")
        val arrayList = arrayListOf<AudioInfo>()
        AudioDatabaseHelper(context).queryAllAudio(arrayList)
        arrayList.sortBy { audioInfo -> audioInfo.audioTitlePinyin }
        CoroutineScope(Dispatchers.Main).launch {
            if (!webControlUtil.playServiceStarted) {
                context.startService(PlayService::class.java) {
                    putExtra(ACTION_EXTRA, EXTRA_INIT)
                    putExtra(EXTRAS_DATA, webControlUtil)
                    putExtra(EXTRAS_AUDIO_INFO_LIST, arrayList)
                    putExtra(EXTRAS_AUDIO_INFO_POS, pos)
                    putExtra(EXTRA_MEDIA_ID, id)
                }
            }
            webControlUtil.playFromMediaId(pos, id, arrayList)
        }
        return newFixedLengthResponse("COMPLETE").withHeaders(session)
    }

    private val String.mimeType get() = when {
        endsWith("css") -> "text/css$CONTENT_TYPE_CHARSET"
        endsWith("js") -> "text/javascript$CONTENT_TYPE_CHARSET"
        endsWith("ico") -> "image/x-icon"
        else -> MIME_HTML
    }

    private fun Response.withHeaders(session: IHTTPSession) = apply {
        addHeader("Access-Control-Allow-Origin", session.headers["Access-Control-Allow-Origin"] ?: "*")
        addHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS")
        addHeader("Access-Control-Allow-Headers", session.headers["Access-Control-Allow-Headers"] ?: "*")
        addHeader("Access-Control-Allow-Credentials", "true")
        addHeader("Access-Control-Max-Age", "0")
    }

}