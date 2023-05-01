package projekt.cloud.piece.music.player.ui.fragment.library.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.base.ViewBindingInflater
import projekt.cloud.piece.music.player.databinding.FragmentLibraryObjectBinding

abstract class BaseLibraryObjectFragment: BaseFragment<FragmentLibraryObjectBinding>() {

    override val viewBindingInflater: ViewBindingInflater<FragmentLibraryObjectBinding>
        get() = FragmentLibraryObjectBinding::inflate

    protected val recyclerView: RecyclerView
        get() = binding.recyclerView

    fun findItemViewOfPos(pos: Int): View {
        return recyclerView.findViewHolderForAdapterPosition(pos)!!
            .itemView
    }

}