package projekt.cloud.piece.music.player.ui.play.player

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.activityViewModels
import projekt.cloud.piece.music.player.MainActivityViewModel
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.databinding.FragmentPlayerBinding
import projekt.cloud.piece.music.player.service.play.ServiceConstants.CUSTOM_ACTION_SHUFFLE_MODE
import projekt.cloud.piece.music.player.widget.PlaybackStateButton
import projekt.cloud.piece.music.player.widget.ShuffleButton

class PlayerFragment: BaseFragment(), View.OnClickListener {
    
    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding
        get() = _binding!!
    private val root get() = binding.root
    
    private val playbackStateButton: PlaybackStateButton
        get() = binding.playbackStateButton
    private val appCompatImageButtonPrev: AppCompatImageButton
        get() = binding.appCompatImageButtonPrev
    private val appCompatImageButtonNext: AppCompatImageButton
        get() = binding.appCompatImageButtonNext
    private val shuffleButton: ShuffleButton
        get() = binding.shuffleButton
    
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayerBinding.inflate(inflater)
        binding.viewModel = activityViewModel
        binding.lifecycleOwner = this
        return root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playbackStateButton.setOnClickListener(this)
        appCompatImageButtonPrev.setOnClickListener(this)
        appCompatImageButtonNext.setOnClickListener(this)
        shuffleButton.setOnClickListener(this)
    }
    
    override fun onClick(v: View?) {
        when (v) {
            playbackStateButton -> when (playbackStateButton.switchPlaybackState()) {
                STATE_PLAYING -> play()
                STATE_PAUSED -> pause()
            }
            appCompatImageButtonPrev -> skipToPrevious()
            appCompatImageButtonNext -> skipToNext()
            shuffleButton -> sendCustomAction(CUSTOM_ACTION_SHUFFLE_MODE)
        }
    }

}