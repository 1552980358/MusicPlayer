package projekt.cloud.piece.music.player.ui.main.home

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import lib.github1552980358.ktExtension.android.graphics.toBitmap
import lib.github1552980358.ktExtension.androidx.fragment.app.getDrawable
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.FragmentHomeBinding
import projekt.cloud.piece.music.player.ui.main.home.util.DatabaseUtil.initializeApp
import projekt.cloud.piece.music.player.ui.main.home.util.DatabaseUtil.launchAppCoroutine
import projekt.cloud.piece.music.player.ui.main.home.util.RecyclerViewAdapterUtil

class HomeFragment: BaseFragment() {

    companion object {
        const val TAG = "HomeFragment"
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        when {
            activityViewModel.isLoaded -> initializeRecyclerView(activityViewModel.audioList)
            else -> when (checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE)) {
                PERMISSION_GRANTED -> launchAppCoroutine(requireContext(), activityViewModel.database, activityViewModel.audioArtMap, activityViewModel.albumArtMap) {
                    activityViewModel.audioList = it
                    initializeRecyclerView(it)
                }
                else -> registerForActivityResult(RequestPermission()) {
                    if (it) {
                        initializeApp(requireContext(), activityViewModel.database, activityViewModel.audioArtMap, activityViewModel.albumArtMap) { list ->
                            activityViewModel.audioList = list
                            initializeRecyclerView(list)
                        }
                    }
                }.launch(READ_EXTERNAL_STORAGE)
            }
        }

    }

    private fun initializeRecyclerView(audioList: List<AudioItem>) = ui {
        if (!activityViewModel.isLoaded) {
            activityViewModel.defaultCoverArt = getDrawable(R.drawable.ic_music)!!.toBitmap()!!
        }
        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(
            binding.recyclerView,
            audioList,
            activityViewModel.audioArtMap,
            activityViewModel.albumArtMap,
            activityViewModel.defaultCoverArt
        )
        activityViewModel.isLoaded = true
    }

}