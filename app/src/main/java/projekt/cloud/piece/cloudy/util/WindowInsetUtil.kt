package projekt.cloud.piece.cloudy.util

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.updatePadding

object WindowInsetUtil {

    /**
     * [android.view.View.requestInsets]
     * @param block [kotlin.jvm.functions.Function1]<[androidx.core.view.WindowInsetsCompat], [Unit]>
     *
     * Implementation of requesting insets
     **/
    private fun View.requestInsets(block: (WindowInsetsCompat) -> Unit) {
        ViewCompat.setOnApplyWindowInsetsListener(this) { _, windowInsetsCompat ->
            block.invoke(windowInsetsCompat)
            windowInsetsCompat
        }
    }

    /**
     * [android.view.View.applySystemBarsInsets]
     *
     * Apply system bars insets to view's top & bottom
     **/
    fun View.applySystemBarsInsets() {
        applySystemBarsInsets(paddingTop, paddingBottom)
    }

    /**
     * [androidx.core.view.WindowInsetsCompat.systemBarsInsets]
     * @return [androidx.core.graphics.Insets]
     *
     * Get system insets of system bars
     **/
    private val WindowInsetsCompat.systemBarsInsets
        get() = getInsets(Type.systemBars())

    /**
     * [android.view.View.applySystemBarsInsets]
     * @param paddingTop [Int]
     * @param paddingBottom [Int]
     *
     * Implementation of [View.applySystemBarsInsets]
     **/
    private fun View.applySystemBarsInsets(paddingTop: Int, paddingBottom: Int) {
        requestInsets { windowInsetsCompat ->
            windowInsetsCompat.systemBarsInsets
                .let { insets ->
                    updatePadding(
                        top = paddingTop + insets.top,
                        bottom = paddingBottom + insets.bottom
                    )
                }
        }
    }

    /**
     * [androidx.core.view.WindowInsetsCompat.insetStatusBar]
     * @return [Int]
     *
     * Get status bar inset
     **/
    private val WindowInsetsCompat.insetStatusBar: Int
        get() = systemBarsInsets.top

    /**
     * [android.view.View.applyStatusBarInset]
     *
     * Apply status bar inset to view's top
     **/
    fun View.applyStatusBarInset() {
        paddingTop.let { paddingTop ->
            requestInsets { windowInsetsCompat ->
                updatePadding(top = paddingTop + windowInsetsCompat.insetStatusBar)
            }
        }
    }

    /**
     * [androidx.core.view.WindowInsetsCompat.insetNavigationBar]
     * @return [Int]
     *
     * Get navigation bar inset
     **/
    private val WindowInsetsCompat.insetNavigationBar: Int
        get() = systemBarsInsets.bottom

    /**
     * [android.view.View.applyNavigationBarInset]
     *
     * Apply navigation bar inset to view's bottom
     **/
    fun View.applyNavigationBarInset() {
        paddingBottom.let { paddingBottom ->
            requestInsets { windowInsetsCompat ->
                updatePadding(bottom = paddingBottom + windowInsetsCompat.insetNavigationBar)
            }
        }
    }

}

