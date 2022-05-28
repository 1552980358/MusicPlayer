package projekt.cloud.piece.music.player.service.web

object ServerRouting {
    
    const val STATIC_PATH_ROOT = "/"
    const val STATIC_FILE_INDEX_HTML = "index.html"
    const val STATIC_FILE_FAVICON_ICO = "favicon.ico"
    
    const val STATIC_PATH_CSS = "css"
    const val STATIC_PATH_IMG = "img"
    const val STATIC_PATH_JS = "js"
    const val STATIC_FILE_ALL = "."
    
    private const val ROUTE_PARAM_ID = "id"
    
    const val ROUTE_PLAYER = "/player"
    const val ROUTE_PLAYER_PLAYLIST = "/playlist"
    const val ROUTE_PLAYER_PLAYLIST_PARAM_ALL = "/all"
    const val ROUTE_PLAYER_PLAYLIST_ID = "/{$ROUTE_PARAM_ID}"
    const val ROUTE_PLAYER_PLAYLIST_PARAM_ID = ROUTE_PARAM_ID
    const val ROUTE_PLAYER_AUDIO = "/audio"
    const val ROUTE_PLAYER_AUDIO_ID = "/{id}"
    const val ROUTE_PLAYER_AUDIO_ID_PARAM_ID = ROUTE_PARAM_ID

}