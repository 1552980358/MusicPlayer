package app.fokkusu.music.fragment.main

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import app.fokkusu.music.R

/**
 * @File    : SettingFragment
 * @Author  : 1552980358
 * @Date    : 6 Oct 2019
 * @TIME    : 8:47 AM
 **/

class SettingFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.fragment_setting)
    }
}