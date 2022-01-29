package sakuraba.saki.player.music.ui.setting.fragment

import android.media.MediaCodecList
import android.media.MediaCodecList.ALL_CODECS
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import sakuraba.saki.player.music.databinding.FragmentCodecBinding
import sakuraba.saki.player.music.ui.setting.fragment.util.RecyclerViewAdapterUtil
import sakuraba.saki.player.music.util.CoroutineUtil.ui

class CodecFragment: Fragment() {

    private var _fragmentCodecBinding: FragmentCodecBinding? = null
    private val layout get() = _fragmentCodecBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _fragmentCodecBinding = FragmentCodecBinding.inflate(layoutInflater)
        return layout.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        io {
            MediaCodecList(ALL_CODECS).codecInfos.let {
                ui { RecyclerViewAdapterUtil(it, layout.recyclerView) }
            }
        }
    }

}