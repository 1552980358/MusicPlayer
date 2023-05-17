package projekt.cloud.piece.cloudy.ui.activity.guide

import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import projekt.cloud.piece.cloudy.ui.fragment.permissions.PermissionsFragment
import projekt.cloud.piece.cloudy.util.CoroutineUtil.defaultBlocking
import projekt.cloud.piece.cloudy.util.Permission
import projekt.cloud.piece.cloudy.util.Permission.PermissionUtil.permissionList

class GuideViewModel: ViewModel() {

    companion object GuideViewModelUtil {

        @MainThread
        fun PermissionsFragment.guideViewModel(): Lazy<GuideViewModel> {
            return activityViewModels()
        }

        @MainThread
        fun GuideActivity.guideViewModel(): Lazy<GuideViewModel> {
            return viewModels()
        }

    }

    private var _permissions: ArrayList<Permission>? = null
    val permissions: List<Permission>
        get() = _permissions!!

    init {
        _permissions = permissionList
    }

    suspend fun permissionStrings(): Array<String> {
        return defaultBlocking {
            permissions.map { it.permission }
                .toTypedArray()
        }
    }

    override fun onCleared() {
        Log.d("GuidViewModel", "onCleared")
        super.onCleared()
        clearPermissionList()
    }

    private fun clearPermissionList() {
        _permissions?.clear()
        _permissions = null
    }

}