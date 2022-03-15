package projekt.cloud.piece.music.player.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseMainFragment
import projekt.cloud.piece.music.player.databinding.FragmentHomeBinding
import projekt.cloud.piece.music.player.ui.main.home.util.RecyclerViewAdapterUtil

class HomeFragment: BaseMainFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    private val audioList get() = activityInterface.audioList
    private val audioBitmap40DpMap get() = activityInterface.audioBitmap40DpMap
    private val albumBitmap40DpMap get() = activityInterface.albumBitmap40DpMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (_binding == null) {
            _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_home, container, false)

            activityInterface.setRefreshListener(
                refreshStageChanged = {
                    recyclerViewAdapterUtil = RecyclerViewAdapterUtil(
                        binding.recyclerView,
                        audioList,
                        activityInterface.defaultAudioImage,
                        audioBitmap40DpMap,
                        albumBitmap40DpMap,
                        rootClick = { activityInterface.itemClick(it) },
                        optionClick = { index, relativeLayout -> }
                    )
                },
                refreshCompleted = {
                    recyclerViewAdapterUtil.notifyUpdate()
                }
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

}