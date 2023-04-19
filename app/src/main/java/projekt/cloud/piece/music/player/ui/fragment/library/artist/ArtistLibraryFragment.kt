package projekt.cloud.piece.music.player.ui.fragment.library.artist

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.base.ViewBindingInflater
import projekt.cloud.piece.music.player.databinding.FragmentLibraryObjectBinding
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase.RuntimeDatabaseUtil.runtimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.main

class ArtistLibraryFragment: BaseFragment<FragmentLibraryObjectBinding>() {

    override val viewBindingInflater: ViewBindingInflater<FragmentLibraryObjectBinding>
        get() = FragmentLibraryObjectBinding::inflate

    private val recyclerView: RecyclerView
        get() = binding.recyclerView

    override fun onSetupBinding(binding: FragmentLibraryObjectBinding, savedInstanceState: Bundle?) {
        setupArtistList()
    }

    private fun setupArtistList() {
        lifecycleScope.main {
            val runtimeDatabase = requireContext().runtimeDatabase

            val artists = queryArtists(runtimeDatabase)
            applyAlbumsToArtist(
                artists,
                queryAlbums(runtimeDatabase)
            )
            recyclerView.adapter = createAdapter(artists) {
            }
        }
    }

    private suspend fun queryArtists(runtimeDatabase: RuntimeDatabase): List<ArtistView> {
        return withContext(default) {
            runtimeDatabase.databaseViewDao()
                .queryArtist()
        }
    }

    private suspend fun queryAlbums(runtimeDatabase: RuntimeDatabase): Map<String, List<String>> {
        return withContext(default) {
            runtimeDatabase.audioMetadataDao()
                .queryAlbumOfArtist()
        }
    }

    private suspend fun applyAlbumsToArtist(artists: List<ArtistView>, albums: Map<String, List<String>>) {
        return withContext(default) {
            artists.forEach { artist ->
                artist.setAlbums(albums[artist.id])
            }
        }
    }

    private suspend fun createAdapter(
        artistList: List<ArtistView>,
        onItemClick: (String) -> Unit
    ): RecyclerView.Adapter<*> {
        return withContext(default) {
            ArtistLibraryRecyclerAdapter(
                artistList,
                this@ArtistLibraryFragment,
                onItemClick
            )
        }
    }

}