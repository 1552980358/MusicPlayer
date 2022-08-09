package projekt.cloud.piece.music.player.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.android.material.transition.platform.Hold
import projekt.cloud.piece.music.player.MainActivityViewModel
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.databinding.FragmentMainBinding
import projekt.cloud.piece.music.player.ui.main.initial.InitialDialogFragment
import projekt.cloud.piece.music.player.ui.main.initial.InitialDialogFragment.Companion.isInitialized
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui
import projekt.cloud.piece.music.player.util.DialogFragmentUtil.showNow

class MainFragment: BaseFragment() {

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
    private val relativeLayoutBottomPlayBar: RelativeLayout
        get() = binding.relativeLayoutBottomPlayBar
    private val relativeLayoutBottomPlayBarContainer: RelativeLayout
        get() = binding.relativeLayoutBottomPlayBarContainer

    private lateinit var childNavController: NavController
    private lateinit var navController: NavController
    
    private val viewModel: MainViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        exitTransition = Hold()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.viewModel = activityViewModel
        binding.lifecycleOwner = this
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        childNavController = fragmentContainerView.getFragment<NavHostFragment>().navController
        with(requireActivity() as AppCompatActivity) {
            setSupportActionBar(materialToolbar)
        }
        materialToolbar.setupWithNavController(
            childNavController,
            AppBarConfiguration(setOf(R.id.home_fragment, R.id.artist_fragment), drawerLayout)
        )
        navigationView.setupWithNavController(childNavController)
    
        relativeLayoutBottomPlayBarContainer.setOnClickListener {
            navController.navigate(
                MainFragmentDirections.actionMainFragmentToPlayFragment(),
                FragmentNavigatorExtras(relativeLayoutBottomPlayBar to relativeLayoutBottomPlayBar.transitionName)
            )
        }
        
        when {
            isInitialized(requireContext()) -> viewModel.setInitialized(true)
            else -> initialize()
        }
    }
    
    private fun initialize() = ui {
        InitialDialogFragment()
            .setCallback {
                viewModel.setInitialized(true)
            }.showNow(parentFragmentManager)
    }
    
    fun setBottomPlayBarEnable() {
        @Suppress("UNCHECKED_CAST")
        ((relativeLayoutBottomPlayBar.layoutParams as? CoordinatorLayout.LayoutParams)
            ?.behavior as? BottomPlayBarBehavior<RelativeLayout>)
            ?.setEnableAllowMoving(relativeLayoutBottomPlayBar)
    }

}