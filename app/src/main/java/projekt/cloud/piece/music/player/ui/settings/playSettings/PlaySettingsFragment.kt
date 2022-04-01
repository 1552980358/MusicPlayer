package projekt.cloud.piece.music.player.ui.settings.playSettings

import android.os.Bundle
import android.view.View
import com.google.android.material.transition.MaterialContainerTransform
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BasePreferenceFragment
import projekt.cloud.piece.music.player.preference.util.PreferenceFragmentUtil.switchPreference
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_AUDIO_FOCUS

class PlaySettingsFragment: BasePreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.transitionName = getString(R.string.key_setting_play)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_play_settings, rootKey)

        switchPreference(R.string.key_setting_play_audio_focus_enable) {

            setOnPreferenceChangeListener { _, newValue ->
                activityViewModel.updateConfig(PLAY_CONFIG_AUDIO_FOCUS, newValue as Boolean)
                true
            }

        }

    }

    override fun setTitle() = R.string.setting_play

}