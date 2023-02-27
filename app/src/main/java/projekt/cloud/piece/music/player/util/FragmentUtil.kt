package projekt.cloud.piece.music.player.util

import androidx.fragment.app.Fragment

object FragmentUtil {

    fun <T> Fragment.viewLifecycleProperty(): LifecycleProperty<T> =
        ViewLifecycleProperty(this)

    open class ViewLifecycleProperty<T>(fragment: Fragment): LifecycleProperty<T>() {

        init {
            fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
                viewLifecycleOwner.lifecycle.addObserver(this)
            }
        }

    }

}