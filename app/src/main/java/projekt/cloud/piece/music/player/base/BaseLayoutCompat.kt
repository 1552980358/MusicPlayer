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

        const val METHOD_GET_IMPL = "getImpl"

        fun determineLayoutCompat(
            screenDensity: ScreenDensity, layoutCompatArray: Array<KClass<*>>
        ): KClass<*> {
            return when (screenDensity) {
                COMPACT -> layoutCompatArray[0]
                MEDIUM -> layoutCompatArray[1]
                EXPANDED -> layoutCompatArray[2]
            }
        }

        fun <VB: ViewBinding, LC: BaseLayoutCompat<VB>> VB.reflectLayoutCompat(
            layoutCompatClass: KClass<LC>, screenDensity: ScreenDensity
        ): LC {
            return getLayoutCompatInstance(
                invokeGetImpl(layoutCompatClass, screenDensity)
            )
        }

        private fun <VB: ViewBinding, LC: BaseLayoutCompat<VB>> invokeGetImpl(
            kClass: KClass<LC>, screenDensity: ScreenDensity
        ): KClass<out LC> {
            @Suppress("UNCHECKED_CAST")
            return kClass.java.getDeclaredMethod(METHOD_GET_IMPL, ScreenDensity::class.java)
                .apply { isAccessible = true }
                .invoke(null, screenDensity) as KClass<out LC>
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