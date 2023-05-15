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
import projekt.cloud.piece.cloudy.util.LifecycleOwnerProperty

object PreferenceUtil {

    fun defaultSharedPreference(): ReadOnlyProperty<LifecycleOwner, SharedPreferences> {
        return LifecycleOwnerDefaultSharedPreference()
    }

    private class LifecycleOwnerDefaultSharedPreference: LifecycleOwnerProperty<SharedPreferences>() {

        override fun syncCreateValue(thisRef: LifecycleOwner): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(
                getContext(thisRef)
            )
        }

        private fun getContext(thisRef: LifecycleOwner): Context {
            return when (thisRef) {
                is Activity, is Service -> { thisRef as Context }
                is Fragment -> { thisRef.requireContext() }
                else -> throw IllegalArgumentException("Unknown $thisRef: Host class should be the subclass of android.content.Context")
            }
        }

    }

}