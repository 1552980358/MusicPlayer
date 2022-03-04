package sakuraba.saki.player.music.ui.home

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.MenuCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import lib.github1552980358.ktExtension.android.content.getSerializableOf
import lib.github1552980358.ktExtension.android.content.intent
import lib.github1552980358.ktExtension.androidx.fragment.app.show
import sakuraba.saki.player.music.AudioDetailActivity
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.databinding.FragmentHomeBinding
import sakuraba.saki.player.music.ui.home.util.DividerItemDecoration
import sakuraba.saki.player.music.ui.home.util.RecyclerViewAdapterUtil
import sakuraba.saki.player.music.base.BaseMainFragment
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.ui.common.addToPlaylist.AddToPlaylistDialogFragment
import sakuraba.saki.player.music.util.Constants.EXTRAS_DATA

class HomeFragment: BaseMainFragment() {
    
    companion object {
        private const val TAG = "HomeFragment"
    }

    private var _fragmentHomeBinding: FragmentHomeBinding? = null
    private val fragmentHome get() = _fragmentHomeBinding!!

    private lateinit var recyclerViewAdapter: RecyclerViewAdapterUtil

    private val audioInfoList get() = activityInterface.audioInfoList
    private val audioDatabaseHelper get() = activityInterface.audioDatabaseHelper
    
    private lateinit var navController: NavController
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        
        _fragmentHomeBinding = FragmentHomeBinding.inflate(inflater)

        fragmentHome.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerViewAdapter = RecyclerViewAdapterUtil(activityInterface) { pos ->
            activityInterface.onFragmentListItemClick(pos, activityInterface.audioInfoList[pos], activityInterface.audioInfoList)
        }
        val audioDetailActivityLauncher = registerForActivityResult(StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.getSerializableOf<AudioInfo>(EXTRAS_DATA)?.let { audioInfo ->
                    activityInterface.onArtUpdate(audioInfo)
                }
            }
        }

        recyclerViewAdapter.setLongClickListener { position, relativeLayout, imageView ->
            PopupMenu(requireContext(), relativeLayout).apply {
                inflate(R.menu.menu_option_home)
                menu.getItem(0).title = audioInfoList[position].audioTitle
                MenuCompat.setGroupDividerEnabled(menu, true)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_detail -> audioDetailActivityLauncher.launch(
                            intent(requireContext(), AudioDetailActivity::class.java) {
                                putExtra(EXTRAS_DATA, audioInfoList[position])
                            },
                            ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), imageView, imageView.transitionName)
                        )
                        R.id.menu_add_to_playlist ->
                            AddToPlaylistDialogFragment(activityInterface.playlistList, activityInterface.playlistMap) { playlist ->
                                audioInfoList[position].apply {
                                    playlist += this
                                    audioDatabaseHelper.addPlaylistContent(playlist, this)
                                    audioDatabaseHelper.writeComplete()
                                }
                            }.show(this@HomeFragment)
                    }
                    return@setOnMenuItemClickListener true
                }
            }.show()
        }

        recyclerViewAdapter.setAdapterToRecyclerView(fragmentHome.recyclerView)
        fragmentHome.recyclerView.addItemDecoration(DividerItemDecoration())

        fragmentHome.root.isEnabled = false

        setHasOptionsMenu(true)

        activityInterface.apply {
            setLoadingStageChangeListener {
                recyclerViewAdapter.notifyDataSetChanged()
            }
            setCompleteLoadingListener {
                recyclerViewAdapter.notifyDataSetChanged()
                fragmentHome.root.isRefreshing = false
            }
            setContentChangeRefreshListener {
                fragmentHome.root.isRefreshing = true
            }
        }

        if (activityInterface.refreshCompleted) {
            fragmentHome.root.isRefreshing = false
        }

        navController = findNavController()

        return fragmentHome.root
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_home, menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> navController.navigate(HomeFragmentDirections.actionNavHomeToNavSetting())
            R.id.action_search -> navController.navigate(HomeFragmentDirections.actionNavHomeToNavSearch())
            R.id.action_refresh -> {
                fragmentHome.root.isRefreshing = true
                activityInterface.onRequestRefresh()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activityInterface.hasAudioInfoListUpdated) {
            recyclerViewAdapter.notifyDataSetChanged()
            activityInterface.hasAudioInfoListUpdated = false
        }
    }

    override fun onDestroy() {
        activityInterface.removeContentChangeRefreshListener()
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentHomeBinding = null
    }
    
}