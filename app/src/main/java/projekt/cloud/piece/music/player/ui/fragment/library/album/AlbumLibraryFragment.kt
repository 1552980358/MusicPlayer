package projekt.cloud.piece.music.player.ui.fragment.library.album

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.music.player.base.ViewBindingInflater
import projekt.cloud.piece.music.player.databinding.FragmentLibraryObjectBinding
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase.RuntimeDatabaseUtil.runtimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.main

class AlbumLibraryFragment: BaseFragment<FragmentLibraryObjectBinding>() {

    override val viewBindingInflater: ViewBindingInflater<FragmentLibraryObjectBinding>
        get() = FragmentLibraryObjectBinding::inflate

    private val recyclerView: RecyclerView
        get() = binding.recyclerView

    override fun onSetupBinding(binding: FragmentLibraryObjectBinding, savedInstanceState: Bundle?) {
        startQueryAlbums()
    }

    private fun startQueryAlbums() {
        lifecycleScope.main {
            val albums = queryAlbums(requireActivity().runtimeDatabase)
            recyclerView.adapter = createRecyclerViewAdapter(albums) {
            }
        }
    }

    private suspend fun queryAlbums(runtimeDatabase: RuntimeDatabase): List<AlbumView> {
        return withContext(default) {
            runtimeDatabase.databaseViewDao()
                .queryAlbum()
        }
    }

    private suspend fun createRecyclerViewAdapter(
        albums: List<AlbumView>, onItemClick: (String) -> Unit
    ): BaseRecyclerViewAdapter {
        return withContext(default) {
            AlbumLibraryRecyclerAdapter(albums, this@AlbumLibraryFragment, onItemClick)
        }
    }

}