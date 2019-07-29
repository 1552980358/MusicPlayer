package app.skynight.musicplayer.view

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.R
import app.skynight.musicplayer.util.UnitUtil.Companion.getPx
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.PLAYER_BROADCAST_ONPAUSE
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.PLAYER_BROADCAST_ONSTART
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.PLAYER_BROADCAST_ONSTOP

/**
 * @FILE:   BottomPlayerView
 * @AUTHOR: 1552980358
 * @DATE:   19 Jul 2019
 * @TIME:   8:42 AM
 **/

@Suppress("PrivatePropertyName", "MemberVisibilityCanBePrivate", "PropertyName")
class BottomPlayerView : LinearLayout {

    lateinit var imageView_album: AppCompatImageView

    lateinit var textView_title: AppCompatTextView
    lateinit var textView_subTitle: AppCompatTextView

    //lateinit var imageButton_list : AppCompatImageButton
    lateinit var checkBox_controller: AppCompatCheckBox

    private lateinit var linearLayotu_Root: LinearLayout

    private fun createView() {
        orientation = HORIZONTAL
        addView(LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            isClickable = true
            isFocusable = true
            background = ContextCompat.getDrawable(context, R.drawable.ripple_effect)
            linearLayotu_Root = this

            addView(LinearLayout(context).apply {
                orientation = HORIZONTAL

                addView(AppCompatImageView(context).apply {
                    imageView_album = this
                    setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play))
                }, LayoutParams(getPx(40), getPx(40)).apply {
                    setMargins(getPx(7), getPx(5), getPx(5), getPx(5))
                    gravity = Gravity.CENTER_VERTICAL
                })

                addView(LinearLayout(context).apply {
                    orientation = VERTICAL

                    addView(AppCompatTextView(context).apply {
                        textView_title = this
                        textSize = getPx(7f)
                        //text = "title"
                    }, LayoutParams(MATCH_PARENT, WRAP_CONTENT))
                    addView(AppCompatTextView(context).apply {
                        textView_subTitle = this
                        //text = "subtitle"
                        textSize = getPx(4f)
                    }, LayoutParams(MATCH_PARENT, WRAP_CONTENT))

                }, LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
                    setMargins(getPx(5), getPx(5), getPx(5), getPx(5))
                })
            }, LayoutParams(0, MATCH_PARENT).apply {
                weight = 222f
            })

            addView(LinearLayout(context).apply {

                orientation = HORIZONTAL
                gravity = Gravity.CENTER

                addView(AppCompatImageButton(context).apply {
                    //imageButton_last = this
                    background = ContextCompat.getDrawable(context, R.drawable.ic_play_last)
                }, LayoutParams(getPx(30), getPx(30)).apply {
                    setMargins(getPx(5), getPx(7), getPx(5), getPx(5))
                })

                addView(AppCompatCheckBox(context).apply {
                    checkBox_controller = this
                    buttonDrawable = null
                    background = ContextCompat.getDrawable(context, R.drawable.ic_play_control)
                    setOnClickListener {
                        context.sendBroadcast(Intent(if (isChecked) PLAYER_BROADCAST_ONSTART else PLAYER_BROADCAST_ONPAUSE))
                    }
                    setOnLongClickListener {
                        context.sendBroadcast(Intent(PLAYER_BROADCAST_ONSTOP))
                        true
                    }
                }, LayoutParams(getPx(35), getPx(35)).apply {
                    setMargins(getPx(5), getPx(5), getPx(5), getPx(5))
                })
                addView(AppCompatImageButton(context).apply {
                    //imageButton_next = this
                    background = ContextCompat.getDrawable(context, R.drawable.ic_play_next)
                    setOnClickListener {

                    }
                }, LayoutParams(getPx(30), getPx(30)).apply {
                    setMargins(getPx(5), getPx(7), getPx(5), getPx(5))
                })
            })
        }, LayoutParams(MATCH_PARENT, getPx(50)))
    }

    constructor(context: Context) : this(context, null)
    @Suppress("UNUSED_PARAMETER")
    constructor(context: Context, attributeSet: AttributeSet?) : super(context) {
        background = if (MainApplication.customize) ContextCompat.getDrawable(context, R.color.player_widget_bg) else ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        createView()
    }

    override fun setOnClickListener(l: OnClickListener) {
        linearLayotu_Root.setOnClickListener(l)
    }
}