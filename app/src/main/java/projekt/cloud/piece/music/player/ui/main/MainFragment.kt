package projekt.cloud.piece.music.player.ui.main

import android.content.res.ColorStateList.valueOf
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat.START
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
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
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.FragmentMainBinding
import projekt.cloud.piece.music.player.util.Constant.SCROLL_WAIT_DELAY
import projekt.cloud.piece.music.player.util.ViewUtil.screenshot

class MainFragment: BaseFragment(), OnNavigationItemSelectedListener {

    companion object {
        private const val TAG = "MainFragment"
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val appBarMain get() = binding.appBarMain
    private val toolbar get() = appBarMain.toolbar
    private val navigationView get() = binding.navigationView
    private val viewPager get() = appBarMain.viewPager
    private val bottomNavigation get() = appBarMain.bottomNavigation
    private val extendedFloatingActionButton get() = appBarMain.extendedFloatingActionButton
    private val swipeRefreshLayout get() = appBarMain.swipeRefreshLayout

    private lateinit var viewModel: MainViewModel
    private val database get() = activityViewModel.database

    private lateinit var navController: NavController
    private var countJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        bottomNavigation.background = null
        navController = findNavController()
        setHasOptionsMenu(true)
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

        val bottomNavigationItems = listOf(R.id.nav_home, R.id.nav_album, R.id.nav_artist, R.id.nav_playlist)

        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigation.menu.getItem(position).apply {
                    isChecked = true
                    toolbar.title = title
                }
            }
        })

        bottomNavigation.setOnItemSelectedListener {
            viewPager.currentItem = bottomNavigationItems.indexOfFirst { id -> id == it.itemId }
            toolbar.title = it.title
            true
        }

        extendedFloatingActionButton.setOnClickListener {
            if (activityViewModel.audioItem != null) {
                exitTransition = Hold()
                navController.navigate(
                    MainFragmentDirections.actionToPlayFragment(),
                    FragmentNavigatorExtras(extendedFloatingActionButton to extendedFloatingActionButton.transitionName)
                )
            }
        }

        activityViewModel.initialIsNightMode(requireContext())

        with(navigationView) {
            getHeaderView(0).findViewById<RelativeLayout>(R.id.relative_layout).setOnClickListener {
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
            setNavigationItemSelectedListener(this@MainFragment)
        }

        with(swipeRefreshLayout) {
            isEnabled = false
            isRefreshing = activityViewModel.isRefreshing
        }
        activityViewModel.setRefreshObserver(TAG) { isRefreshing, _, _, _ ->
            ui { swipeRefreshLayout.isRefreshing = isRefreshing }
        }

        activityViewModel.setCoverArtBitmapObserver(TAG) {
            extendedFloatingActionButton.icon = BitmapDrawable(resources, it)
        }
        activityViewModel.setAudioItemObserver(TAG, false) { audioItem ->
            updateAudioItem(audioItem)
        }
        activityViewModel.audioItem?.let { updateAudioItem(it, false) }
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
        activityViewModel.removeAllObservers(TAG)
        _binding = null
        super.onDestroyView()
    }

    override fun onBackPressed(): Boolean {
        if (binding.root.isDrawerOpen(START)) {
            binding.root.close()
            return false
        }
        return super.onBackPressed()
    }

    private fun updateAudioItem(audioItem: AudioItem, needExtend: Boolean = true) {
        countJob?.cancel()
        countJob = io {
            database.color.query(audioItem.id, audioItem.album).apply {
                with(extendedFloatingActionButton) {
                    ui {
                        setTextColor(primaryColor)
                        backgroundTintList = valueOf(backgroundColor)
                        if (text != audioItem.title) {
                            text = audioItem.title
                        }
                    }

                    if (needExtend) {
                        if (!isExtended) {
                            ui { extend() }
                        }

                        delay(SCROLL_WAIT_DELAY)
                        ui { shrink() }
                    }
                }
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> {
                exitTransition = null
                navController.navigate(MainFragmentDirections.actionToSettings())
            }
            R.id.nav_statistics -> {
                exitTransition = null
                navController.navigate(MainFragmentDirections.actionToStatisticsFragment())
            }
        }
        return true
    }

}