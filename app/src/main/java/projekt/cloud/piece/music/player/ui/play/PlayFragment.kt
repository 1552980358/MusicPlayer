package projekt.cloud.piece.music.player.ui.play

import android.graphics.Color.TRANSPARENT
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewAnimationUtils.createCircularReveal
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.transition.Transition
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.transition.MaterialContainerTransform
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.FragmentPlayBinding
import projekt.cloud.piece.music.player.ui.play.playControl.PlayControlFragment
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION
import kotlin.math.hypot

class PlayFragment: BaseFragment() {

    companion object {
        private const val TAG = "PlayFragment"
    }

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private val toolbar get() = binding.toolbar
    private val viewPager get() = binding.viewPager

    private val fragmentList = listOf<BaseFragment>(
        PlayControlFragment()
    )

    private val database get() = activityViewModel.database
    private val audioItem get() = activityViewModel.audioItem!!

    private val circularRevelPoint = Point()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
        (sharedElementEnterTransition as Transition).addListener(object : Transition.TransitionListener{
            override fun onTransitionStart(transition: Transition) {
            }
            override fun onTransitionEnd(transition: Transition) {
                requireActivity().window.statusBarColor = TRANSPARENT
            }
            override fun onTransitionCancel(transition: Transition) {
            }
            override fun onTransitionPause(transition: Transition) {
            }
            override fun onTransitionResume(transition: Transition) {
            }
        })
        requireActivity().window.navigationBarColor = TRANSPARENT

        with(circularRevelPoint) {
            x = resources.displayMetrics.widthPixels / 2
            y = (resources.displayMetrics.heightPixels - resources.displayMetrics.widthPixels) * 2 / 5 -
                resources.getDimensionPixelSize(R.dimen.play_control_seekbar_height) * 2 +
                resources.displayMetrics.widthPixels
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_play, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(requireActivity() as AppCompatActivity) {
            setSupportActionBar(toolbar)
            toolbar.setNavigationOnClickListener { onBackPressed() }
            supportActionBar?.setDisplayShowTitleEnabled(false)
            invalidateOptionsMenu()
        }
        activityViewModel.setAudioItemObserver(TAG, false) {
            updateColor(it)
        }
        updateColor(audioItem, false)
        with(viewPager) {
            adapter = object : FragmentStateAdapter(this@PlayFragment) {
                override fun getItemCount() = fragmentList.size
                override fun createFragment(position: Int) = fragmentList[position]
            }
        }
    }

    private fun updateColor(audioItem: AudioItem, requireAnimation: Boolean = true) = io {
        database.color.query(audioItem.id, audioItem.album).apply {
            when {
                requireAnimation -> {
                    ui {
                        binding.relativeLayout.visibility = GONE
                        binding.relativeLayout.setBackgroundColor(backgroundColor)
                    }
                    createCircularReveal(
                        binding.relativeLayout,
                        circularRevelPoint.x, circularRevelPoint.y,
                        0F,
                        hypot(circularRevelPoint.x.toFloat(), circularRevelPoint.y.toFloat())
                    ).apply {
                        duration = ANIMATION_DURATION
                        doOnEnd { binding.root.setBackgroundColor(backgroundColor) }
                        ui {
                            binding.relativeLayout.visibility = VISIBLE
                            start()
                        }
                    }
                }
                else -> ui {
                    binding.root.setBackgroundColor(backgroundColor)
                }
            }
        }
    }

    override fun onDestroyView() {
        activityViewModel.setAudioItemObserver(TAG)
        _binding = null
        super.onDestroyView()
    }

    override fun onBackPressed() = fragmentList[viewPager.currentItem].canBackStack

}