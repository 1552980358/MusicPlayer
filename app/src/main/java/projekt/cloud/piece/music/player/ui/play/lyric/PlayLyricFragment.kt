package projekt.cloud.piece.music.player.ui.play.lyric

import android.animation.ValueAnimator
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_AUDIO_ITEM
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_COLOR_ITEM
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_POSITION
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseActivity
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.database.audio.item.ColorItem
import projekt.cloud.piece.music.player.databinding.FragmentPlayLyricBinding
import projekt.cloud.piece.music.player.util.ColorUtil.isLight
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.LyricUtil.readLyric
import projekt.cloud.piece.music.player.util.LyricUtil.storeLyric
import projekt.cloud.piece.music.player.util.LyricUtil.toLyric

class PlayLyricFragment: BaseFragment() {

    companion object {
        private const val TAG = "PlayLyricFragment"
        private const val MIME_LYRIC = "*/*"
    }

    private var _binding: FragmentPlayLyricBinding? = null
    private val binding get() = _binding!!

    private var menuColor = WHITE

    private lateinit var menu: Menu

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayLyricBinding.inflate(inflater, container, false)
        binding.colorItem = containerViewModel.colorItem
        binding.textSize = getString(R.string.lyric_default_text_size)
        binding.position = containerViewModel.position
        binding.lyric = containerViewModel.audioItem?.run { requireContext().readLyric(id) }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        containerViewModel.register<AudioItem>(TAG, LABEL_AUDIO_ITEM) {
            binding.lyric = it?.run { requireContext().readLyric(id) }
        }
        containerViewModel.register<ColorItem>(TAG, LABEL_COLOR_ITEM) {
            binding.colorItem = it
            it?.background?.isLight?.apply {
                val color = if (this) BLACK else WHITE
                ValueAnimator.ofArgb(menuColor, color).apply {
                    duration = ANIMATION_DURATION
                    addUpdateListener { menu.forEach { it.icon.setTint(animatedValue as Int) } }
                }.start()
                menuColor = color
            }
        }
        containerViewModel.register<Long>(TAG, LABEL_POSITION) {
            binding.position = it
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.fragment_play_lyric, menu)
        this.menu = menu
        menuColor = if (containerViewModel.colorItem?.background?.isLight == true) BLACK else WHITE
        menu.forEach { it.icon.setTint(menuColor) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_pick_lyric -> containerViewModel.audioItem?.let { pickLyricFile(it) }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        containerViewModel.unregisterAll(TAG)
        super.onDestroyView()
        _binding = null
    }

    private fun pickLyricFile(audioItem: AudioItem) {
        (requireActivity() as? BaseActivity)?.getContent(MIME_LYRIC) { uri ->
            uri?.let { dataUri ->
                io {
                    @Suppress("BlockingMethodInNonBlockingContext")
                    requireContext().contentResolver.openInputStream(dataUri).use {
                        it?.bufferedReader()
                            ?.readLines()
                            ?.toLyric
                            ?.apply {
                                if (containerViewModel.audioItem == audioItem) {
                                    binding.lyric = this
                                }
                            }
                            ?.storeLyric(requireActivity(), audioItem.id)
                    }
                }
            }
        }
    }

}