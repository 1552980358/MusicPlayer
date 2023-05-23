package projekt.cloud.piece.cloudy.ui.fragment.import_audio

import android.view.View.GONE
import android.view.View.OnClickListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.VISIBLE
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.base.BaseLayoutAdapter
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.base.LayoutAdapterConstructor
import projekt.cloud.piece.cloudy.databinding.FragmentImportAudioBinding
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView
import projekt.cloud.piece.cloudy.util.PixelDensity
import projekt.cloud.piece.cloudy.util.PixelDensity.COMPAT
import projekt.cloud.piece.cloudy.util.SurfaceColorUtil.setSurface3BackgroundColor
import projekt.cloud.piece.cloudy.util.SurfaceColorUtil.surfaceColor2
import projekt.cloud.piece.cloudy.util.CastUtil.safeCast

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

    private val listContainer: ConstraintLayout
        get() = binding.constraintLayoutList
    private val recyclerView: RecyclerView
        get() = binding.recyclerView

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

    /**
     * [ImportAudioLayoutAdapter.setupRecyclerView]
     * @param lifecycleOwner [androidx.lifecycle.LifecycleOwner]
     * @param viewModel [ImportAudioViewModel]
     *
     * Set [ImportAudioRecyclerViewAdapter] as adapter of [R.id.recycler_view],
     * and setup content switching due to [ImportAudioViewModel.loadingStatus]
     **/
    fun setupRecyclerView(lifecycleOwner: LifecycleOwner, viewModel: ImportAudioViewModel) {
        // Set adapter
        recyclerView.adapter = ImportAudioRecyclerViewAdapter()
        // Set observer
        setupListContainer(
            lifecycleOwner,
            viewModel,
            loadingConstraintSet,
            endLoadingConstraintSet
        )
    }

    /**
     * [ImportAudioLayoutAdapter.setupListContainer]
     * @param lifecycleOwner [androidx.lifecycle.LifecycleOwner]
     * @param viewModel [ImportAudioViewModel]
     * @param loadingConstraintSet [androidx.constraintlayout.widget.ConstraintSet]
     * @param endLoadingConstraintSet [androidx.constraintlayout.widget.ConstraintSet]
     *
     * Setup observer to [ImportAudioViewModel.loadingStatus] for determining
     * whether show loading animation or show list content
     **/
    private fun setupListContainer(
        lifecycleOwner: LifecycleOwner,
        viewModel: ImportAudioViewModel,
        loadingConstraintSet: ConstraintSet,
        endLoadingConstraintSet: ConstraintSet
    ) {
        viewModel.loadingStatus.observe(lifecycleOwner) { isLoading ->
            applyLoadingStatusConstraintSet(
                getLoadingStatusConstraintSet(isLoading, loadingConstraintSet, endLoadingConstraintSet)
            )
        }
    }

    /**
     * [ImportAudioLayoutAdapter.getLoadingStatusConstraintSet]
     * @param isLoading [Boolean]
     * @param loadingConstraintSet [androidx.constraintlayout.widget.ConstraintSet]
     * @param endLoadingConstraintSet [androidx.constraintlayout.widget.ConstraintSet]
     *
     * Return related [androidx.constraintlayout.widget.ConstraintSet] due to [isLoading]
     **/
    private fun getLoadingStatusConstraintSet(
        isLoading: Boolean, loadingConstraintSet: ConstraintSet, endLoadingConstraintSet: ConstraintSet
    ): ConstraintSet {
        return when {
            isLoading -> loadingConstraintSet
            else -> endLoadingConstraintSet
        }
    }

    /**
     * [ImportAudioLayoutAdapter.applyLoadingStatusConstraintSet]
     * @param constraintSet [androidx.constraintlayout.widget.ConstraintSet]
     *
     * Apply [androidx.constraintlayout.widget.ConstraintSet]
     * to [androidx.constraintlayout.widget.ConstraintLayout]
     **/
    private fun applyLoadingStatusConstraintSet(constraintSet: ConstraintSet) {
        constraintSet.applyTo(listContainer)
    }

    /**
     * [ImportAudioLayoutAdapter.loadingConstraintSet]
     * @type [androidx.constraintlayout.widget.ConstraintSet]
     **/
    private val loadingConstraintSet: ConstraintSet
        get() = ConstraintSet().also(::setupHiddenLoadingConstraintSet)
    /**
     * [ImportAudioLayoutAdapter.endLoadingConstraintSet]
     * @type [androidx.constraintlayout.widget.ConstraintSet]
     **/
    private val endLoadingConstraintSet: ConstraintSet
        get() = loadingConstraintSet.also(::setupEndLoadingConstraintSet)

    /**
     * [ImportAudioLayoutAdapter.setupHiddenLoadingConstraintSet]
     * @param constraintSet [androidx.constraintlayout.widget.ConstraintSet]
     *
     * Clone [R.id.constraint_layout_list] into [androidx.constraintlayout.widget.ConstraintSet]
     **/
    private fun setupHiddenLoadingConstraintSet(constraintSet: ConstraintSet) {
        constraintSet.clone(listContainer)
    }

    /**
     * [ImportAudioLayoutAdapter.setupHiddenLoadingConstraintSet]
     * @param constraintSet [androidx.constraintlayout.widget.ConstraintSet]
     *
     * Hide [R.id.circular_progress_indicator] and show [R.id.recycler_view]
     * of [R.id.constraint_layout_list]
     **/
    private fun setupEndLoadingConstraintSet(constraintSet: ConstraintSet) {
        constraintSet.setVisibility(R.id.circular_progress_indicator, GONE)
        constraintSet.setVisibility(R.id.recycler_view, VISIBLE)
    }

    /**
     * [ImportAudioLayoutAdapter.updateMetadataList]
     * @param metadataList [List]
     *
     * Update metadata list in [ImportAudioRecyclerViewAdapter]
     */
    fun updateMetadataList(metadataList: List<MetadataView>) {
        recyclerView.adapter
            .mayType<ImportAudioRecyclerViewAdapter>()
            ?.update(metadataList)
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