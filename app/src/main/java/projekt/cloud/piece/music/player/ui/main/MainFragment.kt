package projekt.cloud.piece.music.player.ui.main

import android.Manifest.permission
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.transition.Hold
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_AUDIO_ITEM
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_BITMAP_ART
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.databinding.FragmentMainBinding
import projekt.cloud.piece.music.player.ui.main.album.AlbumFragment
import projekt.cloud.piece.music.player.ui.main.artist.ArtistFragment
import projekt.cloud.piece.music.player.ui.main.audio.AudioFragment
import projekt.cloud.piece.music.player.ui.main.base.RecyclerViewScrollHandler
import projekt.cloud.piece.music.player.ui.main.playlist.PlaylistFragment
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui

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
    
    private var countJob: Job? = null

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
        with(requireActivity() as AppCompatActivity) {
            setSupportActionBar(materialToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            ActionBarDrawerToggle(this, root, materialToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close).apply {
                root.addDrawerListener(this)
                syncState()
            }
        }
        
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomNavigationView)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.isDraggable = false
        bottomSheetBehavior.state = STATE_EXPANDED
        
        val recyclerViewScrollHandler = RecyclerViewScrollHandler(
            onLeaveBottom = {
                bottomSheetBehavior.state = STATE_EXPANDED
                extFab.show()
            },
            onScrolledToBottom = {
                bottomSheetBehavior.state = STATE_HIDDEN
                extFab.hide()
            }
        )
    
        fragmentList.forEach { it.setRecyclerViewScrollHandler(recyclerViewScrollHandler) }

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
                    recyclerViewScrollHandler.clearState()
                }
            })

            bottomNavigationView.setOnItemSelectedListener {
                currentItem = bottomNavigationItems.indexOfFirst { id -> id == it.itemId }
                recyclerViewScrollHandler.clearState()
                true
            }
        }

        with(extFab) {
            setOnClickListener {
                if (exitTransition == null || exitTransition !is Hold) {
                    exitTransition = Hold()
                }
                navController.navigate(
                    MainFragmentDirections.actionMainFragmentToPlayFragment(),
                    FragmentNavigatorExtras(extFab to extFab.transitionName)
                )
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
        }
    
        containerViewModel.register<AudioItem>(TAG, LABEL_AUDIO_ITEM) {
            it?.let { updateAudioItem(it) }
        }

        when (ContextCompat.checkSelfPermission(requireContext(), permission.READ_EXTERNAL_STORAGE)) {
            PERMISSION_GRANTED -> containerViewModel.launchApplication(requireActivity(), {})
            else -> {
                containerViewModel.setOnPermissionResult {
                    if (it.filter { (_, value) -> !value }.isNotEmpty()) {
                        return@setOnPermissionResult
                    }
                    containerViewModel.initialApplication(requireContext(), {})
                }
                containerViewModel.requestPermissions.launch(arrayOf(permission.READ_EXTERNAL_STORAGE))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

}