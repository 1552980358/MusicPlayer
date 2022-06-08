package projekt.cloud.piece.music.player.ui.dialog.webServer

import android.os.Bundle
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BasePreferenceFragment
import projekt.cloud.piece.music.player.service.WebService
import projekt.cloud.piece.music.player.service.web.Extra.ACTION_START_COMMAND
import projekt.cloud.piece.music.player.service.web.Extra.ACTION_START_SERVER
import projekt.cloud.piece.music.player.service.web.Extra.ACTION_STOP_SERVER
import projekt.cloud.piece.music.player.service.web.WebAssets.webAssetsDir
import projekt.cloud.piece.music.player.service.web.WebAssets.webAssetsVersionFile
import projekt.cloud.piece.music.player.util.FragmentUtil.startService
import projekt.cloud.piece.music.player.util.PreferenceUtil.preference
import projekt.cloud.piece.music.player.util.PreferenceUtil.switchPreference

class WebServerPreferenceFragment: BasePreferenceFragment() {
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.xml_web_server, rootKey)
        
        preference(R.string.key_web_server_package_version) {
            val versionFile = requireContext().webAssetsVersionFile
            if (requireContext().webAssetsDir?.exists() != true || !versionFile.exists()) {
                return@preference setSummary(R.string.web_server_package_version_not_installed)
            }
            summary = versionFile.readText()
        }
        
        switchPreference(R.string.key_web_server_switch) {
            setOnPreferenceChangeListener { _, newValue ->
                startService<WebService> {
                    putExtra(
                        ACTION_START_COMMAND,
                        when {
                            newValue as Boolean -> ACTION_START_SERVER
                            else -> ACTION_STOP_SERVER
                        }
                    )
                }
                true
            }
        }
    }
    
}