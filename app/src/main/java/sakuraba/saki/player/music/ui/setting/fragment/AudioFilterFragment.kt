package sakuraba.saki.player.music.ui.setting.fragment

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import lib.github1552980358.ktExtension.android.content.commit
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.ui.setting.dialog.TextInputDialogFragment
import sakuraba.saki.player.music.util.SettingUtil.KEY_AUDIO_FILTER_DURATION_ENABLE
import sakuraba.saki.player.music.util.SettingUtil.KEY_AUDIO_FILTER_DURATION_VALUE
import sakuraba.saki.player.music.util.SettingUtil.KEY_AUDIO_FILTER_SIZE_ENABLE
import sakuraba.saki.player.music.util.SettingUtil.KEY_AUDIO_FILTER_SIZE_VALUE
import sakuraba.saki.player.music.util.SettingUtil.defaultSharedPreference
import sakuraba.saki.player.music.util.SettingUtil.getIntSetting

class AudioFilterFragment: PreferenceFragmentCompat() {
    
    companion object {
        private const val DEFAULT_AUDIO_FILTER_SIZE = "1024"
        private const val DEFAULT_AUDIO_DURATION_SIZE = "5000"
    }
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.xml_audio_filter, rootKey)
        
        getSwitchPreference(KEY_AUDIO_FILTER_SIZE_ENABLE) {
            setPreferenceEnabled(KEY_AUDIO_FILTER_SIZE_VALUE, isChecked)
            setOnPreferenceChangeListener { _, newValue ->
                setPreferenceEnabled(KEY_AUDIO_FILTER_SIZE_VALUE, newValue as Boolean)
                return@setOnPreferenceChangeListener true
            }
        }
        getPreference(KEY_AUDIO_FILTER_SIZE_VALUE) {
            if (getIntSetting(KEY_AUDIO_FILTER_SIZE_VALUE) == null) {
                defaultSharedPreference.commit(KEY_AUDIO_FILTER_SIZE_VALUE, DEFAULT_AUDIO_FILTER_SIZE)
            }
            setOnPreferenceClickListener {
                TextInputDialogFragment(R.string.setting_audio_filter_size_title, KEY_AUDIO_FILTER_SIZE_VALUE, DEFAULT_AUDIO_FILTER_SIZE).show(parentFragmentManager)
                return@setOnPreferenceClickListener true
            }
        }
        
        getSwitchPreference(KEY_AUDIO_FILTER_DURATION_ENABLE) {
            setPreferenceEnabled(KEY_AUDIO_FILTER_DURATION_VALUE, isChecked)
            setOnPreferenceChangeListener { _, newValue ->
                setPreferenceEnabled(KEY_AUDIO_FILTER_DURATION_VALUE, newValue as Boolean)
                return@setOnPreferenceChangeListener true
            }
        }
        getPreference(KEY_AUDIO_FILTER_DURATION_VALUE) {
            if (getIntSetting(KEY_AUDIO_FILTER_DURATION_VALUE) == null) {
                defaultSharedPreference.commit(KEY_AUDIO_FILTER_DURATION_VALUE, DEFAULT_AUDIO_DURATION_SIZE)
            }
            setOnPreferenceClickListener {
                TextInputDialogFragment(R.string.setting_audio_filter_duration_title, KEY_AUDIO_FILTER_DURATION_VALUE, DEFAULT_AUDIO_DURATION_SIZE).show(parentFragmentManager)
                return@setOnPreferenceClickListener true
            }
        }
    }
    
    private fun setPreferenceEnabled(key: String, isEnabled: Boolean) {
        findPreference<Preference>(key)?.isEnabled = isEnabled
    }
    
    private fun getPreference(key: String, block: Preference.() -> Unit) {
        findPreference<Preference>(key)?.apply(block)
    }
    
    private fun getSwitchPreference(key: String, block: SwitchPreferenceCompat.() -> Unit) {
        findPreference<SwitchPreferenceCompat>(key)?.apply(block)
    }
    
}