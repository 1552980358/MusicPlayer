package projekt.cloud.piece.music.player.ui.play.palyLyric

import android.animation.ObjectAnimator.ofArgb
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.FragmentPlayLyricBinding
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION
import projekt.cloud.piece.music.player.util.LyricUtil.decodeLyric
import projekt.cloud.piece.music.player.util.LyricUtil.loadLyric
import projekt.cloud.piece.music.player.util.LyricUtil.writeLyric

class PlayLyricFragment: BaseFragment() {

    companion object {
        private const val TAG = "PlayLyricFragment"
        private const val GET_CONTENT_LAUNCH_TYPE = "*/*"
    }

    private var _binding: FragmentPlayLyricBinding? = null
    private val binding get() = _binding!!
    private val recyclerLyricView get() = binding.recyclerLyricView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_play_lyric, container, false)
        activityViewModel.setGetContentCallback { uri ->
            activityViewModel.audioItem?.let { audioItem ->
                tryOnly {
                    requireActivity().contentResolver.openInputStream(uri)
                        ?.bufferedReader()
                        ?.readLines()
                        ?.decodeLyric
                        ?.let { lyric ->
                            requireContext().writeLyric(audioItem.id, lyric)
                            recyclerLyricView.lyric = lyric
                        }
                }
            }

        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        io {
            activityViewModel.setAudioItemObserver(TAG) { updateAudioItem(it) }
            activityViewModel.setProgressObservers(TAG) { recyclerLyricView.updateProgress(it) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_play_lyric, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_lyric -> activityViewModel.getContent.launch(GET_CONTENT_LAUNCH_TYPE)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        activityViewModel.removeAllObservers(TAG)
        super.onDestroyView()
        _binding = null
    }

    private fun updateAudioItem(audioItem: AudioItem) = io {
        requireContext().loadLyric(audioItem.id).let { ui { recyclerLyricView.lyric = it } }
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