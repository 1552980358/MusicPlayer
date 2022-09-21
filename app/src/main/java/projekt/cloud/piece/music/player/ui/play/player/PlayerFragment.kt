package projekt.cloud.piece.music.player.ui.play.player

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.GravityCompat.END
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.music.player.MainActivityViewModel
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.databinding.FragmentPlayerBinding
import projekt.cloud.piece.music.player.service.play.ServiceConstants.CUSTOM_ACTION_REPEAT_MODE
import projekt.cloud.piece.music.player.service.play.ServiceConstants.CUSTOM_ACTION_SHUFFLE_MODE
import projekt.cloud.piece.music.player.widget.PlaybackStateButton
import projekt.cloud.piece.music.player.widget.ProgressSeekbar
import projekt.cloud.piece.music.player.widget.RepeatButton
import projekt.cloud.piece.music.player.widget.ShuffleButton

class PlayerFragment: BaseFragment(), View.OnClickListener {
    
    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding
        get() = _binding!!
    private val root get() = binding.root
    
    private val progressSeekbar: ProgressSeekbar
        get() = binding.progressSeekbar
    private val repeatButton: RepeatButton
        get() = binding.repeatButton
    private val playbackStateButton: PlaybackStateButton
        get() = binding.playbackStateButton
    private val appCompatImageButtonPrev: AppCompatImageButton
        get() = binding.appCompatImageButtonPrev
    private val appCompatImageButtonNext: AppCompatImageButton
        get() = binding.appCompatImageButtonNext
    private val shuffleButton: ShuffleButton
        get() = binding.shuffleButton
    private val recyclerView: RecyclerView
        get() = binding.recyclerView
    
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var playingQueueAdapter: PlayingQueueAdapter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayerBinding.inflate(inflater)
        binding.viewModel = activityViewModel
        binding.lifecycleOwner = this
        return root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progressSeekbar.setProgressControlledListener { position, isReleased ->
            if (isReleased) {
                binding.isTouched = false
                return@setProgressControlledListener seekTo(position)
            }
            if (binding.isTouched != true) {
                binding.isTouched = true
            }
            binding.touchedPosition = position
        }
        repeatButton.setOnClickListener(this)
        playbackStateButton.setOnClickListener(this)
        appCompatImageButtonPrev.setOnClickListener(this)
        appCompatImageButtonNext.setOnClickListener(this)
        shuffleButton.setOnClickListener(this)
        
        playingQueueAdapter = PlayingQueueAdapter(recyclerView) { playAudio(it.id) }
        activityViewModel.playingQueue.observe(viewLifecycleOwner) {
            playingQueueAdapter.updateAudioMetadataList(it)
        }
        activityViewModel.playingPosition.observe(viewLifecycleOwner) {
            playingQueueAdapter.updatePlayingPosition(it)
            recyclerView.smoothScrollToPosition(it)
        }
    }
    
    override fun onClick(v: View?) {
        when (v) {
            repeatButton -> sendCustomAction(CUSTOM_ACTION_REPEAT_MODE)
            playbackStateButton -> when (playbackStateButton.switchPlaybackState()) {
                STATE_PLAYING -> play()
                STATE_PAUSED -> pause()
            }
            appCompatImageButtonPrev -> skipToPrevious()
            appCompatImageButtonNext -> skipToNext()
            shuffleButton -> sendCustomAction(CUSTOM_ACTION_SHUFFLE_MODE)
        }
    }
    
    override fun onBackPressed(): Boolean {
        if (root.isDrawerOpen(END)) {
            root.closeDrawer(END)
            return false
        }
        return true
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}