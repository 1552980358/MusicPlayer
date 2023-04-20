package projekt.cloud.piece.music.player.ui.fragment.home

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_ALL
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.base.LayoutCompatInflater
import projekt.cloud.piece.music.player.base.ViewBindingInflater
import projekt.cloud.piece.music.player.databinding.FragmentHomeBinding
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase.RuntimeDatabaseUtil.runtimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity
import projekt.cloud.piece.music.player.storage.runtime.entity.PlaybackEntity
import projekt.cloud.piece.music.player.ui.activity.main.MainViewModel
import projekt.cloud.piece.music.player.ui.fragment.home.HomeLayoutCompat.HomeLayoutCompatUtil
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.main

class HomeFragment: BaseMultiDensityFragment<FragmentHomeBinding, HomeLayoutCompat>() {

    override val viewBindingInflater: ViewBindingInflater<FragmentHomeBinding>
        get() = FragmentHomeBinding::inflate

    override val layoutCompatInflater: LayoutCompatInflater<FragmentHomeBinding, HomeLayoutCompat>
        get() = HomeLayoutCompatUtil::inflate

    override fun onSetupLayoutCompat(layoutCompat: HomeLayoutCompat, savedInstanceState: Bundle?) {
        layoutCompat.setupRecyclerViewAction(this)
        layoutCompat.setupRecyclerViewBottomMargin(this)

        val mainViewModel: MainViewModel by activityViewModels()

        val runtimeDatabase = requireContext().runtimeDatabase

        mainViewModel.isMediaBrowserCompatConnected.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                MediaControllerCompat.getMediaController(requireActivity())?.let { mediaControllerCompat ->
                    startRegisterTransportControls(runtimeDatabase, mediaControllerCompat)
                }
            }
        }
    }

    private var job: Job? = null

    private fun startRegisterTransportControls(
        runtimeDatabase: RuntimeDatabase, mediaControllerCompat: MediaControllerCompat
    ) {
        lifecycleScope.main {
            val audioList = queryAudioList(runtimeDatabase)

            val onItemClick = { id: String ->
                job = startAudioUpdateJob(id, audioList, runtimeDatabase, mediaControllerCompat)
            }

            // Update RecyclerView
            layoutCompat.setupRecyclerViewAdapter(
                createRecyclerViewAdapter(audioList, onItemClick)
            )
        }
    }

    private suspend fun queryAudioList(runtimeDatabase: RuntimeDatabase): List<AudioMetadataEntity> {
        return withContext(default) {
            runtimeDatabase.audioMetadataDao()
                .query()
        }
    }

    private suspend fun createRecyclerViewAdapter(
        audioList: List<AudioMetadataEntity>,
        onItemClick: (String) -> Unit
    ): HomeRecyclerAdapter {
        // Other than create on Main thread,
        // let it be created at background has a better performance for UI update
        return withContext(default) {
            HomeRecyclerAdapter(this@HomeFragment, audioList, onItemClick)
        }
    }

    private fun startAudioUpdateJob(
        id: String,
        audioList: List<AudioMetadataEntity>,
        runtimeDatabase: RuntimeDatabase,
        mediaControllerCompat: MediaControllerCompat
    ): Job {
        // Clear previous work
        job?.cancel()
        // Start wORK
        return lifecycleScope.main {
            putPlaylistIntoRuntimeDatabase(
                runtimeDatabase,
                // Convert into playback entity list
                getPlaybackList(
                    // Check if shuffle required, then shuffle list
                    shuffleAudioMetadataListIfRequired(mediaControllerCompat, audioList)
                )
            )
            // Call for audio play
            mediaControllerCompat.transportControls
                .playFromMediaId(id, null)
        }
    }

    private suspend fun getPlaybackList(
        audioMetadataList: List<AudioMetadataEntity>
    ) = withContext(default) {
        audioMetadataList.mapIndexed { index, audioMetadataEntity ->
            PlaybackEntity(index, audioMetadataEntity.id)
        } as ArrayList
    }

    private suspend fun shuffleAudioMetadataListIfRequired(
        mediaControllerCompat: MediaControllerCompat, audioMetadataList: List<AudioMetadataEntity>
    ) = withContext(default) {
        audioMetadataList.takeIf { mediaControllerCompat.shuffleMode != SHUFFLE_MODE_ALL }
            ?: audioMetadataList.shuffled()
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