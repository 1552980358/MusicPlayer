package projekt.cloud.piece.music.player.util

import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

object SnackBarUtil {
    
    @JvmStatic
    fun CoordinatorLayout.snackBar(text: String, length: Int = Snackbar.LENGTH_SHORT) =
        Snackbar.make(this, text, length)
    
    @JvmStatic
    fun CoordinatorLayout.snackBar(@StringRes resId: Int, length: Int = Snackbar.LENGTH_SHORT) =
        Snackbar.make(this, resId, length)
    
    @JvmStatic
    fun CoordinatorLayout.showSnack(text: String, length: Int = Snackbar.LENGTH_SHORT) =
        snackBar(text, length).show()
    
    @JvmStatic
    fun CoordinatorLayout.showSnack(@StringRes resId: Int, length: Int = Snackbar.LENGTH_SHORT) =
        snackBar(resId, length).show()
    
}