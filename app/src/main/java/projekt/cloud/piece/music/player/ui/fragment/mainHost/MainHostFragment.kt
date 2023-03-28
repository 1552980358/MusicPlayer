package projekt.cloud.piece.music.player.ui.fragment.mainHost

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import projekt.cloud.piece.music.player.base.BaseLayoutCompat.BaseLayoutCompatUtil.layoutCompat
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.databinding.FragmentMainHostBinding
import projekt.cloud.piece.music.player.ui.activity.main.MainViewModel
import projekt.cloud.piece.music.player.util.ScreenDensity.ScreenDensityUtil.screenDensity

private typealias BaseMainHostFragment =
        BaseMultiDensityFragment<FragmentMainHostBinding, MainHostLayoutCompat>

class MainHostFragment: BaseMainHostFragment() {

    private val mediaControllerCallback = object: MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
            layoutCompat.notifyPlaybackStateChanged(state.state)
        }
        override fun onMetadataChanged(metadata: MediaMetadataCompat) {
            layoutCompat.notifyMetadataChanged(requireContext(), metadata)
        }
    }

    override val viewBindingClass: Class<FragmentMainHostBinding>
        get() = FragmentMainHostBinding::class.java

    override fun onCreateLayoutCompat(binding: FragmentMainHostBinding): MainHostLayoutCompat {
        return binding.layoutCompat(requireContext().screenDensity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val childNavController = binding.fragmentContainerView
            .getFragment<NavHostFragment>()
            .navController

        layoutCompat.setupColor(requireContext())
        layoutCompat.setupNavigation(childNavController)
        layoutCompat.setupNavigationItems(this, childNavController)
        layoutCompat.setupPlaybackBar(this, childNavController)

        val mainViewModel: MainViewModel by activityViewModels()
        mainViewModel.isMediaBrowserCompatConnected.observe(viewLifecycleOwner) { isConnected ->
            val mediaControllerCompat = MediaControllerCompat.getMediaController(requireActivity())
            if (isConnected && mediaControllerCompat != null) {
                registerCallback(mediaControllerCompat)
            }
        }
    }

    private fun registerCallback(mediaControllerCompat: MediaControllerCompat) {
        mediaControllerCompat.registerCallback(mediaControllerCallback)
        if (mediaControllerCompat.playbackState.state != STATE_NONE) {
            layoutCompat.recoverPlaybackBar(this, mediaControllerCompat)
        }
    }

    override fun onDestroyView() {
        MediaControllerCompat.getMediaController(requireActivity())
            ?.unregisterCallback(mediaControllerCallback)

        super.onDestroyView()
    }

}