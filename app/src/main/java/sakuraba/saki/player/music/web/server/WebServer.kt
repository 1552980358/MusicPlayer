package sakuraba.saki.player.music.web.server

import android.content.Context
import android.os.Environment.DIRECTORY_MUSIC
import android.os.Environment.getExternalStoragePublicDirectory
import android.util.Log
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response.Status.BAD_REQUEST
import fi.iki.elonen.NanoHTTPD.Response.Status.OK
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
            else -> {
                val indexStream = context.assets.open("web$uri")
                newFixedLengthResponse(OK, uri.mimeType, indexStream, indexStream.available().toLong())
            }
        }
    }

    private fun upload(session: IHTTPSession): Response {
        Log.e(TAG, "upload")
        val fileName = session.parameters[UPLOAD_FILE_NAME]?.first() ?: return newFixedLengthResponse(BAD_REQUEST, "$MIME_HTML$CONTENT_TYPE_CHARSET", UPLOAD_NAME_NOT_SPECIFIED)
        val file = HashMap<String, String>().apply { session.parseBody(this) }[UPLOAD_FILE]
            ?: return newFixedLengthResponse(OK, "$MIME_HTML$CONTENT_TYPE_CHARSET", fileName.UPLOAD_POST_ALLOW).withHeaders(session)
        File(file).copyTo(File(getExternalStoragePublicDirectory(DIRECTORY_MUSIC), fileName))
        return newFixedLengthResponse(OK, "$MIME_HTML$CONTENT_TYPE_CHARSET", fileName.UPLOAD_ACCEPTED).withHeaders(session)
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