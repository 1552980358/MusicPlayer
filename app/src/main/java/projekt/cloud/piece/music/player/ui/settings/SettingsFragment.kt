package projekt.cloud.piece.music.player.ui.settings

import android.os.Bundle
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BasePreferenceFragment

class SettingsFragment: BasePreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragmet_settings, rootKey)
    }

}