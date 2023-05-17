package projekt.cloud.piece.cloudy.ui.fragment.import_audio

import android.view.View.OnClickListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import projekt.cloud.piece.cloudy.base.BaseLayoutAdapter
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.base.LayoutAdapterConstructor
import projekt.cloud.piece.cloudy.databinding.FragmentImportAudioBinding
import projekt.cloud.piece.cloudy.util.PixelDensity
import projekt.cloud.piece.cloudy.util.PixelDensity.COMPAT
import projekt.cloud.piece.cloudy.util.SurfaceColorUtil.setSurface3BackgroundColor
import projekt.cloud.piece.cloudy.util.SurfaceColorUtil.surfaceColor2

private typealias ImportAudioLayoutAdapterBuilder =
    LayoutAdapterBuilder<FragmentImportAudioBinding, ImportAudioLayoutAdapter>

private typealias ImportAudioLayoutAdapterConstructor =
    LayoutAdapterConstructor<FragmentImportAudioBinding, ImportAudioLayoutAdapter>

abstract class ImportAudioLayoutAdapter(
    binding: FragmentImportAudioBinding
): BaseLayoutAdapter<FragmentImportAudioBinding>(binding) {

    companion object {

        /**
         * [ImportAudioLayoutAdapter.builder]
         * @type [LayoutAdapterBuilder]
         **/
        val builder: ImportAudioLayoutAdapterBuilder
            get() = ::builder

        /**
         * [ImportAudioLayoutAdapter.builder]
         * @param pixelDensity [PixelDensity]
         * @return [LayoutAdapterConstructor]
         **/
        private fun builder(pixelDensity: PixelDensity): ImportAudioLayoutAdapterConstructor {
            return when (pixelDensity) {
                COMPAT -> ::CompatImpl
                else -> ::W600dpImpl
            }
        }

    }

    /**
     * [ImportAudioLayoutAdapter.retryButton]
     * @type [com.google.android.material.button.MaterialButton]
     **/
    private val retryButton: MaterialButton
        get() = binding.materialButtonRetry

    /**
     * [ImportAudioLayoutAdapter.setupBackgroundColor]
     *
     * @impl [W600dpImpl.setupBackgroundColor]
     **/
    open fun setupBackgroundColor() = Unit

    /**
     * [ImportAudioLayoutAdapter.setupRetryButton]
     * @param fragment [Fragment]
     * @param viewModel [ImportAudioViewModel]
     * @param onClickListener [android.view.View.OnClickListener]
     **/
    open fun setupRetryButton(
        fragment: Fragment, viewModel: ImportAudioViewModel, onClickListener: OnClickListener
    ) {
        val circularDrawable = fragment.requireContext().let { context ->
            IndeterminateDrawable.createCircularDrawable(
                context, CircularProgressIndicatorSpec(context, null)
            )
        }
        retryButton.icon = circularDrawable
        viewModel.loadingStatus.observe(fragment.viewLifecycleOwner) { isLoading ->
            retryButton.icon = when {
                isLoading -> circularDrawable
                else -> null
            }
        }
        retryButton.setOnClickListener(onClickListener)
    }

    private class CompatImpl(binding: FragmentImportAudioBinding): ImportAudioLayoutAdapter(binding)

    private class W600dpImpl(binding: FragmentImportAudioBinding): ImportAudioLayoutAdapter(binding) {

        private val root: ConstraintLayout
            get() = binding.constraintLayoutRoot
        private val card: MaterialCardView
            get() = binding.materialCardViewRoot!!

        override fun setupBackgroundColor() {
            root.setSurface3BackgroundColor()
            card.setCardBackgroundColor(
                card.context.surfaceColor2
            )
        }

    }

}