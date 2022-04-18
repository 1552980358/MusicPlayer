package projekt.cloud.piece.music.player.service

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SEEK_TO
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_NEXT
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM
import android.support.v4.media.session.PlaybackStateCompat.ACTION_STOP
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import androidx.core.os.bundleOf
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import projekt.cloud.piece.music.player.BuildConfig.APPLICATION_ID
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.service.play.AudioList
import projekt.cloud.piece.music.player.service.play.Configs
import projekt.cloud.piece.music.player.service.play.Extras.EXTRA_AUDIO_LIST
import projekt.cloud.piece.music.player.service.play.Extras.EXTRA_CONFIGS
import projekt.cloud.piece.music.player.service.play.Extras.EXTRA_INDEX

/**
 * Class [PlayService]
 *  inherits to [MediaBrowserServiceCompat]
 *
 **/
class PlayService: MediaBrowserServiceCompat(), Player.Listener {

    companion object {
        private const val ROOT_ID = "PlayService"
    
        private const val PLAYBACK_STATE_ACTIONS =
            ACTION_PLAY or ACTION_PAUSE or ACTION_PLAY_PAUSE or ACTION_STOP or ACTION_SEEK_TO or
                ACTION_PLAY_FROM_MEDIA_ID or ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS or ACTION_SKIP_TO_QUEUE_ITEM
    
        private const val DEFAULT_PLAYBACK_SPEED = 1F
        
    }
    
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        
        
        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            extras?.let {
                if (!it.containsKey(EXTRA_INDEX)) {
                    return
                }
                val index = it.getInt(EXTRA_INDEX)
                playAudioItem(
                    when {
                        it.containsKey(EXTRA_AUDIO_LIST) ->
                            @Suppress("UNCHECKED_CAST")
                            audioList.updateList(index, it.getSerializable(EXTRA_AUDIO_LIST) as List<AudioItem>)
        
                        else -> audioList.setIndex(index)
                    }
                )
            }
        }
        
        private fun playAudioItem(audioItem: AudioItem) {
        
        }
        
    }
    
    private lateinit var mediaMetadataCompat: MediaMetadataCompat
    private lateinit var playbackStateCompat: PlaybackStateCompat
    
    private var exoPlayer = ExoPlayer.Builder(this)
        .build()

    private val audioList = AudioList()

    private val configs = Configs()
    
    override fun onCreate() {
        super.onCreate()
    
        playbackStateCompat = PlaybackStateCompat.Builder()
            .setState(STATE_NONE, 0, DEFAULT_PLAYBACK_SPEED)
            .setActions(PLAYBACK_STATE_ACTIONS)
            .setExtras(bundleOf(EXTRA_CONFIGS to configs))
            .build()
    
        mediaSessionCompat = MediaSessionCompat(this, ROOT_ID).apply {
            setCallback(mediaSessionCallback)
            setPlaybackState(playbackStateCompat)
            isActive = true
            
            this@PlayService.sessionToken = sessionToken
        }
        
        exoPlayer.addListener(this)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?) = when {
        clientPackageName != APPLICATION_ID -> null
        else -> BrowserRoot(ROOT_ID, null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
    }
    
    override fun onPlaybackStateChanged(playbackState: Int) {
    
    }

}