package projekt.cloud.piece.music.player.ui.main

import android.content.res.ColorStateList.valueOf
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.transition.Hold
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import lib.github1552980358.ktExtension.android.content.intent
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.ThemeSwitchActivity
import projekt.cloud.piece.music.player.ThemeSwitchActivity.Companion.EXTRA_IS_NIGHT
import projekt.cloud.piece.music.player.ThemeSwitchActivity.Companion.setScreenshot
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.databinding.FragmentMainBinding
import projekt.cloud.piece.music.player.util.ViewUtil.screenshot

class MainFragment: BaseFragment() {

    companion object {
        private const val TAG = "MainFragment"
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val appBarMain get() = binding.appBarMain
    private val toolbar get() = appBarMain.toolbar
    private val viewPager get() = appBarMain.viewPager
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

        with(appBarMain.viewPager) {

            adapter = object : FragmentStateAdapter(this@MainFragment) {
                override fun getItemCount() = viewModel.fragments.size
                override fun createFragment(position: Int) = viewModel.fragments[position]
            }

        }

        val bottomNavigationItems = listOf(R.id.nav_home, R.id.nav_album)

        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigation.menu.getItem(position).isChecked = true
            }
        })

        bottomNavigation.setOnItemSelectedListener {
            viewPager.currentItem = bottomNavigationItems.indexOfFirst { id -> id == it.itemId }
            true
        }

        extendedFloatingActionButton.setOnClickListener {
            if (activityViewModel.audioItem != null) {
                navController.navigate(
                    MainFragmentDirections.actionNavMainToNavPlay(),
                    FragmentNavigatorExtras(extendedFloatingActionButton to extendedFloatingActionButton.transitionName)
                )
            }
        }

        activityViewModel.initialIsNightMode(requireContext())

        binding.navigationView.getHeaderView(0).findViewById<RelativeLayout>(R.id.relative_layout).setOnClickListener {
            binding.root.screenshot(requireActivity().window) { screenshot ->
                setScreenshot(screenshot)
                with(activityViewModel) {
                    isNightMode = !isNightMode
                    startActivity(intent(requireContext(), ThemeSwitchActivity::class.java) {
                        putExtra(EXTRA_IS_NIGHT, isNightMode)
                    })
                    requireActivity().overridePendingTransition(0, 0)
                }
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

    override fun onStart() {
        super.onStart()
        TypedValue().run {
            requireActivity().theme.resolveAttribute(android.R.attr.statusBarColor, this, true)
            requireActivity().window.statusBarColor = data
        }
    }

    override fun onDestroyView() {
        countJob?.cancel()
        countJob = null
        activityViewModel.setAudioItemObserver(TAG)
        _binding = null
        viewModel.isDestroyed = true
        super.onDestroyView()
    }

}