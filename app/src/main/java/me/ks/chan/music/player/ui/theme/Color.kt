package me.ks.chan.music.player.ui.theme

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun colorScheme(
    darkTheme: Boolean, dynamicColor: Boolean
): ColorScheme = when {
    dynamicColor && supportDynamicColor -> { dynamicColorScheme(darkTheme) }
    else -> { staticColorScheme(darkTheme) }
}

private inline val supportDynamicColor: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@RequiresApi(Build.VERSION_CODES.S)
@Composable
private fun dynamicColorScheme(
    darkTheme: Boolean,
    dynamicColorScheme: (Context) -> ColorScheme = when {
        darkTheme -> { ::dynamicDarkColorScheme }
        else -> { ::dynamicLightColorScheme }
    },
): ColorScheme = dynamicColorScheme(LocalContext.current)

private fun staticColorScheme(darkTheme: Boolean): ColorScheme = when {
    darkTheme -> { darkColorScheme() }
    else -> { lightColorScheme() }
}
