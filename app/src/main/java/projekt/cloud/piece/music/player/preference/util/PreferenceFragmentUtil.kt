package projekt.cloud.piece.music.player.preference.util

import androidx.annotation.StringRes
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import projekt.cloud.piece.music.player.preference.TransitionPreference

object PreferenceFragmentUtil {

    private fun <T: Preference> PreferenceFragmentCompat.preference(@StringRes resId: Int) =
        findPreference<T>(getString(resId))

    fun PreferenceFragmentCompat.preference(@StringRes resId: Int, block: Preference.() -> Unit = {}) =
        preference<Preference>(resId)?.apply(block)

    fun PreferenceFragmentCompat.transitionPreference(@StringRes resId: Int, block: TransitionPreference.() -> Unit) =
        preference<TransitionPreference>(resId)?.apply(block)

}