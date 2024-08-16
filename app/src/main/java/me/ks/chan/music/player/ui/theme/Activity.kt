package me.ks.chan.music.player.ui.theme

import android.graphics.Color
import android.os.Build
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.annotation.RequiresApi

fun ComponentActivity.configureActivity() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        disableSystemBarsContrast()
    }
    enableSystemBarsEdgeToEdge()
}

private fun ComponentActivity.enableSystemBarsEdgeToEdge() {
    enableEdgeToEdge(
        navigationBarStyle = SystemBarStyle.auto(
            Color.TRANSPARENT, Color.TRANSPARENT
        )
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun ComponentActivity.disableSystemBarsContrast() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        @Suppress("Deprecation")
        window.isStatusBarContrastEnforced = false
    }
    window.isNavigationBarContrastEnforced = false
}