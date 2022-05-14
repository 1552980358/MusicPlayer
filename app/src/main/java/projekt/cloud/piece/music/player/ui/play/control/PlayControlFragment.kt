package projekt.cloud.piece.music.player.ui.play.control

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_NONE
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ONE
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_NONE
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.os.bundleOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_AUDIO_ITEM
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_BITMAP_ART
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_COLOR_ITEM
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_IS_PLAYING
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_POSITION
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_REPEAT_MODE
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_SHUFFLE_MODE
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.database.audio.item.ColorItem
import projekt.cloud.piece.music.player.databinding.FragmentPlayControlBinding
import projekt.cloud.piece.music.player.service.play.Actions.ACTION_UPDATE_REPEAT_MODE
import projekt.cloud.piece.music.player.service.play.Actions.ACTION_UPDATE_SHUFFLE_MODE
import projekt.cloud.piece.music.player.service.play.Extras.EXTRA_REPEAT_MODE
import projekt.cloud.piece.music.player.service.play.Extras.EXTRA_SHUFFLE_MODE
import projekt.cloud.piece.music.player.ui.play.dialog.SleepTimerDialogFragment
import projekt.cloud.piece.music.player.ui.play.dialog.SleepTimerDialogFragment.Companion.EXTRA_VALUE
import projekt.cloud.piece.music.player.util.ColorUtil.isLight
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.DialogFragmentUtil.showNow
import projekt.cloud.piece.music.player.util.TimeUtil.minToMills

class PlayControlFragment: BaseFragment(), OnClickListener {

    companion object {
        private const val TAG = "PlayControlFragment"
    }

    private var _binding: FragmentPlayControlBinding? = null
    private val binding get() = _binding!!
    private val root get() = binding.root
    private val buttonPlayControl get() = binding.buttonsPlayControl
    private val appCompatImageButtonRepeat get() = buttonPlayControl.appCompatImageButtonRepeat
    private val appCompatImageButtonPrev get() = buttonPlayControl.appCompatImageButtonPrev
    private val appCompatImageButtonNext get() = buttonPlayControl.appCompatImageButtonNext
    private val appCompatImageButtonShuffle get() = buttonPlayControl.appCompatImageButtonShuffle
    private val appCompatImageButtonSleep get() = buttonPlayControl.appCompatImageButtonSleep
    private val positionPlayControl get() = binding.positionPlayControl
    private val progressBar get() = positionPlayControl.progressBar
    
    private val floatingActionButton get() = binding.buttonsPlayControl.floatingActionButton

    private val transportControls get() = requireActivity().mediaController.transportControls
    
    private var sleepTimerJob: Job? = null
    private var sleepCountDownMillis: String? = null
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayControlBinding.inflate(layoutInflater, container, false)
        with(binding) {
            imageBitmap = containerViewModel.bitmapArt
            audioItem = containerViewModel.audioItem
            containerViewModel.colorItem?.let {
                backgroundColor = it.background
                primaryColor = it.primary
                secondaryColor = it.secondary
                buttonColor = when {
                    it.background.isLight -> BLACK
                    else -> WHITE
                }
                rippleColorStateList = ColorStateList.valueOf(it.primary)
            }
            isControlling = false
            position = containerViewModel.position
            repeatMode = containerViewModel.repeatMode
            shuffleMode = containerViewModel.shuffleMode
            isSleepTimerEnabled = false
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        containerViewModel.register<AudioItem>(TAG, LABEL_AUDIO_ITEM) {
            binding.audioItem = it
        }
        containerViewModel.register<Bitmap>(TAG, LABEL_BITMAP_ART) {
            binding.imageBitmap = it
        }
        containerViewModel.register<ColorItem>(TAG, LABEL_COLOR_ITEM) { colorItem ->
            colorItem?.let {
                updateColorsAnimated(it.background, it.primary, it.secondary)
                binding.rippleColorStateList = ColorStateList.valueOf(it.primary)
            }
        }
        containerViewModel.register<Long>(TAG, LABEL_POSITION) {
            binding.position = it
        }
        containerViewModel.register<Int>(TAG, LABEL_REPEAT_MODE) {
            binding.repeatMode = it
        }
        containerViewModel.register<Int>(TAG, LABEL_SHUFFLE_MODE) {
            binding.shuffleMode = it
        }
        
        appCompatImageButtonRepeat.setOnClickListener(this)
        appCompatImageButtonPrev.setOnClickListener(this)
        appCompatImageButtonNext.setOnClickListener(this)
        appCompatImageButtonShuffle.setOnClickListener(this)
        appCompatImageButtonSleep.setOnClickListener(this)
        
        with(progressBar) {
            setOnProgressChanged { position, isReleased ->
                if (binding.isControlling != !isReleased) {
                    binding.isControlling = !isReleased
                }
                binding.controlDuration = position
                if (isReleased) {
                    transportControls.seekTo(position)
                    binding.position = position
                }
            }
        }
        
        with(floatingActionButton) {
            setImageResource(
                when {
                    containerViewModel.isPlaying -> R.drawable.ic_baseline_pause_24
                    else -> R.drawable.ic_baseline_play_arrow_24
                }
            )
            containerViewModel.register<Boolean>(TAG, LABEL_IS_PLAYING) { isPlaying ->
                isPlaying?.let {
                    if (drawable == null) {
                        return@register setImageResource(
                            when {
                                it -> R.drawable.ic_baseline_pause_24
                                else -> R.drawable.ic_baseline_play_arrow_24
                            }
                        )
                    }
                    setImageResource(
                        when {
                            it -> R.drawable.anim_baseline_play_24
                            else -> R.drawable.anim_baseline_pause_24
                        }
                    )
                    (drawable as? AnimatedVectorDrawable)?.start()
                }
            }
            setOnClickListener {
                when (containerViewModel.isPlaying) {
                    true -> transportControls.pause()
                    false -> transportControls.play()
                }
            }
        }
    }

