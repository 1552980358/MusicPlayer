package projekt.cloud.piece.music.player.ui.play

import android.graphics.Color.TRANSPARENT
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OVER_SCROLL_NEVER
import android.view.ViewAnimationUtils.createCircularReveal
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_SETTLING
import com.google.android.material.transition.MaterialContainerTransform
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseMainFragment
import projekt.cloud.piece.music.player.base.BasePlayFragment
import projekt.cloud.piece.music.player.databinding.FragmentPlayBinding
import projekt.cloud.piece.music.player.ui.play.playCover.PlayCoverFragment
import projekt.cloud.piece.music.player.ui.play.util.Extra.EXTRA_FRAGMENT_MANAGER
import projekt.cloud.piece.music.player.ui.play.util.FragmentManager
import projekt.cloud.piece.music.player.util.Constant
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION_LONG
import kotlin.math.hypot

class PlayFragment: BaseMainFragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!

    private lateinit var fragmentList: List<BasePlayFragment>

    private var isScrolling = false
    private var scrollOffsetPixel = 0

    private lateinit var fragmentManager: FragmentManager

    private val circularStartPoint = Point()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().window.statusBarColor = TRANSPARENT
        requireActivity().window.navigationBarColor = TRANSPARENT

        sharedElementEnterTransition = MaterialContainerTransform()

        fragmentManager = FragmentManager(activityInterface)
        fragmentList = listOf(
            PlayCoverFragment(fragmentManager)
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_play, container, false)

        with(binding.viewPager) {

            /**
             * For ViewPager2, method of disabling over scroll is token from
             * https://stackoverflow.com/a/56942231/11685230
             **/
            getChildAt(0).overScrollMode = OVER_SCROLL_NEVER

            adapter = object : FragmentStateAdapter(this@PlayFragment) {
                override fun getItemCount() = fragmentList.size
                override fun createFragment(position: Int) = fragmentList[position]
            }

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    isScrolling = state == SCROLL_STATE_DRAGGING || state == SCROLL_STATE_SETTLING
                }
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    scrollOffsetPixel = positionOffsetPixels
                }
            })
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        io {
            val audioItem = fragmentManager.audioItem
            if (audioItem != null) {
                fragmentManager.audioItem = audioItem
                activityInterface.audioDatabase.color.query(audioItem.id, audioItem.album).apply {
                    ui {
                        binding.root.setBackgroundColor(backgroundColor)
                        fragmentManager.updateColor(backgroundColor, primaryColor, secondaryColor)
                    }
                }

                circularStartPoint.apply {
                    x = resources.displayMetrics.widthPixels / 2
                    y = (resources.displayMetrics.heightPixels - resources.displayMetrics.widthPixels) * 2 / 5 -
                            resources.getDimensionPixelSize(R.dimen.fragment_play_content_buttons_seek_height) * 2 +
                            resources.displayMetrics.widthPixels
                }
            }
        }
        fragmentManager.initial {
            io {
                activityInterface.audioDatabase.color.query(it.id, it.album).apply {
                    binding.relativeLayout.setBackgroundColor(backgroundColor)
                    createCircularReveal(
                        binding.relativeLayout, circularStartPoint.x, circularStartPoint.y, 0F,
                        hypot(circularStartPoint.x.toFloat(), circularStartPoint.y.toFloat())).apply {
                        duration = ANIMATION_DURATION_LONG
                        doOnEnd { binding.root.setBackgroundColor(backgroundColor) }
                        ui { start() }
                    }
                    ui { fragmentManager.updateColor(backgroundColor, primaryColor, secondaryColor) }
                }
            }
        }
    }

    override fun onDestroyView() {
        activityInterface.onDestroyPlay()
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressed() =
        fragmentList[binding.viewPager.currentItem].canBackStack

}