package sakuraba.saki.player.music.ui.setting

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.util.SettingUtil.KEY_AUDIO
import sakuraba.saki.player.music.util.SettingUtil.KEY_PLAY

class SettingFragment: PreferenceFragmentCompat() {
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.xml_setting, rootKey)
        
        setHasOptionsMenu(true)
        
        findPreference<Preference>(KEY_AUDIO)?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.nav_setting_audio_filter)
            return@setOnPreferenceClickListener true
        }
        findPreference<Preference>(KEY_PLAY)?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.nav_setting_audio_play)
            return@setOnPreferenceClickListener true
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }
    
}