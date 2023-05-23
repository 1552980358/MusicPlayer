package projekt.cloud.piece.cloudy.ui.fragment.home

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import projekt.cloud.piece.cloudy.base.BaseLayoutAdapter
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.base.LayoutAdapterConstructor
import projekt.cloud.piece.cloudy.databinding.FragmentHomeBinding
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView
import projekt.cloud.piece.cloudy.util.PixelDensity
import projekt.cloud.piece.cloudy.util.PixelDensity.COMPAT
import projekt.cloud.piece.cloudy.util.CastUtil.cast
import projekt.cloud.piece.cloudy.util.WindowInsetUtil.applyStatusBarInset

private typealias HomeLayoutAdapterBuilder =
    LayoutAdapterBuilder<FragmentHomeBinding, HomeLayoutAdapter>
private typealias HomeLayoutAdapterConstructor =
    LayoutAdapterConstructor<FragmentHomeBinding, HomeLayoutAdapter>

abstract class HomeLayoutAdapter(
    binding: FragmentHomeBinding
): BaseLayoutAdapter<FragmentHomeBinding>(binding) {

    companion object {

        /**
         * [HomeLayoutAdapter.builder]
         * @type [HomeLayoutAdapterBuilder]
         **/
        val builder: HomeLayoutAdapterBuilder
            get() = ::builder

        /**
         * [HomeLayoutAdapter.builder]
         * @param pixelDensity [PixelDensity]
         * @type [HomeLayoutAdapterConstructor]
         **/
        private fun builder(pixelDensity: PixelDensity): HomeLayoutAdapterConstructor {
            return when (pixelDensity) {
                COMPAT -> ::CompatImpl
                else -> ::W600dpImpl
            }
        }

    }

    /**
     * [HomeLayoutAdapter.recyclerView]
     * @type [androidx.recyclerview.widget.RecyclerView]
     * @id [R.id.recycler_view]
     **/
    private val recyclerView: RecyclerView
        get() = binding.recyclerView

    /**
     * [HomeLayoutAdapter.setupRecyclerView]
     * @param fragment
     * @param onClicked [kotlin.jvm.functions.Function1]<[Int], [Unit]>
     *
     * Setup adapter for [HomeLayoutAdapter.recyclerView]
     **/
    fun setupRecyclerView(fragment: Fragment, onClicked: (Int) -> Unit) {
        recyclerView.adapter = HomeRecyclerAdapter(fragment, onClicked)
    }

    /**
     * [HomeLayoutAdapter.setupRecyclerView]
     * @param metadataList [List]<[MetadataView]>
     * @return [Boolean]
     *
     * Set metadata list for [HomeLayoutAdapter.recyclerView]
     **/
    fun updateMetadataList(metadataList: List<MetadataView>?) {
        recyclerView.adapter.cast<HomeRecyclerAdapter>()
            .updateMetadataList(metadataList)
    }

    /**
     * [HomeLayoutAdapter.setupWindowInsets]
     *
     * @impl [CompatImpl.setupWindowInsets], [W600dpImpl.setupWindowInsets]
     *
     * Setup window insets to required views
     **/
    open fun setupWindowInsets() = Unit

    private class CompatImpl(binding: FragmentHomeBinding): HomeLayoutAdapter(binding) {

        /**
         * [CompatImpl.appBarLayout]
         * @type [com.google.android.material.appbar.AppBarLayout]
         * @id [R.id.app_bar_layout]
         **/
        private val appBarLayout: AppBarLayout
            get() = binding.appBarLayout!!

        /**
         * [HomeLayoutAdapter.setupWindowInsets]
         **/
        override fun setupWindowInsets() {
            appBarLayout.applyStatusBarInset()
        }

    }

    private class W600dpImpl(binding: FragmentHomeBinding): HomeLayoutAdapter(binding) {

        /**
         * [W600dpImpl.layoutRoot]
         * @type [androidx.coordinatorlayout.widget.CoordinatorLayout]
         * @id [R.id.coordinator_layout_root]
         **/
        private val layoutRoot: CoordinatorLayout
            get() = binding.coordinatorLayoutRoot

        /**
         * [HomeLayoutAdapter.setupWindowInsets]
         **/
        override fun setupWindowInsets() {
            layoutRoot.applyStatusBarInset()
        }

    }

}