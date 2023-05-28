package projekt.cloud.piece.cloudy.ui.fragment.permissions

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import projekt.cloud.piece.cloudy.base.BaseLayoutAdapter
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.base.LayoutAdapterConstructor
import projekt.cloud.piece.cloudy.databinding.FragmentPermissionsBinding
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.util.PixelDensity
import projekt.cloud.piece.cloudy.util.PixelDensity.COMPAT
import projekt.cloud.piece.cloudy.util.implementation.ListUpdatable
import projekt.cloud.piece.cloudy.util.SurfaceColorUtil.setSurface2BackgroundColor
import projekt.cloud.piece.cloudy.util.WindowInsetUtil.applyNavigationBarInset
import projekt.cloud.piece.cloudy.util.WindowInsetUtil.applyStatusBarInset
import projekt.cloud.piece.cloudy.util.WindowInsetUtil.applySystemBarsInsets

private typealias PermissionsLayoutAdapterBuilder =
    LayoutAdapterBuilder<FragmentPermissionsBinding, PermissionsLayoutAdapter>
private typealias PermissionsLayoutAdapterConstructor =
    LayoutAdapterConstructor<FragmentPermissionsBinding, PermissionsLayoutAdapter>

/**
 * [PermissionsLayoutAdapter]
 * @abstractExtends [BaseLayoutAdapter]
 *   @typeParam [FragmentPermissionsBinding]
 * @interface [ListUpdatable]
 * @param binding [FragmentPermissionsBinding]
 **/
abstract class PermissionsLayoutAdapter(
    binding: FragmentPermissionsBinding
): BaseLayoutAdapter<FragmentPermissionsBinding>(binding), ListUpdatable {

    companion object {

        /**
         * [PermissionsLayoutAdapter.builder]
         * @type [PermissionsLayoutAdapterBuilder]
         **/
        val builder: PermissionsLayoutAdapterBuilder
            get() = ::builder

        /**
         * [PermissionsLayoutAdapter.builder]
         * @param pixelDensity [PixelDensity]
         * @return [PermissionsLayoutAdapterConstructor]
         **/
        private fun builder(pixelDensity: PixelDensity): PermissionsLayoutAdapterConstructor {
            return when (pixelDensity) {
                COMPAT -> ::CompatImpl
                else -> ::LargeScreenImpl
            }
        }

    }

    /**
     * [PermissionsLayoutAdapter.recyclerView]
     * @type [androidx.constraintlayout.widget.ConstraintLayout]
     * @layout [R.layout.fragment_permissions]
     * @id [R.id.constraint_layout_root]
     **/
    protected val constraintLayoutRoot: ConstraintLayout
        get() = binding.constraintLayoutRoot

    /**
     * [PermissionsLayoutAdapter.setupWindowInsets]
     *
     * @impl [CompatImpl.setupWindowInsets], [LargeScreenImpl.setupWindowInsets]
     **/
    open fun setupWindowInsets() = Unit

    /**
     * [PermissionsLayoutAdapter.setupRootColor]
     *
     * @impl [LargeScreenImpl.setupRootColor]
     **/
    open fun setupRootColor() = Unit

    /**
     * [PermissionsLayoutAdapter.recyclerView]
     * @type [androidx.recyclerview.widget.RecyclerView]
     * @layout [R.layout.fragment_permissions]
     * @id [R.id.recycler_view]
     **/
    private val recyclerView: RecyclerView
        get() = binding.recyclerView

    /**
     * [PermissionsLayoutAdapter.setRecyclerViewAdapter]
     * @param adapter [androidx.recyclerview.widget.RecyclerView.Adapter]
     *
     * Set adapter for [androidx.recyclerview.widget.RecyclerView]
     **/
    fun setRecyclerViewAdapter(adapter: RecyclerView.Adapter<*>) {
        recyclerView.adapter = adapter
    }

    /**
     * [ListUpdatable.update]
     **/
    override fun update() {
        recyclerView.adapter?.let { adapter ->
            adapter.notifyItemRangeChanged(
                0, adapter.itemCount
            )
        }
    }

    /**
     * [ListUpdatable.update]
     * @param position [Int]
     **/
    override fun update(position: Int) {
        recyclerView.adapter?.notifyItemChanged(position)
    }

    /**
     * [PermissionsLayoutAdapter.CompatImpl]
     * @extends [PermissionsLayoutAdapter]
     **/
    private class CompatImpl(binding: FragmentPermissionsBinding): PermissionsLayoutAdapter(binding) {

        /**
         * [CompatImpl.appBarLayout]
         * @type [com.google.android.material.appbar.AppBarLayout]
         * @layout [R.layout.fragment_permissions]
         * @id [R.id.app_bar_layout]
         **/
        private val appBarLayout: AppBarLayout
            get() = binding.appBarLayout!!

        /**
         * [PermissionsLayoutAdapter.setupWindowInsets]
         **/
        override fun setupWindowInsets() {
            appBarLayout.applyStatusBarInset()
            constraintLayoutRoot.applyNavigationBarInset()
        }

    }

    /**
     * [PermissionsLayoutAdapter.LargeScreenImpl]
     * @extends [PermissionsLayoutAdapter]
     **/
    private class LargeScreenImpl(binding: FragmentPermissionsBinding): PermissionsLayoutAdapter(binding) {

        /**
         * [PermissionsLayoutAdapter.setupWindowInsets]
         **/
        override fun setupWindowInsets() {
            constraintLayoutRoot.applySystemBarsInsets()
        }

        /**
         * [PermissionsLayoutAdapter.setupRootColor]
         **/
        override fun setupRootColor() {
            constraintLayoutRoot.setSurface2BackgroundColor()
        }

    }

}