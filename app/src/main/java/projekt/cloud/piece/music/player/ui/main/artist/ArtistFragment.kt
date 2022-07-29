package projekt.cloud.piece.music.player.ui.main.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import projekt.cloud.piece.music.player.databinding.FragmentArtistBinding

class ArtistFragment: Fragment() {
    
    private var _binding: FragmentArtistBinding? = null
    private val binding: FragmentArtistBinding
        get() = _binding!!
    
    private val root: CoordinatorLayout
        get() = binding.root
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentArtistBinding.inflate(inflater, container, false)
        return root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    
    }

}