    override fun onDestroyView() {
        containerViewModel.unregisterAll(TAG)
        super.onDestroyView()
        _binding = null
    }
    
    override fun onClick(v: View?) {
        when (v) {
            appCompatImageButtonRepeat -> sendCustomAction(
                ACTION_UPDATE_REPEAT_MODE,
                Pair(
                    EXTRA_REPEAT_MODE,
                    when (binding.repeatMode) {
                        REPEAT_MODE_ALL -> REPEAT_MODE_ONE
                        REPEAT_MODE_ONE -> REPEAT_MODE_NONE
                        REPEAT_MODE_NONE -> REPEAT_MODE_ALL
                        else -> REPEAT_MODE_ALL
                    }
                )
            )
            
            appCompatImageButtonPrev -> transportControls.skipToPrevious()
            
            appCompatImageButtonNext -> transportControls.skipToNext()
            
            appCompatImageButtonShuffle -> sendCustomAction(
                ACTION_UPDATE_SHUFFLE_MODE,
                Pair(
                    EXTRA_SHUFFLE_MODE,
                    when (binding.shuffleMode) {
                        SHUFFLE_MODE_NONE -> SHUFFLE_MODE_ALL
                        SHUFFLE_MODE_ALL -> SHUFFLE_MODE_NONE
                        else -> SHUFFLE_MODE_NONE
                    }
                )
            )
            
            appCompatImageButtonSleep -> {
                SleepTimerDialogFragment()
                    .apply { arguments = bundleOf(EXTRA_VALUE to sleepCountDownMillis) }
                    .setOnStart {
                        sleepTimerJob?.cancel()
                        sleepCountDownMillis = it
                        sleepTimerJob = sleepTimerJob(it)
                        updateSleepTimerState(true)
                    }
                    .setOnClose {
                        sleepCountDownMillis = null
                        sleepTimerJob?.cancel()
                        sleepTimerJob = null
                        updateSleepTimerState(false)
                    }
                    .showNow(requireActivity())
            }
        }
    }

    private fun updateColorsAnimated(background: Int, primary: Int, secondary: Int) {
        ValueAnimator.ofArgb(binding.backgroundColor!!, background).apply {
            duration = ANIMATION_DURATION
            addUpdateListener { binding.backgroundColor = animatedValue as Int }
            start()
        }
        ValueAnimator.ofArgb(binding.primaryColor!!, primary).apply {
            duration = ANIMATION_DURATION
            addUpdateListener { binding.primaryColor = animatedValue as Int }
            start()
        }
        ValueAnimator.ofArgb(binding.secondaryColor!!, secondary).apply {
            duration = ANIMATION_DURATION
            addUpdateListener { binding.secondaryColor = animatedValue as Int }
            start()
        }
        ValueAnimator.ofArgb(binding.buttonColor!!, when {
            background.isLight -> BLACK
            else -> WHITE
        }).apply {
            duration = ANIMATION_DURATION
            addUpdateListener { binding.buttonColor = animatedValue as Int }
            start()
        }
    }
    
    private fun sleepTimerJob(millis: String) = io {
        delay(millis.toLong().minToMills)
        sleepCountDownMillis = null
        transportControls.pause()
        sleepTimerJob = null
        updateSleepTimerState(false)
    }
    
    private fun updateSleepTimerState(newState: Boolean) {
        if (binding.isSleepTimerEnabled != newState) {
            binding.isSleepTimerEnabled = newState
        }
    }

}