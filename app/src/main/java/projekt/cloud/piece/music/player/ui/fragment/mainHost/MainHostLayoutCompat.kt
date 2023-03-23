package projekt.cloud.piece.music.player.ui.fragment.mainHost

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import kotlin.reflect.KClass
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseLayoutCompat
import projekt.cloud.piece.music.player.databinding.FragmentMainHostBinding
import projekt.cloud.piece.music.player.ui.fragment.home.HomeViewModel

private interface MainHostInterface {

    fun setupNavigation(navController: NavController) = Unit

    fun setupNavigationItems(fragment: Fragment, navController: NavController) = Unit

}

open class MainHostLayoutCompat: BaseLayoutCompat<FragmentMainHostBinding>, MainHostInterface {

    constructor(): super(null)
    constructor(binding: FragmentMainHostBinding): super(binding)

    override val compatImpl: KClass<*>
        get() = CompatImpl::class
    override val w600dpImpl: KClass<*>
        get() = W600dpImpl::class
    override val w1240dpImpl: KClass<*>
        get() = W1240dpImpl::class

    private class CompatImpl(binding: FragmentMainHostBinding): MainHostLayoutCompat(binding) {

        private val bottomNavigationView: BottomNavigationView
            get() = binding.bottomNavigationView!!

        override fun setupNavigation(navController: NavController) {
            bottomNavigationView.setupWithNavController(navController)
        }

        override fun setupNavigationItems(fragment: Fragment, navController: NavController) {
            val homeViewModel: HomeViewModel by fragment.viewModels(
                { navController.getViewModelStoreOwner(R.id.nav_graph_main_host) }
            )

            bottomNavigationView.setOnItemReselectedListener {
                if (navController.currentDestination?.id == R.id.home) {
                    if (!homeViewModel.isOnTop) {
                        homeViewModel.scrollToTop()
                    }
                }
            }
        }

    }

    private class W600dpImpl(binding: FragmentMainHostBinding): MainHostLayoutCompat(binding) {

        private val navigationRailView: NavigationRailView
            get() = binding.navigationRailView!!

        override fun setupNavigation(navController: NavController) {
            navigationRailView.setupWithNavController(navController)
        }

    }

    private class W1240dpImpl(binding: FragmentMainHostBinding): MainHostLayoutCompat(binding) {

        private val navigationView: NavigationView
            get() = binding.navigationView!!

        override fun setupNavigation(navController: NavController) {
            navigationView.setupWithNavController(navController)
        }

    }

}