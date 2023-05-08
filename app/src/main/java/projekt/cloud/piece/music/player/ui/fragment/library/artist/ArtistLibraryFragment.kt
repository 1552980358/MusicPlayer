package projekt.cloud.piece.music.player.ui.fragment.library.artist

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.base.LayoutCompatInflater
import projekt.cloud.piece.music.player.databinding.FragmentLibraryObjectBinding
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase.RuntimeDatabaseUtil.runtimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView
import projekt.cloud.piece.music.player.ui.fragment.library.artist.ArtistLibraryLayoutCompat.AlbumLibraryLayoutCompatUtil
import projekt.cloud.piece.music.player.ui.fragment.library.base.BaseLibraryObjectFragment
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.main

class ArtistLibraryFragment: BaseLibraryObjectFragment<ArtistLibraryLayoutCompat>() {

    override val layoutCompatInflater: LayoutCompatInflater<FragmentLibraryObjectBinding, ArtistLibraryLayoutCompat>
        get() = AlbumLibraryLayoutCompatUtil::inflate

    override fun onSetupLayoutCompat(layoutCompat: ArtistLibraryLayoutCompat, savedInstanceState: Bundle?) {
        setupArtists(layoutCompat)
    }

    private var artists: List<ArtistView>? = null

    private fun setupArtists(layoutCompat: ArtistLibraryLayoutCompat) {
        when (val artists = artists) {
            null -> {
                startQueryArtist(layoutCompat)
            }
            else -> {
                setupRecyclerViewAdapter(layoutCompat, artists)
                layoutCompat.doOnRecovery(this)
            }
        }
    }

    private fun startQueryArtist(layoutCompat: ArtistLibraryLayoutCompat) {
        lifecycleScope.main {
            val runtimeDatabase = requireContext().runtimeDatabase

            val artists = queryAndSetArtists(runtimeDatabase)
            applyAlbumsToArtist(
                artists,
                queryAlbums(runtimeDatabase)
            )

            setupRecyclerViewAdapter(layoutCompat, artists)
        }
    }

    private suspend fun queryAndSetArtists(runtimeDatabase: RuntimeDatabase): List<ArtistView> {
        return queryArtists(runtimeDatabase).also {
            artists = it
        }
    }

    private suspend fun queryArtists(
        runtimeDatabase: RuntimeDatabase
    ): List<ArtistView> {
        return withContext(default) {
            runtimeDatabase.databaseViewDao()
                .queryArtist()
        }
    }

    private suspend fun queryAlbums(
        runtimeDatabase: RuntimeDatabase
    ): Map<String, List<String>> {
        return withContext(default) {
            runtimeDatabase.databaseViewDao()
                .queryAlbumsOfArtists()
        }
    }

    private suspend fun applyAlbumsToArtist(
        artists: List<ArtistView>, albums: Map<String, List<String>>
    ) {
        return withContext(default) {
            artists.forEach { artist ->
                artist.setAlbums(albums[artist.id])
            }
        }
    }

    private fun setupRecyclerViewAdapter(
        layoutCompat: ArtistLibraryLayoutCompat, artists: List<ArtistView>
    ) {
        layoutCompat.setupRecyclerViewAdapter(this, artists)
    }

}