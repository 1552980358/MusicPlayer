package sakuraba.saki.player.music.ui.home

import android.view.View
import android.view.ViewTreeObserver
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import lib.github1552980358.ktExtension.androidx.coordinatorlayout.widget.makeSnack
import sakuraba.saki.player.music.R

class HomeViewModel: ViewModel() {
    
    private var _snackbar: Snackbar? = null
    fun initSnackBar(fragmentActivity: FragmentActivity, listener: View.OnClickListener) {
        _snackbar = fragmentActivity.findViewById<CoordinatorLayout>(R.id.coordinator_layout)
            ?.makeSnack(R.string.home_snack_open_setting_text, BaseTransientBottomBar.LENGTH_INDEFINITE)
            ?.setAction(R.string.home_snack_open_setting_button, listener)?.apply {
                view.viewTreeObserver.addOnPreDrawListener(object: ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        snackbar.view.viewTreeObserver.removeOnPreDrawListener(this)
                        (snackbar.view.layoutParams as CoordinatorLayout.LayoutParams).behavior = null
                        return true
                    }
                })
            }
    }
    val snackbar get() = _snackbar!!
    
}