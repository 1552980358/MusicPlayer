package projekt.cloud.piece.music.player.ui.fragment.permission

import android.Manifest.permission.FOREGROUND_SERVICE
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Rect
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
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.base.interfaces.WindowInsetsInterface
import projekt.cloud.piece.music.player.databinding.FragmentPermissionBinding

class PermissionFragment: BaseFragment<FragmentPermissionBinding>(), WindowInsetsInterface, View.OnClickListener {

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

    private lateinit var requestPermission: ActivityResultLauncher<Array<String>>

    override fun onSetupRequireWindowInsets() = { insets: Rect ->
        appBarLayout.updatePadding(top = insets.top)
        container.updatePadding(bottom = insets.bottom)
    }

    override fun onSetupBinding(binding: FragmentPermissionBinding, savedInstanceState: Bundle?) {

        requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            Log.e(TAG, "RequestMultiplePermissions() => $results")

            results.forEach { (_, isGranted) ->
                if (!isGranted) {
                    return@registerForActivityResult onPermissionGrantDenied()
                }
            }
            completePermissionGrant()
        }

        grant.setOnClickListener(this)
        settings.setOnClickListener(this)

        when {
            checkPermissionsGranted() -> { completePermissionGrant() }
            else -> { startGrantPermissions() }
        }
    }

    private fun startGrantPermissions() {
        Log.e(TAG, "startGrantPermission()")
        onPermissionGrantStart()
        requestPermission.launch(permissions)
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

    private val readMediaPermission: String
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> READ_MEDIA_AUDIO
            else -> READ_EXTERNAL_STORAGE
        }

    private fun checkPermissionsGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(), readMediaPermission
            ) != PERMISSION_GRANTED) {
            return false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && ContextCompat.checkSelfPermission(
                requireContext(), FOREGROUND_SERVICE
            ) != PERMISSION_GRANTED) {
            return false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && ContextCompat.checkSelfPermission(
                requireContext(), POST_NOTIFICATIONS
            ) != PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    private val permissions: Array<String>
        get() {
            return arrayListOf(readMediaPermission)
                .also { arrayList ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        arrayList.add(FOREGROUND_SERVICE)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        arrayList.add(POST_NOTIFICATIONS)
                    }
                }
                .toTypedArray()
        }

    private fun completePermissionGrant() {
        Log.e(TAG, "completePermissionGrant()")
        findNavController().navigate(
            PermissionFragmentDirections.toQueryMedia()
        )
    }

    override fun onClick(v: View?) {
        when (v) {
            grant -> { startGrantPermissions() }
            settings -> { startApplicationDetailPage() }
        }
    }

}