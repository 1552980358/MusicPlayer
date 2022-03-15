package projekt.cloud.piece.music.player.ui.play.playCover

import android.animation.ValueAnimator.ofArgb
import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.delay
import lib.github1552980358.ktExtension.android.content.getStatusBarHeight
import lib.github1552980358.ktExtension.android.view.heightF
import lib.github1552980358.ktExtension.android.view.widthF
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BasePlayFragment
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.FragmentPlayCoverBinding
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_REPEAT
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_REPEAT_ONE
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_SHUFFLE
import projekt.cloud.piece.music.player.service.play.Config.getConfig
import projekt.cloud.piece.music.player.service.play.Config.setConfig
import projekt.cloud.piece.music.player.ui.play.util.FragmentManager
import projekt.cloud.piece.music.player.ui.play.playCover.util.RecyclerViewAdapterUtil
import projekt.cloud.piece.music.player.util.ActivityUtil.pixelHeight
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION_HALF_LONG
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION_LONG
import projekt.cloud.piece.music.player.util.ContextUtil.navigationBarHeight
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArtRaw
import projekt.cloud.piece.music.player.util.ImageUtil.loadAudioArtRaw

class PlayCoverFragment(FragmentManager: FragmentManager): BasePlayFragment(FragmentManager) {

    private var _binding: FragmentPlayCoverBinding? = null
    private val binding get() = _binding!!
    private val contentPlayCoverFragmentBottomSheet get() =
        binding.contentPlayCoverFragmentBottomSheet
    private val contentPlayCoverFragmentButtons get() =
        binding.contentPlayCoverFragmentButtons

    private val imageViewCycle get() = contentPlayCoverFragmentButtons.imageViewCycle

    private val imageViewPrev get() = contentPlayCoverFragmentButtons.imageViewPrev
    private val imageViewNext get() = contentPlayCoverFragmentButtons.imageViewNext
    private val imageViewShuffle get() = contentPlayCoverFragmentButtons.imageViewShuffle
    
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()

