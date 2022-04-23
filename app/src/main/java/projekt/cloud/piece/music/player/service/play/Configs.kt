package projekt.cloud.piece.music.player.service.play

import java.io.Serializable

class Configs: Serializable {
    
    var configs = 0
        private set
    
    fun isTrue(config: Int) =
        ((configs shr config) and 1) == 1

    fun isFalse(config: Int) = !isTrue(config)

    fun and(vararg configs: Int) =
        configs.indexOfFirst { isFalse(it) } == -1

    fun nAnd(vararg configs: Int) =
        configs.indexOfFirst { isTrue(it) } == -1
    
    operator fun set(config: Int, newValue: Boolean) {
        configs = when {
            newValue -> configs or config.shl
            else -> configs and config.shl.inv()
        }
    }

    operator fun get(config: Int) = isTrue(config)

    fun update(config: Int, newValue: Boolean) = apply {
        this[config] = newValue
    }
    
    fun update(vararg pairs: Pair<Int, Boolean>) =
        pairs.forEach { this[it.first] = it.second }
    
    private val Int.shl get() = (1 shl this)
    
}