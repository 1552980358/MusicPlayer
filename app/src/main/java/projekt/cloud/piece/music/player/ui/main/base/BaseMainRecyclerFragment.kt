package projekt.cloud.piece.music.player.ui.main.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.music.player.databinding.FragmentBaseMainRecyclerBinding

open class BaseMainRecyclerFragment: BaseMainFragment() {
    
    private var _binding: FragmentBaseMainRecyclerBinding? = null
    protected val binding: FragmentBaseMainRecyclerBinding
        get() = _binding!!
    private val root: CoordinatorLayout
        get() = binding.root
    protected val recyclerView: RecyclerView
        get() = binding.recyclerView
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBaseMainRecyclerBinding.inflate(inflater)
        return root
    }
    
}