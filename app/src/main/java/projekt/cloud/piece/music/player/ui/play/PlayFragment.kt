package projekt.cloud.piece.music.player.ui.play

import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
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

class PlayFragment: BaseFragment() {

    companion object {
        private const val TAG = "PlayFragment"
    }

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private val viewPager get() = binding.viewPager

    private val fragmentList = listOf<BaseFragment>(
        PlayControlFragment()
    )

    private val database get() = activityViewModel.database
    private val audioItem get() = activityViewModel.audioItem!!

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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_play, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
                    binding.relativeLayout.visibility = GONE
                    binding.relativeLayout.setBackgroundColor(backgroundColor)
                }
                else -> ui {
                    binding.root.setBackgroundColor(backgroundColor)
                }
            }
        }
    }

    override fun onBackPressed() = fragmentList[viewPager.currentItem].canBackStack

}