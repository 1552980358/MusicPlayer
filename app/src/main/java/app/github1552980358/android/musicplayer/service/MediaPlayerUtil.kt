package app.github1552980358.android.musicplayer.service

import android.content.Context
import android.media.AudioDeviceCallback
import android.media.MediaPlayer
import android.media.MediaPlayer.SEEK_PREVIOUS_SYNC
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.session.MediaSessionCompat
import java.io.File

/**
 * @file    : [MediaPlayerUtil]
 * @author  : 1552980358
 * @since   :
 * @date    : 2020/5/11
 * @time    : 8:58
 **/

interface MediaPlayerUtil: MediaPlayer.OnCompletionListener {
    
    fun initialMediaPlayer(mediaPlayer: MediaPlayer) {
        mediaPlayer.setOnCompletionListener(this)
    }
    
    fun onPlay(mediaPlayer: MediaPlayer) {
        mediaPlayer.start()
    }
    
    fun onPause(mediaPlayer: MediaPlayer) {
        mediaPlayer.pause()
    }
    
    fun onSeekTo(mediaPlayer: MediaPlayer, position: Long) {
        mediaPlayer.seekTo(position.toInt())
    }
    
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
    
    override fun onCompletion(mp: MediaPlayer?) {
    
    }
    
}