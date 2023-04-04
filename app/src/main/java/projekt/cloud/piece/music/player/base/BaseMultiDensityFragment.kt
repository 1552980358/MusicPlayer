package projekt.cloud.piece.music.player.base

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import projekt.cloud.piece.music.player.util.ContextUtil.requireWindowInsets
import projekt.cloud.piece.music.player.util.FragmentUtil.viewLifecycleProperty

private class NullRequireWindowInsetsListenerException: IllegalArgumentException(MESSAGE) {
    companion object {
        const val MESSAGE = "You should return a RequireWindowInsetsListener on onSetupRequireWindowInsets()"
    }
}

abstract class BaseMultiDensityFragment<VB: ViewBinding, LC: BaseLayoutCompat<VB>>: BaseFragment<VB>() {

    protected var layoutCompat: LC by viewLifecycleProperty()
        private set

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