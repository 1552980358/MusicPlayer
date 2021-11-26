package sakuraba.saki.player.music.base

import androidx.preference.PreferenceFragmentCompat
import sakuraba.saki.player.music.util.MainActivityInterface

abstract class BasePreferenceFragmentCompat: PreferenceFragmentCompat() {

    lateinit var activityInterface: MainActivityInterface

}