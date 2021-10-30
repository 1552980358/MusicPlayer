package sakuraba.saki.player.music.ui.setting.fragment

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.preference.PreferenceFragmentCompat
import lib.github1552980358.ktExtension.androidx.coordinatorlayout.widget.shortSnack
import lib.github1552980358.ktExtension.androidx.fragment.app.findActivityViewById
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.R.string.key_web_server_enable
import sakuraba.saki.player.music.service.util.startService
import sakuraba.saki.player.music.util.Constants.EXTRA_WEBSERVER
import sakuraba.saki.player.music.util.Constants.EXTRA_WEBSERVER_START
import sakuraba.saki.player.music.util.Constants.EXTRA_WEBSERVER_STOP
import sakuraba.saki.player.music.util.PreferenceUtil.editTextPreference
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

        editTextPreference(R.string.key_web_server_port) {
            summary = text
            setOnBindEditTextListener { editText ->
                editText.inputType = EditorInfo.TYPE_CLASS_NUMBER
            }
            setOnPreferenceChangeListener { _, newValue ->
                tryOnly {
                    if ((newValue as String).toInt() in 1025 .. 65535) {
                        summary = text
                        if (switchPreference(key_web_server_enable)?.isEnabled == true) {
                            requireContext().startService(WebService::class.java) {
                                putExtra(EXTRA_WEBSERVER, EXTRA_WEBSERVER_START)
                            }
                        }
                        return@setOnPreferenceChangeListener true
                    }
                }
                findActivityViewById<CoordinatorLayout>(R.id.coordinator_layout)?.shortSnack(R.string.web_server_port_range)
                return@setOnPreferenceChangeListener false
            }
        }
    }

}