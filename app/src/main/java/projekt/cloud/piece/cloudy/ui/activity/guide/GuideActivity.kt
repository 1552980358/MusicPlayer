package projekt.cloud.piece.cloudy.ui.activity.guide

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import kotlinx.coroutines.CoroutineScope
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.databinding.ActivityGuideBinding
import projekt.cloud.piece.cloudy.storage.audio.AudioDatabase.AudioDatabaseUtil.audioDatabase
import projekt.cloud.piece.cloudy.storage.runtime.RuntimeDatabase.RuntimeDatabaseUtil.runtimeDatabase
import projekt.cloud.piece.cloudy.storage.util.PreferenceUtil.defaultSharedPreference
import projekt.cloud.piece.cloudy.ui.activity.main.MainActivity
import projekt.cloud.piece.cloudy.util.CoroutineUtil.defaultBlocking
import projekt.cloud.piece.cloudy.util.CoroutineUtil.ioAsync
import projekt.cloud.piece.cloudy.util.CoroutineUtil.ioBlocking
import projekt.cloud.piece.cloudy.util.LifecycleOwnerUtil.main
import projekt.cloud.piece.cloudy.util.Permission
import projekt.cloud.piece.cloudy.util.Permission.PermissionUtil.permissions

class GuideActivity: AppCompatActivity() {

    /**
     * [GuideActivity.sharedPreferences]
     * @type [android.content.SharedPreferences]
     **/
    private val sharedPreferences by defaultSharedPreference()

    /**
     * [GuideActivity.splashScreen]
     * @type [androidx.core.splashscreen.SplashScreen]
     **/
    private val splashScreen by lazy { installSplashScreen() }

    /**
     * [android.app.Activity.onCreate]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        splashScreen.setKeepOnScreenCondition { true }
        super.onCreate(savedInstanceState)

        main(::startSetupCheck)
    }

    /**
     * [GuideActivity.startGuideAndSetup]
     * @param coroutineScope [CoroutineScope]
     *
     * Start setup check in coroutine
     **/
    private suspend fun startSetupCheck(coroutineScope: CoroutineScope) {
        when {
            checkPermissions() && isSetupCompleted() -> {
                completeSetup(coroutineScope)
            }
            else -> {
                startGuideAndSetup()
            }
        }
    }

    /**
     * [GuideActivity.completeSetup]
     * @param coroutineScope [CoroutineScope]
     *
     * Complete setup / complete setup check
     **/
    private suspend fun completeSetup(coroutineScope: CoroutineScope) {
        initialDatabases(coroutineScope)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /**
     * [GuideActivity.startGuideAndSetup]
     *
     * Show guide and setup content
     **/
    private fun startGuideAndSetup() {
        setContentView(
            ActivityGuideBinding.inflate(layoutInflater)
                .root
        )
        splashScreen.setKeepOnScreenCondition { false }
    }

    /**
     * [GuideActivity.initialDatabases]
     * @param coroutineScope [CoroutineScope]
     *
     * Create databases instances
     **/
    private suspend fun initialDatabases(coroutineScope: CoroutineScope) {
        val audioDatabaseAsync = coroutineScope.ioAsync { audioDatabase }
        val runtimeDatabaseAsync = coroutineScope.ioAsync { runtimeDatabase }
        audioDatabaseAsync.await()
        runtimeDatabaseAsync.await()
    }

    /**
     * [GuideActivity.checkPermissions]
     * @return [Boolean]
     *
     * Check if permissions are all gained
     **/
    private suspend fun checkPermissions(): Boolean {
        return defaultBlocking {
            checkPermissions(permissions)
        }
    }

    /**
     * [GuideActivity.checkPermissions]
     * @param permissions [List]<[Permission]>
     * @return [Boolean]
     *
     * Implementation of [GuideActivity.checkPermissions]
     **/
    private fun checkPermissions(permissions: List<Permission>): Boolean {
        permissions.forEach { permission ->
            if (!permission.isGranted(this)) {
                return false
            }
        }
        return true
    }

    /**
     * [GuideActivity.isSetupCompleted]
     * @return [Boolean]
     *
     * Check if setup procedure are done
     **/
    private suspend fun isSetupCompleted(): Boolean {
        return ioBlocking {
            sharedPreferences.getBoolean(
                getString(R.string.guide_setup), false
            )
        }
    }

}