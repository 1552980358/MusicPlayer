package projekt.cloud.piece.music.player.util

import androidx.annotation.IntRange
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import projekt.cloud.piece.music.player.util.ResourceUtil.getLong

object FragmentUtil {

    @JvmOverloads
    fun <T> Fragment.viewLifecycleProperty(doOnDestroy: ((T) -> Unit)? = null): ViewLifecycleProperty<T> =
        ViewLifecycleProperty(this, doOnDestroy)

    open class ViewLifecycleProperty<T>(
        fragment: Fragment, private val doOnDestroy: ((T) -> Unit)?
    ): LifecycleProperty<Fragment, T>() {

        init {
            fragment.viewLifecycleOwnerLiveData.observe(
                fragment,
                object: Observer<LifecycleOwner> {
                    override fun onChanged(value: LifecycleOwner) {
                        value.lifecycle.addObserver(this@ViewLifecycleProperty)
                        fragment.viewLifecycleOwnerLiveData
                            .removeObserver(this)
                    }
                }
            )
        }

        override fun onDestroy(owner: LifecycleOwner) {
            doOnDestroy?.invoke(field)
            super.onDestroy(owner)
        }

    }

    fun Fragment.getInt(@IntRange resId: Int) =
        resources.getInteger(resId)

    fun Fragment.getLong(@IntRange resId: Int) =
        resources.getLong(resId)

    val Fragment.childFragments: List<Fragment>
        get() = childFragmentManager.fragments

    fun Fragment.childFragments(list: List<Fragment>) =
        childFragments.takeIf { it.isNotEmpty() } ?: list

}