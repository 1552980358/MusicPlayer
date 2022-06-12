package projekt.cloud.piece.music.player.service.web

import android.content.Context
import java.io.File
import projekt.cloud.piece.music.player.service.web.ServerRouting.STATIC_PATH_CSS
import projekt.cloud.piece.music.player.service.web.ServerRouting.STATIC_PATH_IMG
import projekt.cloud.piece.music.player.service.web.ServerRouting.STATIC_PATH_JS

/**
 * [WebAssets]
 *
 * Methods:
 * [webAssetsDir]
 * [webAssetsCssDir]
 * [webAssetsImgDir]
 * [webAssetsJSDir]
 * [webAssetsVersionFile]
 **/
object WebAssets {
    
    private const val DIR_WEB_ASSETS = "web"
    private const val DIR_WEB_ASSETS_CSS = STATIC_PATH_CSS
    private const val DIR_WEB_ASSETS_IMG = STATIC_PATH_IMG
    private const val DIR_WEB_ASSETS_JS = STATIC_PATH_JS
    private const val FILE_WEB_VERSION_FILE = "version"
    
    @JvmStatic
    val Context.webAssetsDir get() = getExternalFilesDir(DIR_WEB_ASSETS)
    
    @JvmStatic
    val Context.webAssetsCssDir get() = File(webAssetsDir, DIR_WEB_ASSETS_CSS)
    
    @JvmStatic
    val Context.webAssetsImgDir get() = File(webAssetsDir, DIR_WEB_ASSETS_IMG)
    
    @JvmStatic
    val Context.webAssetsJSDir get() = File(webAssetsDir, DIR_WEB_ASSETS_JS)
    
    @JvmStatic
    val Context.webAssetsVersionFile get() = File(webAssetsDir, FILE_WEB_VERSION_FILE)
    
}