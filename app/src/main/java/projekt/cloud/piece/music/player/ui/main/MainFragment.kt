package projekt.cloud.piece.music.player.ui.main

import android.content.res.ColorStateList.valueOf
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.android.material.transition.Hold
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import lib.github1552980358.ktExtension.androidx.fragment.app.getColor
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.databinding.FragmentMainBinding
import projekt.cloud.piece.music.player.ui.main.home.HomeFragment

class MainFragment: BaseFragment(), OnNavigationItemSelectedListener {

    companion object {
        private const val TAG = "MainFragment"
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val appBarMain get() = binding.appBarMain
    private val toolbar get() = appBarMain.toolbar
    private val bottomNavigation get() = appBarMain.bottomNavigation
    private val extendedFloatingActionButton get() = appBarMain.extendedFloatingActionButton

    private lateinit var viewModel: MainViewModel

    private val audioArtMap get() = activityViewModel.audioArtMap
    private val albumArtMap get() = activityViewModel.albumArtMap
    private val defaultCoverArt get() = activityViewModel.defaultCoverArt
    private val database get() = activityViewModel.database

    private lateinit var navController: NavController
    private var countJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = Hold()
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        bottomNavigation.background = null
        navController = findNavController()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            with(ActionBarDrawerToggle(this,
                binding.root,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)) {
                binding.root.addDrawerListener(this)
                syncState()
            }
        }
        binding.navigationView.setNavigationItemSelectedListener(this)

        with(appBarMain.viewPager) {

            val fragments = listOf(
                HomeFragment()
            )

            adapter = object : FragmentStateAdapter(this@MainFragment) {
                override fun getItemCount() = fragments.size
                override fun createFragment(position: Int) = fragments[position]
            }

        }

        extendedFloatingActionButton.setOnClickListener {
            if (activityViewModel.audioItem != null) {
                navController.navigate(
                    MainFragmentDirections.actionNavMainToNavPlay(),
                    FragmentNavigatorExtras(extendedFloatingActionButton to extendedFloatingActionButton.transitionName)
                )
            }
        }

        activityViewModel.setAudioItemObserver(TAG) { audioItem ->
            io {
                val drawable = BitmapDrawable(
                    resources,
                    audioArtMap[audioItem.id] ?: albumArtMap[audioItem.album] ?: defaultCoverArt
                )

                database.color.query(audioItem.id, audioItem.album).apply {
                    with(extendedFloatingActionButton) {
                        ui {
                            icon = drawable
                            setTextColor(primaryColor)
                            backgroundTintList = valueOf(backgroundColor)
                            if (text != audioItem.title) {
                                text = audioItem.title
                            }
                        }

                        when {
                            viewModel.isDestroyed -> {
                                viewModel.isDestroyed = false
                                when {
                                    viewModel.isExtended -> ui { extend() }
                                    else -> return@apply
                                }
                            }
                            else -> {
                                if (!isExtended) {
                                    ui { extend() }
                                    viewModel.isExtended = true
                                }
                            }
                        }

                        countJob?.cancel()
                        countJob = io {
                            delay(5000L)
                            viewModel.isExtended = false
                            ui { shrink() }
                        }
                    }
                }
            }
        }

    }

    override fun onResume() {
        requireActivity().window.statusBarColor = getColor(R.color.purple_500)
        super.onResume()
    }

    override fun onDestroyView() {
        countJob?.cancel()
        countJob = null
        activityViewModel.setAudioItemObserver(TAG)
        _binding = null
        viewModel.isDestroyed = true
        super.onDestroyView()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }

}