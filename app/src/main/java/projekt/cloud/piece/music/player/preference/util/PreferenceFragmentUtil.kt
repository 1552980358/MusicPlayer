package projekt.cloud.piece.music.player.preference.util

import androidx.annotation.StringRes
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import projekt.cloud.piece.music.player.preference.TextInputPreference
import projekt.cloud.piece.music.player.preference.TransitionPreference

object PreferenceFragmentUtil {

    private fun <T: Preference> PreferenceFragmentCompat.preference(@StringRes resId: Int) =
        findPreference<T>(getString(resId))

    fun PreferenceFragmentCompat.preference(@StringRes resId: Int, block: Preference.() -> Unit = {}) =
        preference<Preference>(resId)?.apply(block)

    fun PreferenceFragmentCompat.transitionPreference(@StringRes resId: Int, block: TransitionPreference.() -> Unit) =
        preference<TransitionPreference>(resId)?.apply(block)

    fun PreferenceFragmentCompat.editTextPreference(resId: Int, block: EditTextPreference.() -> Unit) =
        preference<EditTextPreference>(resId)?.apply(block)

    fun PreferenceFragmentCompat.switchPreference(resId: Int, block: SwitchPreferenceCompat.() -> Unit) =
        preference<SwitchPreferenceCompat>(resId)?.apply(block)

    fun PreferenceFragmentCompat.textInputPreference(resId: Int, block: TextInputPreference.() -> Unit) =
        preference<TextInputPreference>(resId)?.apply(block)

}