package projekt.cloud.piece.music.player.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import projekt.cloud.piece.music.player.base.interfaces.BackPressedInterface
import projekt.cloud.piece.music.player.base.interfaces.WindowInsetsInterface
import projekt.cloud.piece.music.player.util.FragmentUtil.viewLifecycleProperty
import projekt.cloud.piece.music.player.util.KotlinUtil.tryTo
import projekt.cloud.piece.music.player.util.ScreenDensity.ScreenDensityUtil.screenDensity

typealias ViewBindingInflater<VB> = (LayoutInflater, ViewGroup?, Boolean) -> VB

abstract class BaseFragment<VB: ViewBinding>: Fragment() {

    protected var binding: VB by viewLifecycleProperty()

    protected abstract val viewBindingInflater: ViewBindingInflater<VB>

    protected val screenDensity by screenDensity()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = viewBindingInflater.invoke(layoutInflater, container, false)
        binding.tryTo<ViewDataBinding>()
            ?.lifecycleOwner = this
        return binding.root
    }

    /**
     * Don't override [onViewCreated],
     * go and override [onSetupBinding]!
     **/
    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onSetupBinding(binding, savedInstanceState)
        if (this is WindowInsetsInterface) {
            requireWindowInset(requireContext())
        }

        tryTo<BackPressedInterface> { backPressedInterface ->
            requireActivity().onBackPressedDispatcher
                .addCallback(viewLifecycleOwner) {
                    if (backPressedInterface.onBackPressed(this@BaseFragment)) {
                        isEnabled = false
                        remove()
                    }
                }
        }

    }

    protected open fun onSetupBinding(binding: VB, savedInstanceState: Bundle?) = Unit

}