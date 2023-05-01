package projekt.cloud.piece.music.player.ui.fragment.artist

import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.platform.MaterialContainerTransform
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.base.OnBackPressedListener
import projekt.cloud.piece.music.player.base.ViewBindingInflater
import projekt.cloud.piece.music.player.databinding.FragmentArtistBinding

class ArtistFragment: BaseFragment<FragmentArtistBinding>() {

    override val viewBindingInflater: ViewBindingInflater<FragmentArtistBinding>
        get() = FragmentArtistBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = TRANSPARENT
        }
    }

    override val onBackPressed: OnBackPressedListener
        get() = {
            val args: ArtistFragmentArgs by navArgs()
            setFragmentResult(
                getString(R.string.library_transition),
                bundleOf(
                    getString(R.string.library_transition) to getString(R.string.library_transition_artist),
                    getString(R.string.library_transition_pos) to args.pos
                )
            )
            findNavController().navigateUp()
            true
        }

}