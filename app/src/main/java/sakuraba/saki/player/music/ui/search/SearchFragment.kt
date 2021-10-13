package sakuraba.saki.player.music.ui.search

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import sakuraba.saki.player.music.MainActivity.Companion.INTENT_ACTIVITY_FRAGMENT_INTERFACE
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.databinding.FragmentSearchBinding
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.ui.search.util.RecyclerViewAdapterUtil
import sakuraba.saki.player.music.ui.search.util.SoftKeyboardUtil.hideSoftKeyboard
import sakuraba.saki.player.music.util.ActivityFragmentInterface

class SearchFragment: Fragment() {

    private var _fragmentSearchBinding: FragmentSearchBinding? = null
    private val fragmentSearch get() = _fragmentSearchBinding!!
    private lateinit var recyclerViewAdapter: RecyclerViewAdapterUtil
    private lateinit var audioInfoList: ArrayList<AudioInfo>
    private lateinit var activityFragmentInterface: ActivityFragmentInterface

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activityFragmentInterface = requireActivity().intent.getSerializableExtra(INTENT_ACTIVITY_FRAGMENT_INTERFACE) as ActivityFragmentInterface
        _fragmentSearchBinding = FragmentSearchBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return fragmentSearch.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewAdapter = RecyclerViewAdapterUtil(fragmentSearch.recyclerView) { audioInfo ->
            activityFragmentInterface.onFragmentListItemClick(audioInfo.index, audioInfo, audioInfoList)
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

    fun setAudioInfoList(audioInfoList: ArrayList<AudioInfo>) {
        this.audioInfoList = audioInfoList
    }

}