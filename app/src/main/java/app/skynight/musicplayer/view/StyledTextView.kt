package app.skynight.musicplayer.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.R

/**
 * @File    : StyledTextView
 * @Author  : 1552980358
 * @Date    : 31 Jul 2019
 * @TIME    : 5:40 PM
 **/
class StyledTextView : AppCompatTextView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        setTextColor(
            ContextCompat.getColor(
                context, if (MainApplication.customize) {
                    R.color.white
                } else R.color.black
            )
        )
    }
}