package sakuraba.saki.player.music.ui.setting.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import sakuraba.saki.player.music.R

class AudioPlayFragment: PreferenceFragmentCompat() {
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.xml_audio_play, rootKey)
    }
    
}