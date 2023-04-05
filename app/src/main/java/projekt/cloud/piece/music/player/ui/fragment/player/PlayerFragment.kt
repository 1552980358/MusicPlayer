package projekt.cloud.piece.music.player.ui.fragment.player

import android.graphics.Color.TRANSPARENT
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ART_URI
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseLayoutCompat.BaseLayoutCompatUtil.layoutCompat
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.databinding.FragmentPlayerBinding
import projekt.cloud.piece.music.player.ui.activity.main.MainViewModel
import projekt.cloud.piece.music.player.util.FragmentUtil.viewLifecycleProperty
import projekt.cloud.piece.music.player.util.PlaybackPositionManager
import projekt.cloud.piece.music.player.util.PlaybackStateManager
import projekt.cloud.piece.music.player.util.ScreenDensity.ScreenDensityUtil.screenDensity

class PlayerFragment: BaseMultiDensityFragment<FragmentPlayerBinding, PlayerLayoutCompat>() {

    override val viewBindingClass: Class<FragmentPlayerBinding>
        get() = FragmentPlayerBinding::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resources.getInteger(R.integer.anim_duration_400).toLong().let { transitionDuration ->
            setupTransition(transitionDuration, transitionDuration * 2)
        }
    }

    private fun setupTransition(transitionDurationShort: Long, transitionDurationLong: Long) {
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = transitionDurationShort       /** 400 **/
            scrimColor = TRANSPARENT
        }
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true).apply {
            duration = transitionDurationLong       /** 800 **/
            addTarget(R.id.drawer_layout_root)
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false).apply {
            duration = transitionDurationLong       /** 800 **/
            addTarget(R.id.drawer_layout_root)
        }
    }

    override fun onCreateLayoutCompat(binding: FragmentPlayerBinding): PlayerLayoutCompat {
        return binding.layoutCompat(requireContext().screenDensity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateMetadataFromArgs()

        val mainViewModel: MainViewModel by activityViewModels()
        mainViewModel.isMediaBrowserCompatConnected.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                MediaControllerCompat.getMediaController(requireActivity())
                    ?.let { mediaControllerCompat ->
                        var isSliding = false

                        layoutCompat.setupSlider(mediaControllerCompat.transportControls) { isSliderTouching ->
                            // slidingListener: (Boolean) -> Unit
                            isSliding = isSliderTouching
                        }

                        setupMediaController(mediaControllerCompat) { position: Long ->
                            // doOnPositionUpdate: (Long) -> Unit
                            layoutCompat.updatePlaybackPosition(position, isSliding)
                        }
                    }
            }
        }
    }

    private var callback: MediaControllerCompat.Callback by viewLifecycleProperty { callback ->
        MediaControllerCompat.getMediaController(requireActivity())
            ?.unregisterCallback(callback)
    }

    private fun setupMediaController(
        mediaControllerCompat: MediaControllerCompat, doOnPositionUpdate: (Long) -> Unit
    ) {
        val playbackStateManager = PlaybackStateManager()

        var playbackPositionManager: PlaybackPositionManager? = null
        callback = object: MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(playbackStateCompat: PlaybackStateCompat) {
                playbackPositionManager = setupPlaybackPositionManager(
                    playbackPositionManager, playbackStateCompat.position, doOnPositionUpdate
                )
                layoutCompat.notifyUpdatePlaybackController(
                    this@PlayerFragment,
                    playbackStateManager.updatePlaybackState(playbackStateCompat.state)
                )
            }
            override fun onMetadataChanged(mediaMetadataCompat: MediaMetadataCompat) {
                updateMetadataFromMediaMetadataCompat(mediaMetadataCompat)
            }
        }
        mediaControllerCompat.registerCallback(callback)
        // Recover state
        mediaControllerCompat.playbackState?.let { playbackStateCompat ->
            if (playbackStateCompat.state != STATE_NONE) {
                playbackPositionManager = setupPlaybackPositionManager(
                    playbackPositionManager, playbackStateCompat.position, doOnPositionUpdate
                )
                layoutCompat.notifyUpdatePlaybackController(
                    this,
                    playbackStateManager.updatePlaybackState(playbackStateCompat.state)
                )
            }
        }

        layoutCompat.setupPlaybackControls(
            playbackStateManager, mediaControllerCompat.transportControls
        )
    }

    private fun setupPlaybackPositionManager(
        playbackPositionManager: PlaybackPositionManager?, playbackPosition: Long, doOnPositionUpdate: (Long) -> Unit
    ): PlaybackPositionManager {
        playbackPositionManager?.close()
        return PlaybackPositionManager(lifecycleScope, playbackPosition, doOnPositionUpdate)
    }

    private fun updateMetadataFromArgs() {
        val playerFragmentArgs: PlayerFragmentArgs by navArgs()
        updateMetadataFromArgs(playerFragmentArgs)
    }

    private fun updateMetadataFromArgs(playerFragmentArgs: PlayerFragmentArgs) {
        layoutCompat.updateMetadata(
            playerFragmentArgs.title,
            playerFragmentArgs.artist,
            playerFragmentArgs.album,
            playerFragmentArgs.duration
        )
        layoutCompat.updateCoverImage(
            this,
            Uri.parse(playerFragmentArgs.coverUri)
        )
    }

    private fun updateMetadataFromMediaMetadataCompat(mediaMetadataCompat: MediaMetadataCompat) {
        layoutCompat.updateMetadata(
            mediaMetadataCompat.getString(METADATA_KEY_TITLE),
            mediaMetadataCompat.getString(METADATA_KEY_ARTIST),
            mediaMetadataCompat.getString(METADATA_KEY_ALBUM),
            mediaMetadataCompat.getLong(METADATA_KEY_DURATION)
        )
        layoutCompat.updateCoverImage(
            this,
            Uri.parse(mediaMetadataCompat.getString(METADATA_KEY_ART_URI))
        )
    }

}