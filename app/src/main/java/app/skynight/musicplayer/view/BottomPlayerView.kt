package app.skynight.musicplayer.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.R
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_LAST
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_NEXT
import app.skynight.musicplayer.util.getPx
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_ONPAUSE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_ONSTART
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.CLIENT_BROADCAST_ONSTOP
import app.skynight.musicplayer.util.Player
import app.skynight.musicplayer.util.Player.Companion.Theme_0

/**
 * @FILE:   BottomPlayerView
 * @AUTHOR: 1552980358
 * @DATE:   19 Jul 2019
 * @TIME:   8:42 AM
 **/

@Suppress("PropertyName")
class BottomPlayerView : LinearLayout {

    lateinit var imageView_album: MusicAlbumRoundedImageView

    lateinit var textView_title: AppCompatTextView
    lateinit var textView_subTitle: AppCompatTextView

    lateinit var checkBox_controller: AppCompatCheckBox

    lateinit var linearLayout_Root: LinearLayout

    private fun createView() {
        orientation = VERTICAL
        addView(
            View(context).apply {
                background = ContextCompat.getDrawable(
                    context, if (MainApplication.customize) R.color.white else R.color.black
                )
            }, LayoutParams(
                MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.bottomPlayerView_divider)
            )
        )
        addView(
            LinearLayout(context).apply {
                orientation = HORIZONTAL
                gravity = Gravity.CENTER
                isClickable = true
                isFocusable = true
                background = ContextCompat.getDrawable(context, R.drawable.ripple_effect)
                linearLayout_Root = this

                addView(LinearLayout(context).apply {
                    orientation = HORIZONTAL

                    addView(MusicAlbumRoundedImageView(context).apply {
                        imageView_album = this
                        size = resources.getDimensionPixelSize(R.dimen.bottomPlayerView_size)
                        //setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_player_play))
                    }, LayoutParams(
                        resources.getDimensionPixelSize(R.dimen.bottomPlayerView_size),
                        resources.getDimensionPixelSize(R.dimen.bottomPlayerView_size)
                    ).apply {
                        setMargins(
                            resources.getDimensionPixelSize(R.dimen.bottomPlayerView_marginLeft),
                            0,
                            0,
                            0
                        )
                        gravity = Gravity.CENTER_VERTICAL
                    })

                    addView(LinearLayout(context).apply {
                        orientation = VERTICAL

                        addView(AppCompatTextView(context).apply {
                            textView_title = this
                            setTextColor(Player.ThemeTextColor)
                            textSize = resources.getDimension(R.dimen.bottomPlayerView_title_size)
                            //text = "title"
                            setSingleLine()
                            setHorizontallyScrolling(true)
                            marqueeRepeatLimit = -1
                            ellipsize = TextUtils.TruncateAt.MARQUEE
                            isSelected = true
                        }, LayoutParams(MATCH_PARENT, WRAP_CONTENT))

                        addView(AppCompatTextView(context).apply {
                            textView_subTitle = this
                            //text = "subtitle"
                            setTextColor(Player.ThemeTextColor)
                            textSize =
                                resources.getDimension(R.dimen.bottomPlayerView_subTitle_size)
                            setSingleLine()
                            ellipsize = TextUtils.TruncateAt.END
                        }, LayoutParams(MATCH_PARENT, WRAP_CONTENT))

                    }, LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
                        setMargins(
                            resources.getDimensionPixelSize(R.dimen.bottomPlayerView_text_margin),
                            resources.getDimensionPixelSize(R.dimen.bottomPlayerView_text_margin),
                            0, //resources.getDimensionPixelSize(R.dimen.bottomPlayerView_text_margin),
                            resources.getDimensionPixelSize(R.dimen.bottomPlayerView_text_margin)
                        )
                    })
                }, LayoutParams(0, MATCH_PARENT).apply {
                    weight = 1f
                })

                addView(LinearLayout(context).apply {

                    orientation = HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL

                    addView(AppCompatImageButton(context).apply {
                        //imageButton_last = this
                        background =
                            ContextCompat.getDrawable(context, R.drawable.ic_playerbar_last)
                        isClickable = true
                        isFocusable = true
                        setOnClickListener {
                            context.sendBroadcast(Intent(CLIENT_BROADCAST_LAST))
                        }
                    }, LayoutParams(getPx(30), getPx(30)))

                    addView(AppCompatCheckBox(context).apply {
                        checkBox_controller = this
                        buttonDrawable = null
                        background =
                            ContextCompat.getDrawable(context, R.drawable.ic_playerbar_ctrl)
                        setOnClickListener {
                            context.sendBroadcast(Intent(if (isChecked) CLIENT_BROADCAST_ONSTART else CLIENT_BROADCAST_ONPAUSE))
                        }
                        setOnLongClickListener {
                            context.sendBroadcast(Intent(CLIENT_BROADCAST_ONSTOP))
                            true
                        }
                    }, LayoutParams(getPx(45), getPx(45)))
                    addView(AppCompatImageButton(context).apply {
                        //imageButton_next = this
                        background =
                            ContextCompat.getDrawable(context, R.drawable.ic_playerbar_next)
                        isClickable = true
                        isFocusable = true
                        setOnClickListener {
                            context.sendBroadcast(Intent(CLIENT_BROADCAST_NEXT))
                        }
                    }, LayoutParams(getPx(30), getPx(30)))
                }, LayoutParams(WRAP_CONTENT, MATCH_PARENT))
            }, LayoutParams(
                MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.bottomPlayerView_object)
            )
        )
    }

    constructor(context: Context) : this(context, null)
    @Suppress("UNUSED_PARAMETER")
    constructor(context: Context, attributeSet: AttributeSet?) : super(context) {
        background =  ContextCompat.getDrawable(context, if (Player.settings[Player.Theme] != Theme_0) R.color.theme1_colorPrimary else R.color.white)
        createView()
    }

    fun setRootOnClickListener(l: OnClickListener) {
        linearLayout_Root.setOnClickListener(l)
    }
}