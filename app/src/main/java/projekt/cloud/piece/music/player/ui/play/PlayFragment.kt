package projekt.cloud.piece.music.player.ui.play

import android.animation.ValueAnimator
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.KEEP_SCREEN_ON
import android.view.View.OVER_SCROLL_NEVER
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.transition.MaterialContainerTransform
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_COLOR_ITEM
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.base.BasePagerViewModel
import projekt.cloud.piece.music.player.database.audio.item.ColorItem
import projekt.cloud.piece.music.player.databinding.FragmentPlayBinding
import projekt.cloud.piece.music.player.ui.play.base.BasePlayFragment
import projekt.cloud.piece.music.player.ui.play.control.PlayControlFragment
import projekt.cloud.piece.music.player.ui.play.lyric.PlayLyricFragment
import projekt.cloud.piece.music.player.ui.play.util.SleepTimer
import projekt.cloud.piece.music.player.util.ColorUtil.isLight
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION

/**
 * Class [PlayFragment], inherit to [BaseFragment]
 *
 * Getters:
 *   [binding]
 *   [root]
 *
 * Variables:
 *   [_binding]
 *
 * Methods:
 *   [onCreate]
 *   [onCreateView]
 *   [onDestroyView]
 *
 **/
class PlayFragment: BasePlayFragment() {

    companion object {
        private const val TAG = "PlayFragment"
    }

    class PlayFragmentViewModel: BasePagerViewModel<BasePlayFragment>() {
        override fun setFragments() =
            arrayOf(PlayControlFragment(), PlayLyricFragment())
    }

    private lateinit var viewModel: PlayFragmentViewModel

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private val root get() = binding.root
    private val viewPager2 get() = binding.viewPager2
    private val materialToolbar get() = binding.materialToolbar
    private lateinit var sleepTimer: SleepTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = ANIMATION_DURATION
        }
        viewModel = ViewModelProvider(this)[PlayFragmentViewModel::class.java]
        sleepTimer = SleepTimer { onSleepTimerStop() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        containerViewModel.colorItem?.background?.let {
            binding.backgroundColor = it
            binding.exitButtonColor = it.closeButtonColor
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.setUpViewPager2(this, viewPager2)
        /**
         * Solution token from
         * [Remove ViewPager2 Overscroll animation](https://stackoverflow.com/a/56942231/11685230)
         **/
        viewPager2.getChildAt(0).overScrollMode = OVER_SCROLL_NEVER
        with(requireActivity() as AppCompatActivity) {
            setSupportActionBar(materialToolbar)
            materialToolbar.setNavigationOnClickListener {
                navController.navigateUp()
            }
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        containerViewModel.register<ColorItem>(TAG, LABEL_COLOR_ITEM) { colorItem ->
            colorItem?.let {
                updateBackgroundColor(binding.backgroundColor!!, it.background)
                ValueAnimator.ofArgb(binding.exitButtonColor!!, it.background.closeButtonColor).apply {
                    duration = ANIMATION_DURATION
                    addUpdateListener { binding.exitButtonColor = animatedValue as Int }
                    start()
                }
            }
        }

    }

    override fun onBackPressed() =
        viewModel[viewPager2.currentItem].canReturn

    override fun onDestroyView() {
        requireActivity().window.clearFlags(KEEP_SCREEN_ON)
        containerViewModel.unregisterAll(TAG)
        super.onDestroyView()
        _binding = null
    }
    
    override fun onSleepTimerStop() {
        viewModel.forEach {
            if (it.isResumed) {
                it.onSleepTimerStop()
            }
        }
    }
    
    override val isSleepTimerEnabled: Boolean
        get() = sleepTimer.isStarted
    
    override val sleepTimerMillis: String?
        get() = sleepTimer.millis
    
    override val isKeepScreenOnEnabled: Boolean
        get() = root.keepScreenOn
    
    override fun setKeepScreenOnState(state: Boolean) {
        if (root.keepScreenOn != state) {
            root.keepScreenOn = state
        }
    }
    
    override fun startSleepTimer(millis: String) = sleepTimer.start(millis)
    
    override fun stopSleepTimer() = sleepTimer.stop()
    
    private val Int.closeButtonColor get() =
        if (isLight) BLACK else WHITE

    private fun updateBackgroundColor(@ColorInt originColor: Int, @ColorInt newColor: Int) {
        ValueAnimator.ofArgb(originColor, newColor).apply {
            duration = ANIMATION_DURATION
            addUpdateListener { binding.backgroundColor = it.animatedValue as Int }
            start()
        }
    }

}