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
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.base.LayoutCompatInflater
import projekt.cloud.piece.music.player.base.ViewBindingInflater
import projekt.cloud.piece.music.player.databinding.FragmentPlayerBinding
import projekt.cloud.piece.music.player.ui.activity.main.MainViewModel
import projekt.cloud.piece.music.player.ui.fragment.player.PlayerLayoutCompat.LibraryLayoutCompatUtil
import projekt.cloud.piece.music.player.util.PlaybackPositionManager
import projekt.cloud.piece.music.player.util.PlaybackStateManager

class PlayerFragment: BaseMultiDensityFragment<FragmentPlayerBinding, PlayerLayoutCompat>() {

    override val viewBindingInflater: ViewBindingInflater<FragmentPlayerBinding>
        get() = FragmentPlayerBinding::inflate

    override val layoutCompatInflater: LayoutCompatInflater<FragmentPlayerBinding, PlayerLayoutCompat>
        get() = LibraryLayoutCompatUtil::inflate

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

    override fun onSetupLayoutCompat(layoutCompat: PlayerLayoutCompat, savedInstanceState: Bundle?) {
        layoutCompat.setupExit(this)
        layoutCompat.setupShuffleMode()

        val playerFragmentArgs: PlayerFragmentArgs by navArgs()
        updateMetadataFromArgs(layoutCompat, playerFragmentArgs)

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

                        setupMediaController(layoutCompat, mediaControllerCompat) { position: Long ->
                            // doOnPositionUpdate: (Long) -> Unit
                            layoutCompat.updatePlaybackPosition(position, isSliding)
                        }
                    }
            }
        }
    }

    private lateinit var mediaControllerCallback: MediaControllerCompat.Callback

    private fun setupMediaController(
        layoutCompat: PlayerLayoutCompat,
        mediaControllerCompat: MediaControllerCompat,
        doOnPositionUpdate: (Long) -> Unit
    ) {
        val playbackStateManager = PlaybackStateManager()
        var playbackPositionManager: PlaybackPositionManager? = null

        mediaControllerCallback = object: MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(playbackStateCompat: PlaybackStateCompat) {
                playbackPositionManager?.close()
                if (playbackStateCompat.state == STATE_PLAYING) {
                    playbackPositionManager = setupPlaybackPositionManager(
                        playbackStateCompat.position,
                        mediaControllerCompat.metadata,
                        doOnPositionUpdate
                    )
                }
                layoutCompat.notifyUpdatePlaybackController(
                    this@PlayerFragment,
                    playbackStateManager.updatePlaybackState(playbackStateCompat.state)
                )
            }

            override fun onMetadataChanged(mediaMetadataCompat: MediaMetadataCompat) {
                updateMetadataFromMediaMetadataCompat(layoutCompat, mediaMetadataCompat)
                playbackPositionManager?.duration = mediaMetadataCompat.getLong(METADATA_KEY_DURATION)
            }

            override fun onShuffleModeChanged(shuffleMode: Int) {
                layoutCompat.notifyPlaybackModesChanged(mediaControllerCompat.repeatMode, shuffleMode)
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                layoutCompat.notifyPlaybackModesChanged(repeatMode, mediaControllerCompat.shuffleMode)
            }
        }

        mediaControllerCompat.registerCallback(mediaControllerCallback)
        // Recover state
        mediaControllerCompat.playbackState?.let { playbackStateCompat ->

            playbackStateCompat.state.let { playbackState ->
                // Check if require update position
                if (playbackState == STATE_PLAYING) {
                    playbackPositionManager = setupPlaybackPositionManager(
                        playbackStateCompat.position,
                        mediaControllerCompat.metadata,
                        doOnPositionUpdate
                    )
                }

                if (playbackState != STATE_NONE) {
                    doOnPositionUpdate.invoke(playbackStateCompat.position)
                    layoutCompat.notifyUpdatePlaybackController(
                        this,
                        playbackStateManager.updatePlaybackState(playbackStateCompat.state)
                    )
                }
            }
        }
        layoutCompat.setPlaybackModes(
            mediaControllerCompat.repeatMode, mediaControllerCompat.shuffleMode
        )

        layoutCompat.setupPlaybackControls(
            playbackStateManager, mediaControllerCompat, mediaControllerCompat.transportControls
        )
    }

    private fun setupPlaybackPositionManager(
        playbackPosition: Long,
        mediaMetadataCompat: MediaMetadataCompat?,
        doOnPositionUpdate: (Long) -> Unit
    ): PlaybackPositionManager {
        return PlaybackPositionManager(
            lifecycleScope, playbackPosition, mediaMetadataCompat, doOnPositionUpdate
        )
    }

    private fun updateMetadataFromArgs(layoutCompat: PlayerLayoutCompat, playerFragmentArgs: PlayerFragmentArgs) {
        updateMetadata(
            layoutCompat,
            playerFragmentArgs.title,
            playerFragmentArgs.artist,
            playerFragmentArgs.album,
            playerFragmentArgs.duration,
            Uri.parse(playerFragmentArgs.coverUri)
        )
    }

    private fun updateMetadataFromMediaMetadataCompat(
        layoutCompat: PlayerLayoutCompat, mediaMetadataCompat: MediaMetadataCompat
    ) {
        updateMetadata(
            layoutCompat,
            mediaMetadataCompat.getString(METADATA_KEY_TITLE),
            mediaMetadataCompat.getString(METADATA_KEY_ARTIST),
            mediaMetadataCompat.getString(METADATA_KEY_ALBUM),
            mediaMetadataCompat.getLong(METADATA_KEY_DURATION),
            Uri.parse(mediaMetadataCompat.getString(METADATA_KEY_ART_URI))
        )
    }

    private fun updateMetadata(
        layoutCompat: PlayerLayoutCompat,
        title: String, artist: String, album: String, duration: Long,
        coverUri: Uri
    ) {
        layoutCompat.updateMetadata(title, artist, album, duration)
        layoutCompat.updateCoverImage(this@PlayerFragment, coverUri)
    }

    override fun onStop() {
        MediaControllerCompat.getMediaController(requireActivity())
            .unregisterCallback(mediaControllerCallback)
        super.onStop()
    }

}