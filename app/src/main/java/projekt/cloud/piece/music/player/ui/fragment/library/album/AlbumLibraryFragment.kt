package projekt.cloud.piece.music.player.ui.fragment.library.album

import android.os.Bundle
import projekt.cloud.piece.music.player.base.LayoutCompatInflater
import projekt.cloud.piece.music.player.databinding.FragmentLibraryObjectBinding
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase.RuntimeDatabaseUtil.runtimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView
import projekt.cloud.piece.music.player.ui.fragment.library.album.AlbumLibraryLayoutCompat.AlbumLibraryLayoutCompatUtil
import projekt.cloud.piece.music.player.ui.fragment.library.base.BaseLibraryObjectFragment
import projekt.cloud.piece.music.player.util.CoroutineUtil.defaultBlocking
import projekt.cloud.piece.music.player.util.CoroutineUtil.main

class AlbumLibraryFragment: BaseLibraryObjectFragment<AlbumLibraryLayoutCompat>() {

    override val layoutCompatInflater: LayoutCompatInflater<FragmentLibraryObjectBinding, AlbumLibraryLayoutCompat>
        get() = AlbumLibraryLayoutCompatUtil::inflate

    override fun onSetupLayoutCompat(layoutCompat: AlbumLibraryLayoutCompat, savedInstanceState: Bundle?) {
        setupAlbums(layoutCompat)
    }

    private var albums: List<AlbumView>? = null

    private fun setupAlbums(layoutCompat: AlbumLibraryLayoutCompat) {
        when (val albums = albums) {
            null -> {
                startQueryAlbums(layoutCompat)
            }
            else -> {
                setRecyclerViewAdapter(layoutCompat, albums)
                layoutCompat.doOnRecovery(this)
            }
        }
    }

    private fun startQueryAlbums(layoutCompat: AlbumLibraryLayoutCompat) {
        main {
            setRecyclerViewAdapter(
                layoutCompat,
                queryAndSetAlbums(requireActivity().runtimeDatabase)
            )
        }
    }

    private suspend fun queryAndSetAlbums(runtimeDatabase: RuntimeDatabase): List<AlbumView> {
        return queryAlbums(runtimeDatabase).also {
            albums = it
        }
    }

    private suspend fun queryAlbums(runtimeDatabase: RuntimeDatabase): List<AlbumView> {
        return defaultBlocking {
            runtimeDatabase.databaseViewDao()
                .queryAlbum()
        }
    }

    private fun setRecyclerViewAdapter(layoutCompat: AlbumLibraryLayoutCompat, albums: List<AlbumView>) {
        layoutCompat.setupRecyclerViewAdapter(this, albums)
    }

}