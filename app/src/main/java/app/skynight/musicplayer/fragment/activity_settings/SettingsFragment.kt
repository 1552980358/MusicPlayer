package app.skynight.musicplayer.fragment.activity_settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
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
        findPreference<Preference>("settingPreference_searchMusic")?.let {
            it.setOnPreferenceClickListener {
                if (Player.fullList) {
                    makeToast("功能施工中...")
                } else {
                    makeToast(R.string.abc_settings_bgLoading)
                }
                return@setOnPreferenceClickListener true
            }
        }
    }
}