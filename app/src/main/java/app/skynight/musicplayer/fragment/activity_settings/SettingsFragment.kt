package app.skynight.musicplayer.fragment.activity_settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import app.skynight.musicplayer.R
import app.skynight.musicplayer.util.Player
import app.skynight.musicplayer.util.Player.Companion.Pulse
import app.skynight.musicplayer.util.Player.Companion.PulseColor
import app.skynight.musicplayer.util.Player.Companion.PulseDensity
import app.skynight.musicplayer.util.Player.Companion.PulseType
import app.skynight.musicplayer.util.log

/**
 * @File    : SettingsFragment
 * @Author  : 1552980358
 * @Date    : 4 Aug 2019
 * @TIME    : 9:39 PM
 **/

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        log("SettingsFragment", "- onCreatePreferences")
        addPreferencesFromResource(R.xml.preference_settings)
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
            findPreference<SwitchPreference>("settingPreference_pulse")!!.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    Player.settings[Pulse] = newValue as Boolean
                    findPreference<PreferenceCategory>("settingPreference_pulse_opts")!!.isEnabled = newValue
                    return@setOnPreferenceChangeListener true
                }
            }
            findPreference<SwitchPreference>("settingPreference_pulse_density")!!.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    Player.settings[PulseDensity] = newValue as Boolean
                    return@setOnPreferenceChangeListener true
                }
            }
            findPreference<ListPreference>("settingPreference_pulse_type")!!.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    Player.settings[PulseType] = newValue as String
                    return@setOnPreferenceChangeListener true
                }
            }
            findPreference<SwitchPreference>("settingPreference_pulse_color")!!.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    Player.settings[PulseColor] = newValue as Boolean
                    return@setOnPreferenceChangeListener true
                }
            }

            findPreference<PreferenceCategory>("settingPreference_pulse_opts")!!.isEnabled = Player.settings[Pulse] as Boolean

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