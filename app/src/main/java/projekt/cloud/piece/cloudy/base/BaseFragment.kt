package projekt.cloud.piece.cloudy.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

typealias BindingInflater<B> = (LayoutInflater, ViewGroup?, Boolean) -> B

abstract class BaseFragment<B>: Fragment() where B: ViewBinding {

    /**
     * [_binding]
     * @type [B]
     **/
    private var _binding: B? = null
    protected val binding: B
        get() = _binding!!

    /**
     * [BaseFragment.requireBinding]
     * @param block [kotlin.reflect.KFunction1]
     *
     * Require binding in a safe way prevent null pointer exception
     * if binding required after the calling of [androidx.fragment.app.Fragment.onDestroyView]
     **/
    protected fun requireBinding(block: (B) -> Unit) {
        _binding?.let(block)
    }

    /**
     * [BaseFragment.bindingInflater]
     * @return [BindingInflater]
     *
     * Called for inflating binding during [androidx.fragment.app.Fragment.onCreateView]
     **/
    protected abstract val bindingInflater: BindingInflater<B>

    /**
     * [BaseFragment.inflateBinding]
     * @param inflater [android.view.LayoutInflater]
     * @param container [android.view.ViewGroup]
     * @return [B]
     *
     * Called for inflating binding during [androidx.fragment.app.Fragment.onCreateView]
     * by returning result of calling [BaseFragment.bindingInflater]
     **/
    private fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): B {
        return bindingInflater.invoke(inflater, container, false)
    }

    /**
     * [androidx.fragment.app.Fragment.onCreateView]
     * @param inflater [android.view.LayoutInflater]
     * @param container [android.view.ViewGroup]
     * @param savedInstanceState [android.os.Bundle]
     * @return [android.view.View]
     *
     * Create view for showing content
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return when (val binding = _binding) {
            null -> {
                inflateBinding(inflater, container)
                    .apply(::setupBinding)
                    .root
            }
            else -> {
                setupViewDataBinding(binding)
                binding.root
            }
        }
    }

    /**
     * [BaseFragment.setupBinding]
     * @param binding [B]
     *
     * Setup binding
     **/
    private fun setupBinding(binding: B) {
        _binding = binding
        setupViewDataBinding(binding)
    }

    /**
     * [BaseFragment.setupViewDataBinding]
     * @param binding [B]
     *
     * Setup view data binding binding if [binding] is
     * a child of [androidx.databinding.ViewDataBinding]
     **/
    private fun setupViewDataBinding(binding: B) {
        if (binding is ViewDataBinding) {
            binding.lifecycleOwner = viewLifecycleOwner
        }
    }

    /**
     * [androidx.fragment.app.Fragment.onViewCreated]
     * @param view [android.view.View]
     * @param savedInstanceState [android.os.Bundle]
     *
     * Called after [androidx.fragment.app.Fragment.onCreateView] completed
     **/
    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onSetupBinding(binding, savedInstanceState)
    }

    /**
     * [BaseFragment.onSetupBinding]
     * @param binding [B]
     * @param savedInstanceState [android.os.Bundle]
     *
     * Setup binding during [Fragment.onViewCreated]
     **/
    protected open fun onSetupBinding(binding: B, savedInstanceState: Bundle?) = Unit

    /**
     * [androidx.fragment.app.Fragment.onDestroyView]
     *
     * Called after [androidx.fragment.app.Fragment.onStop],
     * and before [androidx.fragment.app.Fragment.onDestroy]
     **/
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}