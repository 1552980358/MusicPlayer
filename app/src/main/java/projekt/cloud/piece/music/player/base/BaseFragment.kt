package projekt.cloud.piece.music.player.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KFunction2
import projekt.cloud.piece.music.player.base.interfaces.BackPressedInterface
import projekt.cloud.piece.music.player.base.interfaces.TransitionInterface
import projekt.cloud.piece.music.player.base.interfaces.WindowInsetsInterface
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.mainBlocking
import projekt.cloud.piece.music.player.util.KotlinUtil.tryTo
import projekt.cloud.piece.music.player.util.ScreenDensity.ScreenDensityUtil.screenDensity

typealias ViewBindingInflater<VB> = (LayoutInflater, ViewGroup?, Boolean) -> VB

abstract class BaseFragment<VB: ViewBinding>: Fragment() {

    private companion object {

        @JvmField
        val asyncableMethodList = listOf(
            BaseFragment<*>::setupBackPressedInterface
        )

    }

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!

    protected abstract val viewBindingInflater: ViewBindingInflater<VB>

    protected val screenDensity by screenDensity()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = viewBindingInflater.invoke(layoutInflater, container, false)
        _binding.tryTo<ViewDataBinding>()
            ?.lifecycleOwner = this
        return binding.root
    }

    /**
     * Don't override [onViewCreated],
     * go and override [onSetupBinding]!
     **/
    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupInterfaces(requireActivity())
        onSetupBinding(binding, savedInstanceState)
    }

    private fun setupInterfaces(parentActivity: FragmentActivity) {
        invokeAsyncableMethods(this, parentActivity)
        setupWindowInsetsInterface(parentActivity)
        setupTransitionInterface()
    }

    private fun invokeAsyncableMethods(fragment: BaseFragment<*>, parentActivity: FragmentActivity) {
        /**
         * Limit execution in lifecycle of this child of [BaseFragment]
         **/
        fragment.default {
            asyncableMethodList.forEach { method ->
                /**
                 * Execute within [suspend],
                 * allow being cancelled due to lifecycle [androidx.lifecycle.Lifecycle.State.DESTROYED]
                 **/
                invokeAsyncableMethod(method, fragment, parentActivity)
            }
        }
    }

    private suspend fun invokeAsyncableMethod(
        method: KFunction2<BaseFragment<*>, FragmentActivity, Unit>,
        fragment: BaseFragment<*>, parentActivity: FragmentActivity
    ) = mainBlocking {
        method.invoke(fragment, parentActivity)
    }

    private fun setupBackPressedInterface(parentActivity: FragmentActivity) {
        tryTo<BackPressedInterface> { backPressedInterface ->
            parentActivity.onBackPressedDispatcher
                .addCallback(viewLifecycleOwner) {
                    if (backPressedInterface.onBackPressed(this@BaseFragment)) {
                        isEnabled = false
                        remove()
                    }
                }
        }
    }

    private fun setupWindowInsetsInterface(parentActivity: FragmentActivity) {
        tryTo<WindowInsetsInterface> { windowInsetsInterface ->
            windowInsetsInterface.requireWindowInset(parentActivity)
        }
    }

    private fun setupTransitionInterface() {
        tryTo<TransitionInterface> { transitionInterface ->
            transitionInterface.applyTransitions(this, screenDensity)
        }
    }

    protected open fun onSetupBinding(binding: VB, savedInstanceState: Bundle?) = Unit

    @CallSuper
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}