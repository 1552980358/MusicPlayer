package sakuraba.saki.player.music.ui.play

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.graphics.Color
import android.media.AudioManager
import android.media.AudioManager.FLAG_PLAY_SOUND
import android.media.AudioManager.STREAM_MUSIC
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lib.github1552980358.ktExtension.android.content.broadcastReceiver
import lib.github1552980358.ktExtension.android.content.register
import sakuraba.saki.player.music.databinding.LayoutVolumePopupWindowBinding

class VolumePopupWindow(context: Context, private val view: View, isLight: Boolean, backgroundColor: Int): PopupWindow(context) {
    
    private companion object {
        const val VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION"
        const val EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE"
    }
    
    private var _layoutVolumePopupWindowBinding: LayoutVolumePopupWindowBinding? = null
    private val layoutVolumePopupWindow get() = _layoutVolumePopupWindowBinding!!
    
    private lateinit var audioManager: AudioManager
    
    private lateinit var job: Job
    private var isDismissible = false
    
    private val broadcastReceiver = broadcastReceiver { _, intent, _ ->
        if (intent?.action == VOLUME_CHANGED_ACTION && intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1) == STREAM_MUSIC) {
            layoutVolumePopupWindow.verticalSeekbar.apply {
                if (!isUser) {
                    layoutVolumePopupWindow.verticalSeekbar.progress = audioManager.getStreamVolume(STREAM_MUSIC)
                }
            }
        }
    }
    
    init {
        isOutsideTouchable = true
        _layoutVolumePopupWindowBinding = LayoutVolumePopupWindowBinding.inflate(LayoutInflater.from(context))
        contentView = layoutVolumePopupWindow.root
    
        audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
    
        layoutVolumePopupWindow.verticalSeekbar.setOnSeekChangeListener { progress, isUser ->
            layoutVolumePopupWindow.textView.text = progress.toString()
            if (isUser) {
                audioManager.setStreamVolume(STREAM_MUSIC, progress, FLAG_PLAY_SOUND)
                if (isDismissible) {
                    isDismissible = false
                }
            }
        }
    
        layoutVolumePopupWindow.verticalSeekbar
            .initSettings(
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> audioManager.getStreamMinVolume(STREAM_MUSIC)
                    else -> 0
                },
                audioManager.getStreamVolume(STREAM_MUSIC),
                audioManager.getStreamMaxVolume(STREAM_MUSIC)
            )
    
        broadcastReceiver.register(context, arrayOf(VOLUME_CHANGED_ACTION))
        updateBackground(backgroundColor)
        updateIsLight(isLight)
        
        setOnDismissListener {
            isDismissible = true
            job.cancel()
        }
    }
    
    fun updateBackground(backgroundColor: Int) = layoutVolumePopupWindow.root.setBackgroundColor(backgroundColor)
    
    private fun updateIsLight(isLight: Boolean) = updateIsLight(if (isLight) Color.BLACK else Color.WHITE, isLight)
    
    fun updateIsLight(newColor: Int, isLight: Boolean) {
        layoutVolumePopupWindow.textView.setTextColor(newColor)
        layoutVolumePopupWindow.verticalSeekbar.updateColor(newColor, isLight)
    }
    
    fun show() {
        showAsDropDown(view, (width - view.width) / 2, 0)
        job = CoroutineScope(Dispatchers.IO).launch {
            do {
                isDismissible = true
                delay(5000)
                if (isDismissible) {
                    launch(Dispatchers.Main) { dismiss() }
                }
            } while (!isDismissible)
        }
    }
    
}