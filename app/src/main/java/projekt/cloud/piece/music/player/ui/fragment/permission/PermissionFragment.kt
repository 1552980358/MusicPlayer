package projekt.cloud.piece.music.player.ui.fragment.permission

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.view.updatePadding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.databinding.FragmentPermissionBinding
import projekt.cloud.piece.music.player.util.ContextUtil.requireWindowInsets

class PermissionFragment: BaseFragment<FragmentPermissionBinding>(), View.OnClickListener {

    private companion object {
        const val TAG = "PermissionFragment"
        const val URI_SCHEME_PACKAGE = "package"
    }

    override val viewBindingClass: Class<FragmentPermissionBinding>
        get() = FragmentPermissionBinding::class.java

    private val container: ConstraintLayout
        get() = binding.constraintLayout
    private val appBarLayout: AppBarLayout
        get() = binding.appBarLayout
    private val grant: MaterialButton
        get() = binding.materialButtonGrant
    private val settings: MaterialButton
        get() = binding.materialButtonSettings

    private lateinit var requestPermission: ActivityResultLauncher<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireContext().requireWindowInsets {  rect ->
            appBarLayout.updatePadding(top = rect.top)
            container.updatePadding(bottom = rect.bottom)
        }

        requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Log.e(TAG, "RequestPermission() => $isGranted")
            when {
                isGranted -> { completePermissionGrant() }
                else -> { onPermissionGrantDenied() }
            }
        }

        grant.setOnClickListener(this)
        settings.setOnClickListener(this)

        when {
            checkPermissionGranted -> { completePermissionGrant() }
            else -> { startGrantPermission() }
        }
    }

    private val checkPermissionGranted: Boolean
        get() = ContextCompat.checkSelfPermission(requireContext(), permissionRequest) == PERMISSION_GRANTED

    private fun startGrantPermission() {
        Log.e(TAG, "startGrantPermission()")
        onPermissionGrantStart()
        requestPermission.launch(permissionRequest)
    }

    private fun startApplicationDetailPage() {
        startActivity(
            Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                .addFlags(FLAG_ACTIVITY_NEW_TASK)
                .setData(Uri.fromParts(URI_SCHEME_PACKAGE, requireContext().packageName, null))
        )
    }

    private fun onPermissionGrantStart() {
        Log.e(TAG, "onPermissionGrantDenied()")
        grant.icon = IndeterminateDrawable.createCircularDrawable(
            requireContext(),
            CircularProgressIndicatorSpec(requireContext(), null).apply {
                indicatorColors[0] = TypedValue().let { typeValue ->
                    requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typeValue, true)
                    typeValue.data
                }
            }
        )
        // Hide text
        grant.textScaleX = 0F
    }

    private fun onPermissionGrantDenied() {
        Log.e(TAG, "onPermissionGrantDenied()")
        grant.icon = null
        // Show text
        grant.textScaleX = 1F
    }

    private val permissionRequest: String
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> READ_MEDIA_AUDIO
            else -> READ_EXTERNAL_STORAGE
        }

    private fun completePermissionGrant() {
        Log.e(TAG, "completePermissionGrant()")
    }

    override fun onClick(v: View?) {
        when (v) {
            grant -> { startGrantPermission() }
            settings -> { startApplicationDetailPage() }
        }
    }

}