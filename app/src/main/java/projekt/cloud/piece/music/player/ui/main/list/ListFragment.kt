package projekt.cloud.piece.music.player.ui.main.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.platform.MaterialContainerTransform
import projekt.cloud.piece.music.player.databinding.FragmentListBinding
import projekt.cloud.piece.music.player.ui.main.base.BaseMainFragment

class ListFragment: BaseMainFragment() {
    
    private var _binding: FragmentListBinding? = null
    private val binding: FragmentListBinding
        get() = _binding!!
    private val root: View
        get() = binding.root
    
    private val recyclerView: RecyclerView
        get() = binding.recyclerView
    
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListBinding.inflate(inflater)
        return root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewAdapter = RecyclerViewAdapter(recyclerView) {}
    }
    
}