package projekt.cloud.piece.music.player.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.transition.MaterialContainerTransform
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.databinding.FragmentPlayBinding
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION

/**
 * Class [PlayFragment], inherit to [BaseFragment]
 *
 * Getters:
 *   [binding]
 *   [root]
 *
 * Variables:
 *   [_binding]
 *
 * Methods:
 *   [onCreate]
 *   [onCreateView]
 *   [onDestroyView]
 *
 **/
class PlayFragment: BaseFragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private val root get() = binding.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = ANIMATION_DURATION
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}