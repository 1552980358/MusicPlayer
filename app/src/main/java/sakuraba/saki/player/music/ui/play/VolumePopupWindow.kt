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
import sakuraba.saki.player.music.databinding.LayoutVolumePopupWindowBinding

class VolumePopupWindow(context: Context, private val view: View, isLight: Boolean, backgroundColor: Int, private val audioManager: AudioManager): PopupWindow(context) {
    
    private var _layoutVolumePopupWindowBinding: LayoutVolumePopupWindowBinding? = null
    private val layoutVolumePopupWindow get() = _layoutVolumePopupWindowBinding!!
    
    private lateinit var job: Job
    private var isDismissible = false
    
    init {
        isOutsideTouchable = true
        isTouchable = true
        isFocusable = true
        _layoutVolumePopupWindowBinding = LayoutVolumePopupWindowBinding.inflate(LayoutInflater.from(context))
        contentView = layoutVolumePopupWindow.root
    
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
    
    fun updateVolume(newVolume: Int) {
        layoutVolumePopupWindow.verticalSeekbar.apply {
            if (!isUser) {
                layoutVolumePopupWindow.verticalSeekbar.progress = newVolume
            }
        }
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