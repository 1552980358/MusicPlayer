package sakuraba.saki.player.music.audioDetail.lyricPreview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sakuraba.saki.player.music.audioDetail.lyricPreview.util.RecyclerViewAdapterUtil
import sakuraba.saki.player.music.databinding.FragmentLyricViewBinding
import sakuraba.saki.player.music.util.Constants.EXTRAS_DATA
import sakuraba.saki.player.music.util.LyricUtil.readLyric

class LyricViewFragment: Fragment() {

    private var _fragmentLyricViewBinding: FragmentLyricViewBinding? = null
    private val fragmentLyricView get() = _fragmentLyricViewBinding!!

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _fragmentLyricViewBinding = FragmentLyricViewBinding.inflate(inflater, container, false)
        return fragmentLyricView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(fragmentLyricView.recyclerView)
        requireContext().readLyric(requireArguments().getString(EXTRAS_DATA)!!, recyclerViewAdapterUtil.lyricList, recyclerViewAdapterUtil.timeList)
        recyclerViewAdapterUtil.notifyDataSetChanged()
    }

}