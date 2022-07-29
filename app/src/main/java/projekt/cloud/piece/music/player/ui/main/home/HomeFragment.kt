package projekt.cloud.piece.music.player.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import projekt.cloud.piece.music.player.databinding.FragmentHomeBinding

class HomeFragment: Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!
    
    private val root: CoordinatorLayout
        get() = binding.root
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    
    }

}