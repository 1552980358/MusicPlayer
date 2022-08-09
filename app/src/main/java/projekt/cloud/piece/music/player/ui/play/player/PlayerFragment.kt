package projekt.cloud.piece.music.player.ui.play.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import projekt.cloud.piece.music.player.MainActivityViewModel
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.databinding.FragmentPlayerBinding

class PlayerFragment: BaseFragment() {
    
    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding
        get() = _binding!!
    private val root get() = binding.root
    
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayerBinding.inflate(inflater)
        binding.viewModel = activityViewModel
        binding.lifecycleOwner = this
        return root
    }

}