package projekt.cloud.piece.music.player.ui.fragment.home

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_ALL
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlin.reflect.KClass
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.databinding.FragmentHomeBinding
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase.RuntimeDatabaseUtil.runtimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity
import projekt.cloud.piece.music.player.storage.runtime.entity.PlaybackEntity
import projekt.cloud.piece.music.player.ui.activity.main.MainViewModel
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.main

class HomeFragment: BaseMultiDensityFragment<FragmentHomeBinding, HomeLayoutCompat>() {

    override val viewBindingClass: Class<FragmentHomeBinding>
        get() = FragmentHomeBinding::class.java

    override val layoutCompatClass: KClass<HomeLayoutCompat>
        get() = HomeLayoutCompat::class

    override fun onSetupLayoutCompat(layoutCompat: HomeLayoutCompat, savedInstanceState: Bundle?) {
        layoutCompat.setupRecyclerViewAction(this)
        layoutCompat.setupRecyclerViewBottomMargin(this)

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
                // Put into runtime database
                putPlaylistIntoRuntimeDatabase(
                    runtimeDatabase,
                    // Convert into playback entity list
                    getPlaybackList(
                        // Check if shuffle required, then shuffle list
                        shuffleAudioMetadataListIfRequired(mediaControllerCompat, audioMetadataList)
                    )
                )
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