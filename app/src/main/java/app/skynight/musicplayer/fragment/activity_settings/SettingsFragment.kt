package app.skynight.musicplayer.fragment.activity_settings

import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import app.skynight.musicplayer.R
import app.skynight.musicplayer.util.Player
import app.skynight.musicplayer.util.makeToast
import java.io.InputStream
import java.lang.Exception
import java.net.URL

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
                Thread {
                    Player.getPlayer.onUpdateMusicList(
                        try {
                            Player.THREAD_NO[PreferenceManager.getDefaultSharedPreferences(
                                context
                            ).getString("settingPreference_searchMusicThread", "SINGLE")!!] ?: error("")
                        } catch (e: Exception) {
                            //e.printStackTrace()
                            1
                        }
                    )
                }.start()
                return@setOnPreferenceClickListener true
            }
        }
    }
}