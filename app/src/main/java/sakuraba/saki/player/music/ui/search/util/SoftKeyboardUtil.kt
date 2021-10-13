package sakuraba.saki.player.music.ui.search.util

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager

object SoftKeyboardUtil {

    private val Activity.peekDecorView get() = window?.peekDecorView()

    private val Context.inputMethodManager get() = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

    private fun InputMethodManager.hideSoftKeyboard(view: View?) = view?.let { hideSoftInputFromWindow(it.windowToken, 0) }

    fun Activity.hideSoftKeyboard() = inputMethodManager.hideSoftKeyboard(peekDecorView)

}