package sakuraba.saki.player.music.util

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager

object SettingUtil {
    
    const val KEY_AUDIO = "key_audio"
    
    const val KEY_AUDIO_FILTER_SIZE_ENABLE = "key_audio_filter_size_enable"
    const val KEY_AUDIO_FILTER_SIZE_VALUE = "key_audio_filter_size_value"
    const val KEY_AUDIO_FILTER_DURATION_ENABLE = "key_audio_filter_duration_enable"
    const val KEY_AUDIO_FILTER_DURATION_VALUE = "key_audio_filter_duration_value"
    
    val Activity.defaultSharedPreference get() = PreferenceManager.getDefaultSharedPreferences(this)!!
    val Fragment.defaultSharedPreference get() = requireActivity().defaultSharedPreference
    
    fun Activity.getIntSetting(key: String): Int? {
        val sharedPreferenceManager = defaultSharedPreference
        if (!sharedPreferenceManager.contains(key)) {
            return null
        }
        return sharedPreferenceManager.getString(key, null)?.toInt()
    }
    
    fun Activity.getIntSettingOrThrow(key: String): Int = getIntSetting(key) ?: throw Exception()
    
    fun Fragment.getIntSetting(key: String) = requireActivity().getIntSetting(key)
    
    fun Fragment.getIntSettingOrThrow(key: String) = requireActivity().getIntSettingOrThrow(key)
    
    /**************************************************************************************/
    
    fun Activity.getStringSetting(key: String): String? {
        val sharedPreferenceManager = PreferenceManager.getDefaultSharedPreferences(this)
        if (!sharedPreferenceManager.contains(key)) {
            return null
        }
        return sharedPreferenceManager.getString(key, null)
    }
    
    fun Activity.getStringSettingOrThrow(key: String): String = getStringSetting(key) ?: throw Exception()
    
    fun Fragment.getStringSetting(key: String) = requireActivity().getStringSetting(key)
    
    fun Fragment.getStringSettingOrThrow(key: String) = requireActivity().getStringSettingOrThrow(key)
    
    /**************************************************************************************/
    fun Activity.getBooleanSetting(key: String): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (!sharedPreferences.contains(key)) {
            return false
        }
        return sharedPreferences.getBoolean(key, false)
    }
    
    fun Fragment.getBooleanSetting(key: String) = requireActivity().getBooleanSetting(key)
    
}