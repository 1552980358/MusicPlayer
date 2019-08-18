package app.skynight.musicplayer.fragment.activity_settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import app.skynight.musicplayer.R
import app.skynight.musicplayer.util.Player
import app.skynight.musicplayer.util.log
import app.skynight.musicplayer.util.makeToast

/**
 * @File    : SettingsFragment
 * @Author  : 1552980358
 * @Date    : 4 Aug 2019
 * @TIME    : 9:39 PM
 **/

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        log("SettingsFragment", "- onCreatePreferences")
        addPreferencesFromResource(R.xml.perf_settings)
        try {
            findPreference<SwitchPreference>("settingPreference_bgAlbum")!!.setOnPreferenceChangeListener { _, newValue ->
                Player.bgColor = newValue as Boolean
                return@setOnPreferenceChangeListener true
            }
            findPreference<SwitchPreference>("settingPreference_buttons")!!.setOnPreferenceChangeListener { _, newValue ->
                Player.buttons = newValue as Boolean
                return@setOnPreferenceChangeListener true
            }
            findPreference<SwitchPreference>("settingPreference_filter")!!.setOnPreferenceChangeListener { _, newValue ->
                Player.rmFilter = newValue as Boolean
                return@setOnPreferenceChangeListener true
            }
        } catch (e: Exception) {

        }
    }
}