package projekt.cloud.piece.music.player.ui.fragment.library.album

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
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseLayoutCompat
import projekt.cloud.piece.music.player.databinding.FragmentLibraryObjectBinding
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView
import projekt.cloud.piece.music.player.ui.fragment.library.LibraryFragmentDirections
import projekt.cloud.piece.music.player.ui.fragment.library.LibraryFragmentInterface
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.main
import projekt.cloud.piece.music.player.util.KotlinUtil.ifNotNull
import projekt.cloud.piece.music.player.util.KotlinUtil.tryTo
import projekt.cloud.piece.music.player.util.ScreenDensity
import projekt.cloud.piece.music.player.util.ScreenDensity.COMPACT
import projekt.cloud.piece.music.player.util.ScreenDensity.EXPANDED
import projekt.cloud.piece.music.player.util.ScreenDensity.MEDIUM

abstract class AlbumLibraryLayoutCompat(
    binding: FragmentLibraryObjectBinding
): BaseLayoutCompat<FragmentLibraryObjectBinding>(binding) {

    companion object AlbumLibraryLayoutCompatUtil {

        fun inflate(screenDensity: ScreenDensity, binding: FragmentLibraryObjectBinding): AlbumLibraryLayoutCompat {
            return when (screenDensity) {
                COMPACT -> CompatImpl(binding)
                MEDIUM -> W600dpImpl(binding)
                EXPANDED -> W1240dpImpl(binding)
            }
        }
    }

    protected val recyclerView: RecyclerView
        get() = binding.recyclerView

    fun setupRecyclerViewAdapter(fragment: Fragment, albums: List<AlbumView>) {
        recyclerView.adapter = AlbumLibraryRecyclerAdapter(albums, fragment) { id, pos, view ->
            doOnItemClick(fragment, id, pos, view)
        }
    }

    open fun doOnRecovery(fragment: Fragment) = Unit

    protected open fun doOnItemClick(
        fragment: Fragment, id: String, pos: Int, view: View
    ) = Unit

    private class CompatImpl(binding: FragmentLibraryObjectBinding): AlbumLibraryLayoutCompat(binding) {

        override fun doOnRecovery(fragment: Fragment) {
            recyclerView.doOnPreDraw {
                fragment.requireParentFragment()
                    .startPostponedEnterTransition()
            }
        }

        override fun doOnItemClick(fragment: Fragment, id: String, pos: Int, view: View) {
            fragment.lifecycleScope.default {
                clearTransitionName(recyclerView)
                // Prepare parent fragment transition
                fragment.exitTransition = Hold()
                // Prepare transition name
                view.transitionName = fragment.getString(R.string.album_transition)
                // Start navigate
                navigateToAlbum(
                    fragment,
                    fragment.requireParentFragment()
                        .findNavController(),
                    id, pos, view
                )
            }
        }

        private fun clearTransitionName(recyclerView: RecyclerView) {
            recyclerView.forEach { view ->
                view.transitionName.ifNotNull {
                    view.transitionName = null
                }
            }
        }

        private fun navigateToAlbum(
            fragment: Fragment, navController: NavController, id: String, pos: Int, view: View
        ) {
            /**
             * !!! Important !!!
             * All navigation task should be handled in Main thread
             **/
            fragment.lifecycleScope.main {
                navController.navigate(
                    LibraryFragmentDirections.toAlbum(id, pos),
                    FragmentNavigatorExtras(view to view.transitionName)
                )
            }
        }

    }

    private class W600dpImpl(binding: FragmentLibraryObjectBinding): AlbumLibraryLayoutCompat(binding) {

        override fun doOnItemClick(fragment: Fragment, id: String, pos: Int, view: View) {
            fragment.requireParentFragment()
                .tryTo<LibraryFragmentInterface> { libraryFragmentInterface ->
                    libraryFragmentInterface.navigateToAlbum(id)
                }
        }

    }

    private class W1240dpImpl(binding: FragmentLibraryObjectBinding): AlbumLibraryLayoutCompat(binding)

}