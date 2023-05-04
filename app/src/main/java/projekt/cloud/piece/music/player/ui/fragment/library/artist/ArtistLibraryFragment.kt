package projekt.cloud.piece.music.player.ui.fragment.library.artist

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.platform.Hold
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.databinding.FragmentLibraryObjectBinding
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase.RuntimeDatabaseUtil.runtimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView
import projekt.cloud.piece.music.player.ui.fragment.library.LibraryFragmentDirections
import projekt.cloud.piece.music.player.ui.fragment.library.base.BaseLibraryObjectFragment
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.main
import projekt.cloud.piece.music.player.util.KotlinUtil.ifNull
import projekt.cloud.piece.music.player.util.KotlinUtil.isNotNull

class ArtistLibraryFragment: BaseLibraryObjectFragment() {

    override fun onSetupBinding(binding: FragmentLibraryObjectBinding, savedInstanceState: Bundle?) {
        setupArtistList(requireParentFragment())
    }

    private var _artists: List<ArtistView>? = null
    private val artists: List<ArtistView>
        get() = _artists!!

    private fun setupArtistList(fragment: Fragment) {
        when {
            _artists.isNotNull -> {
                setRecyclerViewAdapter(fragment, artists)
                recyclerView.doOnPreDraw {
                    fragment.startPostponedEnterTransition()
                }
            }
            else -> {
                startQueryArtists(
                    fragment, requireContext().runtimeDatabase
                )
            }
        }
    }

    private fun startQueryArtists(
        fragment: Fragment, runtimeDatabase: RuntimeDatabase
    ) {
        lifecycleScope.main {
            _artists = queryArtists(runtimeDatabase)
            applyAlbumsToArtist(
                artists,
                queryAlbums(runtimeDatabase)
            )

            setRecyclerViewAdapter(fragment, artists)
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

    private fun setRecyclerViewAdapter(fragment: Fragment, artists: List<ArtistView>) {
        recyclerView.adapter.ifNull {
            recyclerView.adapter = createRecyclerViewAdapter(artists) { id, name, pos, view ->
                setupAndNavigateToArtist(fragment, id, name, pos, view)
            }
        }
    }

    private fun createRecyclerViewAdapter(
        artistList: List<ArtistView>,
        onItemClick: (String, String, Int, View) -> Unit
    ): RecyclerView.Adapter<*> {
        return ArtistLibraryRecyclerAdapter(artistList, this, onItemClick)
    }

    private fun setupAndNavigateToArtist(fragment: Fragment, id: String, name: String, pos: Int, view: View) {
        lifecycleScope.default {
            /**
             * !!! Important !!!
             * Should clear all transition name, otherwise
             * transition may not be work properly
             **/
            clearTransitionName(recyclerView)
            // Prepare parent fragment transition
            fragment.exitTransition = Hold()
            // Prepare transition name
            view.transitionName = getString(R.string.artist_transition)
            // Start navigate
            navigateToArtist(fragment.findNavController(), id, name, pos, view)
        }
    }

    private fun clearTransitionName(recyclerView: RecyclerView) {
        recyclerView.forEach { view ->
            if (view.transitionName.isNotNull) {
                view.transitionName = null
            }
        }
    }

    private fun navigateToArtist(
        navController: NavController, id: String, name: String, pos: Int, view: View
    ) {
        lifecycleScope.main {
            navController.navigate(
                LibraryFragmentDirections.toArtist(id, name, pos),
                FragmentNavigatorExtras(view to view.transitionName)
            )
        }
    }

}