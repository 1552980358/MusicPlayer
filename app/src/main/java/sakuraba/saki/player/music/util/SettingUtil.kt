package sakuraba.saki.player.music.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager

object SettingUtil {

    const val KEY_AUDIO_FILTER_SIZE_ENABLE = "key_audio_filter_size_enable"
    const val KEY_AUDIO_FILTER_SIZE_VALUE = "key_audio_filter_size_value"
    const val KEY_AUDIO_FILTER_DURATION_ENABLE = "key_audio_filter_duration_enable"
    const val KEY_AUDIO_FILTER_DURATION_VALUE = "key_audio_filter_duration_value"
    
    val Context.defaultSharedPreference get() = PreferenceManager.getDefaultSharedPreferences(this)!!
    val Fragment.defaultSharedPreference get() = requireContext().defaultSharedPreference
    
    fun Context.getIntSetting(key: String): Int? {
        val sharedPreferenceManager = defaultSharedPreference
        if (!sharedPreferenceManager.contains(key)) {
            return null
        }
        return sharedPreferenceManager.getString(key, null)?.toInt()
    }

    fun Context.getIntSetting(@StringRes resId: Int) = getIntSetting(getString(resId))
    fun Fragment.getIntSetting(@StringRes resId: Int) = requireContext().getIntSetting(resId)

    fun Context.getIntSettingOrThrow(@StringRes resId: Int) = getIntSetting(getString(resId)) ?: throw Exception()
    fun Fragment.getIntSettingOrThrow(@StringRes resId: Int) = requireContext().getIntSettingOrThrow(resId)
    
    /**************************************************************************************/
    
    private fun Context.getStringSetting(key: String): String? {
        val sharedPreferenceManager = PreferenceManager.getDefaultSharedPreferences(this)
        if (!sharedPreferenceManager.contains(key)) {
            return null
        }
        return sharedPreferenceManager.getString(key, null)
    }

    fun Context.getStringSetting(@StringRes resId: Int) = getStringSetting(getString(resId))
    fun Fragment.getStringSetting(@StringRes resId: Int) = requireContext().getStringSetting(resId)

    fun Context.getStringSettingOrThrow(@StringRes resId: Int) = getStringSetting(getString(resId)) ?: throw Exception()
    fun Fragment.getStringSettingOrThrow(@StringRes resId: Int) = requireContext().getStringSettingOrThrow(resId)
    
    /**************************************************************************************/
    fun Context.getBooleanSetting(key: String): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (!sharedPreferences.contains(key)) {
            return false
        }
        return sharedPreferences.getBoolean(key, false)
    }

    fun Context.getBooleanSetting(@StringRes resId: Int) = getBooleanSetting(getString(resId))
    fun Fragment.getBooleanSetting(@StringRes resId: Int) = requireContext().getBooleanSetting(resId)
    
}