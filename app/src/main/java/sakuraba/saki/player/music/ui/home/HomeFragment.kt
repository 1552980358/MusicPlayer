package sakuraba.saki.player.music.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import lib.github1552980358.ktExtension.androidx.coordinatorlayout.widget.makeShortSnack
import lib.github1552980358.ktExtension.androidx.fragment.app.findActivityViewById
import sakuraba.saki.player.music.MainActivity.Companion.INTENT_ACTIVITY_FRAGMENT_INTERFACE
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.databinding.FragmentHomeBinding
import sakuraba.saki.player.music.ui.home.util.DividerItemDecoration
import sakuraba.saki.player.music.ui.home.util.RecyclerViewAdapterUtil
import sakuraba.saki.player.music.util.ActivityFragmentInterface
import sakuraba.saki.player.music.base.BaseMainFragment

class HomeFragment: BaseMainFragment() {
    
    companion object {
        private const val TAG = "HomeFragment"
    }

    private var _fragmentHomeBinding: FragmentHomeBinding? = null
    private val fragmentHome get() = _fragmentHomeBinding!!
    
    private var _activityFragmentInterface: ActivityFragmentInterface? = null
    private val activityFragmentInterface get() = _activityFragmentInterface!!

    private lateinit var recyclerViewAdapter: RecyclerViewAdapterUtil
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        
        _fragmentHomeBinding = FragmentHomeBinding.inflate(inflater)
        
        _activityFragmentInterface = requireActivity().intent.getSerializableExtra(INTENT_ACTIVITY_FRAGMENT_INTERFACE) as ActivityFragmentInterface
        
        fragmentHome.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerViewAdapter = RecyclerViewAdapterUtil(mainFragmentData) { pos ->
            activityFragmentInterface.onFragmentListItemClick(pos, recyclerViewAdapter.audioInfoList[pos], recyclerViewAdapter.audioInfoList)
        }
        recyclerViewAdapter.setAdapterToRecyclerView(fragmentHome.recyclerView)
        fragmentHome.recyclerView.addItemDecoration(DividerItemDecoration())
        
        fragmentHome.root.isRefreshing = true

        fragmentHome.root.setOnRefreshListener {
            findActivityViewById<CoordinatorLayout>(R.id.coordinator_layout)?.makeShortSnack(R.string.home_snack_waiting_for_media_scanner)?.show()

        }

        setHasOptionsMenu(true)

        mainFragmentData.setLoadingStageChangeListener {
            recyclerViewAdapter.notifyDataSetChanged()
        }

        mainFragmentData.setCompleteLoadingListener {
            recyclerViewAdapter.notifyDataSetChanged()
            fragmentHome.root.isRefreshing = false
        }

        if (mainFragmentData.refreshCompleted) {
            fragmentHome.root.isRefreshing = false
        }
        
        return fragmentHome.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        /*
        // Register content observer
        requireContext().contentResolver.apply {
            registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, mediaStoreObserver)
            registerContentObserver(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, true, mediaStoreObserver)
            registerContentObserver(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, true, mediaStoreObserver)
            registerContentObserver(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, true, mediaStoreObserver)
            // registerContentObserver(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, true, observer)
        }
         */
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_home, menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (fragmentHome.root.isRefreshing) {
            return super.onOptionsItemSelected(item)
        }
        when (item.itemId) {
            R.id.action_settings -> findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavSetting())
            R.id.action_search -> findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavSearch())
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onPause() {
        activityFragmentInterface.onHomeFragmentPaused(recyclerViewAdapter.audioInfoList, recyclerViewAdapter.bitmapMap)
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentHomeBinding = null
    }
    
}