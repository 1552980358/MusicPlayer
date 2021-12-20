package sakuraba.saki.player.music.web.server

import android.content.Context
import android.os.Environment.DIRECTORY_MUSIC
import android.os.Environment.getExternalStoragePublicDirectory
import android.util.Log
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Method.OPTIONS
import fi.iki.elonen.NanoHTTPD.Method.POST
import fi.iki.elonen.NanoHTTPD.Response.Status.BAD_REQUEST
import fi.iki.elonen.NanoHTTPD.Response.Status.NO_CONTENT
import fi.iki.elonen.NanoHTTPD.Response.Status.NOT_FOUND
import fi.iki.elonen.NanoHTTPD.Response.Status.OK
import lib.github1552980358.ktExtension.android.graphics.getByteArray
import sakuraba.saki.player.music.database.AudioDatabaseHelper
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.service.util.mediaUriStr
import sakuraba.saki.player.music.service.util.parseAsUri
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArtRaw
import sakuraba.saki.player.music.util.BitmapUtil.loadAudioArtRaw
import sakuraba.saki.player.music.web.util.AudioInfoUtil.convertIntoJson
import java.io.ByteArrayInputStream
import java.io.File
import java.io.File.separator

class WebServer(port: Int, private val context: Context): NanoHTTPD(port) {

    companion object {
        private const val TAG = "WebServer"
        private const val SLASH = "/"
        private const val EMPTY = ""
        private const val URL_GET_INDEX = "${SLASH}index.html"
        private const val CONTENT_TYPE_CHARSET = ";charset=UTF-8"

        private const val UPLOAD = "${SLASH}upload"
        private const val UPLOAD_FILE = "file"
        private const val UPLOAD_FILE_NAME = "fileName"
        private const val UPLOAD_NAME_NOT_SPECIFIED = "Name Not Specified"
        private val String.UPLOAD_POST_ALLOW get() = "$this: Post Allowed"
        private val String.UPLOAD_ACCEPTED get() = "$this: Accepted"
        private const val UPLOAD_REQUIRE_POST = "Should use POST to upload"

        private const val DOWNLOAD = "${SLASH}download"
        private const val DOWNLOAD_AUDIO_ID = "id"
        private val String.DOWNLOAD_ALLOW get() = "$this: Allowed"
        private const val DOWNLOAD_AUDIO_ID_NOT_SPECIFIED = "Audio ID Not Specified"
        private val String.DOWNLOAD_AUDIO_ID_KNOWN get() = "$this: Unknown Audio Id"

        private const val REQUIRE = "${SLASH}require"
        private const val REQUIRE_TYPE = "type"
        private const val REQUIRE_TYPE_NOT_SPECIFIED = "Require Type Not Specified"
        private val String.REQUIRE_TYPE_UNKNOWN get() = "$this: Unknown Require Type"
        private const val REQUIRE_TYPE_AUDIO_LIST = "list"
        private const val REQUIRE_TYPE_AUDIO_ART = "art"
        private const val REQUIRE_TYPE_AUDIO_ART_AUDIO = "audio"
        private const val REQUIRE_TYPE_AUDIO_ART_ALBUM = "album"
        private const val REQUIRE_TYPE_AUDIO_ART_EMPTY_AUDIO_ALBUM = "Audio Id and Album Id should be specified"

    }

    override fun serve(session: IHTTPSession?): Response {
        val uri = session?.uri ?: return newFixedLengthResponse("404 Unknown Uri")
        return when (session.uri) {
            EMPTY, SLASH, URL_GET_INDEX -> {
                Log.e(TAG, "index")
                val indexStream = context.assets.open("web${separator}index.html")
                newFixedLengthResponse(OK, "$MIME_HTML$CONTENT_TYPE_CHARSET", indexStream, indexStream.available().toLong()).withHeaders(session)
            }
            UPLOAD -> upload(session)
            DOWNLOAD -> download(session)
            REQUIRE -> require(session)
            else -> {
                val indexStream = context.assets.open("web$uri")
                newFixedLengthResponse(OK, uri.mimeType, indexStream, indexStream.available().toLong())
            }
        }
    }

    private fun upload(session: IHTTPSession): Response {
        Log.e(TAG, "upload")
        val fileName = session.parameters[UPLOAD_FILE_NAME]?.first()
            ?: return newFixedLengthResponse(BAD_REQUEST, "$MIME_PLAINTEXT$CONTENT_TYPE_CHARSET", UPLOAD_NAME_NOT_SPECIFIED)
        return when (session.method) {
            OPTIONS -> responseCROS(session, fileName)
            POST -> {
                val file = HashMap<String, String>().apply { session.parseBody(this) }[UPLOAD_FILE]
                    ?: return responseCROS(session, fileName)
                File(getExternalStoragePublicDirectory(DIRECTORY_MUSIC), fileName).writeBytes(File(file).readBytes())
                return newFixedLengthResponse(OK, "$MIME_PLAINTEXT$CONTENT_TYPE_CHARSET", fileName.UPLOAD_ACCEPTED).withHeaders(session)
            }
            else -> newFixedLengthResponse(BAD_REQUEST, "$MIME_PLAINTEXT$CONTENT_TYPE_CHARSET", UPLOAD_REQUIRE_POST).withHeaders(session)
        }
    }

