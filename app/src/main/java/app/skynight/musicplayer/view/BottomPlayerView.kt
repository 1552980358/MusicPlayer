package app.skynight.musicplayer.view

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.R
import app.skynight.musicplayer.util.UnitUtil.Companion.getPx
import app.skynight.musicplayer.view.StyledTextView
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

    lateinit var textView_title: StyledTextView
    lateinit var textView_subTitle: StyledTextView

    //lateinit var imageButton_list : AppCompatImageButton
    lateinit var checkBox_controller: AppCompatCheckBox

    private lateinit var linearLayout_Root: LinearLayout

    private fun createView() {
        orientation = VERTICAL
        addView(
            View(context).apply {
                background = ContextCompat.getDrawable(
                    context,
                    if (MainApplication.customize) R.color.white else R.color.black
                )
            },
            LayoutParams(
                MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.bottomPlayerView_divider)
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

                    addView(AppCompatImageView(context).apply {
                        imageView_album = this
                        setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play))
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

                        addView(StyledTextView(context).apply {
                            textView_title = this
                            textSize = resources.getDimension(R.dimen.bottomPlayerView_title_size)
                            //text = "title"
                        }, LayoutParams(MATCH_PARENT, WRAP_CONTENT))
                        addView(StyledTextView(context).apply {
                            textView_subTitle = this
                            //text = "subtitle"
                            textSize =
                                resources.getDimension(R.dimen.bottomPlayerView_subTitle_size)
                        }, LayoutParams(MATCH_PARENT, WRAP_CONTENT))

                    }, LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
                        setMargins(
                            resources.getDimensionPixelSize(R.dimen.bottomPlayerView_text_margin),
                            resources.getDimensionPixelSize(R.dimen.bottomPlayerView_text_margin),
                            resources.getDimensionPixelSize(R.dimen.bottomPlayerView_text_margin),
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
                            ContextCompat.getDrawable(context, R.drawable.ic_mini_play_last_def)
                    }, LayoutParams(getPx(30), getPx(30)).apply {
                        //setMargins(getPx(5), getPx(7), getPx(5), getPx(5))
                    })

                    addView(AppCompatCheckBox(context).apply {
                        checkBox_controller = this
                        buttonDrawable = null
                        background =
                            ContextCompat.getDrawable(context, R.drawable.ic_mini_play_control_def)
                        setOnClickListener {
                            context.sendBroadcast(Intent(if (isChecked) PLAYER_BROADCAST_ONSTART else PLAYER_BROADCAST_ONPAUSE))
                        }
                        setOnLongClickListener {
                            context.sendBroadcast(Intent(PLAYER_BROADCAST_ONSTOP))
                            true
                        }
                    }, LayoutParams(getPx(35), getPx(35)).apply {
                        // gravity = Gravity.CENTER_VERTICAL
                        //setMargins(getPx(5), getPx(5), getPx(5), getPx(5))
                    })
                    addView(AppCompatImageButton(context).apply {
                        //imageButton_next = this
                        background =
                            ContextCompat.getDrawable(context, R.drawable.ic_mini_play_next_def)
                        setOnClickListener {

                        }
                    }, LayoutParams(getPx(30), getPx(30)).apply {
                        //setMargins(getPx(5), getPx(7), getPx(5), getPx(5))
                    })
                }, LayoutParams(WRAP_CONTENT, MATCH_PARENT).apply {
                    //weight = 72f
                })
            },
            LayoutParams(
                MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.bottomPlayerView_object)
            )
        )
    }

    constructor(context: Context) : this(context, null)
    @Suppress("UNUSED_PARAMETER")
    constructor(context: Context, attributeSet: AttributeSet?) : super(context) {
        background = if (MainApplication.customize) ContextCompat.getDrawable(
            context, R.color.player_widget_bg
        ) else ContextCompat.getDrawable(context, R.color.colorPrimaryDark)
        createView()
    }

    override fun setOnClickListener(l: OnClickListener) {
        linearLayout_Root.setOnClickListener(l)
    }
}