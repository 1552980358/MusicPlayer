package projekt.cloud.piece.cloudy.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import projekt.cloud.piece.cloudy.base.BaseLayoutAdapter.LayoutAdapterUtil.build
import projekt.cloud.piece.cloudy.util.PixelDensity
import projekt.cloud.piece.cloudy.util.PixelDensity.PixelDensityUtil.pixelDensity

abstract class BaseMultiLayoutFragment<B: ViewBinding, A: BaseLayoutAdapter<B>>: BaseFragment<B>() {

    /**
     * [BaseMultiLayoutFragment.layoutAdapterBuilder]
     * @type [LayoutAdapterBuilder]
     **/
    protected abstract val layoutAdapterBuilder: LayoutAdapterBuilder<B, A>

    /**
     * [BaseMultiLayoutFragment._layoutAdapter]
     * @type [A]
     **/
    private var _layoutAdapter: A? = null

    /**
     * [BaseMultiLayoutFragment.layoutAdapterNullable]
     * @type [A]
     *
     * Modifier `protected` is set for
     * allowing `inline` modifier set to [BaseMultiLayoutFragment.requireLayoutAdapter]
     * Don't call this call [BaseMultiLayoutFragment.requireLayoutAdapter]
     **/
    protected val layoutAdapterNullable: A?
        get() = _layoutAdapter

    /**
     * [BaseMultiLayoutFragment.layoutAdapter]
     * @type [A]
     **/
    protected val layoutAdapter: A
        get() = _layoutAdapter!!

    /**
     * [BaseMultiLayoutFragment.requireLayoutAdapter]
     * @param block [kotlin.jvm.functions.Function1]<[A], [Unit]>
     *
     * Require binding in a safe way prevent null pointer exception
     * if LayoutAdapter required after the calling of [androidx.fragment.app.Fragment.onDestroyView]
     **/
    protected inline fun requireLayoutAdapter(block: (A) -> Unit): A? {
        return layoutAdapterNullable?.apply(block)
    }

    /**
     * [BaseMultiLayoutFragment.pixelDensity]
     * @type [PixelDensity]
     **/
    private val pixelDensity by pixelDensity()

    /**
     * [BaseFragment.onSetupBinding]
     * @param binding [B]
     * @param savedInstanceState [android.os.Bundle]
     **/
    @CallSuper
    override fun onSetupBinding(binding: B, savedInstanceState: Bundle?) {
        _layoutAdapter = createLayoutAdapter(pixelDensity, binding)
        onSetupLayoutAdapter(layoutAdapter, savedInstanceState)
    }

    /**
     * [BaseMultiLayoutFragment.createLayoutAdapter]
     * @param pixelDensity [PixelDensity]
     * @param binding [B]
     * @return [A]
     *
     * Create layout adapter for this [BaseMultiLayoutFragment]
     **/
    private fun createLayoutAdapter(pixelDensity: PixelDensity, binding: B): A {
        return layoutAdapterBuilder.build(pixelDensity, binding) // layoutAdapterInflater.inflate(pixelDensity, binding)
    }

    /**
     * [BaseMultiLayoutFragment.onSetupLayoutAdapter]
     * @param layoutAdapter [A]
     * @param savedInstanceState [android.os.Bundle]
     *
     * Setup layout adapter here
     **/
    protected open fun onSetupLayoutAdapter(layoutAdapter: A, savedInstanceState: Bundle?) = Unit

    /**
     * [androidx.fragment.app.Fragment.onDestroyView]
     *
     * Called after [androidx.fragment.app.Fragment.onStop],
     * and before [androidx.fragment.app.Fragment.onDestroy]
     **/
    override fun onDestroyView() {
        _layoutAdapter = null
        super.onDestroyView()
    }

}