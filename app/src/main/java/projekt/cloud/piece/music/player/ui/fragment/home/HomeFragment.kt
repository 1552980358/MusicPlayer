package projekt.cloud.piece.music.player.ui.fragment.home

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.base.BaseLayoutCompat.BaseLayoutCompatUtil.layoutCompat
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.databinding.FragmentHomeBinding
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase.RuntimeDatabaseUtil.runtimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity
import projekt.cloud.piece.music.player.storage.runtime.entity.PlaybackEntity
import projekt.cloud.piece.music.player.ui.activity.main.MainViewModel
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.main
import projekt.cloud.piece.music.player.util.ScreenDensity.ScreenDensityUtil.screenDensity

class HomeFragment: BaseMultiDensityFragment<FragmentHomeBinding, HomeLayoutCompat>() {

    override val viewBindingClass: Class<FragmentHomeBinding>
        get() = FragmentHomeBinding::class.java

    override fun onCreateLayoutCompat(binding: FragmentHomeBinding): HomeLayoutCompat {
        return binding.layoutCompat(requireContext().screenDensity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainViewModel: MainViewModel by activityViewModels()

        lifecycleScope.main {
            val audioMetadataList = withContext(default) {
                requireContext().runtimeDatabase
                    .audioMetadataDao()
                    .query()
            }

            layoutCompat.setupRecyclerViewAdapter(
                HomeRecyclerViewUtil.getRecyclerViewAdapter(
                    this@HomeFragment, audioMetadataList, layoutCompat
                )
            )

            mainViewModel.isMediaBrowserCompatConnected.observe(viewLifecycleOwner) { isConnected ->
                val mediaControllerCompat = MediaControllerCompat.getMediaController(requireActivity())
                if (isConnected && mediaControllerCompat != null) {
                    registerTransportControls(mediaControllerCompat, audioMetadataList)
                }
            }
        }

    }

    private var job: Job? = null

    private fun registerTransportControls(
        mediaControllerCompat: MediaControllerCompat, audioMetadataList: List<AudioMetadataEntity>
    ) {
        val runtimeDatabase = requireContext().runtimeDatabase
        layoutCompat.setPlayMediaWithId { id ->
            job?.cancel()
            job = lifecycleScope.main {
                // Convert into playback entity list
                val playbackList = getPlaybackList(audioMetadataList)
                // Put playlist into runtime database
                putPlaylistIntoRuntimeDatabase(runtimeDatabase, playbackList)
                // Call for audio play
                mediaControllerCompat.transportControls
                    .playFromMediaId(id, null)
                job = null
            }
        }
    }

    private suspend fun getPlaybackList(
        audioMetadataList: List<AudioMetadataEntity>
    ) = withContext(default) {
        audioMetadataList.mapIndexed { index, audioMetadataEntity ->
            PlaybackEntity(index, audioMetadataEntity.id)
        }
    }

    private suspend fun putPlaylistIntoRuntimeDatabase(
        runtimeDatabase: RuntimeDatabase, playbackList: List<PlaybackEntity>
    ) = withContext(default) {
        with(runtimeDatabase.playbackDao()) {
            clear()
            insert(playbackList)
        }
    }

}