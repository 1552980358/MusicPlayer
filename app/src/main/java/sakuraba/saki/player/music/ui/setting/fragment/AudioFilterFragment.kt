package sakuraba.saki.player.music.ui.setting.fragment

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.preference.PreferenceFragmentCompat
import lib.github1552980358.ktExtension.android.content.commit
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.ui.setting.dialog.TextInputDialogFragment
import sakuraba.saki.player.music.util.PreferenceUtil.preference
import sakuraba.saki.player.music.util.PreferenceUtil.switchPreference
import sakuraba.saki.player.music.util.SettingUtil.defaultSharedPreference
import sakuraba.saki.player.music.util.SettingUtil.getIntSetting

class AudioFilterFragment: PreferenceFragmentCompat() {
    
    companion object {
        private const val DEFAULT_AUDIO_FILTER_SIZE = "1024"
        private const val DEFAULT_AUDIO_DURATION_SIZE = "5000"
    }
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.xml_audio_filter, rootKey)

        switchPreference(R.string.key_audio_filter_size_enable) {
            setPreferenceEnabled(R.string.key_audio_filter_size_value, isChecked)
            setOnPreferenceChangeListener { _, newValue ->
                setPreferenceEnabled(R.string.key_audio_filter_size_value, newValue as Boolean)
                return@setOnPreferenceChangeListener true
            }
        }
        preference(R.string.key_audio_filter_size_value) {
            if (getIntSetting(R.string.key_audio_filter_size_value) == null) {
                defaultSharedPreference.commit(getString(R.string.key_audio_filter_size_value), DEFAULT_AUDIO_FILTER_SIZE)
            }
            setOnPreferenceClickListener {
                TextInputDialogFragment(
                    R.string.setting_audio_filter_size_title,
                    getString(R.string.key_audio_filter_size_value),
                    DEFAULT_AUDIO_FILTER_SIZE
                ).show(parentFragmentManager)
                return@setOnPreferenceClickListener true
            }
        }
        switchPreference(R.string.key_audio_filter_duration_enable) {
            setPreferenceEnabled(R.string.key_audio_filter_duration_value, isChecked)
            setOnPreferenceChangeListener { _, newValue ->
                setPreferenceEnabled(R.string.key_audio_filter_duration_value, newValue as Boolean)
                return@setOnPreferenceChangeListener true
            }
        }
        preference(R.string.key_audio_filter_duration_value) {
            if (getIntSetting(R.string.key_audio_filter_duration_value) == null) {
                defaultSharedPreference.commit(getString(R.string.key_audio_filter_duration_value), DEFAULT_AUDIO_DURATION_SIZE)
            }
            setOnPreferenceClickListener {
                TextInputDialogFragment(
                    R.string.setting_audio_filter_duration_title,
                    getString(R.string.key_audio_filter_duration_value),
                    DEFAULT_AUDIO_DURATION_SIZE
                ).show(parentFragmentManager)
                return@setOnPreferenceClickListener true
            }
        }
    }
    
    private fun setPreferenceEnabled(@StringRes resId: Int, isEnabled: Boolean) {
        preference(resId)?.isEnabled = isEnabled
    }

}