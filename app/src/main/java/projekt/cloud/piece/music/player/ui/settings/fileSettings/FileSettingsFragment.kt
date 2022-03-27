package projekt.cloud.piece.music.player.ui.settings.fileSettings

import android.os.Bundle
import android.text.InputType.TYPE_CLASS_NUMBER
import android.view.View
import androidx.preference.EditTextPreference
import androidx.preference.Preference.SummaryProvider
import com.google.android.material.transition.MaterialContainerTransform
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BasePreferenceFragment
import projekt.cloud.piece.music.player.preference.util.PreferenceFragmentUtil.editTextPreference
import projekt.cloud.piece.music.player.preference.util.PreferenceFragmentUtil.switchPreference

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
    }

    private fun initializeDurationPreferences() {
        val editTextPreference = editTextPreference(R.string.key_setting_file_filter_duration_set) {
            setOnBindEditTextListener {
                it.inputType = TYPE_CLASS_NUMBER
            }
            summaryProvider = SummaryProvider<EditTextPreference> {
                it.text + getString(R.string.setting_file_filter_duration_set_unit)
            }
            setOnPreferenceChangeListener { _, newValue ->
                activityViewModel.hasSettingsUpdated = true
                if ((newValue as String?).isNullOrBlank()) {
                    text = getString(R.string.setting_file_filter_duration_default)
                    return@setOnPreferenceChangeListener false
                }
                true
            }
        }
        switchPreference(R.string.key_setting_file_filter_duration_enable) {
            setOnPreferenceChangeListener { _, newValue ->
                activityViewModel.hasSettingsUpdated = true
                editTextPreference?.isEnabled = newValue as Boolean
                true
            }
        }
    }

    override fun setToolbarNavigationIcon() = R.drawable.ic_back

}