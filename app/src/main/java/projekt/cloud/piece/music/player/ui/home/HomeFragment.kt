package projekt.cloud.piece.music.player.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import lib.github1552980358.ktExtension.androidx.fragment.app.getDrawable
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseMainFragment
import projekt.cloud.piece.music.player.databinding.FragmentHomeBinding
import projekt.cloud.piece.music.player.ui.home.util.RecyclerViewAdapterUtil

class HomeFragment : BaseMainFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val audioList get() = activityInterface.audioList
    private val artistList get() = activityInterface.artistList
    private val albumList get() = activityInterface.albumList
    private val audioBitmap40DpMap get() = activityInterface.audioBitmap40DpMap
    private val albumBitmap40DpMap get() = activityInterface.albumBitmap40DpMap

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
    
        binding.isRefreshing = true

        activityInterface.setRefreshListener(
            refreshStageChanged = {
                recyclerViewAdapterUtil = RecyclerViewAdapterUtil(binding.recyclerView,
                    audioList,
                    getDrawable(R.drawable.ic_music, null)!!.toBitmap(),
                    audioBitmap40DpMap,
                    albumBitmap40DpMap,
                    rootClick = {
                    
                    },
                    optionClick = { index, relativeLayout ->
                    
                    }
                )
            },
            refreshCompleted = {
                recyclerViewAdapterUtil.notifyUpdate()
                binding.isRefreshing = false
            }
        )

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}