package projekt.cloud.piece.music.player.util

import androidx.annotation.StringRes
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat

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
    
}