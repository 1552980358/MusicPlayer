package projekt.cloud.piece.music.player.ui.play

import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewAnimationUtils.createCircularReveal
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Transition
import androidx.transition.Transition.TransitionListener
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.transition.MaterialContainerTransform
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.FragmentPlayBinding
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

    private val database get() = activityViewModel.database
    private val audioItem get() = activityViewModel.audioItem!!

    private lateinit var viewModel: PlayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()

        /**
         * Please look at the lifecycle of Fragment,
         * an [onSaveInstanceState] is called when the restart of fragment is triggered,
         * and [savedInstanceState] instance is created
         * Hence, we have 2 status on the
         *    1) savedInstanceState == null -> just started
         *    2) savedInstanceState != null -> Activity restarted
         * So, when savedInstanceState != null, just restore status bar color
         **/
        // Restore status bar color
        when {
            savedInstanceState != null -> requireActivity().window.statusBarColor = TRANSPARENT
            else -> (sharedElementEnterTransition as Transition).addListener(object : TransitionListener {
                override fun onTransitionStart(transition: Transition) = Unit
                override fun onTransitionEnd(transition: Transition) {
                    requireActivity().window.statusBarColor = TRANSPARENT
                }
                override fun onTransitionCancel(transition: Transition) = Unit
                override fun onTransitionPause(transition: Transition) = Unit
                override fun onTransitionResume(transition: Transition) = Unit
            })
        }
        requireActivity().window.navigationBarColor = TRANSPARENT
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(this)[PlayViewModel::class.java]
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
                override fun getItemCount() = viewModel.fragmentList.size
                override fun createFragment(position: Int) = viewModel.fragmentList[position]
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
                        viewModel.circularRevelPoint.x, viewModel.circularRevelPoint.y,
                        0F,
                        hypot(viewModel.circularRevelPoint.x.toFloat(), viewModel.circularRevelPoint.y.toFloat())
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

    override fun onBackPressed() = viewModel.fragmentList[viewPager.currentItem].canBackStack

}