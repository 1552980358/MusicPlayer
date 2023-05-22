package projekt.cloud.piece.cloudy.ui.fragment.main_container

import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import projekt.cloud.piece.cloudy.base.BaseLayoutAdapter
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.base.LayoutAdapterConstructor
import projekt.cloud.piece.cloudy.databinding.FragmentMainContainerBinding
import projekt.cloud.piece.cloudy.util.PixelDensity
import projekt.cloud.piece.cloudy.util.PixelDensity.COMPAT
import projekt.cloud.piece.cloudy.util.PixelDensity.EXPANDED
import projekt.cloud.piece.cloudy.util.PixelDensity.MEDIUM

private typealias MainContainerLayoutAdapterBuilder =
    LayoutAdapterBuilder<FragmentMainContainerBinding, MainContainerLayoutAdapter>
private typealias MainContainerLayoutAdapterConstructor =
    LayoutAdapterConstructor<FragmentMainContainerBinding, MainContainerLayoutAdapter>

abstract class MainContainerLayoutAdapter(
    binding: FragmentMainContainerBinding
): BaseLayoutAdapter<FragmentMainContainerBinding>(binding) {

    companion object {

        val builder: MainContainerLayoutAdapterBuilder
            get() = ::builder

        private fun builder(pixelDensity: PixelDensity): MainContainerLayoutAdapterConstructor {
            return when (pixelDensity) {
                COMPAT -> ::CompatImpl
                MEDIUM -> ::W600dpImpl
                EXPANDED -> ::W1240dpImpl
            }
        }

    }

    protected val fragmentContainerView: FragmentContainerView
        get() = binding.fragmentContainerView

    protected val childNavController: NavController
        get() = fragmentContainerView.getFragment<NavHostFragment>()
            .navController

    open fun setupFragmentContainerViewMargins() = Unit

    /**
     * [MainContainerLayoutAdapter.setupNavigation]
     *
     * @impl [CompatImpl.setupNavigation], [W600dpImpl.setupNavigation], [W1240dpImpl.setupNavigation]
     **/
    open fun setupNavigation() = Unit

    private class CompatImpl(binding: FragmentMainContainerBinding): MainContainerLayoutAdapter(binding) {

        private val bottomNavigationView: BottomNavigationView
            get() = binding.bottomNavigationView!!

        override fun setupFragmentContainerViewMargins() {
            bottomNavigationView.addOnLayoutChangeListener { _, _, top, _, bottom, _, _, _, _ ->
                fragmentContainerView.updateLayoutParams<MarginLayoutParams> {
                    setMargins(
                        fragmentContainerView.marginLeft,
                        fragmentContainerView.marginTop,
                        fragmentContainerView.marginRight,
                        bottom - top
                    )
                }
            }
        }

        /**
         * [MainContainerLayoutAdapter.setupNavigation]
         **/
        override fun setupNavigation() {
            bottomNavigationView.setupWithNavController(childNavController)
        }

    }

    private class W600dpImpl(binding: FragmentMainContainerBinding): MainContainerLayoutAdapter(binding) {

        private val navigationRailView: NavigationRailView
            get() = binding.navigationRailView!!

        /**
         * [MainContainerLayoutAdapter.setupNavigation]
         **/
        override fun setupNavigation() {
            navigationRailView.setupWithNavController(childNavController)
        }

    }

    private class W1240dpImpl(binding: FragmentMainContainerBinding): MainContainerLayoutAdapter(binding) {

        private val navigationView: NavigationView
            get() = binding.navigationView!!

        /**
         * [MainContainerLayoutAdapter.setupNavigation]
         **/
        override fun setupNavigation() {
            navigationView.setupWithNavController(childNavController)
        }

    }

}