package projekt.cloud.piece.music.player.service.web

import android.content.Context
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.default
import io.ktor.server.http.content.file
import io.ktor.server.http.content.files
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticRootFolder
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.routing.Routing
import io.ktor.server.routing.routing
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
        private const val SERVER_PORT = 80
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
    
    private val server = embeddedServer(Netty, SERVER_PORT) {
        install(CORS)
        routing {
            implementStaticWebsite()
        }
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
    
}