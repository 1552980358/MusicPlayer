package projekt.cloud.piece.music.player.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.preference.PreferenceManager
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui

object SharedPreferencesUtil {
    
    @JvmStatic
    fun Context.strPrefs(@StringRes resId: Int, defaultValue: String? = null) =
        PreferenceManager.getDefaultSharedPreferences(this@strPrefs).getString(getString(resId), defaultValue)
    
    @JvmStatic
    fun Context.strPrefsUnblock(@StringRes resId: Int, defaultValue: String? = null, preference: (String?) -> Unit) = io {
        strPrefs(resId, defaultValue).let {
            ui { preference.invoke(it) }
        }
    }
    
    @JvmStatic
    fun Context.boolPrefs(@StringRes resId: Int, defaultValue: Boolean = false) =
        PreferenceManager.getDefaultSharedPreferences(this@boolPrefs).getBoolean(getString(resId), defaultValue)
    
    @JvmStatic
    fun Context.boolPrefsUnblock(@StringRes resId: Int, defaultValue: Boolean = false, preference: (Boolean) -> Unit) = io {
        boolPrefs(resId, defaultValue).let {
            ui { preference.invoke(it) }
        }
    }
    
}