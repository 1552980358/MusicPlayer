package projekt.cloud.piece.music.player.service.play

object Config {

    @JvmStatic
    fun Int.getConfig(config: Int): Boolean =
        ((this shr config) and 1) == 1
    
    @JvmStatic
    fun Int.setConfig(config: Int, newValue: Boolean) = when {
        newValue -> this or config.shl
        else -> this and config.shl.inv()
    }
    
    @JvmStatic
    fun getConfigs(vararg pairs: Pair<Int, Boolean>): Int {
        var temp = 0
        pairs.forEach { pair ->
            if (pair.second) {
                temp += pair.first.shl
            }
        }
        return temp
    }
    
    const val FOREGROUND_SERVICE = 0
    
    val Int.shl get() = (1 shl this)
    
    const val PLAY_CONFIG_SHUFFLE = 0
    const val PLAY_CONFIG_REPEAT = 1
    const val PLAY_CONFIG_REPEAT_ONE = 2

}