package projekt.cloud.piece.music.player.ui.fragment.library.album

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
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView
import projekt.cloud.piece.music.player.ui.fragment.library.LibraryFragmentDirections
import projekt.cloud.piece.music.player.ui.fragment.library.base.BaseLibraryObjectFragment
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.main
import projekt.cloud.piece.music.player.util.KotlinUtil.ifNull
import projekt.cloud.piece.music.player.util.KotlinUtil.isNotNull

class AlbumLibraryFragment: BaseLibraryObjectFragment() {

    override fun onSetupBinding(binding: FragmentLibraryObjectBinding, savedInstanceState: Bundle?) {
        setupAlbumList(requireParentFragment())
    }

    private var _albums: List<AlbumView>? = null
    private val albums: List<AlbumView>
        get() = _albums!!

    private fun setupAlbumList(fragment: Fragment) {
        when {
            _albums.isNotNull -> {
                setRecyclerViewAdapter(fragment, albums)
                recyclerView.doOnPreDraw {
                    fragment.startPostponedEnterTransition()
                }
            }
            else -> {
                startQueryAlbums(fragment)
            }
        }
    }

    private fun startQueryAlbums(fragment: Fragment) {
        lifecycleScope.main {
            _albums = queryAlbums(requireActivity().runtimeDatabase)
            setRecyclerViewAdapter(fragment, albums)
        }
    }

    private suspend fun queryAlbums(runtimeDatabase: RuntimeDatabase): List<AlbumView> {
        return withContext(default) {
            runtimeDatabase.databaseViewDao()
                .queryAlbum()
        }
    }

    private fun setRecyclerViewAdapter(fragment: Fragment, albums: List<AlbumView>) {
        recyclerView.adapter.ifNull {
            recyclerView.adapter = createRecyclerViewAdapter(albums) { id, pos, view ->
                setupAndNavigateToAlbum(fragment, id, pos, view)
            }
        }
    }

    private fun createRecyclerViewAdapter(
        albums: List<AlbumView>, onItemClick: (String, Int, View) -> Unit
    ): RecyclerView.Adapter<*> {
        return AlbumLibraryRecyclerAdapter(albums, this@AlbumLibraryFragment, onItemClick)
    }

    private fun setupAndNavigateToAlbum(fragment: Fragment, id: String, pos: Int, view: View) {
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
            view.transitionName = getString(R.string.album_transition)
            // Start navigate
            navigateToAlbum(fragment.findNavController(), id, pos, view)
        }
    }

    private fun clearTransitionName(recyclerView: RecyclerView) {
        recyclerView.forEach { view ->
            if (view.transitionName.isNotNull) {
                view.transitionName = null
            }
        }
    }

    private fun navigateToAlbum(navController: NavController, id: String, pos: Int, view: View) {
        lifecycleScope.main {
            navController.navigate(
                LibraryFragmentDirections.toAlbum(id, pos),
                FragmentNavigatorExtras(view to view.transitionName)
            )
        }
    }

}