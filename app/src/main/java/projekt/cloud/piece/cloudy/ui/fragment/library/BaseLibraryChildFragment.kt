package projekt.cloud.piece.cloudy.ui.fragment.library

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.platform.MaterialFadeThrough
import projekt.cloud.piece.cloudy.base.BaseLayoutAdapter
import projekt.cloud.piece.cloudy.base.BaseMultiLayoutFragment
import projekt.cloud.piece.cloudy.databinding.LibraryChildFragmentBinding
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.base.BaseFragment
import projekt.cloud.piece.cloudy.ui.fragment.library.album.AlbumLibraryFragment
import projekt.cloud.piece.cloudy.util.FragmentUtil.getLong
import projekt.cloud.piece.cloudy.util.ViewBindingInflater

/**
 * [BaseLibraryChildFragment]
 * @generic [LC] [BaseLayoutAdapter]<[LibraryChildFragmentBinding]>
 * @abstractExtends [BaseMultiLayoutFragment]
 *   @typeParam [LibraryChildFragmentBinding]
 *   @typeParam [LC]
 **/
abstract class BaseLibraryChildFragment<LC>: BaseMultiLayoutFragment<LibraryChildFragmentBinding, LC>()
where LC: BaseLayoutAdapter<LibraryChildFragmentBinding> {

    /**
     * [BaseMultiLayoutFragment.viewBindingInflater]
     * @type [ViewBindingInflater]<[LibraryChildFragmentBinding]>
     **/
    override val viewBindingInflater: ViewBindingInflater<LibraryChildFragmentBinding>
        get() = LibraryChildFragmentBinding::inflate

    /**
     * [BaseLibraryChildFragment.recyclerView]
     * @type [androidx.recyclerview.widget.RecyclerView]
     * @layout [R.layout.library_child_fragment]
     * @id [R.id.recycler_view]
     **/
    private val recyclerView: RecyclerView
        get() = binding.nonnull().recyclerView

    /**
     * [androidx.fragment.app.Fragment.onCreate]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransitions(getLong(R.integer.md_spec_transition_duration_400))
    }

    /**
     * [AlbumLibraryFragment.setupTransitions]
     * @param transitionDuration [Long]
     **/
    private fun setupTransitions(transitionDuration: Long) {
        enterTransition = MaterialFadeThrough().apply {
            duration = transitionDuration
        }
        exitTransition = MaterialFadeThrough().apply {
            duration = transitionDuration
        }
        reenterTransition = MaterialFadeThrough().apply {
            duration = transitionDuration
        }
    }

    /**
     * [BaseFragment.onSetupBinding]
     * @param binding [LibraryChildFragmentBinding]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onSetupBinding(binding: LibraryChildFragmentBinding, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        super.onSetupBinding(binding, savedInstanceState)
        binding.recyclerView.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    /**
     * [BaseLibraryChildFragment.requestItemAtPos]
     * @param pos [Int]
     * @return [android.view.View]
     *
     * Return the [android.view.View] instance at [pos] of [BaseLibraryChildFragment.recyclerView]
     **/
    fun requestItemAtPos(pos: Int): View {
        return recyclerView.findViewHolderForAdapterPosition(pos)!!
            .itemView
    }

}