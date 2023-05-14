package projekt.cloud.piece.cloudy.storage.util

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceManager
import java.lang.IllegalArgumentException
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object PreferenceUtil {

    fun defaultSharedPreference(): ReadOnlyProperty<LifecycleOwner, SharedPreferences> {
        return FragmentDefaultSharedPreference()
    }

    private class FragmentDefaultSharedPreference: ReadOnlyProperty<LifecycleOwner, SharedPreferences> {

        private var defaultSharedPreference: SharedPreferences? = null

        override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): SharedPreferences {
            return defaultSharedPreference ?: setDefaultSharedPreference(thisRef)
        }

        private fun getContext(thisRef: LifecycleOwner): Context {
            return when (thisRef) {
                is Activity, is Service -> { thisRef as Context }
                is Fragment -> { thisRef.requireContext() }
                else -> throw IllegalArgumentException("Unknown $thisRef: Host class should be the subclass of android.content.Context")
            }
        }

        @Synchronized
        private fun setDefaultSharedPreference(thisRef: LifecycleOwner): SharedPreferences {
            return defaultSharedPreference ?: getDefaultSharedPreference(getContext(thisRef))
        }

        @Synchronized
        private fun getDefaultSharedPreference(context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .apply(::setDefaultSharedPreference)
        }

        private fun setDefaultSharedPreference(sharedPreferences: SharedPreferences) {
            defaultSharedPreference = sharedPreferences
        }

    }

}