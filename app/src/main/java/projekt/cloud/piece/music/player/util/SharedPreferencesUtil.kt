package projekt.cloud.piece.music.player.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.preference.PreferenceManager
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui

object SharedPreferencesUtil {
    
    @JvmStatic
    fun Context.strPrefs(@StringRes resId: Int, defaultValue: String? = null, preference: (String?) -> Unit) = CoroutineUtil.io {
        PreferenceManager.getDefaultSharedPreferences(this@strPrefs).getString(getString(resId), defaultValue).let {
            ui { preference.invoke(it) }
        }
    }
    
    @JvmStatic
    fun Context.boolPrefs(@StringRes resId: Int, defaultValue: Boolean = false, preference: (Boolean) -> Unit) = CoroutineUtil.io {
        PreferenceManager.getDefaultSharedPreferences(this@boolPrefs).getBoolean(getString(resId), defaultValue).let {
            ui { preference.invoke(it) }
        }
    }
    
}