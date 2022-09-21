package projekt.cloud.piece.music.player.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.transition.platform.MaterialContainerTransform
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.base.BaseHostFragment
import projekt.cloud.piece.music.player.databinding.FragmentPlayBinding

class PlayFragment: BaseHostFragment() {
    
    private var _binding: FragmentPlayBinding? = null
    private val binding: FragmentPlayBinding
        get() = _binding!!
    private val root get() = binding.root
    private val fragmentContainerView: FragmentContainerView
        get() = binding.fragmentContainerView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayBinding.inflate(inflater)
        return root
    }
    
    override fun onBackPressed() = (fragmentContainerView.getFragment<NavHostFragment>()
        .childFragmentManager
        .fragments
        .first() as? BaseFragment)
        ?.onBackPressed() != false
    
}