package projekt.cloud.piece.music.player.ui.fragment.home

import android.graphics.Rect
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updatePadding
import com.google.android.material.appbar.AppBarLayout
import kotlin.reflect.KClass
import projekt.cloud.piece.music.player.base.BaseLayoutCompat
import projekt.cloud.piece.music.player.databinding.FragmentHomeBinding

open class HomeLayoutCompat: BaseLayoutCompat<FragmentHomeBinding> {

    constructor(): super(null)
    constructor(binding: FragmentHomeBinding): super(binding)

    override val compatImpl: KClass<*>
        get() = CompatImpl::class
    override val w600dpImpl: KClass<*>
        get() = W600dpImpl::class
    override val w1240dpImpl: KClass<*>
        get() = W1240dpImpl::class

    override val requireWindowInsets: Boolean
        get() = true

    private class CompatImpl(binding: FragmentHomeBinding): HomeLayoutCompat(binding) {

        private val appBarLayout: AppBarLayout
            get() = binding.appBarLayout!!

        override fun onSetupRequireWindowInsets() = { insets: Rect ->
            appBarLayout.updatePadding(top = insets.top)
        }

    }

    private class W600dpImpl(binding: FragmentHomeBinding): HomeLayoutCompat(binding) {

        private val coordinatorLayout: CoordinatorLayout
            get() = binding.coordinatorLayout

        override fun onSetupRequireWindowInsets() = { insets: Rect ->
            coordinatorLayout.updatePadding(bottom = insets.bottom)
        }

    }

    private class W1240dpImpl(binding: FragmentHomeBinding): HomeLayoutCompat(binding) {

        private val coordinatorLayout: CoordinatorLayout
            get() = binding.coordinatorLayout

        override fun onSetupRequireWindowInsets() = { insets: Rect ->
            coordinatorLayout.updatePadding(bottom = insets.bottom)
        }

    }

}