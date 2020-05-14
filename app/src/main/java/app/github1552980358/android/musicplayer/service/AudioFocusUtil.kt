package app.github1552980358.android.musicplayer.service

import android.app.Service
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_MUSIC
import android.media.AudioAttributes.USAGE_MEDIA
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.AUDIOFOCUS_GAIN
import android.media.AudioManager.AUDIOFOCUS_LOSS
import android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
import android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK
import android.media.AudioManager.STREAM_MUSIC
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi

/**
 * @file    : [AudioFocusUtil]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/14
 * @time    : 17:09
 **/

interface AudioFocusUtil {
    
    /**
     * [getAudioManager]
     * @param context [Context]
     * @return [AudioManager]
     * @author 1552980358
     * @since 0.1
     **/
    fun getAudioManager(context: Context) = context.getSystemService(Service.AUDIO_SERVICE) as AudioManager
    
    /**
     * [getAudioFocusRequest]
     * @param listener [AudioManager.OnAudioFocusChangeListener]
     * @return [AudioFocusRequest]
     * @author 1552980358
     * @since 0.1
     **/
    @Suppress("HasPlatformType")
    @RequiresApi(26)
    fun getAudioFocusRequest(listener: AudioManager.OnAudioFocusChangeListener) =
        AudioFocusRequest.Builder(AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(USAGE_MEDIA)
                    .setContentType(CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setWillPauseWhenDucked(true)
            .setOnAudioFocusChangeListener(listener)
            .build()
    
    /**
     * [getOnAudioFocusChangeListener]
     * @param callback [MediaSessionCompat.Callback]
     * @return [AudioManager.OnAudioFocusChangeListener]
     * @author 1552980358
     * @since 0.1
     **/
    fun getOnAudioFocusChangeListener(callback: MediaSessionCompat.Callback) =
        AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK, AUDIOFOCUS_LOSS_TRANSIENT, AUDIOFOCUS_LOSS -> {
                    callback.onPause()
                }
                AUDIOFOCUS_GAIN -> {
                    callback.onPlay()
                }
            }
        }
    
    /**
     * [abandonAudioFocusRequest]
     * @param audioManager [AudioManager]
     * @param audioFocusRequest [AudioFocusRequest]
     * @param listener [AudioManager.OnAudioFocusChangeListener]
     * @author 1552980358
     * @since 0.1
     **/
    @Suppress("DEPRECATION")
    fun abandonAudioFocusRequest(
        audioManager: AudioManager,
        audioFocusRequest: AudioFocusRequest,
        listener: AudioManager.OnAudioFocusChangeListener
    ) = audioManager.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                abandonAudioFocusRequest(audioFocusRequest)
            } else {
                abandonAudioFocus(listener)
            }
        }
    
    /**
     * [gainAudioFocusRequest]
     * @param audioManager [AudioManager]
     * @param audioFocusRequest [AudioFocusRequest]
     * @param listener [AudioManager.OnAudioFocusChangeListener]
     * @author 1552980358
     * @since 0.1
     **/
    @Suppress("DEPRECATION")
    fun gainAudioFocusRequest(
        audioManager: AudioManager,
        audioFocusRequest: AudioFocusRequest,
        listener: AudioManager.OnAudioFocusChangeListener
    ) = audioManager.run {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestAudioFocus(audioFocusRequest)
        } else {
            @Suppress("DEPRECATION")
            requestAudioFocus(
                listener,
                STREAM_MUSIC,
                AUDIOFOCUS_GAIN
            )
        }
    }
    
}