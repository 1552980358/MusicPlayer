package projekt.cloud.piece.music.player.util

import android.content.Context
import androidx.fragment.app.Fragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import projekt.cloud.piece.music.player.R

enum class ScreenDensity(val value: Int) {
    COMPACT(0),
    MEDIUM(1),
    EXPANDED(2);

    companion object ScreenDensityUtil {
        @JvmStatic
        val Context.screenDensity: ScreenDensity
            get() = when {
                resources.getBoolean(R.bool.screen_density_medium) -> MEDIUM
                resources.getBoolean(R.bool.screen_density_expanded) -> EXPANDED
                else -> COMPACT
            }

        @JvmStatic
        val Fragment.screenDensity: ScreenDensity
            get() = requireContext().screenDensity

        fun screenDensity(): ReadOnlyProperty<Fragment, ScreenDensity> {
            return ScreenDensityFragmentProperty()
        }

        private class ScreenDensityFragmentProperty: ReadOnlyProperty<Fragment, ScreenDensity> {

            @Volatile
            private var screenDensity: ScreenDensity? = null

            override fun getValue(thisRef: Fragment, property: KProperty<*>): ScreenDensity {
                return screenDensity ?: setValue(thisRef)
            }

            @Synchronized
            private fun setValue(fragment: Fragment): ScreenDensity {
                return screenDensity ?: fragment.screenDensity.also(this::setValue)
            }

            private fun setValue(screenDensity: ScreenDensity) {
                this.screenDensity = screenDensity
            }

        }

    }

}