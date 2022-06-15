package projekt.cloud.piece.music.player.ui.main

import android.Manifest.permission
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.android.material.transition.Hold
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_AUDIO_ITEM
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_BITMAP_ART
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_COLOR_ITEM
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.base.BasePagerViewModel
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.database.audio.item.ColorItem
import projekt.cloud.piece.music.player.databinding.FragmentMainBinding
import projekt.cloud.piece.music.player.ui.dialog.WebServerDialogFragment
import projekt.cloud.piece.music.player.ui.main.album.AlbumFragment
import projekt.cloud.piece.music.player.ui.main.artist.ArtistFragment
import projekt.cloud.piece.music.player.ui.main.audio.AudioFragment
import projekt.cloud.piece.music.player.ui.main.base.BaseMainFragment
import projekt.cloud.piece.music.player.ui.main.playlist.PlaylistFragment
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui
import projekt.cloud.piece.music.player.util.DialogFragmentUtil.showNow

/**
 * Class [MainFragment], inherit to [BaseFragment]
 *
 * Variables:
 *   [viewModel]
 *   [_binding]
 *   [navController]
 *
 *   [binding]
 *   [root]
 *   [appBarMain]
 *   [materialToolbar]
 *   [viewPager2]
 *   [bottomNavigationView]
 *   [extFab]
 *   [navigationView]
 *   [bottomNavigationViewBehavior]
 *   [extFabBehavior]
 *   [countJob]
 *
 * Methods:
 *   [onCreateView]
 *   [onViewCreated]
 *   [onDestroyView]
 *   [onNavigationItemSelected]
 *   [updateAudioItem]
 *   [colorItemUpdated]
 *
 **/
class MainFragment: BaseFragment(), OnNavigationItemSelectedListener {
    
    companion object {
        private const val TAG = "MainFragment"
        private const val DELAY_EXT_FAB_UPDATED = 5000L
    }

    /**
     * Inner Class [MainFragmentViewModel]
     *
     * Variables:
     *   [fragmentList]
     *
     **/
    class MainFragmentViewModel: BasePagerViewModel<BaseMainFragment>() {
        override fun setFragments() =
            arrayOf(AudioFragment(), AlbumFragment(), ArtistFragment(), PlaylistFragment())
    }

    private lateinit var viewModel: MainFragmentViewModel

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val root get() = binding.root
    private val appBarMain get() = binding.appBarMain
    private val materialToolbar get() = appBarMain.materialToolbar
    private val viewPager2 get() = appBarMain.viewPager2
    private val bottomNavigationView get() = appBarMain.bottomNavigationView
    private val extFab get() = appBarMain.extendedFloatingActionButton
    private val navigationView get() = binding.navigationView
    
    private lateinit var bottomNavigationViewBehavior: HideBottomViewOnScrollBehavior<BottomNavigationView>
    private lateinit var extFabBehavior: HideBottomViewOnScrollBehavior<ExtendedFloatingActionButton>
    
    private var countJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = Hold()
        viewModel = ViewModelProvider(this)[MainFragmentViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(requireActivity() as AppCompatActivity) {
            setSupportActionBar(materialToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            ActionBarDrawerToggle(this, root, materialToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close).apply {
                root.addDrawerListener(this)
                syncState()
            }
        }
        navigationView.setNavigationItemSelectedListener(this)
    
        @Suppress("UNCHECKED_CAST")
        bottomNavigationViewBehavior = (bottomNavigationView.layoutParams as CoordinatorLayout.LayoutParams)
            .behavior as HideBottomViewOnScrollBehavior<BottomNavigationView>
        @Suppress("UNCHECKED_CAST")
        extFabBehavior = (extFab.layoutParams as CoordinatorLayout.LayoutParams)
            .behavior as HideBottomViewOnScrollBehavior<ExtendedFloatingActionButton>
        
        val bottomNavigationItems = listOf(R.id.nav_audio_track, R.id.nav_album, R.id.nav_artist, R.id.nav_playlist)

        with(viewPager2) {
            viewModel.setUpViewPager2(this@MainFragment, this)

            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    bottomNavigationView.menu.getItem(position).apply {
                        isChecked = true
                    }
                    when {
                        viewModel[position].isRecyclerViewBottom -> {
                            extFabBehavior.slideDown(extFab)
                            bottomNavigationViewBehavior.slideDown(bottomNavigationView)
                        }
                        else -> {
                            extFabBehavior.slideUp(extFab)
                            bottomNavigationViewBehavior.slideUp(bottomNavigationView)
                        }
                    }
                }
            })

            bottomNavigationView.setOnItemSelectedListener {
                currentItem = bottomNavigationItems.indexOfFirst { id -> id == it.itemId }
                true
            }
        }

        with(extFab) {
            setOnClickListener {
                if (containerViewModel.audioItem != null) {
                    if (exitTransition == null || exitTransition !is Hold) {
                        exitTransition = Hold()
                    }
                    navController.navigate(
                        MainFragmentDirections.actionMainFragmentToPlayFragment(),
                        FragmentNavigatorExtras(extFab to extFab.transitionName)
                    )
                }
            }
            
            setAnimateShowBeforeLayout(true)
            
            when (val bitmap = containerViewModel.bitmapArt) {
                null -> setIconResource(R.drawable.ic_round_audiotrack_24)
                else -> icon = BitmapDrawable(resources, bitmap)
            }
            when (val audioItem = containerViewModel.audioItem) {
                null -> shrink()
                else -> updateAudioItem(audioItem)
            }
            
            containerViewModel.register<Bitmap>(TAG, LABEL_BITMAP_ART) {
                when (it) {
                    null -> setIconResource(R.drawable.ic_round_audiotrack_24)
                    else -> icon = BitmapDrawable(resources, it)
                }
            }

            containerViewModel.colorItem?.let { colorItemUpdated(this, it) }

            containerViewModel.register<ColorItem>(TAG, LABEL_COLOR_ITEM) { colorItem ->
                colorItem?.let { colorItemUpdated(this, it) }
            }
        }
    
        containerViewModel.register<AudioItem>(TAG, LABEL_AUDIO_ITEM) {
            it?.let { updateAudioItem(it) }
        }

        when (ContextCompat.checkSelfPermission(requireContext(), permission.READ_EXTERNAL_STORAGE)) {
            PERMISSION_GRANTED -> containerViewModel.launchApplication(requireActivity())
            else -> {
                containerViewModel.setOnPermissionResult {
                    if (it.filter { (_, value) -> !value }.isNotEmpty()) {
                        return@setOnPermissionResult
                    }
                    containerViewModel.initialApplication(requireContext())
                }
                containerViewModel.requestPermissions.launch(arrayOf(permission.READ_EXTERNAL_STORAGE))
            }
        }
    }

    override fun onDestroyView() {
        containerViewModel.unregisterAll(TAG)
        countJob?.cancel()
        super.onDestroyView()
        _binding = null
    }
    
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_web_server -> WebServerDialogFragment()
                .showNow(this)
        }
        return true
    }
    
    private fun updateAudioItem(audioItem: AudioItem) {
        extFab.text = audioItem.title
        countJob?.cancel()
        countJob = io {
            if (!extFab.isExtended) {
                ui { extFab.extend() }
            }
            delay(DELAY_EXT_FAB_UPDATED)
            ui { extFab.shrink() }
        }
    }

    private fun colorItemUpdated(extendedFloatingActionButton: ExtendedFloatingActionButton, colorItem: ColorItem) {
        extendedFloatingActionButton.setBackgroundColor(colorItem.background)
        extendedFloatingActionButton.setTextColor(colorItem.primary)
    }

}