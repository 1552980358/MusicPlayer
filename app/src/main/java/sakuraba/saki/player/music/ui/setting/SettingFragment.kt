package sakuraba.saki.player.music.ui.setting

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.R.string.key_audio
import sakuraba.saki.player.music.R.string.key_play
import sakuraba.saki.player.music.R.string.key_web_server
import sakuraba.saki.player.music.util.PreferenceUtil.preference

class SettingFragment: PreferenceFragmentCompat() {
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.xml_setting, rootKey)
        
        setHasOptionsMenu(true)
        
        preference(key_audio)?.setOnPreferenceClickListener {
            findNavController().navigate(SettingFragmentDirections.actionNavSettingToNavSettingAudioFilter())
            return@setOnPreferenceClickListener true
        }
        preference(key_play)?.setOnPreferenceClickListener {
            findNavController().navigate(SettingFragmentDirections.actionNavSettingToNavSettingAudioPlay())
            return@setOnPreferenceClickListener true
        }

        preference(key_web_server)?.setOnPreferenceClickListener {
            findNavController().navigate(SettingFragmentDirections.actionNavSettingToNavSettingWebServer())
            return@setOnPreferenceClickListener true
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }
    
}