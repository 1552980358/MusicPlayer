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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import lib.github1552980358.ktExtension.android.content.getSerializableOf
import lib.github1552980358.ktExtension.android.content.intent
import sakuraba.saki.player.music.AudioDetailActivity
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.databinding.FragmentHomeBinding
import sakuraba.saki.player.music.ui.home.util.DividerItemDecoration
import sakuraba.saki.player.music.ui.home.util.RecyclerViewAdapterUtil
import sakuraba.saki.player.music.base.BaseMainFragment
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.util.Constants.EXTRAS_DATA

class HomeFragment: BaseMainFragment() {
    
    companion object {
        private const val TAG = "HomeFragment"
    }

    private var _fragmentHomeBinding: FragmentHomeBinding? = null
    private val fragmentHome get() = _fragmentHomeBinding!!

    private lateinit var recyclerViewAdapter: RecyclerViewAdapterUtil

    private val audioInfoList get() = activityInterface.audioInfoList
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        
        _fragmentHomeBinding = FragmentHomeBinding.inflate(inflater)

        fragmentHome.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerViewAdapter = RecyclerViewAdapterUtil(this, activityInterface) { pos ->
            activityInterface.onFragmentListItemClick(pos, activityInterface.audioInfoList[pos], activityInterface.audioInfoList)
        }
        val audioDetailActivityLauncher = registerForActivityResult(StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.getSerializableOf<AudioInfo>(EXTRAS_DATA)?.let { audioInfo ->
                    activityInterface.onArtUpdate(audioInfo)
                }
            }
        }
        recyclerViewAdapter.setLongClickListener { audioInfo, activityOptionsCompat ->
            audioDetailActivityLauncher.launch(
                intent(requireContext(), AudioDetailActivity::class.java) {
                    putExtra(EXTRAS_DATA, audioInfo)
                },
                activityOptionsCompat
            )
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

        return fragmentHome.root
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_home, menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavSetting())
            R.id.action_search -> findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavSearch())
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