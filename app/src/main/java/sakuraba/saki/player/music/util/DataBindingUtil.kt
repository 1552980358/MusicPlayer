package sakuraba.saki.player.music.util

import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageButton
import androidx.databinding.BindingAdapter
import sakuraba.saki.player.music.view.DurationView
import sakuraba.saki.player.music.widget.LyricLayout
import sakuraba.saki.player.music.widget.PlaySeekbar

object DataBindingUtil {

    @JvmStatic
    @BindingAdapter("app:tintColor")
    fun AppCompatImageButton.setTintColor(@ColorInt color: Int) {
        setColorFilter(color)
    }

    @JvmStatic
    @BindingAdapter("app:backgroundColor")
    fun ViewGroup.setBackground(@ColorInt color: Int) {
        setBackgroundColor(color)
    }

    @JvmStatic
    @BindingAdapter("app:duration")
    fun DurationView.setDuration(duration: Long) {
        this.duration = duration
    }

    @JvmStatic
    @BindingAdapter("app:max")
    fun PlaySeekbar.setMax(max: Long) {
        this.max = max
    }

    @JvmStatic
    @BindingAdapter("app:progress")
    fun PlaySeekbar.setProgress(progress: Long) {
        updateProgressDataBinding(progress)
    }

    @JvmStatic
    @BindingAdapter("app:progress")
    fun LyricLayout.updateProgress(progress: Long) {
        updatePosition(progress)
    }

}