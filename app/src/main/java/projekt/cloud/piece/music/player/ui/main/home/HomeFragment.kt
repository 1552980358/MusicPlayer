package projekt.cloud.piece.music.player.ui.main.home

import android.Manifest.permission.BLUETOOTH
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.SharedPreferences
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.S
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.core.view.MenuCompat.setGroupDividerEnabled
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import lib.github1552980358.ktExtension.android.graphics.toBitmap
import lib.github1552980358.ktExtension.androidx.fragment.app.getDrawable
import lib.github1552980358.ktExtension.androidx.fragment.app.show
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.FragmentHomeBinding
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_INDEX
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_LIST
import projekt.cloud.piece.music.player.ui.addToPlaylist.AddToPlaylistDialogFragment
import projekt.cloud.piece.music.player.ui.main.MainViewModel
import projekt.cloud.piece.music.player.util.DatabaseUtil.initializeApp
import projekt.cloud.piece.music.player.util.DatabaseUtil.launchAppCoroutine
import projekt.cloud.piece.music.player.ui.main.home.util.RecyclerViewAdapterUtil
import projekt.cloud.piece.music.player.util.DatabaseUtil.loadDatabase

class HomeFragment: BaseFragment() {

    companion object {
        const val TAG = "HomeFragment"
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    private lateinit var mainViewModel: MainViewModel

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(parentFragment as BaseFragment)[MainViewModel::class.java]
        sharedPreferences = getDefaultSharedPreferences(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val permissionRequestList = arrayListOf(READ_EXTERNAL_STORAGE, BLUETOOTH)
        if (SDK_INT >= S) {
            permissionRequestList.add(BLUETOOTH_CONNECT)
        }
        when {
            activityViewModel.isLoaded -> io {
                if (activityViewModel.hasSettingsUpdated) {
                    activityViewModel.hasSettingsUpdated = false
                    activityViewModel.audioList = loadDatabase(requireContext(), sharedPreferences, activityViewModel.database)
                }
                initializeRecyclerView(activityViewModel.audioList)
            }
            else -> when (checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE)) {
                PERMISSION_GRANTED -> launchAppCoroutine(requireContext(), sharedPreferences, activityViewModel.database, activityViewModel.audioArtMap, activityViewModel.albumArtMap) {
                    activityViewModel.audioList = it
                    loadDefaultCoverArt()
                    initializeRecyclerView(it)
                }
                else -> registerForActivityResult(RequestMultiplePermissions()) {
                    if (it.filter { (_, value) -> !value }.isNotEmpty()) {
                        return@registerForActivityResult
                    }
                    initializeApp(requireContext(), sharedPreferences, activityViewModel.database, activityViewModel.audioArtMap, activityViewModel.albumArtMap) { list ->
                        activityViewModel.audioList = list
                        loadDefaultCoverArt()
                        initializeRecyclerView(list)
                    }
                }.launch(permissionRequestList.toTypedArray())
            }
        }

        activityViewModel.setRefreshObserver(TAG) { isRefreshing, audioList, _, _ ->
            if (!isRefreshing && audioList != null) {
                activityViewModel.audioList = audioList
                ui { recyclerViewAdapterUtil.audioList = audioList }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> activityViewModel.requestRefreshDatabase(requireContext())
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadDefaultCoverArt() {
        activityViewModel.defaultCoverArt = getDrawable(R.drawable.ic_music)!!.toBitmap()!!
    }

    private fun initializeRecyclerView(audioList: List<AudioItem>) = ui {
        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(
            binding.recyclerView,
            audioList,
            activityViewModel.audioArtMap,
            activityViewModel.albumArtMap,
            activityViewModel.defaultCoverArt,
            onClick = {
                activityViewModel.mediaControllerCompat.transportControls.playFromMediaId(
                    audioList[it].id,
                    bundleOf(EXTRA_LIST to audioList, EXTRA_INDEX to it)
                )
            },
            onOptionClick = { anchorView, index ->
                PopupMenu(requireContext(), anchorView).apply {
                    inflate(R.menu.menu_recycler_home)
                    val audioItem = audioList[index]
                    menu.getItem(0).title = audioItem.title
                    setGroupDividerEnabled(menu, true)
                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.menu_add_to_playlist -> {
                                AddToPlaylistDialogFragment().apply {
                                    setAudioItem(audioItem)
                                }.show(this@HomeFragment)
                            }
                        }
                        true
                    }
                }.show()
            }
        )
        activityViewModel.isLoaded = true
    }

}