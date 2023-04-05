package projekt.cloud.piece.music.player.base

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass
import projekt.cloud.piece.music.player.base.BaseLayoutCompat.BaseLayoutCompatUtil.reflectLayoutCompat
import projekt.cloud.piece.music.player.util.ContextUtil.requireWindowInsets
import projekt.cloud.piece.music.player.util.FragmentUtil.viewLifecycleProperty
import projekt.cloud.piece.music.player.util.ScreenDensity.ScreenDensityUtil.screenDensity

private class NullRequireWindowInsetsListenerException: IllegalArgumentException(MESSAGE) {
    companion object {
        const val MESSAGE = "You should return a RequireWindowInsetsListener on onSetupRequireWindowInsets()"
    }
}

abstract class BaseMultiDensityFragment<VB: ViewBinding, LC: BaseLayoutCompat<VB>>: BaseFragment<VB>() {

    protected var layoutCompat: LC by viewLifecycleProperty()
        private set

    protected abstract val layoutCompatClass: KClass<LC>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        layoutCompat = binding.reflectLayoutCompat(layoutCompatClass, requireActivity().screenDensity)
        if (layoutCompat.requireWindowInsets) {
            when (val listener = layoutCompat.windowInsetsRequireListener) {
                null -> { throw NullRequireWindowInsetsListenerException() }
                else -> { requireWindowInsets(listener) }
            }
        }
    }

    private fun requireWindowInsets(listener: (Rect) -> Unit) {
        requireContext().requireWindowInsets(listener)
    }

}