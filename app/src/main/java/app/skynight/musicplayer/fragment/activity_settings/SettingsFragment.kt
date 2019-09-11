package app.skynight.musicplayer.fragment.activity_settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.SwitchPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceCategory
import androidx.preference.Preference
import app.skynight.musicplayer.R
import app.skynight.musicplayer.activity.SplashActivity
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.BROADCAST_APPLICATION_RESTART
import app.skynight.musicplayer.util.Player
import app.skynight.musicplayer.util.Player.Companion.Lyric
import app.skynight.musicplayer.util.Player.Companion.LyricColor
import app.skynight.musicplayer.util.Player.Companion.LyricSupport
import app.skynight.musicplayer.util.Player.Companion.Pulse
import app.skynight.musicplayer.util.Player.Companion.PulseColor
import app.skynight.musicplayer.util.Player.Companion.PulseDensity
import app.skynight.musicplayer.util.Player.Companion.PulseType
import app.skynight.musicplayer.util.log
import kotlin.system.exitProcess

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
                    findPreference<PreferenceCategory>("settingPreference_pulse_opts")!!.isEnabled =
                        newValue
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

            findPreference<PreferenceCategory>("settingPreference_pulse_opts")!!.isEnabled =
                Player.settings[Pulse] as Boolean

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

            findPreference<Preference>("settingPreference_externalSD")!!.setOnPreferenceClickListener {
                startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), 0)
                return@setOnPreferenceClickListener true
            }

            findPreference<Preference>("settingPreference_theme")!!.setOnPreferenceChangeListener { _, _ ->
                Thread {
                    (context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager).set(
                        AlarmManager.RTC,
                        System.currentTimeMillis() + 600,
                        PendingIntent.getActivity(context, 1, Intent(context, SplashActivity::class.java), PendingIntent.FLAG_CANCEL_CURRENT)
                    )
                    context!!.sendBroadcast(Intent(BROADCAST_APPLICATION_RESTART))
                    Thread.sleep(500)
                    exitProcess(0)
                }.start()
                return@setOnPreferenceChangeListener true
            }
            findPreference<SwitchPreference>("settingPreference_extremeSimple")!!.setOnPreferenceChangeListener { _, newValue ->
                Player.settings[Player.SimpleMode] = newValue as Boolean
                return@setOnPreferenceChangeListener true
            }

            findPreference<SwitchPreference>("settingPreference_lyric")!!.setOnPreferenceChangeListener { _, newValue ->
                Player.settings[Lyric] = newValue as Boolean
                findPreference<Preference>("settingPreference_lyric_opts")!!.isEnabled = newValue
                return@setOnPreferenceChangeListener true
            }
            findPreference<SwitchPreference>("settingPreference_lyricColor")!!.setOnPreferenceChangeListener { _, newValue ->
                Player.settings[LyricColor] = newValue as Boolean
                return@setOnPreferenceChangeListener true
            }
            findPreference<ListPreference>("settingPreference_lyricSupplier")!!.setOnPreferenceChangeListener { _, newValue ->
                Player.settings[LyricSupport] = newValue as String
                return@setOnPreferenceChangeListener true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}