package sakuraba.saki.player.music.widget

import android.animation.ValueAnimator
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.AudioManager.FLAG_PLAY_SOUND
import android.media.AudioManager.STREAM_MUSIC
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import com.google.android.renderscript.Toolkit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lib.github1552980358.ktExtension.android.content.broadcastReceiver
import lib.github1552980358.ktExtension.android.content.register
import sakuraba.saki.player.music.databinding.LayoutLyricBinding
import sakuraba.saki.player.music.util.LyricUtil.readLyric

class LyricLayout: RelativeLayout {

    companion object {
        private const val VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION"
        private const val EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE"
    }

    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet)

    private var _layoutLyricBinding: LayoutLyricBinding? = null
    private val layoutLyric get() = _layoutLyricBinding!!

    private var isShow = false

    private lateinit var audioManager: AudioManager

    private val broadcastReceiver = broadcastReceiver { _, intent, _ ->
        if (intent?.action == VOLUME_CHANGED_ACTION && intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1) == STREAM_MUSIC) {
            layoutLyric.verticalSeekbar.progress = audioManager.getStreamVolume(STREAM_MUSIC)
        }
    }

    init {
        _layoutLyricBinding = LayoutLyricBinding.inflate(LayoutInflater.from(context), this, false)
        addView(layoutLyric.root)
        alpha = 0F
        audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager

        layoutLyric.verticalSeekbar.setOnSeekChangeListener { progress, isUser ->
            layoutLyric.textView.text = progress.toString()
            if (isUser) {
                audioManager.setStreamVolume(STREAM_MUSIC, progress, FLAG_PLAY_SOUND)
            }
        }
        layoutLyric.verticalSeekbar.initSettings(
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> audioManager.getStreamMinVolume(STREAM_MUSIC)
                else -> 0
            },
            audioManager.getStreamVolume(STREAM_MUSIC),
            audioManager.getStreamMaxVolume(STREAM_MUSIC)
        )

        broadcastReceiver.register(context, arrayOf(VOLUME_CHANGED_ACTION))

        layoutLyric.relativeLayout.apply {
            layoutParams = layoutParams.apply {
                height = resources.displayMetrics.widthPixels * 3 / 5
            }
        }
    }

    fun updateVisibility(): Boolean {
        isShow = !isShow
        (if (isShow) ValueAnimator.ofFloat(0F, 1F) else ValueAnimator.ofFloat(1F, 0F)).apply {
            duration = 500
            addUpdateListener { alpha = animatedValue as Float }
            start()
        }
        return isShow
    }

    fun updatePrimaryColor(@ColorInt newColor: Int) {
        layoutLyric.lyricView.updatePrimaryColor(newColor)
        layoutLyric.textView.setTextColor(newColor)
        layoutLyric.verticalSeekbar.updatePrimaryColor(newColor)
    }

    fun updateSecondaryColor(@ColorInt newColor: Int) {
        layoutLyric.lyricView.updateSecondaryColor(newColor)
        layoutLyric.verticalSeekbar.updateSecondaryColor(newColor)
    }

    fun updateBitmap(bitmap: Bitmap?) {
        if (bitmap == null) {
            layoutLyric.imageView.setImageBitmap(null)
            return
        }
        val blurredBitmap = Toolkit.blur(bitmap, 25)
        CoroutineScope(Dispatchers.Main).launch { layoutLyric.imageView.setImageBitmap(blurredBitmap) }
    }

    fun unregisterBroadcastReceiver() {
        context.unregisterReceiver(broadcastReceiver)
    }

    fun updateLyric(audioId: String) {
        layoutLyric.lyricView.isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            val lyricList = layoutLyric.lyricView.lyricList
            val timeList = layoutLyric.lyricView.timeList
            lyricList.clear()
            timeList.clear()
            context.readLyric(audioId, lyricList, timeList)
            layoutLyric.lyricView.isLoading = false
        }
    }

    fun updatePosition(position: Long) =
        layoutLyric.lyricView.updatePosition(position)

}