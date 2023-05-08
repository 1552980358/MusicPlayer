package projekt.cloud.piece.music.player.ui.fragment.library.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.music.player.base.BaseLayoutCompat
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.base.ViewBindingInflater
import projekt.cloud.piece.music.player.databinding.FragmentLibraryObjectBinding

abstract class BaseLibraryObjectFragment<LC>: BaseMultiDensityFragment<FragmentLibraryObjectBinding, LC>()
        where LC: BaseLayoutCompat<FragmentLibraryObjectBinding> {

    override val viewBindingInflater: ViewBindingInflater<FragmentLibraryObjectBinding>
        get() = FragmentLibraryObjectBinding::inflate

    private val recyclerView: RecyclerView
        get() = binding.recyclerView

    fun findItemViewOfPos(pos: Int): View {
        return recyclerView.findViewHolderForAdapterPosition(pos)!!
            .itemView
    }

}