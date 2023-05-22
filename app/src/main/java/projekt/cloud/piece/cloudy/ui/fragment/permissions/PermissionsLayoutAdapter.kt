package projekt.cloud.piece.cloudy.ui.fragment.permissions

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.cloudy.base.BaseLayoutAdapter
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.base.LayoutAdapterConstructor
import projekt.cloud.piece.cloudy.databinding.FragmentPermissionsBinding
import projekt.cloud.piece.cloudy.util.PixelDensity
import projekt.cloud.piece.cloudy.util.PixelDensity.COMPAT
import projekt.cloud.piece.cloudy.util.implementation.ListUpdatable
import projekt.cloud.piece.cloudy.util.SurfaceColorUtil.setSurface2BackgroundColor
import projekt.cloud.piece.cloudy.util.WindowInsetUtil.applySystemBarsInsets

private typealias PermissionsLayoutAdapterConstructor =
    LayoutAdapterConstructor<FragmentPermissionsBinding, PermissionsLayoutAdapter>

abstract class PermissionsLayoutAdapter(
    binding: FragmentPermissionsBinding
): BaseLayoutAdapter<FragmentPermissionsBinding>(binding), ListUpdatable {

    companion object {

        val builder: LayoutAdapterBuilder<FragmentPermissionsBinding, PermissionsLayoutAdapter>
            get() = ::builder

        private fun builder(pixelDensity: PixelDensity): PermissionsLayoutAdapterConstructor {
            return when (pixelDensity) {
                COMPAT -> ::CompatImpl
                else -> ::W600dpImpl
            }
        }

    }

    /** Common views **/
    private val recyclerView: RecyclerView
        get() = binding.recyclerView

    /**
     * [PermissionsLayoutAdapter.setupRootColor]
     *
     * @pixelDensity [PixelDensity.MEDIUM], [PixelDensity.EXPANDED]
     **/
    open fun setupRootColor() = Unit

    /**
     * [PermissionsLayoutAdapter.setRecyclerViewAdapter]
     * @param adapter [androidx.recyclerview.widget.RecyclerView.Adapter]
     *
     * Set adapter for [androidx.recyclerview.widget.RecyclerView]
     **/
    fun setRecyclerViewAdapter(adapter: RecyclerView.Adapter<*>) {
        recyclerView.adapter = adapter
    }

    override fun update() {
        recyclerView.adapter?.let { adapter ->
            adapter.notifyItemRangeChanged(
                0, adapter.itemCount
            )
        }
    }

    override fun update(position: Int) {
        recyclerView.adapter?.notifyItemChanged(position)
    }

    private class CompatImpl(binding: FragmentPermissionsBinding): PermissionsLayoutAdapter(binding)

    private class W600dpImpl(binding: FragmentPermissionsBinding): PermissionsLayoutAdapter(binding) {

        private val constraintLayoutRoot: ConstraintLayout
            get() = binding.constraintLayoutRoot

        override fun setupRootColor() {
            constraintLayoutRoot.let { constraintLayout ->
                constraintLayout.applySystemBarsInsets()
                constraintLayout.setSurface2BackgroundColor()
            }
        }

    }

}