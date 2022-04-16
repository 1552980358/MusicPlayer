package projekt.cloud.piece.music.player.ui.settings.fileSettings

import android.os.Bundle
import android.text.InputType.TYPE_CLASS_NUMBER
import android.view.View
import com.google.android.material.transition.MaterialContainerTransform
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BasePreferenceFragment
import projekt.cloud.piece.music.player.preference.util.PreferenceFragmentUtil.switchPreference
import projekt.cloud.piece.music.player.preference.util.PreferenceFragmentUtil.textInputPreference

class FileSettingsFragment: BasePreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.transitionName = getString(R.string.key_setting_file)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_file_settings, rootKey)
        initializeDurationPreferences()
        initializeSizePreferences()
    }

    override fun setTitle() = R.string.setting_file

    private fun initializeDurationPreferences() {
        val textInputPreference = textInputPreference(R.string.key_setting_file_filter_duration_set) {
            setDialogTitle(R.string.setting_file_filter_duration_set)
            setHint(R.string.setting_file_filter_duration_set_hint)
            setSuffix(getString(R.string.setting_file_filter_duration_set_unit))
            setInputType(TYPE_CLASS_NUMBER)
            setOnChange {
                activityViewModel.hasSettingsUpdated = true
            }
        }
        switchPreference(R.string.key_setting_file_filter_duration_enable) {
            setOnPreferenceChangeListener { _, newValue ->
                activityViewModel.hasSettingsUpdated = true
                textInputPreference?.isEnabled = newValue as Boolean
                true
            }
        }
    }

    private fun initializeSizePreferences() {
        val textInputPreference = textInputPreference(R.string.key_setting_file_filter_size_set) {
            setDialogTitle(R.string.setting_file_filter_size_set)
            setHint(R.string.setting_file_filter_size_set_hint)
            setSuffix(getString(R.string.setting_file_filter_size_set_unit))
            setInputType(TYPE_CLASS_NUMBER)
            setOnChange {
                activityViewModel.hasSettingsUpdated = true
            }
        }
        switchPreference(R.string.key_setting_file_filter_size_enable) {
            setOnPreferenceChangeListener { _, newValue ->
                activityViewModel.hasSettingsUpdated = true
                textInputPreference?.isEnabled = newValue as Boolean
                true
            }
        }
    }

}