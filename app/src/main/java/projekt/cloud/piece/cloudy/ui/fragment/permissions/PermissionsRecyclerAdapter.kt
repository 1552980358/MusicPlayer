package projekt.cloud.piece.cloudy.ui.fragment.permissions

import android.content.Context
import projekt.cloud.piece.cloudy.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.cloudy.databinding.PermissionsRecyclerLayoutBinding
import projekt.cloud.piece.cloudy.util.Permission
import projekt.cloud.piece.cloudy.util.ViewBindingInflater

class PermissionsRecyclerAdapter(
    private val permissions: List<Permission>,
    private val onItemClicked: (Permission, Int) -> Unit
): BaseRecyclerViewAdapter<PermissionsRecyclerLayoutBinding>() {

    /**
     * [BaseRecyclerViewAdapter.viewBindingInflater]
     * @type [ViewBindingInflater]
     **/
    override val viewBindingInflater: ViewBindingInflater<PermissionsRecyclerLayoutBinding>
        get() = PermissionsRecyclerLayoutBinding::inflate

    /**
     * [androidx.recyclerview.widget.RecyclerView.Adapter.getItemCount]
     * @return [Int]
     **/
    override fun getItemCount() = permissions.size

    /**
     * [BaseRecyclerViewAdapter.onViewBindingCreated]
     * @param binding [PermissionsRecyclerLayoutBinding]
     **/
    override fun onViewBindingCreated(binding: PermissionsRecyclerLayoutBinding) {
        binding.onClick = onItemClicked
    }

    /**
     * [BaseRecyclerViewAdapter.onBindViewHolder]
     * @param context [android.content.Context]
     * @param binding [PermissionsRecyclerLayoutBinding]
     * @param position [Int]
     **/
    override fun onBindViewHolder(
        context: Context, binding: PermissionsRecyclerLayoutBinding, position: Int
    ) {
        setBindingData(context, binding, position, permissions[position])
    }

    /**
     * [PermissionsRecyclerAdapter.setBindingData]
     * @param context [android.content.Context]
     * @param binding [PermissionsRecyclerLayoutBinding]
     * @param position [Int]
     * @param permission [Permission]
     *
     * Set data to [PermissionsRecyclerLayoutBinding]
     **/
    private fun setBindingData(
        context: Context,
        binding: PermissionsRecyclerLayoutBinding,
        position: Int,
        permission: Permission
    ) {
        binding.permission = permission
        binding.position = position
        binding.isGranted = permission.isGranted(context)
        binding.appCompatImageViewLeading
            .setImageResource(permission.icon)
    }

}