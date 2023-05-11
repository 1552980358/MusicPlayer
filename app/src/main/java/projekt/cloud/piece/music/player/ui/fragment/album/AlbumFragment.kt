package projekt.cloud.piece.music.player.ui.fragment.album

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
import projekt.cloud.piece.music.player.databinding.FragmentAlbumBinding
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase.RuntimeDatabaseUtil.runtimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity
import projekt.cloud.piece.music.player.ui.fragment.album.AlbumLayoutCompat.AlbumLayoutCompatUtil
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.main

class AlbumFragment: BaseMultiDensityFragment<FragmentAlbumBinding, AlbumLayoutCompat>() {

    override val viewBindingInflater: ViewBindingInflater<FragmentAlbumBinding>
        get() = FragmentAlbumBinding::inflate

    override val layoutCompatInflater: LayoutCompatInflater<FragmentAlbumBinding, AlbumLayoutCompat>
        get() = AlbumLayoutCompatUtil::inflate

    private val args by navArgs<AlbumFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = TRANSPARENT
        }
    }

    override fun onSetupLayoutCompat(layoutCompat: AlbumLayoutCompat, savedInstanceState: Bundle?) {
        val albumId = args.id

        layoutCompat.setupAlbumCover(this, albumId)
        layoutCompat.setupCollapsingAppBar(this)
        layoutCompat.setupNavigation(this)
        layoutCompat.setupMargin(this)

        lifecycleScope.main {
            val runtimeDatabase = requireContext().runtimeDatabase

            setupAlbumMetadata(
                layoutCompat,
                queryAlbum(runtimeDatabase, albumId)
            )

            setRecyclerViewAdapter(
                layoutCompat,
                queryAudioList(runtimeDatabase, albumId)
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResult(args)
    }

    private fun setupAlbumMetadata(layoutCompat: AlbumLayoutCompat, albumView: AlbumView) {
        layoutCompat.setupAlbumMetadata(this, albumView)
    }

    private suspend fun queryAlbum(runtimeDatabase: RuntimeDatabase, id: String): AlbumView {
        return withContext(default) {
            runtimeDatabase.databaseViewDao()
                .queryAlbum(id)
        }
    }

    private fun setRecyclerViewAdapter(
        layoutCompat: AlbumLayoutCompat, audioList: List<AudioMetadataEntity>
    ) {
        layoutCompat.setRecyclerViewAdapter(audioList) {
            // TODO: Preserved for playing audio and creating playlist
        }
    }

    private suspend fun queryAudioList(
        runtimeDatabase: RuntimeDatabase, id: String
    ): List<AudioMetadataEntity> {
        return withContext(default) {
            runtimeDatabase.audioMetadataDao()
                .queryForAlbum(id)
        }
    }

    private fun setFragmentResult(args: AlbumFragmentArgs) {
        setFragmentResult(
            getString(R.string.library_transition),
            bundleOf(
                getString(R.string.library_transition) to getString(R.string.library_transition_album),
                getString(R.string.library_transition_pos) to args.pos
            )
        )
    }

}