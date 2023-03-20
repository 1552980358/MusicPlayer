package projekt.cloud.piece.music.player.base

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import projekt.cloud.piece.music.player.util.ContextUtil.requireWindowInsets
import projekt.cloud.piece.music.player.util.FragmentUtil.ViewLifecycleProperty
import projekt.cloud.piece.music.player.util.LifecycleProperty

private class NullRequireWindowInsetsListenerException: IllegalArgumentException(MESSAGE) {
    companion object {
        const val MESSAGE = "You should return a RequireWindowInsetsListener on onSetupRequireWindowInsets()"
    }
}

abstract class BaseMultiDensityFragment<VB: ViewBinding, LC: BaseLayoutCompat<VB>>: BaseFragment<VB>() {

    protected var layoutCompat: LC by layoutCompatProperty()
        private set

    private fun layoutCompatProperty(): LifecycleProperty<Fragment, LC> =
        LayoutCompatProperty(this)

    private class LayoutCompatProperty<VB: ViewBinding, LC: BaseLayoutCompat<VB>>(fragment: Fragment): ViewLifecycleProperty<LC>(fragment) {
        override fun onDestroy(owner: LifecycleOwner) {
            field.onDestroy()
            super.onDestroy(owner)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        layoutCompat = onCreateLayoutCompat(binding)
        if (layoutCompat.requireWindowInsets) {
            when (val listener = layoutCompat.windowInsetsRequireListener) {
                null -> { throw NullRequireWindowInsetsListenerException() }
                else -> { requireWindowInsets(listener) }
            }
        }
    }

    protected abstract fun onCreateLayoutCompat(binding: VB): LC

    private fun requireWindowInsets(listener: (Rect) -> Unit) {
        requireContext().requireWindowInsets(listener)
    }

}