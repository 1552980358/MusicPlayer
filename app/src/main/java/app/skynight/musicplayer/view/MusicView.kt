package app.skynight.musicplayer.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.TextView
import app.skynight.musicplayer.base.InitConstructorNotAllowedException
import app.skynight.musicplayer.util.MusicInfo
import app.skynight.musicplayer.R

/**
 * @File    : MusicView
 * @Author  : 1552980358
 * @Date    : 4 Aug 2019
 * @TIME    : 9:35 AM
 **/
class MusicView: LinearLayout {
    private lateinit var textView_title: StyledTextView
    private lateinit var textView_subTitle: StyledTextView

    constructor(context: Context, index: Int,musicInfo: MusicInfo): super(context) {
        orientation = HORIZONTAL
        addView(TextView(context).apply {
            gravity = Gravity.CENTER
            text = index.toString()
        }, LayoutParams(0, resources.getDimensionPixelSize(R.dimen.musicView_height)).apply {
            weight = 1f
        })
        addView(LinearLayout(context).apply {
            orientation = VERTICAL

            // Title
            addView(StyledTextView(context).apply {
                textView_title = this
                text = musicInfo.title
                textSize = resources.getDimension(R.dimen.musicView_title_size)
            })

            // Subtitle
            addView(StyledTextView(context).apply {
                textView_subTitle = this
                text = musicInfo.artist
                textSize = resources.getDimension(R.dimen.musicView_subTitle_size)
            })
        })
    }

    /* Not allowed */
    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): super(context) {
        throw InitConstructorNotAllowedException()
    }
}