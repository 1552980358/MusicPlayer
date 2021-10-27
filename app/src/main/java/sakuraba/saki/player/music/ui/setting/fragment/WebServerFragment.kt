package sakuraba.saki.player.music.ui.setting.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.SwitchPreferenceCompat
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.R.string.key_web_server_enable
import sakuraba.saki.player.music.service.util.startService
import sakuraba.saki.player.music.util.Constants.EXTRA_WEBSERVER
import sakuraba.saki.player.music.util.Constants.EXTRA_WEBSERVER_START
import sakuraba.saki.player.music.util.Constants.EXTRA_WEBSERVER_STOP
import sakuraba.saki.player.music.util.PreferenceUtil.switchPreference
import sakuraba.saki.player.music.web.WebService

class WebServerFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.xml_web_server, rootKey)

        switchPreference(key_web_server_enable) {
            setOnPreferenceChangeListener { _, newValue ->
                requireContext().startService(WebService::class.java) {
                    putExtra(EXTRA_WEBSERVER, if (newValue as Boolean) EXTRA_WEBSERVER_START else EXTRA_WEBSERVER_STOP)
                }
                return@setOnPreferenceChangeListener true
            }
        }
    }

}