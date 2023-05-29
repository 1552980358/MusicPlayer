package projekt.cloud.piece.cloudy.ui.fragment.library

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.cloudy.base.BaseLayoutAdapter
import projekt.cloud.piece.cloudy.base.BaseMultiLayoutFragment
import projekt.cloud.piece.cloudy.databinding.LibraryChildFragmentBinding
import projekt.cloud.piece.cloudy.R
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
        get() = binding.recyclerView

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