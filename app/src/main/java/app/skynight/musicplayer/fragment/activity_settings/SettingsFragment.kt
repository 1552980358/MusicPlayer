package app.skynight.musicplayer.fragment.activity_settings

import android.os.Bundle
import androidx.preference.ListPreference
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
                Player.settings[Player.BgColor] = newValue as Boolean
                return@setOnPreferenceChangeListener true
            }
            findPreference<SwitchPreference>("settingPreference_buttons")!!.setOnPreferenceChangeListener { _, newValue ->
                Player.settings[Player.Button] = newValue as Boolean
                return@setOnPreferenceChangeListener true
            }
            findPreference<SwitchPreference>("settingPreference_filter")!!.setOnPreferenceChangeListener { _, newValue ->
                Player.settings[Player.Filter] = newValue as Boolean
                return@setOnPreferenceChangeListener true
            }
            findPreference<SwitchPreference>("settingPreference_statusBar")!!.setOnPreferenceChangeListener { _, newValue ->
                Player.settings[Player.StatusBar] = newValue as Boolean
                return@setOnPreferenceChangeListener true
            }

            // Headset
            findPreference<SwitchPreference>("settingPreference_wired_plugin")!!.setOnPreferenceChangeListener { _, newValue ->
                Player.settings[Player.WiredPlugIn] = newValue as Boolean
                return@setOnPreferenceChangeListener true
            }
            findPreference<SwitchPreference>("settingPreference_wired_pullout")!!.setOnPreferenceChangeListener { _, newValue ->
                Player.settings[Player.WiredPullOut] = newValue as Boolean
                return@setOnPreferenceChangeListener true
            }
            findPreference<SwitchPreference>("settingPreference_wireless_disconnected")!!.setOnPreferenceChangeListener { _, newValue ->
                Player.settings[Player.WirelessDis] = newValue as Boolean
                return@setOnPreferenceChangeListener true
            }
            findPreference<ListPreference>("settingPreference_arrangement")!!.setOnPreferenceChangeListener { _, newValue ->
                Player.settings[Player.Arrangement] = newValue as String
                return@setOnPreferenceChangeListener true
            }

        } catch (e: Exception) {

        }
    }
}