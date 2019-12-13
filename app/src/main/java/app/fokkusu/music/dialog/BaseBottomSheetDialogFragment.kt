package app.fokkusu.music.dialog

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import app.fokkusu.music.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * @File    : BaseBottomSheetDialogFragment
 * @Author  : 1552980358
 * @Date    : 2019/12/3
 * @TIME    : 18:23
 **/

open class BaseBottomSheetDialogFragment: BottomSheetDialogFragment() {
    private var activityFragmentManager: FragmentManager? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }
    
    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        throw IllegalAccessException()
    }
    
    @Suppress("unused")
    fun show(manager: FragmentManager) {
        showNow(manager, null)
    }
    
    override fun show(manager: FragmentManager, tag: String?) {
        showNow(manager, tag)
    }
    
    fun showNow(manager: FragmentManager) {
        showNow(manager, null)
    }
    
    override fun showNow(manager: FragmentManager, tag: String?) {
        activityFragmentManager = manager
        super.showNow(manager, tag)
    }
    
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        
        activityFragmentManager?.beginTransaction()?.remove(this)?.commit() ?: return
        
        // Remove FragmentManager
        activityFragmentManager = null
    }
}