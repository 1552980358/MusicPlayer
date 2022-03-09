package projekt.cloud.piece.music.player.service.play

object Config {

    fun Int.getConfig(config: Int): Boolean =
        (this and (1 shl config)) == (1 shl config)
    
    fun Int.setConfig(config: Int, newValue: Boolean) = when {
        newValue -> this or (1 shl config)
        else -> this and (1 shl config).inv()
    }
    
    fun getConfigs(vararg pairs: Pair<Int, Boolean>): Int {
        var temp = 0
        pairs.forEach { pair ->
            if (pair.second) {
                temp += (1 shl pair.first)
            }
        }
        return temp
    }
    
    const val FOREGROUND_SERVICE = 0

}