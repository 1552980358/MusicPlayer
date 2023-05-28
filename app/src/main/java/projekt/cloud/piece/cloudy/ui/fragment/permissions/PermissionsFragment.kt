package projekt.cloud.piece.cloudy.ui.fragment.permissions

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.platform.MaterialFadeThrough
import kotlinx.coroutines.CoroutineScope
import projekt.cloud.piece.cloudy.base.BaseFragment
import projekt.cloud.piece.cloudy.base.BaseMultiLayoutFragment
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.databinding.FragmentPermissionsBinding
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.ui.activity.guide.GuideViewModel.GuideViewModelUtil.guideViewModel
import projekt.cloud.piece.cloudy.util.CoroutineUtil.defaultBlocking
import projekt.cloud.piece.cloudy.util.LifecycleOwnerUtil.main
import projekt.cloud.piece.cloudy.util.Permission
import projekt.cloud.piece.cloudy.util.ViewBindingInflater

private typealias BasePermissionsFragment = BaseMultiLayoutFragment<FragmentPermissionsBinding, PermissionsLayoutAdapter>

/**
 * [PermissionsFragment]
 * @extends [BaseMultiLayoutFragment]
 *   @typeParam [FragmentPermissionsBinding]
 *   @typeParam [PermissionsLayoutAdapter]
 **/
class PermissionsFragment: BasePermissionsFragment() {

    /**
     * [BaseMultiLayoutFragment.viewBindingInflater]
     * @type [ViewBindingInflater]
     **/
    override val viewBindingInflater: ViewBindingInflater<FragmentPermissionsBinding>
        get() = FragmentPermissionsBinding::inflate

    /**
     * [BaseMultiLayoutFragment.layoutAdapterBuilder]
     * @type [LayoutAdapterBuilder]
     **/
    override val layoutAdapterBuilder: LayoutAdapterBuilder<FragmentPermissionsBinding, PermissionsLayoutAdapter>
        get() = PermissionsLayoutAdapter.builder

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
        startCheckPermissions()
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
        layoutAdapter safely { layoutAdapter ->
            layoutAdapter.update()
        }
        startCheckPermissions()
    }

    private val viewModel by guideViewModel()

    /**
     * [androidx.fragment.app.Fragment.onCreate]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransitions()
    }

    private fun setupTransitions() {
        exitTransition = MaterialFadeThrough()
        reenterTransition = MaterialFadeThrough()
        enterTransition = MaterialFadeThrough()
    }

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
        main(::checkPermissionsAndStartRequest)
    }

    /**
     * [BaseMultiLayoutFragment.onSetupLayoutAdapter]
     * @param layoutAdapter [PermissionsLayoutAdapter]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onSetupLayoutAdapter(layoutAdapter: PermissionsLayoutAdapter, savedInstanceState: Bundle?) {
        layoutAdapter.setupWindowInsets()
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
        layoutAdapter safely { layoutAdapter ->
            layoutAdapter.setRecyclerViewAdapter(
                getRecyclerViewAdapter(viewModel.permissions)
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
                    layoutAdapter safely { layoutAdapter ->
                        layoutAdapter.update(pos)
                    }
                }
                requestPermission.launch(permission.permission)
            }
        }
    }

    /**
     * [PermissionsFragment.checkPermissionsAndStartRequest]
     * @param coroutineScope [CoroutineScope]
     **/
    private suspend fun checkPermissionsAndStartRequest(
        @Suppress("UNUSED_PARAMETER")
        coroutineScope: CoroutineScope
    ) {
        when {
            checkPermissions() -> {
                navigateToImportAudio()
            }
            else -> {
                requestMultiplePermissions.launch(viewModel.permissionStrings())
            }
        }
    }

    /**
     * [PermissionsFragment.startCheckPermissions]
     **/
    private fun startCheckPermissions() {
        main(::startCheckPermission)
    }

    /**
     * [PermissionsFragment.checkPermissions]
     * @return [Boolean]
     **/
    private suspend fun startCheckPermission(
        @Suppress("UNUSED_PARAMETER")
        coroutineScope: CoroutineScope
    ) {
        if (checkPermissions()) {
            navigateToImportAudio()
        }
    }

    /**
     * [PermissionsFragment.checkPermissions]
     * @return [Boolean]
     **/
    private suspend fun checkPermissions(): Boolean {
        return defaultBlocking {
            checkPermissions(viewModel.permissions)
        }
    }

    /**
     * [PermissionsFragment.checkPermissions]
     * @param permissions [List]<[Permission]>
     * @return [Boolean]
     **/
    private fun checkPermissions(permissions: List<Permission>): Boolean {
        return requireContext().let { context ->
            for (permission in permissions) {
                if (!permission.isGranted(context)) {
                    return@let false
                }
            }
            true
        }
    }

    /**
     * [PermissionsFragment.navigateToImportAudio]
     *
     * Navigate to [R.id.import_audio]
     **/
    private fun navigateToImportAudio() {
        findNavController().navigate(
            PermissionsFragmentDirections.toImportAudio()
        )
    }

}