package projekt.cloud.piece.cloudy.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import projekt.cloud.piece.cloudy.util.ViewBindingInflater
import projekt.cloud.piece.cloudy.util.ViewBindingUtil.inflate

abstract class BaseFragment<B>: Fragment() where B: ViewBinding {

    /**
     * [BaseFragment.viewBindingInflater]
     * @return [ViewBindingInflater]
     *
     * Called for inflating binding during [androidx.fragment.app.Fragment.onCreateView]
     **/
    protected abstract val viewBindingInflater: ViewBindingInflater<B>

    /**
     * [BaseFragment._binding]
     * @type Nullable [B]
     **/
    private var _binding: B? = null

    /**
     * [BaseFragment.bindingNullable]
     * @type Nullable [B]
     *
     * Modifier `protected` is set for
     * allowing `inline` modifier set to [BaseFragment.requireBinding].
     *
     * So, Don't call this,
     * call [BaseFragment.requireBinding]
     **/
    protected val bindingNullable: B?
        get() = _binding
    /**
     * [BaseFragment.binding]
     * @type [B]
     **/
    protected val binding: B
        get() = _binding!!

    /**
     * [BaseFragment.requireBinding]
     * @param block [kotlin.jvm.functions.Function1]
     *
     * Require binding in a safe way prevent null pointer exception
     * if binding required after the calling of [androidx.fragment.app.Fragment.onDestroyView]
     **/
    protected inline fun requireBinding(block: (B) -> Unit = {}): B? {
        return bindingNullable?.apply(block)
    }

    /**
     * [BaseFragment.inflateBinding]
     * @param inflater [android.view.LayoutInflater]
     * @param container [android.view.ViewGroup]
     * @return [B]
     *
     * Called for inflating binding during [androidx.fragment.app.Fragment.onCreateView]
     * by returning result of calling [BaseFragment.viewBindingInflater]
     **/
    private fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): B {
        return viewBindingInflater.inflate(inflater, container)
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
    @CallSuper
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}