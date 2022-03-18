package projekt.cloud.piece.music.player.ui.play.playControl

import android.animation.ValueAnimator.ofArgb
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import lib.github1552980358.ktExtension.android.view.heightF
import lib.github1552980358.ktExtension.android.view.widthF
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.FragmentPlayControlBinding
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_REPEAT
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_REPEAT_ONE
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_SHUFFLE
import projekt.cloud.piece.music.player.service.play.Config.getConfig
import projekt.cloud.piece.music.player.service.play.Config.setConfig
import projekt.cloud.piece.music.player.ui.play.playControl.util.RecyclerViewAdapterUtil
import projekt.cloud.piece.music.player.util.ActivityUtil.pixelHeight
import projekt.cloud.piece.music.player.util.ColorUtil.isLight
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArtRaw
import projekt.cloud.piece.music.player.util.ImageUtil.loadAudioArtRaw

class PlayControlFragment: BaseFragment() {

    companion object {
        private const val TAG = "PlayControlFragment"
    }

    private var _binding: FragmentPlayControlBinding? = null
    private val binding get() = _binding!!
    private val contentControl get() = binding.contentControlPlayControl
    private val bottom get() = binding.contentBottomPlayControl
    private val imageViewCycle get() = contentControl.imageViewCycle
    private val imageViewPrev get() = contentControl.imageViewPrev
    private val imageViewNext get() = contentControl.imageViewNext
    private val imageViewShuffle get() = contentControl.imageViewShuffle
    private val recyclerView get() = bottom.recyclerView

