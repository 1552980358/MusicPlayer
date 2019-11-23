package app.fokkusu.music.fragment.main

import android.content.Intent
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import app.fokkusu.music.R
import app.fokkusu.music.base.Constants.Companion.SP_Play_Disrupt
import app.fokkusu.music.base.Constants.Companion.SP_Play_Disrupt_Check
import app.fokkusu.music.base.Constants.Companion.SP_Player_UI
import app.fokkusu.music.base.Constants.Companion.SP_Pulse_Style
import app.fokkusu.music.base.Constants.Companion.SP_Pulse_Switch
import app.fokkusu.music.base.Constants.Companion.Save_Play_Disrupt
import app.fokkusu.music.base.Constants.Companion.Save_Player_UI
import app.fokkusu.music.base.Constants.Companion.Save_Pulse_Style
import app.fokkusu.music.base.Constants.Companion.Save_Pulse_Switch
import app.fokkusu.music.base.getStack
import app.fokkusu.music.base.makeToast

/**
 * @File    : SettingFragment
 * @Author  : 1552980358
 * @Date    : 6 Oct 2019
 * @TIME    : 8:47 AM
 **/

class SettingFragment : PreferenceFragmentCompat() {
    
    companion object {
        val switchSave = mutableMapOf<String, Boolean>()
        val settingSave = mutableMapOf<String, Char>()
    }
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.fragment_setting)
    
        try {
            findPreference<ListPreference>(SP_Player_UI)?.apply {
                settingSave[Save_Player_UI] = value[0] // change into `char`
        
                setOnPreferenceChangeListener { _, newValue ->
                    settingSave[Save_Player_UI] = (newValue as String)[0]
                    return@setOnPreferenceChangeListener settingSave[Save_Player_UI] == newValue[0]
                }
            }
        } catch (e: Exception) {
            e.getStack()
        }
        
        try {
            findPreference<SwitchPreference>(SP_Pulse_Switch)?.apply {
                switchSave[Save_Pulse_Switch] = isChecked
                findPreference<ListPreference>(SP_Pulse_Style)?.isEnabled = isChecked
                
                setOnPreferenceChangeListener { _, newValue ->
                    switchSave[Save_Pulse_Switch] = newValue as Boolean
                    findPreference<ListPreference>(SP_Pulse_Style)?.isEnabled = newValue
                    return@setOnPreferenceChangeListener switchSave[Save_Pulse_Switch] == newValue
                }
            }
        } catch (e: Exception) {
            e.getStack()
        }
        
        try {
            findPreference<ListPreference>(SP_Pulse_Style)?.apply {
                settingSave[Save_Pulse_Style] = value[0]
                
                setOnPreferenceChangeListener { _, newValue ->
                    settingSave[Save_Pulse_Style] = (newValue as String)[0]
                    return@setOnPreferenceChangeListener settingSave[Save_Pulse_Style] == newValue[0]
                }
            }
        } catch (e: Exception) {
            e.getStack()
        }
        
        try {
            findPreference<SwitchPreference>(SP_Play_Disrupt)?.apply {
                switchSave[Save_Play_Disrupt] = isChecked
                setOnPreferenceChangeListener { _, newValue ->
                    switchSave[Save_Play_Disrupt] = newValue as Boolean
                    return@setOnPreferenceChangeListener switchSave[Save_Play_Disrupt] == newValue
                }
            }
        } catch (e: Exception) {
            e.getStack()
        }
        
        try {
            findPreference<Preference>(SP_Play_Disrupt_Check)?.setOnPreferenceClickListener {
                makeToast(R.string.abc_setting_play_disrupt_check_fail)
                startActivity(Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS))
                return@setOnPreferenceClickListener true
            }
        } catch (e: Exception) {
            e.getStack()
        }
    }
}