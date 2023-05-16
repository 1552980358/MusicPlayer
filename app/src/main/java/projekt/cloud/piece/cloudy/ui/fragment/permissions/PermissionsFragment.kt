package projekt.cloud.piece.cloudy.ui.fragment.permissions

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import projekt.cloud.piece.cloudy.base.BaseFragment
import projekt.cloud.piece.cloudy.base.BaseMultiLayoutFragment
import projekt.cloud.piece.cloudy.base.LayoutAdapterInflater
import projekt.cloud.piece.cloudy.databinding.FragmentPermissionsBinding
import projekt.cloud.piece.cloudy.util.CoroutineUtil.defaultBlocking
import projekt.cloud.piece.cloudy.util.LifecycleOwnerUtil.main
import projekt.cloud.piece.cloudy.util.Permission
import projekt.cloud.piece.cloudy.util.Permission.PermissionUtil.permissionStrings
import projekt.cloud.piece.cloudy.util.Permission.PermissionUtil.permissions
import projekt.cloud.piece.cloudy.util.ViewBindingInflater

private typealias BasePermissionsFragment = BaseMultiLayoutFragment<FragmentPermissionsBinding, PermissionsLayoutAdapter>

class PermissionsFragment: BasePermissionsFragment() {

    /**
     * [BaseMultiLayoutFragment.layoutAdapterInflater]
     * @type [LayoutAdapterInflater]
     **/
    override val layoutAdapterInflater: LayoutAdapterInflater<FragmentPermissionsBinding, PermissionsLayoutAdapter>
        get() = PermissionsLayoutAdapter.inflater

    /**
     * [PermissionsFragment.requestPermission]
     * @type [androidx.activity.result.ActivityResultLauncher]<[String]>
     **/
    private val requestPermission = registerForActivityResult(RequestPermission(), ::requestPermissionCallback)

    /**
     * [PermissionsFragment.requestPermissionCallback]
     * @type [kotlin.jvm.functions.Function0]<[Unit]>
     **/
    private var requestPermissionCallback: (() -> Unit)? = null
    /**
     * [PermissionsFragment.requestPermissionCallback]
     * @param isGranted [Boolean]
     **/
    private fun requestPermissionCallback(@Suppress("UNUSED_PARAMETER") isGranted: Boolean) {
        requestPermissionCallback?.invoke()
        requestPermissionCallback = null
    }

    /**
     * [PermissionsFragment.requestMultiplePermissions]
     * @type [androidx.activity.result.ActivityResultLauncher]<[Array]<[String]>>
     **/
    private val requestMultiplePermissions = registerForActivityResult(
        RequestMultiplePermissions(), ::requestMultiplePermissionsCallback
    )
    /**
     * [PermissionsFragment.requestMultiplePermissionsCallback]
     * @type [Map]<[String]<[Boolean]>>
     **/
    private fun requestMultiplePermissionsCallback(
        @Suppress("UNUSED_PARAMETER") map: Map<String, Boolean>
    ) {
        requireLayoutAdapter { layoutAdapter ->
            layoutAdapter.update()
        }
    }

    /**
     * [BaseMultiLayoutFragment.viewBindingInflater]
     * @type [ViewBindingInflater]
     **/
    override val viewBindingInflater: ViewBindingInflater<FragmentPermissionsBinding>
        get() = FragmentPermissionsBinding::inflate

    /**
     * [BaseFragment.onSetupBinding]
     * @param binding [FragmentPermissionsBinding]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onSetupBinding(binding: FragmentPermissionsBinding, savedInstanceState: Bundle?) {
        super.onSetupBinding(binding, savedInstanceState)
        binding.materialButtonGrant
            .setOnClickListener(::grantButtonClicked)
    }

    /**
     * [PermissionsFragment.grantButtonClicked]
     * @param view [android.view.View]
     **/
    private fun grantButtonClicked(
        @Suppress("UNUSED_PARAMETER")
        view: View
    ) {
        requestMultiplePermissions.launch(permissionStrings)
    }

    /**
     * [BaseMultiLayoutFragment.onSetupLayoutAdapter]
     * @param layoutAdapter [PermissionsLayoutAdapter]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onSetupLayoutAdapter(layoutAdapter: PermissionsLayoutAdapter, savedInstanceState: Bundle?) {
        layoutAdapter.setupRootColor()
        main(::startShowRequiredPermissions)
    }

    /**
     * [PermissionsFragment.startShowRequiredPermissions]
     * @param coroutineScope [CoroutineScope]
     *
     * Start request permissions
     **/
    private suspend fun startShowRequiredPermissions(
        @Suppress("UNUSED_PARAMETER")
        coroutineScope: CoroutineScope
    ) {
        requireLayoutAdapter { layoutAdapter ->
            layoutAdapter.setRecyclerViewAdapter(
                getRecyclerViewAdapter(permissions)
            )
        }
    }

    /**
     * [PermissionsFragment.getRecyclerViewAdapter]
     * @param permissionList [List]<[Permission]>
     * @return [androidx.recyclerview.widget.RecyclerView.Adapter]
     *
     * Create adapter
     **/
    private suspend fun getRecyclerViewAdapter(
        permissionList: List<Permission>
    ): RecyclerView.Adapter<*> {
        return defaultBlocking {
            PermissionsRecyclerAdapter(permissionList, ::onPermissionItemClicked)
        }
    }

    /**
     * [PermissionsFragment.onPermissionItemClicked]
     * @param permission [Permission]
     * @param pos [Int]
     *
     * Task when item in permission list clicked
     **/
    private fun onPermissionItemClicked(permission: Permission, pos: Int) {
        main {
            if (!permission.isGranted(requireContext())) {
                requestPermissionCallback = {
                    requireLayoutAdapter { layoutAdapter ->
                        layoutAdapter.update(pos)
                    }
                }
                requestPermission.launch(permission.permission)
            }
        }
    }

}