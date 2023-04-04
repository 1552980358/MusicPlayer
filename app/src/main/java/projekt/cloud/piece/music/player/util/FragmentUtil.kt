package projekt.cloud.piece.music.player.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

object FragmentUtil {

    @JvmOverloads
    fun <T> Fragment.viewLifecycleProperty(doOnDestroy: ((T) -> Unit)? = null): LifecycleProperty<Fragment, T> =
        ViewLifecycleProperty(this, doOnDestroy)

    open class ViewLifecycleProperty<T>(
        fragment: Fragment, private val doOnDestroy: ((T) -> Unit)?
    ): LifecycleProperty<Fragment, T>() {

        init {
            fragment.viewLifecycleOwnerLiveData.observe(
                fragment,
                object: Observer<LifecycleOwner> {
                    override fun onChanged(viewLifecycleOwner: LifecycleOwner?) {
                        viewLifecycleOwner?.lifecycle?.let { viewLifecycle ->
                            viewLifecycle.addObserver(this@ViewLifecycleProperty)
                            fragment.viewLifecycleOwnerLiveData
                                .removeObserver(this)
                        }
                    }
                }
            )
        }

        override fun onDestroy(owner: LifecycleOwner) {
            doOnDestroy?.invoke(field)
            super.onDestroy(owner)
        }

    }

}