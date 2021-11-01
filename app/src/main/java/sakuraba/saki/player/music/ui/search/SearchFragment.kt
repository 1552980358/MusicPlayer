package sakuraba.saki.player.music.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.base.BaseMainFragment
import sakuraba.saki.player.music.databinding.FragmentSearchBinding
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.ui.search.util.RecyclerViewAdapterUtil
import sakuraba.saki.player.music.ui.search.util.SoftKeyboardUtil.hideSoftKeyboard

class SearchFragment: BaseMainFragment() {

    private var _fragmentSearchBinding: FragmentSearchBinding? = null
    private val fragmentSearch get() = _fragmentSearchBinding!!
    private lateinit var recyclerViewAdapter: RecyclerViewAdapterUtil
    private lateinit var audioInfoList: ArrayList<AudioInfo>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _fragmentSearchBinding = FragmentSearchBinding.inflate(inflater)
        setHasOptionsMenu(true)
        audioInfoList = activityInterface.audioInfoList
        return fragmentSearch.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewAdapter = RecyclerViewAdapterUtil(fragmentSearch.recyclerView) { audioInfo ->
            activityInterface.onFragmentListItemClick(audioInfo.index, audioInfo, audioInfoList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_search, menu)
        with(menu.findItem(R.id.menu_search_view).actionView as SearchView) {
            isIconified = false
            onActionViewExpanded()
            setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = true

                override fun onQueryTextChange(newText: String?): Boolean {
                    val searchResultList = recyclerViewAdapter.audioInfoList
                    searchResultList.clear()

                    if (newText != null && audioInfoList.isNotEmpty()) {
                        newText.split(' ').forEach { keyWord ->
                            audioInfoList.forEach { audioInfo ->
                                if (audioInfo.check(keyWord) && !searchResultList.contains(audioInfo)) {
                                    searchResultList.add(audioInfo)
                                }
                            }
                        }
                    }

                    recyclerViewAdapter.notifyDataSetChanged()
                    return true
                }
            })
        }
    }

    private fun AudioInfo.check(key: String): Boolean {
        val uppercase = key.uppercase()
        return audioTitle.uppercase().contains(uppercase) ||
                audioArtist.uppercase().contains(uppercase) ||
                audioAlbum.uppercase().contains(uppercase) ||
                audioTitlePinyin.contains(uppercase) ||
                audioArtistPinyin.contains(uppercase) ||
                audioAlbumPinyin.contains(uppercase)
    }

    override fun onDestroyView() {
        _fragmentSearchBinding = null
        requireActivity().hideSoftKeyboard()
        super.onDestroyView()
    }

}