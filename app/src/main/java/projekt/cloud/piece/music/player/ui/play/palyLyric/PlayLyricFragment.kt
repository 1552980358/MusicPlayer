package projekt.cloud.piece.music.player.ui.play.palyLyric

import android.animation.ObjectAnimator.ofArgb
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.FragmentPlayLyricBinding
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION
import projekt.cloud.piece.music.player.util.LyricUtil.loadLyric

class PlayLyricFragment: BaseFragment() {

    companion object {
        private const val TAG = "PlayLyricFragment"
    }

    private var _binding: FragmentPlayLyricBinding? = null
    private val binding get() = _binding!!
    private val recyclerLyricView get() = binding.recyclerLyricView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_play_lyric, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        io {
            activityViewModel.setAudioItemObserver(TAG) { updateAudioItem(it) }
            activityViewModel.setProgressObservers(TAG) { recyclerLyricView.updateProgress(it) }
        }
    }

    override fun onDestroyView() {
        activityViewModel.removeAllObservers(TAG)
        super.onDestroyView()
        _binding = null
    }

    private fun updateAudioItem(audioItem: AudioItem) = io {
        requireContext().loadLyric(audioItem.id).apply {
            recyclerLyricView.lyric = this
        }
        updateColor(audioItem)
    }

    private fun updateColor(audioItem: AudioItem) {
        with(activityViewModel.database.color.query(audioItem.id, audioItem.album)) {
            ofArgb(recyclerLyricView.primaryColor, primaryColor).apply {
                duration = ANIMATION_DURATION
                addUpdateListener { recyclerLyricView.primaryColor = animatedValue as Int }
                ui { start() }
            }
            ofArgb(recyclerLyricView.secondaryColor, secondaryColor).apply {
                duration = ANIMATION_DURATION
                addUpdateListener { recyclerLyricView.secondaryColor = animatedValue as Int }
                ui { start() }
            }
        }
    }

}