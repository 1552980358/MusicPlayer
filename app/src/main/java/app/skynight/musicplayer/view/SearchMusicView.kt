package app.skynight.musicplayer.view

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.R
import app.skynight.musicplayer.activity.PlayerActivity
import app.skynight.musicplayer.base.InitConstructorNotAllowedException
import app.skynight.musicplayer.broadcast.BroadcastBase
import app.skynight.musicplayer.util.MusicInfo
import app.skynight.musicplayer.util.Player

/**
 * @File    : SearchMusicView
 * @Author  : 1552980358
 * @Date    : 21 Aug 2019
 * @TIME    : 5:32 PM
 **/
class SearchMusicView: LinearLayout {

    constructor(context: Context, playList: Int, index: Int, musicInfo: MusicInfo): super(context) {
        orientation = VERTICAL
        background = ContextCompat.getDrawable(context, R.drawable.ripple_effect)
        isClickable = true
        isFocusable = true

        // Title
        addView(AppCompatTextView(context).apply {
            setSingleLine()
            setTextColor(Player.ThemeTextColor)
            text = musicInfo.title()
            textSize = resources.getDimension(R.dimen.musicView_title_size)
            setHorizontallyScrolling(true)
            marqueeRepeatLimit = -1
            ellipsize = TextUtils.TruncateAt.MARQUEE
            isSelected = true
        }, LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        // Subtitle
        addView(AppCompatTextView(context).apply {
            setSingleLine()
            text = musicInfo.artist()
            setTextColor(Player.ThemeTextColor)
            textSize = resources.getDimension(R.dimen.musicView_subTitle_size)
            marqueeRepeatLimit = -1
            ellipsize = TextUtils.TruncateAt.MARQUEE
        }, LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        setOnClickListener {
            MainApplication.getMainApplication().apply {
                sendBroadcast(
                    Intent(BroadcastBase.CLIENT_BROADCAST_CHANGE).putExtra(BroadcastBase.BROADCAST_INTENT_PLAYLIST, playList)
                        .putExtra(BroadcastBase.BROADCAST_INTENT_MUSIC, index)
                )
                startActivity(Intent(this, PlayerActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        }
    }

    /* Not allowed */
    constructor(context: Context): this(context, null)
    @Suppress("UNUSED_PARAMETER")
    constructor(context: Context, attributeSet: AttributeSet?): super(context) {
        throw InitConstructorNotAllowedException()
    }
}