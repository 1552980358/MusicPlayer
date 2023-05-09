package projekt.cloud.piece.music.player.ui.fragment.artist

import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.platform.MaterialContainerTransform
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.base.LayoutCompatInflater
import projekt.cloud.piece.music.player.base.ViewBindingInflater
import projekt.cloud.piece.music.player.databinding.FragmentArtistBinding
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase.RuntimeDatabaseUtil.runtimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity
import projekt.cloud.piece.music.player.ui.fragment.artist.ArtistLayoutCompat.HomeLayoutCompatUtil
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.main

class ArtistFragment: BaseMultiDensityFragment<FragmentArtistBinding, ArtistLayoutCompat>() {

    override val viewBindingInflater: ViewBindingInflater<FragmentArtistBinding>
        get() = FragmentArtistBinding::inflate

    override val layoutCompatInflater: LayoutCompatInflater<FragmentArtistBinding, ArtistLayoutCompat>
        get() = HomeLayoutCompatUtil::inflate

    private val args: ArtistFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = TRANSPARENT
        }

    }

    override fun onSetupLayoutCompat(layoutCompat: ArtistLayoutCompat, savedInstanceState: Bundle?) {
        layoutCompat.setupCollapsingAppBar(this)
        layoutCompat.setupNavigation(this)
        layoutCompat.setupMargin(this)

        lifecycleScope.main {
            val runtimeDatabase = requireContext().runtimeDatabase

            // Query ArtistView
            val artist = queryArtist(runtimeDatabase, args.id)
            layoutCompat.setupArtistMetadata(this@ArtistFragment, artist)

            // Query audios
            val audioList = queryAudio(runtimeDatabase, artist.id)
            setRecyclerViewAdapter(layoutCompat, audioList)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResult(args)
    }

    private suspend fun queryArtist(
        runtimeDatabase: RuntimeDatabase, id: String
    ): ArtistView {
        return withContext(default) {
            runtimeDatabase.databaseViewDao()
                .queryArtist(id)
        }
    }

    private suspend fun queryAudio(
        runtimeDatabase: RuntimeDatabase, id: String
    ): List<AudioMetadataEntity> {
        return withContext(default) {
            runtimeDatabase.audioMetadataDao()
                .queryForArtist(id)
        }
    }

    private fun setRecyclerViewAdapter(layoutCompat: ArtistLayoutCompat, audioList: List<AudioMetadataEntity>) {
        layoutCompat.setRecyclerViewAdapter(this, audioList) { _ ->
            // TODO: Preserved for playing audio and creating playlist
        }
    }

    private fun setFragmentResult(args: ArtistFragmentArgs) {
        setFragmentResult(
            getString(R.string.library_transition),
            bundleOf(
                getString(R.string.library_transition) to getString(R.string.library_transition_artist),
                getString(R.string.library_transition_pos) to args.pos
            )
        )
    }

}