package projekt.cloud.piece.music.player.ui.play.palyLyric

import android.animation.ObjectAnimator.ofArgb
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.util.TypedValue.applyDimension
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import lib.github1552980358.ktExtension.androidx.fragment.app.show
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.FragmentPlayLyricBinding
import projekt.cloud.piece.music.player.ui.dialog.LyricTextSizeDialogFragment
import projekt.cloud.piece.music.player.ui.dialog.LyricTextSizeDialogFragment.Companion.DEFAULT
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

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

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
        if (sharedPreferences.contains(getString(R.string.key_lyric_text_size))) {
            sharedPreferences.getString(getString(R.string.key_lyric_text_size), DEFAULT)?.let {
                updateLyricTextSize(it)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerLyricView.onClickListener = {
            activityViewModel.mediaControllerCompat.transportControls.seekTo(it.time)
        }
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
            R.id.menu_lyric_text_size -> LyricTextSizeDialogFragment()
                .setSharedPreferences(sharedPreferences)
                .setOnChange { newValue -> newValue?.let { updateLyricTextSize(it) } }
                .show(requireActivity())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        activityViewModel.removeAllObservers(TAG)
        super.onDestroyView()
        _binding = null
    }

    private fun updateLyricTextSize(dpStr: String) {
        recyclerLyricView.textSize = applyDimension(COMPLEX_UNIT_SP, dpStr.toFloat(), resources.displayMetrics)
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