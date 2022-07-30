package projekt.cloud.piece.music.player.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.databinding.FragmentMainBinding

class MainFragment: Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() = _binding!!

    private val root get() = binding.root
    private val drawerLayout: DrawerLayout
        get() = binding.root
    private val materialToolbar: MaterialToolbar
        get() = binding.materialToolbar
    private val fragmentContainerView: FragmentContainerView
        get() = binding.fragmentContainerView
    private val navigationView: NavigationView
        get() = binding.navigationView

    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = fragmentContainerView.getFragment<NavHostFragment>().navController
        with(requireActivity() as AppCompatActivity) {
            setSupportActionBar(materialToolbar)
        }
        materialToolbar.setupWithNavController(
            navController,
            AppBarConfiguration(setOf(R.id.home_fragment, R.id.artist_fragment), drawerLayout)
        )
        navigationView.setupWithNavController(navController)
    }

}