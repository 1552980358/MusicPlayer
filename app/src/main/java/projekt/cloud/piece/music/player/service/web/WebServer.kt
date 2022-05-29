package projekt.cloud.piece.music.player.service.web

import android.content.Context
import com.google.gson.JsonObject
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.default
import io.ktor.server.http.content.file
import io.ktor.server.http.content.files
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticRootFolder
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.options
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import projekt.cloud.piece.music.player.database.AudioRoom
import projekt.cloud.piece.music.player.service.play.AudioUtil.parseUri
import projekt.cloud.piece.music.player.service.web.RespondUtil.AUDIO_RESPOND_LIST
import projekt.cloud.piece.music.player.service.web.RespondUtil.AUDIO_RESPOND_URI
import projekt.cloud.piece.music.player.service.web.RespondUtil.PLAYLIST_RESPOND_LIST
import projekt.cloud.piece.music.player.service.web.RespondUtil.PLAYLIST_RESPOND_URI
import projekt.cloud.piece.music.player.service.web.RespondUtil.RESPOND_STATUS
import projekt.cloud.piece.music.player.service.web.RespondUtil.audioJsonArray
import projekt.cloud.piece.music.player.service.web.RespondUtil.playlistJsonArray
import projekt.cloud.piece.music.player.service.web.ServerRouting.ROUTE_PLAYER
import projekt.cloud.piece.music.player.service.web.ServerRouting.ROUTE_PLAYER_AUDIO
import projekt.cloud.piece.music.player.service.web.ServerRouting.ROUTE_PLAYER_AUDIO_ID
import projekt.cloud.piece.music.player.service.web.ServerRouting.ROUTE_PLAYER_AUDIO_ID_PARAM_ID
import projekt.cloud.piece.music.player.service.web.ServerRouting.ROUTE_PLAYER_PLAYLIST
import projekt.cloud.piece.music.player.service.web.ServerRouting.ROUTE_PLAYER_PLAYLIST_ID
import projekt.cloud.piece.music.player.service.web.ServerRouting.ROUTE_PLAYER_PLAYLIST_PARAM_ALL
import projekt.cloud.piece.music.player.service.web.ServerRouting.ROUTE_PLAYER_PLAYLIST_PARAM_ID
import projekt.cloud.piece.music.player.service.web.ServerRouting.STATIC_FILE_ALL
import projekt.cloud.piece.music.player.service.web.ServerRouting.STATIC_FILE_FAVICON_ICO
import projekt.cloud.piece.music.player.service.web.ServerRouting.STATIC_FILE_INDEX_HTML
import projekt.cloud.piece.music.player.service.web.ServerRouting.STATIC_PATH_CSS
import projekt.cloud.piece.music.player.service.web.ServerRouting.STATIC_PATH_IMG
import projekt.cloud.piece.music.player.service.web.ServerRouting.STATIC_PATH_JS
import projekt.cloud.piece.music.player.service.web.ServerRouting.STATIC_PATH_ROOT
import projekt.cloud.piece.music.player.service.web.WebAssets.webAssetsCssDir
import projekt.cloud.piece.music.player.service.web.WebAssets.webAssetsDir
import projekt.cloud.piece.music.player.service.web.WebAssets.webAssetsImgDir
import projekt.cloud.piece.music.player.service.web.WebAssets.webAssetsJSDir

class WebServer(private val context: Context) {
    
    companion object {
        const val SERVER_PORT = 8080
    }
    
    var isLaunched = false
        set(value) {
            if (field != value) {
                field = value
                
                when {
                    value -> server.start()
                    else -> server.stop()
                }
            }
        }
    
    private val audioRoom = AudioRoom.get(context)
    
    private val server = embeddedServer(Netty, SERVER_PORT) {
        installCORS()
        routing {
            implementStaticWebsite()
            implementPlayer()
        }
    }
    
