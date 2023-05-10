package projekt.cloud.piece.music.player.base.interfaces

import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import projekt.cloud.piece.music.player.util.KotlinUtil.ifNotNull
import projekt.cloud.piece.music.player.util.ScreenDensity
import projekt.cloud.piece.music.player.util.ScreenDensity.COMPACT
import projekt.cloud.piece.music.player.util.ScreenDensity.EXPANDED
import projekt.cloud.piece.music.player.util.ScreenDensity.MEDIUM

interface TransitionInterface {

    val compat: TransitionWrapper?
        get() = null

    val medium: TransitionWrapper?
        get() = null

    val expanded: TransitionWrapper?
        get() = null

    fun transitionWrapper(block: TransitionWrapper.() -> Unit): TransitionWrapper {
        return TransitionWrapper(block)
    }

    class TransitionWrapper(block: TransitionWrapper.() -> Unit) {

        init {
            block.invoke(this)
        }

        var enterTransition: Any? = null

        var returnTransition: Any? = null

        var exitTransition: Any? = null

        var reenterTransition: Any? = null

        var sharedElementEnterTransition: Any? = null

        var sharedElementReturnTransition: Any? = null

        var sharedElementEnterTransitionCallback: SharedElementCallback? = null

        var sharedElementExitTransitionCallback: SharedElementCallback? = null

    }

    fun applyTransitions(fragment: Fragment, screenDensity: ScreenDensity) {
        applyTransitions(fragment, getTransitions(screenDensity))
    }

    fun getTransitions(screenDensity: ScreenDensity): TransitionWrapper? {
        return when (screenDensity) {
            COMPACT -> { compat }
            MEDIUM -> { medium }
            EXPANDED -> { expanded }
        }
    }

    private fun applyTransitions(fragment: Fragment, transitionWrapper: TransitionWrapper?) {
        transitionWrapper.ifNotNull {
            fragment.applyTransitionWrapper(it)
        }
    }

    private fun Fragment.applyTransitionWrapper(transitionWrapper: TransitionWrapper) {
        transitionWrapper.enterTransition?.ifNotNull {
            enterTransition = it
        }
        transitionWrapper.returnTransition?.ifNotNull {
            returnTransition = it
        }
        transitionWrapper.exitTransition?.ifNotNull {
            exitTransition = it
        }
        transitionWrapper.reenterTransition?.ifNotNull {
            reenterTransition = it
        }
        transitionWrapper.sharedElementEnterTransition?.ifNotNull {
            sharedElementEnterTransition = it
        }
        transitionWrapper.sharedElementReturnTransition?.ifNotNull {
            sharedElementReturnTransition = it
        }
        transitionWrapper.sharedElementEnterTransitionCallback?.ifNotNull {
            setEnterSharedElementCallback(it)
        }
        transitionWrapper.sharedElementExitTransitionCallback?.ifNotNull {
            setExitSharedElementCallback(it)
        }
    }

}