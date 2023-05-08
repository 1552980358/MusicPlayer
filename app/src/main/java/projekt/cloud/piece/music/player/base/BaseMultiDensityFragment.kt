package projekt.cloud.piece.music.player.base

import android.os.Bundle
import androidx.activity.addCallback
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import projekt.cloud.piece.music.player.base.interfaces.BackPressedInterface
import projekt.cloud.piece.music.player.base.interfaces.SurfaceColorsInterface
import projekt.cloud.piece.music.player.base.interfaces.WindowInsetsInterface
import projekt.cloud.piece.music.player.util.FragmentUtil.viewLifecycleProperty
import projekt.cloud.piece.music.player.util.KotlinUtil.tryTo
import projekt.cloud.piece.music.player.util.ScreenDensity
import projekt.cloud.piece.music.player.util.ScreenDensity.ScreenDensityUtil.screenDensity

typealias LayoutCompatInflater<VB, LC> = (ScreenDensity, VB) -> LC

abstract class BaseMultiDensityFragment<VB, LC>: BaseFragment<VB>()
        where VB: ViewBinding, LC: BaseLayoutCompat<VB> {

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
        onSetupLayoutCompatInterfaces(layoutCompat)
        onSetupLayoutCompat(layoutCompat, savedInstanceState)
    }

    private fun onCreateLayoutCompat(binding: VB) {
        layoutCompat = layoutCompatInflater.invoke(
            requireActivity().screenDensity, binding
        )
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
        layoutCompat.tryTo<BackPressedInterface> { backPressedInterface ->
            requireActivity().onBackPressedDispatcher
                .addCallback(viewLifecycleOwner) {
                    if (backPressedInterface.onBackPressed(this@BaseMultiDensityFragment)) {
                        isEnabled = false
                        remove()
                    }
                }
        }
    }

    /**
     * Set up at here, super is not required to call
     **/
    protected open fun onSetupLayoutCompat(layoutCompat: LC, savedInstanceState: Bundle?) = Unit

}