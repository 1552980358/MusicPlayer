package projekt.cloud.piece.music.player.ui.activity.launcher

import android.Manifest.permission.FOREGROUND_SERVICE
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.databinding.ActivityLauncherBinding
import projekt.cloud.piece.music.player.storage.audio.AudioDatabase.AudioDatabaseUtil.audioDatabase
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase.RuntimeDatabaseUtil.runtimeDatabase
import projekt.cloud.piece.music.player.ui.activity.main.MainActivity
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.main
import projekt.cloud.piece.music.player.util.PreferenceUtil.defaultSharedPreference

class LauncherActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var continueSplashScreen = true
        splashScreen.setKeepOnScreenCondition { continueSplashScreen }

        lifecycleScope.main {
            when {
                checkPermissionsGranted() && checkLauncherComplete(getString(R.string.launcher_complete)) -> {
                    lifecycleScope.main {
                        initialRuntimeDatabase()
                        startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
                        finish()
                    }
                }
                else -> {
                    binding = ActivityLauncherBinding.inflate(layoutInflater)
                    setContentView(binding.root)
                    continueSplashScreen = false
                }
            }
        }
    }

    private val readMediaPermission: String
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> READ_MEDIA_AUDIO
            else -> READ_EXTERNAL_STORAGE
        }

    private fun checkPermissionsGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this, readMediaPermission
        ) != PERMISSION_GRANTED) {
            return false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && ContextCompat.checkSelfPermission(
                this, FOREGROUND_SERVICE
            ) != PERMISSION_GRANTED) {
            return false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && ContextCompat.checkSelfPermission(
                this, POST_NOTIFICATIONS
            ) != PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    private suspend fun checkLauncherComplete(name: String): Boolean {
        return withContext(default) {
            defaultSharedPreference.contains(name) && defaultSharedPreference.getBoolean(name, false)
        }
    }

    private suspend fun initialRuntimeDatabase() {
        withContext(default) {
            runtimeDatabase.audioMetadataDao()
                .insert(audioDatabase.metadataDao().query())
        }
    }

}