    private fun Application.installCORS() = install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowCredentials = true
        allowNonSimpleContentTypes = true
        anyHost()
    }
    
    /**
     * index.html
     * favicon.ico
     * css
     * └── *.css
     * svg
     * └── *.svg
     * js
     * └── *.js
     **/
    private fun Routing.implementStaticWebsite() = static(STATIC_PATH_ROOT) {
        staticRootFolder = context.webAssetsDir
        default(STATIC_FILE_INDEX_HTML)
        file(STATIC_FILE_FAVICON_ICO)
        
        static(STATIC_PATH_CSS) {
            staticRootFolder = context.webAssetsCssDir
            files(STATIC_FILE_ALL)
        }
        
        static(STATIC_PATH_IMG) {
            staticRootFolder = context.webAssetsImgDir
            files(STATIC_FILE_ALL)
        }
    
        static(STATIC_PATH_JS) {
            staticRootFolder = context.webAssetsJSDir
            files(STATIC_FILE_ALL)
        }
    }
    
    /**
     * /player
     * ├── /playlist
     * │   ├── /: Respond list of playlist
     * │   ├── /all: Respond all audio found in database
     * │   └── /{id}: Respond audio metadata list of playlist {id}
     *
     **/
    private fun Routing.implementPlayer() = route(ROUTE_PLAYER) {   // player
        // playlist
        route(ROUTE_PLAYER_PLAYLIST) {
            implementOptions()
            get {
                call.respondText(
                    JsonObject().also {
                        it.addProperty(RESPOND_STATUS, HttpStatusCode.OK.toString())
                        it.addProperty(PLAYLIST_RESPOND_URI, call.request.uri)
                        it.add(PLAYLIST_RESPOND_LIST, audioRoom.playlistDao.query().playlistJsonArray)
                    }.toString(),
                    ContentType.Application.Json,
                    HttpStatusCode.OK
                )
            }
    
            // all
            route(ROUTE_PLAYER_PLAYLIST_PARAM_ALL) {
                implementOptions()
                get {
                    call.respondText(
                        JsonObject().also {
                            it.addProperty(RESPOND_STATUS, HttpStatusCode.OK.toString())
                            it.addProperty(AUDIO_RESPOND_URI, call.request.uri)
                            it.add(AUDIO_RESPOND_LIST, audioRoom.queryAudio.audioJsonArray)
                        }.toString(),
                        ContentType.Application.Json,
                        HttpStatusCode.OK
                    )
                }
            }
            
            // {id}
            route(ROUTE_PLAYER_PLAYLIST_ID) {
                implementOptions()
                get {
                    val playlist = call.parameters[ROUTE_PLAYER_PLAYLIST_PARAM_ID]
                        ?: return@get call.respond(
                            HttpStatusCode.NotFound,
                            JsonObject().also {
                                it.addProperty(RESPOND_STATUS, HttpStatusCode.NotFound.toString())
                                it.addProperty(AUDIO_RESPOND_URI, call.request.uri)
                            }
                        )
                    call.respondText(
                        JsonObject().also {
                            it.addProperty(RESPOND_STATUS, HttpStatusCode.OK.toString())
                            it.addProperty(AUDIO_RESPOND_URI, call.request.uri)
                            it.add(AUDIO_RESPOND_LIST, audioRoom.queryPlaylistAudio(playlist).audioJsonArray)
                        }.toString(),
                        ContentType.Application.Json,
                        HttpStatusCode.OK
                    )
                }
            }
        }
        
        // audio
        route(ROUTE_PLAYER_AUDIO) {
            // {id}
            route(ROUTE_PLAYER_AUDIO_ID) {
                implementOptions()
                get {
                    val id = call.parameters[ROUTE_PLAYER_AUDIO_ID_PARAM_ID]
                        ?: return@get call.response.status(HttpStatusCode.NotFound)
                    @Suppress("BlockingMethodInNonBlockingContext")
                    this@WebServer.context.contentResolver.openInputStream(id.parseUri)?.use { it.readBytes() }?.let {
                        call.respondBytes(it, ContentType.Audio.Any, HttpStatusCode.OK)
                    }
                }
            }
        }
    }
    
    private fun Route.implementOptions() = options {
        call.respondText(
            JsonObject().also {
                it.addProperty(RESPOND_STATUS, HttpStatusCode.OK.toString())
            }.toString(),
            ContentType.Application.Json,
            HttpStatusCode.OK
        )
    }
    
}