package projekt.cloud.piece.music.player.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui

object PreferenceUtil {
    
    private fun <T: Preference> PreferenceFragmentCompat.preferenceImpl(@StringRes resId: Int) =
        preferenceImpl<T>(getString(resId))
    
    private fun <T: Preference> PreferenceFragmentCompat.preferenceImpl(resId: String) =
        findPreference<T>(resId)
    
    @JvmStatic
    fun PreferenceFragmentCompat.preference(@StringRes resId: Int) =
        preferenceImpl<Preference>(resId)
    
    @JvmStatic
    fun PreferenceFragmentCompat.preference(@StringRes resId: Int, preference: Preference.() -> Unit) =
        preference(resId)?.apply(preference)
    
    @JvmStatic
    fun PreferenceFragmentCompat.switchPreference(@StringRes resId: Int) =
        preferenceImpl<SwitchPreferenceCompat>(resId)
    
    @JvmStatic
    fun PreferenceFragmentCompat.switchPreference(@StringRes resId: Int, switchPreference: SwitchPreferenceCompat.() -> Unit) =
        switchPreference(resId)?.apply(switchPreference)
    
    @JvmStatic
    fun Context.strPrefs(@StringRes resId: Int, defaultValue: String? = null, preference: (String?) -> Unit) = io {
        PreferenceManager.getDefaultSharedPreferences(this@strPrefs).getString(getString(resId), defaultValue)?.let {
            ui { preference.invoke(it) }
        }
    }
    
}