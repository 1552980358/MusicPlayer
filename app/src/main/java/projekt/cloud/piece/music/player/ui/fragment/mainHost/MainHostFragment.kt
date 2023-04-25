package projekt.cloud.piece.music.player.ui.fragment.mainHost

import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.base.LayoutCompatInflater
import projekt.cloud.piece.music.player.base.ViewBindingInflater
import projekt.cloud.piece.music.player.databinding.FragmentMainHostBinding
import projekt.cloud.piece.music.player.ui.activity.main.MainViewModel
import projekt.cloud.piece.music.player.ui.fragment.mainHost.MainHostLayoutCompat.LibraryLayoutCompatUtil

class MainHostFragment: BaseMultiDensityFragment<FragmentMainHostBinding, MainHostLayoutCompat>() {

    override val viewBindingInflater: ViewBindingInflater<FragmentMainHostBinding>
        get() = FragmentMainHostBinding::inflate

    override val layoutCompatInflater: LayoutCompatInflater<FragmentMainHostBinding, MainHostLayoutCompat>
        get() = LibraryLayoutCompatUtil::inflate

    private lateinit var mediaControllerCallback: MediaControllerCompat.Callback

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
                        registerCallback(layoutCompat, mediaControllerCompat)
                        layoutCompat.setupPlaybackControl(mediaControllerCompat.transportControls)
                        layoutCompat.setupNavigatingToPlayer(mediaControllerCompat, findNavController())
                    }
            }
        }
    }

    private fun registerCallback(layoutCompat: MainHostLayoutCompat, mediaControllerCompat: MediaControllerCompat) {
        mediaControllerCompat.registerCallback(
            createMediaControllerCallback(layoutCompat, mediaControllerCompat)
        )
        if (mediaControllerCompat.playbackState.state != STATE_NONE) {
            layoutCompat.recoverPlaybackBar(requireContext(), mediaControllerCompat)
        }
    }

    private fun createMediaControllerCallback(
        layoutCompat: MainHostLayoutCompat, mediaControllerCompat: MediaControllerCompat
    ): MediaControllerCompat.Callback {
        mediaControllerCallback = object: MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                unregisterIfDetached(mediaControllerCompat, this) { context ->
                    layoutCompat.notifyPlaybackStateChanged(context, state.state)
                }
            }
            override fun onMetadataChanged(metadata: MediaMetadataCompat) {
                unregisterIfDetached(mediaControllerCompat, this) { context ->
                    layoutCompat.notifyMetadataChanged(context, metadata)
                }
            }
        }
        return mediaControllerCallback
    }

    private fun unregisterIfDetached(
        mediaControllerCompat: MediaControllerCompat, callback: MediaControllerCompat.Callback, block: (Context) -> Unit
    ) {
        when (val context = context) {
            null -> mediaControllerCompat.unregisterCallback(callback)
            else -> block.invoke(context)
        }
    }

    override fun onStop() {
        MediaControllerCompat.getMediaController(requireActivity())
            ?.unregisterCallback(mediaControllerCallback)
        super.onStop()
    }

}