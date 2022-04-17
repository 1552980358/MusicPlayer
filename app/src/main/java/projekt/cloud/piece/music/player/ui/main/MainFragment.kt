package projekt.cloud.piece.music.player.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.transition.Hold
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.databinding.FragmentMainBinding
import projekt.cloud.piece.music.player.ui.main.album.AlbumFragment
import projekt.cloud.piece.music.player.ui.main.artist.ArtistFragment
import projekt.cloud.piece.music.player.ui.main.audio.AudioFragment
import projekt.cloud.piece.music.player.ui.main.playlist.PlaylistFragment

/**
 * Class [MainFragment], inherit to [BaseFragment]
 *
 * Variables:
 *   [viewModel]
 *   [_binding]
 *   [navController]
 *
 * Getters:
 *   [fragmentList]
 *   [fragmentsCount]
 *
 *   [binding]
 *   [root]
 *   [appBarMain]
 *   [materialToolbar]
 *   [viewPager2]
 *   [extFab]
 *
 * Methods:
 *   [onCreateView]
 *   [onViewCreated]
 *   [onDestroyView]
 *
 **/
class MainFragment: BaseFragment() {

    /**
     * Inner Class [MainFragmentViewModel]
     *
     * Variables:
     *   [fragmentList]
     *
     **/
    class MainFragmentViewModel: ViewModel() {
        val fragmentList = listOf(
            AudioFragment(), ArtistFragment(), AlbumFragment(), PlaylistFragment()
        )
    }

    private lateinit var viewModel: MainFragmentViewModel
    private val fragmentList get() = viewModel.fragmentList
    private val fragmentsCount get() = viewModel.fragmentList.size

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val root get() = binding.root
    private val appBarMain get() = binding.appBarMain
    private val materialToolbar get() = appBarMain.materialToolbar
    private val viewPager2 get() = appBarMain.viewPager2
    private val bottomNavigationView get() = appBarMain.bottomNavigationView
    private val extFab get() = appBarMain.extendedFloatingActionButton

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = Hold()
        navController = findNavController()
        viewModel = ViewModelProvider(this)[MainFragmentViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bottomNavigationView.background = null

        with(requireActivity() as AppCompatActivity) {
            setSupportActionBar(materialToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            ActionBarDrawerToggle(this, root, materialToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close).apply {
                root.addDrawerListener(this)
                syncState()
            }
        }

        val bottomNavigationItems = listOf(R.id.nav_audio_track, R.id.nav_album, R.id.nav_artist, R.id.nav_playlist)

        with(viewPager2) {
            adapter = object : FragmentStateAdapter(this@MainFragment) {
                override fun getItemCount() = fragmentsCount
                override fun createFragment(position: Int) = fragmentList[position]
            }

            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    bottomNavigationView.menu.getItem(position).apply {
                        isChecked = true
                    }
                }
            })

            bottomNavigationView.setOnItemSelectedListener {
                currentItem = bottomNavigationItems.indexOfFirst { id -> id == it.itemId }
                true
            }
        }

        extFab.setOnClickListener {
            if (exitTransition == null || exitTransition !is Hold) {
                exitTransition = Hold()
            }
            navController.navigate(
                MainFragmentDirections.actionMainFragmentToPlayFragment(),
                FragmentNavigatorExtras(extFab to extFab.transitionName)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}