    private val database get() = activityViewModel.database
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private val transportControls get() = activityViewModel.mediaControllerCompat.transportControls

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_play_control, container, false)
        binding.imageViewImage.apply {
            layoutParams = layoutParams.apply { height = resources.displayMetrics.widthPixels }
        }
        val bottomHeight = requireActivity().pixelHeight - resources.displayMetrics.widthPixels
        contentControl.root.apply {
            layoutParams = layoutParams.apply { height = bottomHeight * 2 / 5 }
        }
        bottomSheetBehavior = BottomSheetBehavior.from(bottom.root)
        bottomSheetBehavior.peekHeight = bottomHeight * 3 / 5
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activityViewModel.setAudioItemObserver(TAG) { audioItem -> updateAudioItem(audioItem) }
        activityViewModel.setPlayStateObserver(TAG) { isPlaying ->
            if (contentControl.isPlaying != isPlaying) {
                contentControl.isPlaying = isPlaying
            }
        }
        activityViewModel.setProgressObservers(TAG) { progress -> contentControl.progress = progress }
        activityViewModel.setPlayConfigObserver(TAG) { playConfig -> contentControl.playConfig = playConfig }

        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(recyclerView) {

        }
        activityViewModel.setPlaylistObserver(TAG) { playlist -> recyclerViewAdapterUtil.playlist = playlist }
        activityViewModel.getPlaylistSync()

        contentControl.floatingActionButton.setOnClickListener {
            when {
                activityViewModel.isPlaying -> transportControls.pause()
                else -> transportControls.play()
            }
        }

        @Suppress("ClickableViewAccessibility")
        contentControl.linearLayout.setOnTouchListener { _, motionEvent ->

            when (motionEvent.action) {
                ACTION_DOWN -> {
                    (contentControl.root.background as RippleDrawable).setHotspot(
                        motionEvent.x + contentControl.linearLayout.x,
                        motionEvent.y + contentControl.linearLayout.y
                    )
                    contentControl.root.isPressed = true
                }
                ACTION_CANCEL -> contentControl.root.isPressed = false
                ACTION_UP -> {
                    contentControl.root.isPressed = false
                    val rawX = motionEvent.rawX
                    val rawY = motionEvent.rawY
                    val rawAxis = IntArray(2)
                    when {

                        compareAxis(rawX, rawY, imageViewCycle, rawAxis) -> {
                            var playConfig = activityViewModel.playConfig
                            val repeat = playConfig.getConfig(PLAY_CONFIG_REPEAT)
                            val repeatOne = playConfig.getConfig(PLAY_CONFIG_REPEAT_ONE)
                            when {
                                repeat && !repeatOne ->
                                    playConfig = playConfig.setConfig(PLAY_CONFIG_REPEAT, false)
                                        .setConfig(PLAY_CONFIG_REPEAT_ONE, true)
                                !repeat && repeatOne ->
                                    playConfig = playConfig.setConfig(PLAY_CONFIG_REPEAT_ONE, false)
                                !repeat && !repeatOne ->
                                    playConfig = playConfig.setConfig(PLAY_CONFIG_REPEAT, true)
                            }
                            activityViewModel.updatePlayConfig(playConfig)
                        }

                        compareAxis(rawX, rawY, imageViewPrev, rawAxis) ->
                            transportControls.skipToPrevious()

                        compareAxis(rawX, rawY, imageViewNext, rawAxis) ->
                            transportControls.skipToNext()

                        compareAxis(rawX, rawY, imageViewShuffle, rawAxis) -> {
                            recyclerViewAdapterUtil.hasShuffled = true
                            activityViewModel.updatePlayConfig(
                                activityViewModel.playConfig.run { setConfig(PLAY_CONFIG_SHUFFLE, !getConfig(PLAY_CONFIG_SHUFFLE)) }
                            )
                        }

                    }
                }
            }

            true
        }

    }

    override fun onBackPressed(): Boolean {
        if (bottomSheetBehavior.state != STATE_COLLAPSED) {
            bottomSheetBehavior.state = STATE_COLLAPSED
            return false
        }
        return super.onBackPressed()
    }

    private fun updateAudioItem(audioItem: AudioItem) = io {
        val coverArt = requireContext().loadAudioArtRaw(audioItem.id)
            ?: requireContext().loadAlbumArtRaw(audioItem.album)
            ?: activityViewModel.defaultCoverArt
        ui {
            binding.imageBitmap = coverArt
            bottom.audioItem = audioItem
            contentControl.duration = audioItem.duration
        }
        database.color.query(audioItem.id, audioItem.album).apply {
            updateColor(backgroundColor, primaryColor, secondaryColor)
            with(backgroundColor.isLight) {
                when (contentControl.iconTintColor) {
                    null -> contentControl.iconTintColor = if (this) BLACK else WHITE
                    else -> when {
                        this -> if (contentControl.iconTintColor != BLACK) {
                            ofArgb(WHITE, BLACK).apply {
                                duration = ANIMATION_DURATION
                                addUpdateListener { contentControl.iconTintColor = animatedValue as Int }
                                ui { start() }
                            }
                        }
                        else -> if (contentControl.iconTintColor != WHITE) {
                            ofArgb(BLACK, WHITE).apply {
                                duration = ANIMATION_DURATION
                                addUpdateListener { contentControl.iconTintColor = animatedValue as Int }
                                ui { start() }
                            }
                        }
                    }
                }
            }
        }
        activityViewModel.getPlaylistSync()
    }

    private fun updateColor(backgroundColor: Int, primaryColor: Int, secondaryColor: Int) {
        when (contentControl.circleColor) {
            null -> contentControl.circleColor = backgroundColor
            else -> with(ofArgb(contentControl.circleColor!!, backgroundColor)) {
                duration = ANIMATION_DURATION
                addUpdateListener { contentControl.circleColor = animatedValue as Int }
                ui { start() }
            }
        }
        when (contentControl.primaryColor) {
            null -> contentControl.primaryColor = primaryColor
            else -> with(ofArgb(contentControl.primaryColor!!, primaryColor)) {
                duration = ANIMATION_DURATION
                addUpdateListener { contentControl.primaryColor = animatedValue as Int }
                ui { start() }
            }
        }
        when (contentControl.secondaryColor) {
            null -> contentControl.secondaryColor = secondaryColor
            else -> with(ofArgb(contentControl.secondaryColor!!, primaryColor)) {
                duration = ANIMATION_DURATION
                addUpdateListener { contentControl.secondaryColor = animatedValue as Int }
                ui { start() }
            }
        }
    }

    override fun onDestroyView() {
        activityViewModel.setAudioItemObserver(TAG)
        activityViewModel.setAudioItemObserver(TAG)
        activityViewModel.setPlayStateObserver(TAG)
        activityViewModel.setProgressObservers(TAG)
        activityViewModel.setPlayConfigObserver(TAG)
        activityViewModel.setPlaylistObserver(TAG)
        activityViewModel.playList = null
        super.onDestroyView()
    }

    private fun compareAxis(x: Float, y: Float, view: View, rawAxis: IntArray): Boolean {
        view.getLocationOnScreen(rawAxis)
        return compareAxis(x, rawAxis.first().toFloat(), view.widthF) && compareAxis(y, rawAxis.last().toFloat(), view.heightF)
    }

    private fun compareAxis(axis: Float, targetX: Float, length: Float) =
        axis in (targetX .. targetX + length)

}