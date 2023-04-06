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

typealias RequireWindowInsetsListener = (Rect) -> Unit

abstract class BaseLayoutCompat<VB: ViewBinding>(private var _binding: VB?) {

    protected val binding: VB
        get() = _binding!!

    companion object BaseLayoutCompatUtil {

        fun <VB: ViewBinding, LC: BaseLayoutCompat<VB>> VB.reflectLayoutCompat(
            layoutCompatClass: KClass<LC>, screenDensity: ScreenDensity
        ): LC {
            return getLayoutCompatInstance(
                layoutCompatClass.getLayoutCompatImplClass(screenDensity)
            )
        }

        private fun <LC: BaseLayoutCompat<*>> KClass<LC>.getLayoutCompatImplClass(
            screenDensity: ScreenDensity
        ): KClass<out LC> {
            @Suppress("UNCHECKED_CAST")
            return Class.forName("${java.name}$${screenDensity.layoutCompat}")
                .kotlin as KClass<out LC>
        }

        private const val IMPL_COMPAT = "CompatImpl"
        private const val IMPL_W600DP = "W600dpImpl"
        private const val IMPL_W1240DP = "W1240dpImpl"

        private val ScreenDensity.layoutCompat: String
            get() = when (this) {
                COMPACT -> IMPL_COMPAT
                MEDIUM -> IMPL_W600DP
                EXPANDED -> IMPL_W1240DP
            }

        private fun <VB: ViewBinding, LC: BaseLayoutCompat<VB>> VB.getLayoutCompatInstance(
            layoutCompatClass: KClass<out LC>
        ): LC {
            return layoutCompatClass.primaryConstructor!!
                .apply { isAccessible = true }
                .call(this)
        }

    }

    open val requireWindowInsets: Boolean
        get() = false

    val windowInsetsRequireListener: RequireWindowInsetsListener?
        get() = onSetupRequireWindowInsets()
    protected open fun onSetupRequireWindowInsets(): RequireWindowInsetsListener? = null

    fun onDestroy() {
        _binding = null
        onRecycleInstance()
    }

    open fun onRecycleInstance() = Unit

}