package app.skynight.musicplayer.fragment.activity_settings

import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import app.skynight.musicplayer.R
import app.skynight.musicplayer.util.Player
import app.skynight.musicplayer.util.makeToast
import java.lang.Exception

/**
 * @File    : SettingsFragment
 * @Author  : 1552980358
 * @Date    : 4 Aug 2019
 * @TIME    : 9:39 PM
 **/
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.perf_settings)
        findPreference<Preference>("settingPreference_searchMusic")?.let {
            it.setOnPreferenceClickListener {
                Log.e("sf", Player.prepareDone.toString())
                if (Player.prepareDone) {
                    Thread {
                        Player.getPlayer.onUpdateMusicList(
                            try {
                                Player.THREAD_NO[PreferenceManager.getDefaultSharedPreferences(
                                    context
                                ).getString("settingPreference_searchMusicThread", "SINGLE")!!]
                                    ?: error("")
                            } catch (e: Exception) {
                                1
                            }
                        )
                    }.start()
                } else {
                    makeToast(R.string.abc_settings_bgLoading)
                }
                return@setOnPreferenceClickListener true
            }
        }
    }
}