        requireActivity().window.statusBarColor = Color.TRANSPARENT
        requireActivity().window.navigationBarColor = Color.TRANSPARENT
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_play_cover, container, false)

        playFragmentManager.setUpPlayCover(
            updateAudioItem = { updateAudioItemCoroutine(it) },
            updatePlayConfig = { contentPlayCoverFragmentButtons.playConfig = it },
            updateProgress = { contentPlayCoverFragmentButtons.progress = it },
            updatePlayState = {
                if (contentPlayCoverFragmentButtons.isPlaying != it) {
                    contentPlayCoverFragmentButtons.isPlaying = it
                }
            },
            updateColor = { isLight, backgroundColor, primaryColor: Int, secondaryColor ->
                when (contentPlayCoverFragmentButtons.iconTintColor) {
                    null -> contentPlayCoverFragmentButtons.iconTintColor = when {
                        isLight -> BLACK
                        else -> WHITE
                    }
                    else -> when {
                        isLight -> if (contentPlayCoverFragmentButtons.iconTintColor != BLACK) {
                            ofArgb(contentPlayCoverFragmentButtons.iconTintColor!!, BLACK).apply {
                                duration = ANIMATION_DURATION_LONG
                                addUpdateListener { contentPlayCoverFragmentButtons.iconTintColor = animatedValue as Int }
                            }.start()
                        }
                        else -> if (contentPlayCoverFragmentButtons.iconTintColor != WHITE) {
                            ofArgb(contentPlayCoverFragmentButtons.iconTintColor!!, WHITE).apply {
                                duration = ANIMATION_DURATION_LONG
                                addUpdateListener { contentPlayCoverFragmentButtons.iconTintColor = animatedValue as Int }
                            }.start()
                        }
                    }
                }
                when (contentPlayCoverFragmentButtons.primaryColor) {
                    null -> {
                        contentPlayCoverFragmentButtons.circleColor = backgroundColor
                        contentPlayCoverFragmentButtons.primaryColor = primaryColor
                        contentPlayCoverFragmentButtons.secondaryColor = secondaryColor
                    }
                    else -> {
                        ofArgb(contentPlayCoverFragmentButtons.circleColor!!, backgroundColor).apply {
                            duration = ANIMATION_DURATION_LONG
                            addUpdateListener { contentPlayCoverFragmentButtons.circleColor = animatedValue as Int }
                        }.start()
                        ofArgb(contentPlayCoverFragmentButtons.primaryColor!!, primaryColor).apply {
                            duration = ANIMATION_DURATION_LONG
                            addUpdateListener { contentPlayCoverFragmentButtons.primaryColor = animatedValue as Int }
                        }.start()
                        ofArgb(contentPlayCoverFragmentButtons.secondaryColor!!, secondaryColor).apply {
                            duration = ANIMATION_DURATION_LONG
                            addUpdateListener { contentPlayCoverFragmentButtons.secondaryColor = animatedValue as Int }
                        }.start()
                    }
                }
            },
            updateAudioList = {
                when {
                    ::recyclerViewAdapterUtil.isInitialized ->
                        recyclerViewAdapterUtil.audioList = it
                    else -> recyclerViewAdapterUtil = RecyclerViewAdapterUtil(contentPlayCoverFragmentBottomSheet.recyclerView, it) { index ->
                        playFragmentManager.skipToQueueItem(index)
                    }
                }
            }
        )

        binding.imageView.apply {
            layoutParams = layoutParams.apply {
                height = resources.displayMetrics.widthPixels
            }
        }

        contentPlayCoverFragmentBottomSheet.cardView.apply {
            layoutParams = layoutParams.apply {
                height = pixelHeight - requireContext().getStatusBarHeight()
            }
            setContentPadding(0, 0, 0, navigationBarHeight)
        }

        val bottomHeight = pixelHeight - resources.displayMetrics.widthPixels

        contentPlayCoverFragmentButtons.linearLayout.apply {
            layoutParams = layoutParams.apply { height = bottomHeight * 2 / 5 }
        }

        bottomSheetBehavior = BottomSheetBehavior.from(contentPlayCoverFragmentBottomSheet.cardView)
        with(bottomSheetBehavior) {
            peekHeight = bottomHeight * 3 / 5
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        contentPlayCoverFragmentBottomSheet.relativeLayout.setOnClickListener {
            bottomSheetBehavior.state = when (bottomSheetBehavior.state) {
                BottomSheetBehavior.STATE_COLLAPSED -> BottomSheetBehavior.STATE_EXPANDED
                else -> BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        contentPlayCoverFragmentButtons.floatingActionButton.setOnClickListener {
            when {
                playFragmentManager.isPlaying -> playFragmentManager.pause()
                else -> playFragmentManager.play()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playFragmentManager.audioItem?.let { updateAudioItemCoroutine(it) }
        contentPlayCoverFragmentButtons.isPlaying = playFragmentManager.isPlaying
        contentPlayCoverFragmentButtons.playConfig = playFragmentManager.playConfig

        @Suppress("ClickableViewAccessibility")
        contentPlayCoverFragmentButtons.linearLayout.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.relativeLayout.apply {
                        (background as RippleDrawable).setHotspot(motionEvent.x, motionEvent.y)
                        isPressed = true
                    }
                }
                MotionEvent.ACTION_UP -> {
                    binding.relativeLayout.isPressed = false
                    val touchX = motionEvent.rawX
                    val touchY = motionEvent.rawY
                    val rawAxis = IntArray(2)
                    when {
                        compareAxis(touchX, touchY, imageViewCycle, rawAxis) -> {
                            playFragmentManager.playConfig.let { playConfig ->
                                Log.e("PLAY_COVER", playConfig.toString())
                                val repeat = playConfig.getConfig(PLAY_CONFIG_REPEAT)
                                val repeatOne = playConfig.getConfig(PLAY_CONFIG_REPEAT_ONE)
                                when {
                                    // Repeat with list
                                    repeat && !repeatOne -> playFragmentManager.requestUpdatePlayConfig(
                                        playConfig.setConfig(PLAY_CONFIG_REPEAT, false)
                                            .setConfig(PLAY_CONFIG_REPEAT_ONE, true)
                                    )
                                    // Repeat with single audio
                                    !repeat && repeatOne ->
                                        playFragmentManager.requestUpdatePlayConfig(playConfig.setConfig(PLAY_CONFIG_REPEAT_ONE, false))
                                    // Play list, no repeat
                                    !repeat && !repeatOne ->
                                        playFragmentManager.requestUpdatePlayConfig(playConfig.setConfig(PLAY_CONFIG_REPEAT, true))
                                }
                            }
                        }

                        compareAxis(touchX, touchY, imageViewPrev, rawAxis) ->
                            playFragmentManager.skipToPrevious()

                        compareAxis(touchX, touchY, imageViewNext, rawAxis) ->
                            playFragmentManager.skipToNext()

                        compareAxis(touchX, touchY, imageViewShuffle, rawAxis) -> {
                            Log.e("PlayCover", "Shuffle")
                            playFragmentManager.playConfig.let { playConfig ->
                                recyclerViewAdapterUtil.hasShuffledUpdated = true
                                playFragmentManager.requestUpdatePlayConfig(
                                    playConfig.setConfig(PLAY_CONFIG_SHUFFLE, !playConfig.getConfig(PLAY_CONFIG_SHUFFLE))
                                )
                            }
                        }
                    }
                }
                MotionEvent.ACTION_CANCEL -> { binding.relativeLayout.isPressed = false }
            }
            return@setOnTouchListener true
        }

        playFragmentManager.requestPlaylist()
    }

    override fun onStart() {
        super.onStart()

        io {
            delay(ANIMATION_DURATION_HALF_LONG)
            ui {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                bottomSheetBehavior.isHideable = false
            }
        }
    }

    private fun compareAxis(x: Float, y: Float, view: View, rawAxis: IntArray): Boolean {
        view.getLocationOnScreen(rawAxis)
        return compareAxis(x, rawAxis.first().toFloat(), view.widthF) && compareAxis(y, rawAxis.last().toFloat(), view.heightF)
    }

    private fun compareAxis(axis: Float, targetX: Float, length: Float) =
        axis in (targetX .. targetX + length)

    private fun updateAudioItemCoroutine(audioItem: AudioItem) = io { updateAudioItem(audioItem) }

    private fun updateAudioItem(audioItem: AudioItem) {
        val bitmap = requireContext().loadAudioArtRaw(audioItem.id)
            ?: requireContext().loadAlbumArtRaw(audioItem.album)
            ?: playFragmentManager.defaultCoverImage
        ui {
            contentPlayCoverFragmentButtons.duration = audioItem.duration
            contentPlayCoverFragmentBottomSheet.audioItem = audioItem
            binding.coverImage = bitmap
        }
    }

}