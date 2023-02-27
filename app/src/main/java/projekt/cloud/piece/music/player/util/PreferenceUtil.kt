package projekt.cloud.piece.music.player.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object PreferenceUtil {

    val Context.defaultSharedPreference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(this)

}