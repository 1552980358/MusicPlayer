package projekt.cloud.piece.music.player.preference.util

import androidx.annotation.StringRes
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

object PreferenceFragmentUtil {

    private fun <T: Preference> PreferenceFragmentCompat.preference(@StringRes resId: Int) =
        findPreference<T>(getString(resId))

    fun PreferenceFragmentCompat.preference(@StringRes resId: Int, block: Preference.() -> Unit = {}) =
        preference<Preference>(resId)?.apply(block)

}