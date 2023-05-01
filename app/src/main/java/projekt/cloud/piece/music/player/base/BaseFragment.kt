package projekt.cloud.piece.music.player.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import projekt.cloud.piece.music.player.base.interfaces.WindowInsetsInterface
import projekt.cloud.piece.music.player.util.FragmentUtil.viewLifecycleProperty
import projekt.cloud.piece.music.player.util.KotlinUtil.tryTo

typealias ViewBindingInflater<VB> = (LayoutInflater, ViewGroup?, Boolean) -> VB

typealias OnBackPressedListener = () -> Boolean

abstract class BaseFragment<VB: ViewBinding>: Fragment() {

    protected var binding: VB by viewLifecycleProperty()

    protected abstract val viewBindingInflater: ViewBindingInflater<VB>

    protected open val onBackPressed: OnBackPressedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressed?.let { onBackPressed ->
            requireActivity().onBackPressedDispatcher
                .addCallback(this, object: OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (onBackPressed.invoke()) {
                            isEnabled = false
                            remove()
                        }
                    }
                })
        }
    }

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
    }

    protected open fun onSetupBinding(binding: VB, savedInstanceState: Bundle?) = Unit

}