package projekt.cloud.piece.music.player.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass
import projekt.cloud.piece.music.player.base.BaseLayoutCompat.BaseLayoutCompatUtil.reflectLayoutCompat
import projekt.cloud.piece.music.player.base.interfaces.SurfaceColorsInterface
import projekt.cloud.piece.music.player.base.interfaces.WindowInsetsInterface
import projekt.cloud.piece.music.player.util.FragmentUtil.viewLifecycleProperty
import projekt.cloud.piece.music.player.util.ScreenDensity.ScreenDensityUtil.screenDensity

abstract class BaseMultiDensityFragment<VB: ViewBinding, LC: BaseLayoutCompat<VB>>: BaseFragment<VB>() {

    protected var layoutCompat: LC by viewLifecycleProperty()
        private set

    protected abstract val layoutCompatClass: KClass<LC>

    /**
     * Don't override this,
     * go and override [onSetupLayoutCompat]
     **/
    @CallSuper
    override fun onSetupBinding(binding: VB, savedInstanceState: Bundle?) {
        onCreateLayoutCompat(binding)
        onSetupLayoutCompatInterfaces(layoutCompat)
        onSetupLayoutCompat(layoutCompat, savedInstanceState)
    }

    private fun onCreateLayoutCompat(binding: VB) {
        layoutCompat = createLayoutCompat(binding)
    }

    private fun createLayoutCompat(binding: VB): LC {
        return binding.reflectLayoutCompat(layoutCompatClass, requireActivity().screenDensity)
    }

    protected open fun onSetupLayoutCompatInterfaces(layoutCompat: LC) {
        requireContext().let { context ->
            if (layoutCompat is WindowInsetsInterface) {
                layoutCompat.requireWindowInset(context)
            }
            if (layoutCompat is SurfaceColorsInterface) {
                layoutCompat.requireSurfaceColors(context)
            }
        }
    }

    /**
     * Set up at here, super is not required to call
     **/
    protected open fun onSetupLayoutCompat(layoutCompat: LC, savedInstanceState: Bundle?) = Unit

}