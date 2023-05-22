package projekt.cloud.piece.cloudy.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import projekt.cloud.piece.cloudy.base.BaseLayoutAdapter.LayoutAdapterUtil.build
import projekt.cloud.piece.cloudy.util.PixelDensity
import projekt.cloud.piece.cloudy.util.PixelDensity.PixelDensityUtil.pixelDensity
import projekt.cloud.piece.cloudy.util.helper.NullableHelper.NullableHelperUtil.nullable

abstract class BaseMultiLayoutFragment<B: ViewBinding, A: BaseLayoutAdapter<B>>: BaseFragment<B>() {

    /**
     * [BaseMultiLayoutFragment.layoutAdapterBuilder]
     * @type [LayoutAdapterBuilder]
     **/
    protected abstract val layoutAdapterBuilder: LayoutAdapterBuilder<B, A>

    protected val layoutAdapter = nullable<A>()

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
        onSetupLayoutAdapter(
            layoutAdapter valued createLayoutAdapter(pixelDensity, binding),
            savedInstanceState
        )
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
        layoutAdapter.release()
        super.onDestroyView()
    }

}