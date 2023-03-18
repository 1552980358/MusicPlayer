package projekt.cloud.piece.music.player.base

import android.graphics.Rect
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import projekt.cloud.piece.music.player.util.ScreenDensity
import projekt.cloud.piece.music.player.util.ScreenDensity.COMPACT
import projekt.cloud.piece.music.player.util.ScreenDensity.EXPANDED
import projekt.cloud.piece.music.player.util.ScreenDensity.MEDIUM

private typealias RequireWindowInsetsListener = (Rect) -> Unit

abstract class BaseLayoutCompat<VB: ViewBinding>(private var _binding: VB?) {

    protected val binding: VB
        get() = _binding!!

    companion object BaseLayoutCompatUtil {

        inline fun <VB: ViewBinding, reified LC: BaseLayoutCompat<VB>> VB.layoutCompat(screenDensity: ScreenDensity): LC {
            return LC::class.java.newInstance()
                .getImpl(screenDensity)
                .primaryConstructor!!
                .apply { isAccessible = true }
                .call(this) as LC
        }

    }

    fun getImpl(screenDensity: ScreenDensity): KClass<*> {
        return when (screenDensity) {
            COMPACT -> compatImpl
            MEDIUM -> w600dpImpl
            EXPANDED -> w1240dpImpl
        }
    }

    protected abstract val compatImpl: KClass<*>

    protected abstract val w600dpImpl: KClass<*>

    protected abstract val w1240dpImpl: KClass<*>

    open val requireWindowInsets: Boolean
        get() = false

    val windowInsetsRequireListener: RequireWindowInsetsListener?
        get() = onSetupRequireWindowInsets()
    protected open fun onSetupRequireWindowInsets(): RequireWindowInsetsListener? = null

    fun onDestroy() {
        _binding = null
    }

}