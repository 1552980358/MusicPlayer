package app.fokkusu.music.fragment.main

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import app.fokkusu.music.R
import app.fokkusu.music.base.Constants.Companion.Save_Pulse_Style
import app.fokkusu.music.base.Constants.Companion.Save_Pulse_Switch
import app.fokkusu.music.base.getStack

/**
 * @File    : SettingFragment
 * @Author  : 1552980358
 * @Date    : 6 Oct 2019
 * @TIME    : 8:47 AM
 **/

class SettingFragment: PreferenceFragmentCompat() {
    
    companion object {
        val switchSave = mutableMapOf<String, Boolean>()
        val settingSave = mutableMapOf<String, Char>()
    }
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.fragment_setting)
        
        try {
            findPreference<SwitchPreference>("sp_pulse_switch")?.apply {
                switchSave[Save_Pulse_Switch] = isChecked
                findPreference<ListPreference>("sp_pulse_type")?.isEnabled = isChecked
                
                setOnPreferenceChangeListener { _, newValue ->
                    switchSave[Save_Pulse_Switch] = newValue as Boolean
                    findPreference<ListPreference>("sp_pulse_type")?.isEnabled = newValue
                    return@setOnPreferenceChangeListener switchSave[Save_Pulse_Switch] == newValue
                }
            }
        } catch (e: Exception) {
            e.getStack()
        }
    
        try {
            findPreference<ListPreference>("sp_pulse_type")?.apply {
                settingSave[Save_Pulse_Style] = value[0]
        
                setOnPreferenceChangeListener { _, newValue ->
                    settingSave[Save_Pulse_Style] = (newValue as String)[0]
                    return@setOnPreferenceChangeListener settingSave[Save_Pulse_Style] == newValue[0]
                }
            }
        } catch (e: Exception) {
            e.getStack()
        }
    }
}