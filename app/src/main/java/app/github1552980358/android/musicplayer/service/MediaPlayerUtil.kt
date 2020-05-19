package app.github1552980358.android.musicplayer.service

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.session.MediaSessionCompat
import java.io.File

/**
 * @file    : [MediaPlayerUtil]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/11
 * @time    : 8:58
 **/

interface MediaPlayerUtil {
    
    /**
     * [initialMediaPlayer]
     * @author 1552980358
     * @since 0.1
     **/
    fun initialMediaPlayer(mediaPlayer: MediaPlayer, service: PlayService) {
        mediaPlayer.setOnCompletionListener(service)
    }
    
    /**
     * [onPlay]
     * @author 1552980358
     * @since 0.1
     **/
    fun onPlay(mediaPlayer: MediaPlayer) {
        mediaPlayer.start()
    }
    
    /**
     * [onPause]
     * @author 1552980358
     * @since 0.1
     **/
    fun onPause(mediaPlayer: MediaPlayer) {
        mediaPlayer.pause()
    }
    
    /**
     * [onSeekTo]
     * @author 1552980358
     * @since 0.1
     **/
    fun onSeekTo(mediaPlayer: MediaPlayer, position: Long) {
        mediaPlayer.seekTo(position.toInt())
    }
    
    /**
     * [onPlayFromMediaId]
     * @author 1552980358
     * @since 0.1
     **/
    fun onPlayFromMediaId(context: Context, mediaPlayer: MediaPlayer, callback: MediaSessionCompat.Callback, mediaId: String) {
        // Reset
        // 重置
        mediaPlayer.stop()
        mediaPlayer.reset()
        
        mediaPlayer.setDataSource(
            context,
            Uri.parse(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()
                    + File.separator
                    + mediaId
            )
        )
        
        mediaPlayer.prepare()
        
        // Update
        // 更新
        callback.onPlay()
    }
    
}