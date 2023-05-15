package projekt.cloud.piece.cloudy.ui.fragment.permission

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.cloudy.base.BaseLayoutAdapter
import projekt.cloud.piece.cloudy.databinding.FragmentPermissionsBinding
import projekt.cloud.piece.cloudy.util.PixelDensity
import projekt.cloud.piece.cloudy.util.PixelDensity.COMPAT
import projekt.cloud.piece.cloudy.util.PixelDensity.EXPANDED
import projekt.cloud.piece.cloudy.util.PixelDensity.MEDIUM
import projekt.cloud.piece.cloudy.util.Updatable
import projekt.cloud.piece.cloudy.util.SurfaceColorUtil.setSurface2BackgroundColor
import projekt.cloud.piece.cloudy.util.WindowInsetUtil.applySystemBarsInsets

abstract class PermissionLayoutAdapter(
    binding: FragmentPermissionsBinding
): BaseLayoutAdapter<FragmentPermissionsBinding>(binding), Updatable {

    companion object PermissionLayoutAdapterUtil {

        val inflater: (PixelDensity, FragmentPermissionsBinding) -> PermissionLayoutAdapter
            get() = ::inflate

        private fun inflate(
            pixelDensity: PixelDensity, binding: FragmentPermissionsBinding
        ): PermissionLayoutAdapter {
            return when (pixelDensity) {
                COMPAT -> CompatImpl(binding)
                MEDIUM, EXPANDED -> W600dpImpl(binding)
            }
        }

    }

    /** Common views **/
    private val recyclerView: RecyclerView
        get() = binding.recyclerView

    /**
     * [PermissionLayoutAdapter.setupRootColor]
     *
     * @pixelDensity [PixelDensity.MEDIUM], [PixelDensity.EXPANDED]
     **/
    open fun setupRootColor() = Unit

    /**
     * [PermissionLayoutAdapter.setRecyclerViewAdapter]
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

    private class CompatImpl(binding: FragmentPermissionsBinding): PermissionLayoutAdapter(binding)

    private class W600dpImpl(binding: FragmentPermissionsBinding): PermissionLayoutAdapter(binding) {

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