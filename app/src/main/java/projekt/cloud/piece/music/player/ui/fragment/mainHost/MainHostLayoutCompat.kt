package projekt.cloud.piece.music.player.ui.fragment.mainHost

import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import kotlin.reflect.KClass
import projekt.cloud.piece.music.player.base.BaseLayoutCompat
import projekt.cloud.piece.music.player.databinding.FragmentMainHostBinding

private interface MainHostInterface {

    fun setupNavigation(navController: NavController) = Unit

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