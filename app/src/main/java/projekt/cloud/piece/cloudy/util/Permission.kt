package projekt.cloud.piece.cloudy.util

import android.Manifest.permission.FOREGROUND_SERVICE
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import projekt.cloud.piece.cloudy.R

class Permission private constructor(
    val permission: String,
    @StringRes
    val description: Int,
    @DrawableRes
    val icon: Int
) {

    companion object PermissionUtil {

        val permissionList: ArrayList<Permission>
            get() = ArrayList<Permission>().apply(
                ::setupPermissionList
            )

        private fun setupPermissionList(permissionList: ArrayList<Permission>) {
            permissionList += readStoragePermission
            setPostNotificationsPermission(permissionList)
            setForegroundServicePermission(permissionList)
        }

        private val readStoragePermission: Permission
            get() = when {
                SDK_INT >= TIRAMISU -> Permission(
                    READ_MEDIA_AUDIO,
                    R.string.permissions_read_media_audio,
                    R.drawable.ic_round_audio_file_24
                )
                else -> Permission(
                    READ_EXTERNAL_STORAGE,
                    R.string.permissions_read_storage,
                    R.drawable.ic_round_sd_storage_24
                )
            }

        private fun setPostNotificationsPermission(permissionList: ArrayList<Permission>) {
            if (SDK_INT >= TIRAMISU) {
                permissionList += Permission(
                    POST_NOTIFICATIONS,
                    R.string.permissions_post_notifications,
                    R.drawable.ic_round_notifications_24
                )
            }
        }

        private fun setForegroundServicePermission(permissionList: ArrayList<Permission>) {
            if (SDK_INT >= TIRAMISU) {
                permissionList += Permission(
                    FOREGROUND_SERVICE,
                    R.string.permissions_foreground_service,
                    R.drawable.ic_round_play_arrow_24
                )
            }
        }

        fun String.isPermissionGranted(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(context, this) == PackageManager.PERMISSION_GRANTED
        }

    }

    val name: String
        get() = permission.let { permission ->
            permission.substring(permission.lastIndexOf('.') + 1)
        }

    fun isGranted(context: Context): Boolean {
        return permission.isPermissionGranted(context)
    }

}