package app.skynight.musicplayer.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.R

/**
 * @File    : CardView
 * @Author  : 1552980358
 * @Date    : 2 Aug 2019
 * @TIME    : 8:39 PM
 **/
class PlayListContainerView : LinearLayout {

    private var linearLayout: LinearLayout
    constructor(context: Context) : this(context, null)
    @Suppress("UNUSED_PARAMETER")
    constructor(context: Context, attributeSet: AttributeSet?) : super(context) {
        orientation = VERTICAL
        //background = ContextCompat.getDrawable(context, R.drawable.cardview_frame)
        super.addView(LinearLayout(context).apply {
            linearLayout = this
            orientation = VERTICAL
            background = ContextCompat.getDrawable(context, R.color.transparent)
        }, LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
            setPadding(resources.getDimensionPixelSize(R.dimen.cardView_item_margin))
        })
        super.addView(View(context).apply {
            background = ContextCompat.getDrawable(context,if (MainApplication.customize) R.color.white else R.color.black)
        }, LayoutParams(MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.dp_1)))
    }
    override fun addView(view: View) {
        linearLayout.addView(view)
    }
    fun addView(view: View, layoutParams: LayoutParams) {
        linearLayout.addView(view, layoutParams)
    }
}