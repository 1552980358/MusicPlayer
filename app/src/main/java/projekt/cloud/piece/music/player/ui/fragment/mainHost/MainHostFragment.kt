package projekt.cloud.piece.music.player.ui.fragment.mainHost

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import kotlin.reflect.KClass
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.databinding.FragmentMainHostBinding
import projekt.cloud.piece.music.player.ui.activity.main.MainViewModel

class MainHostFragment: BaseMultiDensityFragment<FragmentMainHostBinding, MainHostLayoutCompat>() {

    override val viewBindingClass: Class<FragmentMainHostBinding>
        get() = FragmentMainHostBinding::class.java

    override val layoutCompatClass: KClass<MainHostLayoutCompat>
        get() = MainHostLayoutCompat::class

    private val mediaControllerCallback = object: MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
            layoutCompat.notifyPlaybackStateChanged(requireContext(), state.state)
        }
        override fun onMetadataChanged(metadata: MediaMetadataCompat) {
            layoutCompat.notifyMetadataChanged(requireContext(), metadata)
        }
    }

    override fun onSetupLayoutCompat(layoutCompat: MainHostLayoutCompat, savedInstanceState: Bundle?) {
        val childNavController = binding.fragmentContainerView
            .getFragment<NavHostFragment>()
            .navController

        layoutCompat.setupNavigation(childNavController)
        layoutCompat.setupNavigationItems(this, childNavController)
        layoutCompat.setupPlaybackBar(this, childNavController)

        val mainViewModel: MainViewModel by activityViewModels()
        mainViewModel.isMediaBrowserCompatConnected.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                MediaControllerCompat.getMediaController(requireActivity())
                    ?.let { mediaControllerCompat ->
                        registerCallback(mediaControllerCompat)
                        layoutCompat.setupPlaybackControl(mediaControllerCompat.transportControls)
                        layoutCompat.setupNavigatingToPlayer(mediaControllerCompat, findNavController())
                    }
            }
        }
    }

    private fun registerCallback(mediaControllerCompat: MediaControllerCompat) {
        mediaControllerCompat.registerCallback(mediaControllerCallback)
        if (mediaControllerCompat.playbackState.state != STATE_NONE) {
            layoutCompat.recoverPlaybackBar(requireContext(), mediaControllerCompat)
        }
    }

    override fun onDestroyView() {
        MediaControllerCompat.getMediaController(requireActivity())
            ?.unregisterCallback(mediaControllerCallback)
        super.onDestroyView()
    }

}