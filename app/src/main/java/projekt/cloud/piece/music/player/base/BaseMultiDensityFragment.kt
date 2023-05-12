package projekt.cloud.piece.music.player.base

import android.os.Bundle
import androidx.activity.addCallback
import androidx.annotation.CallSuper
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KFunction3
import projekt.cloud.piece.music.player.base.interfaces.BackPressedInterface
import projekt.cloud.piece.music.player.base.interfaces.SurfaceColorsInterface
import projekt.cloud.piece.music.player.base.interfaces.WindowInsetsInterface
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.mainBlocking
import projekt.cloud.piece.music.player.util.FragmentUtil.viewLifecycleProperty
import projekt.cloud.piece.music.player.util.KotlinUtil.tryTo
import projekt.cloud.piece.music.player.util.ScreenDensity

typealias LayoutCompatInflater<VB, LC> = (ScreenDensity, VB) -> LC

private typealias BaseMultiDensityFragmentAsyncableMethod<VB, LC> =
        KFunction3<BaseMultiDensityFragment<VB, LC>, LC, FragmentActivity, Unit>

private typealias BaseMultiDensityFragmentAsyncableMethodList<VB, LC> =
        List<BaseMultiDensityFragmentAsyncableMethod<VB, LC>>

abstract class BaseMultiDensityFragment<VB, LC>: BaseFragment<VB>()
        where VB: ViewBinding, LC: BaseLayoutCompat<VB> {

    private companion object {

        @JvmStatic
        fun <VB, LC> getAsyncableMethodList(): BaseMultiDensityFragmentAsyncableMethodList<VB, LC>
            where VB: ViewBinding, LC: BaseLayoutCompat<VB> {
            return listOf(
                BaseMultiDensityFragment<VB, LC>::setupBackPressedInterface,
                BaseMultiDensityFragment<VB, LC>::setupWindowInsetsInterface,
                BaseMultiDensityFragment<VB, LC>::setupSurfaceColorsInterface
            )
        }

    }

    protected var layoutCompat: LC by viewLifecycleProperty()
        private set

    protected abstract val layoutCompatInflater: LayoutCompatInflater<VB, LC>

    /**
     * Don't override this,
     * go and override [onSetupLayoutCompat]
     **/
    @CallSuper
    override fun onSetupBinding(binding: VB, savedInstanceState: Bundle?) {
        onCreateLayoutCompat(binding)
        setupLayoutCompatInterfaces(layoutCompat)
        onSetupLayoutCompat(layoutCompat, savedInstanceState)
    }

    private fun onCreateLayoutCompat(binding: VB) {
        layoutCompat = layoutCompatInflater.invoke(screenDensity, binding)
    }

    private fun setupLayoutCompatInterfaces(layoutCompat: LC) {
        default {
            setupAsyncMethods(layoutCompat, requireActivity())
        }
    }

    private suspend fun setupAsyncMethods(
        layoutCompat: LC, parentActivity: FragmentActivity
    ) {
        getAsyncableMethodList<VB, LC>().forEach { asyncableMethod ->
            invokeAsyncMethod(asyncableMethod, this@BaseMultiDensityFragment, layoutCompat, parentActivity)
        }
    }

    private suspend fun invokeAsyncMethod(
        method: BaseMultiDensityFragmentAsyncableMethod<VB, LC>,
        fragment: BaseMultiDensityFragment<VB, LC>,
        layoutCompat: LC,
        parentActivity: FragmentActivity
    ) = mainBlocking { method.invoke(fragment, layoutCompat, parentActivity) }

    private fun setupBackPressedInterface(layoutCompat: LC, parentActivity: FragmentActivity) {
        layoutCompat.tryTo<BackPressedInterface> { backPressedInterface ->
            parentActivity.onBackPressedDispatcher
                .addCallback(viewLifecycleOwner) {
                    if (backPressedInterface.onBackPressed(this@BaseMultiDensityFragment)) {
                        isEnabled = false
                        remove()
                    }
                }
        }
    }

    private fun setupWindowInsetsInterface(layoutCompat: LC, parentActivity: FragmentActivity) {
        layoutCompat.tryTo<WindowInsetsInterface> { windowInsetsInterface ->
            windowInsetsInterface.requireWindowInset(parentActivity)
        }
    }

    private fun setupSurfaceColorsInterface(layoutCompat: LC, parentActivity: FragmentActivity) {
        layoutCompat.tryTo<SurfaceColorsInterface> { surfaceColorsInterface ->
            surfaceColorsInterface.requireSurfaceColors(parentActivity)
        }
    }

    /**
     * Set up at here, super is not required to call
     **/
    protected open fun onSetupLayoutCompat(layoutCompat: LC, savedInstanceState: Bundle?) = Unit

}