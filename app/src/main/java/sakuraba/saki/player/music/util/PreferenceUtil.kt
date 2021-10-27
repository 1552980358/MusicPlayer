package sakuraba.saki.player.music.util

import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat

object PreferenceUtil {

    fun PreferenceFragmentCompat.preference(id: Int) = findPreference<Preference>(getString(id))

    fun PreferenceFragmentCompat.preference(id: Int, block: Preference.() -> Unit) = preference(id)?.block()

    fun PreferenceFragmentCompat.switchPreference(id: Int) =
        findPreference<SwitchPreferenceCompat>(getString(id))

    fun PreferenceFragmentCompat.switchPreference(id: Int, block: SwitchPreferenceCompat.() -> Unit) =
        switchPreference(id)?.block()

}