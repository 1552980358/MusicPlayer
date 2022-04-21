package projekt.cloud.piece.music.player.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.transition.MaterialContainerTransform
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.base.BasePagerViewModel
import projekt.cloud.piece.music.player.databinding.FragmentPlayBinding
import projekt.cloud.piece.music.player.ui.play.control.PlayControlFragment
import projekt.cloud.piece.music.player.ui.play.lyric.PlayLyricFragment
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

    class PlayFragmentViewModel: BasePagerViewModel<BaseFragment>() {
        override fun setFragments() =
            arrayOf(PlayControlFragment(), PlayLyricFragment())
    }

    private lateinit var viewModel: PlayFragmentViewModel

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private val root get() = binding.root
    private val viewPager2 get() = binding.viewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = ANIMATION_DURATION
        }
        viewModel = ViewModelProvider(requireActivity())[PlayFragmentViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.setUpViewPager2(this, viewPager2)
    }

    override fun onBackPressed() =
        viewModel[viewPager2.currentItem].canReturn

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}