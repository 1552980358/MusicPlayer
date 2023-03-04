package projekt.cloud.piece.music.player.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import projekt.cloud.piece.music.player.util.FragmentUtil.ViewLifecycleProperty
import projekt.cloud.piece.music.player.util.LifecycleProperty

abstract class BaseMultiDensityFragment<VB: ViewBinding, LC: BaseLayoutCompat<VB>>: BaseFragment<VB>() {

    protected var layoutCompat: LC by layoutCompatProperty()
        private set

    private fun layoutCompatProperty(): LifecycleProperty<LC> =
        LayoutCompatProperty(this)

    private class LayoutCompatProperty<VB: ViewBinding, LC: BaseLayoutCompat<VB>>(fragment: Fragment): ViewLifecycleProperty<LC>(fragment) {
        override fun onDestroy(owner: LifecycleOwner) {
            field.onDestroy()
            super.onDestroy(owner)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        layoutCompat = onCreateLayoutCompat(binding)
    }

    protected abstract fun onCreateLayoutCompat(binding: VB): LC

}