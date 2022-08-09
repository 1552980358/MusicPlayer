package projekt.cloud.piece.music.player.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.transition.platform.MaterialContainerTransform
import projekt.cloud.piece.music.player.MainActivityViewModel
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.databinding.FragmentPlayBinding

class PlayFragment: BaseFragment() {
    
    private var _binding: FragmentPlayBinding? = null
    private val binding: FragmentPlayBinding
        get() = _binding!!
    private val root get() = binding.root
    
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayBinding.inflate(inflater)
        return root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }
    
}