package projekt.cloud.piece.music.player.ui.dialog.webServer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BasePreferenceFragment
import projekt.cloud.piece.music.player.service.WebService
import projekt.cloud.piece.music.player.service.web.Extra.ACTION_START_COMMAND
import projekt.cloud.piece.music.player.service.web.Extra.ACTION_START_SERVER
import projekt.cloud.piece.music.player.service.web.Extra.ACTION_STOP_SERVER
import projekt.cloud.piece.music.player.service.web.WebAssets.webAssetsDir
import projekt.cloud.piece.music.player.service.web.WebAssets.webAssetsVersionFile
import projekt.cloud.piece.music.player.service.web.WebServer.Companion.SERVER_PORT
import projekt.cloud.piece.music.player.util.FragmentUtil.startService
import projekt.cloud.piece.music.player.util.NetworkHelper
import projekt.cloud.piece.music.player.util.PreferenceUtil.preference
import projekt.cloud.piece.music.player.util.PreferenceUtil.switchPreference
import projekt.cloud.piece.music.player.util.SharedPreferencesUtil.boolPrefs
import projekt.cloud.piece.music.player.util.SnackBarUtil.showSnack

class WebServerPreferenceFragment: BasePreferenceFragment() {
    
    companion object {
        private const val URL_HTTP_PREFIX = "http://"
        private const val URL_SUFFIX = "/"
        private const val CLIPBOARD_LABEL = "WebServerLink"
    }
    
    private lateinit var networkHelper: NetworkHelper
    private lateinit var clipboardManager: ClipboardManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        networkHelper = NetworkHelper.create(requireContext())
        super.onCreate(savedInstanceState)
        clipboardManager = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.xml_web_server, rootKey)
        
        preference(R.string.key_web_server_package_version) {
            val versionFile = requireContext().webAssetsVersionFile
            if (requireContext().webAssetsDir?.exists() != true || !versionFile.exists()) {
                return@preference setSummary(R.string.web_server_package_version_not_installed)
            }
            summary = versionFile.readText()
        }
    
        preference(R.string.key_web_server_info_ip)?.summary = networkHelper.ipAddress
        
        preference(R.string.key_web_server_info_port)?.summary = SERVER_PORT.toString()
        
        preference(R.string.key_web_server_info_link)?.setOnPreferenceClickListener {
            if (requireContext().boolPrefs(R.string.key_web_server_switch, false)) {
                clipboardManager.setPrimaryClip(ClipData.newPlainText(CLIPBOARD_LABEL, preference(R.string.key_web_server_info_link)?.summary))
                (parentFragment?.view as? CoordinatorLayout)?.showSnack(R.string.web_server_info_link_copied)
            }
            true
        }
    
        switchPreference(R.string.key_web_server_switch) {
            setLink(requireContext().boolPrefs(R.string.key_web_server_switch, false))
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
                setLink(newValue as Boolean)
                true
            }
        }
    }
    
    private fun setLink(enabled: Boolean) {
        preference(R.string.key_web_server_info_link)?.summary = when {
            !enabled -> null
            else -> "$URL_HTTP_PREFIX${preference(R.string.key_web_server_info_ip)?.summary}:$SERVER_PORT$URL_SUFFIX"
        }
    }
    
}