    private fun require(session: IHTTPSession): Response {
        Log.e(TAG, "require")
        return when (val type = session.parameters[REQUIRE_TYPE]?.first()) {
            REQUIRE_TYPE_AUDIO_LIST -> {
                val arrayList = arrayListOf<AudioInfo>()
                AudioDatabaseHelper(context).apply {
                    queryAllAudio(arrayList)
                    close()
                }
                newFixedLengthResponse(OK, "application/json$CONTENT_TYPE_CHARSET", arrayList.convertIntoJson).withHeaders(session)
            }
            REQUIRE_TYPE_AUDIO_ART -> {
                val audioId = session.parameters[REQUIRE_TYPE_AUDIO_ART_AUDIO]?.first()
                    ?: return newFixedLengthResponse(BAD_REQUEST, "$MIME_PLAINTEXT$CONTENT_TYPE_CHARSET", REQUIRE_TYPE_AUDIO_ART_EMPTY_AUDIO_ALBUM).withHeaders(session)

                val albumId = session.parameters[REQUIRE_TYPE_AUDIO_ART_ALBUM]?.first()
                    ?: return newFixedLengthResponse(BAD_REQUEST, "$MIME_PLAINTEXT$CONTENT_TYPE_CHARSET", REQUIRE_TYPE_AUDIO_ART_EMPTY_AUDIO_ALBUM).withHeaders(session)

                context.loadAudioArtRaw(audioId)?.let {
                    return ByteArrayInputStream(it.getByteArray()).run {
                        newFixedLengthResponse(OK, "image/jpeg", this, available().toLong()).withHeaders(session)
                    }
                }

                context.loadAlbumArtRaw(albumId.toLong())?.let {
                    return ByteArrayInputStream(it.getByteArray()).run {
                        newFixedLengthResponse(OK, "image/jpeg", this, available().toLong()).withHeaders(session)
                    }
                }

                context.assets.open("mipmap_music.png").run {
                    newFixedLengthResponse(OK, "image/png", this, available().toLong()).withHeaders(session)
                }
            }
            null -> newFixedLengthResponse(BAD_REQUEST, "$MIME_PLAINTEXT$CONTENT_TYPE_CHARSET", REQUIRE_TYPE_NOT_SPECIFIED).withHeaders(session)
            else -> newFixedLengthResponse(BAD_REQUEST, "$MIME_PLAINTEXT$CONTENT_TYPE_CHARSET", type.REQUIRE_TYPE_UNKNOWN).withHeaders(session)
        }
    }

    private fun download(session: IHTTPSession): Response {
        Log.e(TAG, "download")
        val audioId = session.parameters[DOWNLOAD_AUDIO_ID]?.first() ?: return newFixedLengthResponse(NO_CONTENT, MIME_PLAINTEXT, DOWNLOAD_AUDIO_ID_NOT_SPECIFIED)
        if (session.method == OPTIONS) {
            return newFixedLengthResponse(OK, MIME_PLAINTEXT, audioId.DOWNLOAD_ALLOW).apply {
                addHeader("Access-Control-Allow-Credentials", "true")
                addHeader("Access-Control-Allow-Origin", session.headers["origin"] ?: "*")
                session.headers["access-control-request-headers"]?.let { addHeader("Access-Control-Allow-Headers", it) }
                addHeader("Access-Control-Allow-Methods","POST,GET,OPTIONS")
                addHeader("Access-Control-Allow-Headers", session.headers["access-control-request-headers"] ?: "*")
                addHeader("Access-Control-Max-Age", "0")
            }
        }
        AudioDatabaseHelper(context).queryAudioInfo(audioId)?.let {
            context.contentResolver.openInputStream(audioId.mediaUriStr.parseAsUri)?.apply {
                return newFixedLengthResponse(OK, "audio/${it.audioPath.substring(it.audioPath.indexOf('.') + 1)}",this, available().toLong())
            }
        }
        return newFixedLengthResponse(NOT_FOUND, MIME_PLAINTEXT, audioId.DOWNLOAD_AUDIO_ID_KNOWN)
    }

    private fun responseCROS(session: IHTTPSession, fileName: String): Response = newFixedLengthResponse(OK, "$MIME_PLAINTEXT$CONTENT_TYPE_CHARSET", fileName.UPLOAD_POST_ALLOW).apply {
        addHeader("Access-Control-Allow-Credentials", "true")
        addHeader("Access-Control-Allow-Origin", session.headers["origin"] ?: "*")
        session.headers["access-control-request-headers"]?.let { addHeader("Access-Control-Allow-Headers", it) }
        addHeader("Access-Control-Allow-Methods","POST,GET,OPTIONS")
        addHeader("Access-Control-Allow-Headers", session.headers["access-control-request-headers"] ?: "*")
        addHeader("Access-Control-Max-Age", "0")
    }

    private val String.mimeType get() = when {
        endsWith("css") -> "text/css$CONTENT_TYPE_CHARSET"
        endsWith("js") -> "text/javascript$CONTENT_TYPE_CHARSET"
        endsWith("ico") -> "image/x-icon"
        else -> MIME_HTML
    }

    private fun Response.withHeaders(session: IHTTPSession) = apply {
        addHeader("Access-Control-Allow-Origin", session.headers["Access-Control-Allow-Origin"] ?: "*")
        addHeader("Access-Control-Allow-Methods", session.headers["Access-Control-Allow-Methods"] ?: "*")
        addHeader("Access-Control-Allow-Headers", session.headers["Access-Control-Allow-Headers"] ?: "*")
        addHeader("Access-Control-Allow-Credentials", "true")
        addHeader("Access-Control-Max-Age", "0")
    }

}