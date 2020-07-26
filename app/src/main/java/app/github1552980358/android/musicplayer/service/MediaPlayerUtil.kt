package app.github1552980358.android.musicplayer.service

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.session.MediaSessionCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    fun onPlay(mediaPlayer: MediaPlayer, gradual: Boolean = false) {
        if (gradual) {
            mediaPlayer.setVolume(0F, 0F)
        }
        mediaPlayer.start()
        GlobalScope.launch(Dispatchers.IO) {
            if (gradual) {
                mediaPlayer.setVolume(0.25F, 0.25F)
                Thread.sleep(250)
                mediaPlayer.setVolume(0.5F, 0.5F)
                Thread.sleep(250)
                mediaPlayer.setVolume(0.75F, 0.75F)
                Thread.sleep(250)
            }
            mediaPlayer.setVolume(1F, 1F)
        }
    }
    
    /**
     * [onPause]
     * @author 1552980358
     * @since 0.1
     **/
    fun onPause(mediaPlayer: MediaPlayer) {
        mediaPlayer.setVolume(0.75F, 0.75F)
        Thread.sleep(250)
        mediaPlayer.setVolume(0.5F, 0.5F)
        Thread.sleep(250)
        mediaPlayer.setVolume(0.25F, 0.25F)
        Thread.sleep(250)
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
    fun onPlayFromMediaId(
        context: Context,
        mediaPlayer: MediaPlayer,
        callback: MediaSessionCompat.Callback,
        mediaId: String
    ) {
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