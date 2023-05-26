package projekt.cloud.piece.cloudy.util.helper

import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton

/**
 * [ColorGetter]
 **/
private typealias ColorGetter = (View) -> Int

/**
 * [DrawableSetter]
 **/
private typealias DrawableSetter = (AppCompatImageButton, Int) -> Unit

/**
 * [PlaybackControllerHelper]
 * @param drawableTintColor [ColorGetter]
 * @param bufferingDrawable [DrawableSetter]
 * @param playingDrawable [DrawableSetter]
 * @param pausedDrawable [DrawableSetter]
 * @param playToPauseDrawable [DrawableSetter]
 * @param pauseToPlayDrawable [DrawableSetter]
 **/
class PlaybackControllerHelper(

    /**
     * [PlaybackControllerHelper.drawableTintColor]
     * @type [ColorGetter]
     **/
    private val drawableTintColor: ColorGetter,

    /**
     * [PlaybackControllerHelper.bufferingDrawable]
     * @type [DrawableSetter]
     **/
    private val bufferingDrawable: DrawableSetter,

    /**
     * [PlaybackControllerHelper.playingDrawable]
     **/
    private val playingDrawable: DrawableSetter,

    /**
     * [PlaybackControllerHelper.pausedDrawable]
     * @type [DrawableSetter]
     **/
    private val pausedDrawable: DrawableSetter,

    /**
     * [PlaybackControllerHelper.playToPauseDrawable]
     * @type [DrawableSetter]
     **/
    private val playToPauseDrawable: DrawableSetter,

    /**
     * [PlaybackControllerHelper.pauseToPlayDrawable]
     * @type [DrawableSetter]
     **/
    private val pauseToPlayDrawable: DrawableSetter
) {

    /**
     * [PlaybackControllerHelper._isPlaying]
     * @type [Boolean]
     **/
    @Volatile
    private var _isPlaying = false

    /**
     * [PlaybackControllerHelper._isBuffering]
     * @type [Boolean]
     **/
    @Volatile
    private var _isBuffering = false

    /**
     * [PlaybackControllerHelper.updateBufferingState]
     * @param appCompatImageButton [androidx.appcompat.widget.AppCompatImageButton]
     * @param isBuffering [Boolean]
     **/
    fun updateBufferingState(appCompatImageButton: AppCompatImageButton, isBuffering: Boolean) {
        _isBuffering = isBuffering
        notifyStatesChanged(appCompatImageButton, true)
    }

    /**
     * [PlaybackControllerHelper.updatePlayingState]
     * @param appCompatImageButton [androidx.appcompat.widget.AppCompatImageButton]
     * @param isPlaying [Boolean]
     **/
    fun updatePlayingState(appCompatImageButton: AppCompatImageButton, isPlaying: Boolean) {
        _isPlaying = isPlaying
        when {
            // Complete buffering
            _isBuffering && isPlaying -> {
                // Remove buffering
                updateBufferingState(appCompatImageButton, false)
            }
            else -> {
                notifyStatesChanged(appCompatImageButton, false)
            }
        }
        animateAnimatedVectorDrawable(appCompatImageButton.drawable)
    }

    private fun notifyStatesChanged(
        appCompatImageButton: AppCompatImageButton, isBufferingChanged: Boolean
    ) {
        /**
         * Step:
         *   1. Call [PlaybackControllerHelper.getDrawableSetter] to get [DrawableSetter]
         *   2. Call [DrawableSetter] from [PlaybackControllerHelper.getDrawableSetter]
         */
        setDrawable(
            appCompatImageButton,
            getDrawableSetter(isBufferingChanged)
        )
    }

    /**
     * [PlaybackControllerHelper.setDrawable]
     * @param appCompatImageButton [androidx.appcompat.widget.AppCompatImageButton]
     * @param drawableSetter [DrawableSetter]
     *
     * Set drawable to [appCompatImageButton] with [drawableSetter]
     *
     **/
    private fun setDrawable(
        appCompatImageButton: AppCompatImageButton, drawableSetter: DrawableSetter
    ) {
        /**
         * Step:
         *   1. Call [PlaybackControllerHelper.drawableTintColor] to get color
         *   2. Call [drawableSetter] to apply drawable
         **/
        drawableSetter.invoke(
            appCompatImageButton,
            // Get color
            drawableTintColor.invoke(appCompatImageButton)
        )
    }

    /**
     * [PlaybackControllerHelper.getDrawableSetter]
     * @param isBufferingChanged [Boolean]
     * @return [DrawableSetter]
     **/
    private fun getDrawableSetter(isBufferingChanged: Boolean): DrawableSetter {
        return when {
            isBufferingChanged -> buffering()
            else -> playbackStateChanged()
        }
    }

    /**
     * [PlaybackControllerHelper.buffering]
     * @return [DrawableSetter]
     *
     * Check if [PlaybackControllerHelper._isBuffering] changed
     **/
    private fun buffering(): DrawableSetter {
        return when {
            _isBuffering -> bufferingDrawable
            else -> playbackState()
        }
    }

    /**
     * [PlaybackControllerHelper.playbackState]
     * @return [DrawableSetter]
     *
     * Called if [PlaybackControllerHelper._isBuffering] be false
     * check if [PlaybackControllerHelper._isPlaying]
     **/
    private fun playbackState(): DrawableSetter {
        return when {
            _isPlaying -> playingDrawable
            else -> pausedDrawable
        }
    }

    /**
     * [PlaybackControllerHelper.playbackStateChanged]
     * @return [DrawableSetter]
     *
     * Check if [PlaybackControllerHelper._isPlaying]
     **/
    private fun playbackStateChanged(): DrawableSetter {
        return when {
            _isPlaying -> pauseToPlayDrawable
            else -> playToPauseDrawable
        }
    }

    /**
     * [PlaybackControllerHelper.animateAnimatedVectorDrawable]
     * @param drawable [android.graphics.drawable.Drawable]
     *
     * Start animation of [drawable] is [android.graphics.drawable.AnimatedVectorDrawable]
     **/
    private fun animateAnimatedVectorDrawable(drawable: Drawable?) {
        if (drawable is AnimatedVectorDrawable) {
            drawable.start()
        }
    }

}