package app.skynight.musicplayer.activity

import android.content.*
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.Gravity
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.R
import app.skynight.musicplayer.util.UnitUtil.Companion.getPx
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.PLAYER_BROADCAST_LAST
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.PLAYER_BROADCAST_NEXT
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.PLAYER_BROADCAST_ONPAUSE
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.PLAYER_BROADCAST_ONSTART
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.SERVER_BROADCAST_MUSICCHANGE
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.SERVER_BROADCAST_ONPAUSE
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.SERVER_BROADCAST_ONSTART
import app.skynight.musicplayer.util.Player
import app.skynight.musicplayer.util.UnitUtil.Companion.getTime
import app.skynight.musicplayer.view.MusicAlbumRoundedImageView
import com.google.android.material.appbar.AppBarLayout

/**
 * @FILE:   PlayerActivity
 * @AUTHOR: 1552950358
 * @DATE:   18 Jul 2019
 * @TIME:   7:23 PM
 **/

class PlayerActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var musicAlbum: MusicAlbumRoundedImageView

    private lateinit var timePass: AppCompatTextView
    private lateinit var timeTotal: AppCompatTextView
    private lateinit var timeControl: AppCompatSeekBar

    private lateinit var playForm: AppCompatImageButton
    private lateinit var playLast: AppCompatImageButton
    private lateinit var playNext: AppCompatImageButton
    private lateinit var playList: AppCompatImageButton
    private lateinit var playCont: AppCompatCheckBox

    private fun createView(): View {
        return DrawerLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            background = ColorDrawable(ContextCompat.getColor(this@PlayerActivity, android.R.color.holo_purple))
            //rootLayout = this
            addView(LinearLayout(this@PlayerActivity).apply {
                orientation = LinearLayout.VERTICAL
                fitsSystemWindows = true
                // Toolbar
                addView(AppBarLayout(this@PlayerActivity).apply {
                    background = ColorDrawable(Color.TRANSPARENT)
                    stateListAnimator = null
                    addView(Toolbar(this@PlayerActivity).apply {
                        toolbar = this
                        navigationIcon = ContextCompat.getDrawable(this@PlayerActivity, R.drawable.ic_player_exit)
                        popupTheme = R.style.AppTheme_PopupOverlay
                        title = ""
                        try {
                            (this.javaClass.getDeclaredField("mTitleTextView").apply { isAccessible = true }.get(this) as TextView).apply {
                                setHorizontallyScrolling(true)
                                marqueeRepeatLimit = -1
                                ellipsize = TextUtils.TruncateAt.MARQUEE
                                isSelected = true
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        subtitle = ""
                        setTitleTextColor(ContextCompat.getColor(this@PlayerActivity, R.color.player_title))
                        setSubtitleTextColor(ContextCompat.getColor(this@PlayerActivity, R.color.player_subtitle))
                    }, AppBarLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
                }, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))

                addView(LinearLayout(this@PlayerActivity).apply {
                    orientation = LinearLayout.VERTICAL
                    /*
                    addView(FrameLayout(this@PlayerActivity), LinearLayout.LayoutParams(MATCH_PARENT, 0).apply {
                        weight = 8f
                    })
                    */
                    addView(RelativeLayout(this@PlayerActivity).apply {
                        addView(MusicAlbumRoundedImageView(this@PlayerActivity).apply {
                            musicAlbum = this
                        }, RelativeLayout.LayoutParams(this@PlayerActivity.resources.displayMetrics.widthPixels * 2 / 3, this@PlayerActivity.resources.displayMetrics.widthPixels * 2 / 3).apply {
                            addRule(RelativeLayout.CENTER_VERTICAL)
                            addRule(RelativeLayout.CENTER_HORIZONTAL)
                        })/*
                        addView(ImageView(this@PlayerActivity).apply {
                            setImageDrawable(ContextCompat.getDrawable(this@PlayerActivity, R.drawable.unknown))
                        })*/
                    }, LinearLayout.LayoutParams(MATCH_PARENT, 0).apply {
                        weight = 8f
                    })

                    addView(LinearLayout(this@PlayerActivity).apply {
                        orientation = LinearLayout.VERTICAL

                        addView(LinearLayout(this@PlayerActivity).apply {
                            orientation = LinearLayout.HORIZONTAL

                            addView(AppCompatTextView(this@PlayerActivity).apply {
                                timePass = this
                                gravity = Gravity.CENTER
                                setTextColor(ContextCompat.getColor(this@PlayerActivity, android.R.color.white))
                            }, LinearLayout.LayoutParams(0, WRAP_CONTENT).apply {
                                weight = 1f
                            })
                            addView(AppCompatSeekBar(this@PlayerActivity).apply {
                                timeControl = this
                            }, LinearLayout.LayoutParams(0, WRAP_CONTENT).apply {
                                weight = 8f
                            })
                            addView(AppCompatTextView(this@PlayerActivity).apply {
                                timeTotal = this
                                gravity = Gravity.CENTER
                                setTextColor(ContextCompat.getColor(this@PlayerActivity, android.R.color.white))
                            }, LinearLayout.LayoutParams(0, WRAP_CONTENT).apply {
                                weight = 1f
                            })
                        }, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))

                        addView(LinearLayout(this@PlayerActivity).apply {
                            orientation = LinearLayout.HORIZONTAL
                            gravity = Gravity.CENTER

                            val playerActivity_subItem_marginLeftRightWider = resources.getDimensionPixelSize(R.dimen.playerActivity_subItem_marginLeftRightWider)
                            val playerActivity_subItem_marginLeftRightNarrower = resources.getDimensionPixelSize(R.dimen.playerActivity_subItem_marginLeftRightNarrower)
                            val playerActivity_subItem_size = resources.getDimensionPixelSize(R.dimen.playerActivity_subItem_size)
                            val playerActivity_last_next_size = resources.getDimensionPixelSize(R.dimen.playerActivity_last_next_size)
                            val playerActivity_controller_size = resources.getDimensionPixelSize(R.dimen.playerActivity_controller_size)
                            val playerActivity_last_next_marginLeftRight = resources.getDimensionPixelSize(R.dimen.playerActivity_last_next_marginLeftRight)

                            // state
                            addView(AppCompatImageButton(this@PlayerActivity).apply {
                                playForm = this
                            }, LinearLayout.LayoutParams(playerActivity_subItem_size, playerActivity_subItem_size).apply {
                                setMargins(playerActivity_subItem_marginLeftRightWider, 0, playerActivity_subItem_marginLeftRightNarrower, 0)
                            })

                            // last
                            addView(AppCompatImageButton(this@PlayerActivity).apply {
                                playLast = this
                                background = ContextCompat.getDrawable(this@PlayerActivity, R.drawable.ic_play_last)
                                setOnClickListener { sendBroadcast(Intent(PLAYER_BROADCAST_LAST)) }
                            }, LinearLayout.LayoutParams(playerActivity_last_next_size, playerActivity_last_next_size).apply {
                                setMargins(playerActivity_subItem_marginLeftRightNarrower, 0, playerActivity_last_next_marginLeftRight, 0)
                            })

                            // control
                            addView(AppCompatCheckBox(this@PlayerActivity).apply {
                                playCont = this
                                buttonDrawable = null
                                background = ContextCompat.getDrawable(this@PlayerActivity, R.drawable.ic_play_control)

                                setOnClickListener {
                                    sendBroadcast(Intent(if (isChecked) PLAYER_BROADCAST_ONSTART else PLAYER_BROADCAST_ONPAUSE))
                                }
                            }, LinearLayout.LayoutParams(playerActivity_controller_size, playerActivity_controller_size).apply {
                                setMargins(playerActivity_last_next_marginLeftRight, 0, playerActivity_last_next_marginLeftRight, 0)
                            })

                            // next
                            addView(AppCompatImageButton(this@PlayerActivity).apply {
                                playNext = this
                                background = ContextCompat.getDrawable(this@PlayerActivity, R.drawable.ic_play_next)
                                setOnClickListener { sendBroadcast(Intent(PLAYER_BROADCAST_NEXT)) }
                            }, LinearLayout.LayoutParams(playerActivity_last_next_size, playerActivity_last_next_size).apply {
                                setMargins(playerActivity_last_next_marginLeftRight, 0, playerActivity_subItem_marginLeftRightNarrower, 0)
                            })

                            addView(AppCompatImageButton(this@PlayerActivity).apply {
                                playList = this
                                background = ContextCompat.getDrawable(this@PlayerActivity, R.drawable.ic_play_list)
                            }, LinearLayout.LayoutParams(playerActivity_subItem_size, playerActivity_subItem_size).apply {
                                setMargins(playerActivity_subItem_marginLeftRightNarrower, getPx(2), resources.getDimensionPixelSize(R.dimen.playerActivity_subItem_marginLeftRightWider), 0)
                            })

                        }, LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))

                    }, LinearLayout.LayoutParams(MATCH_PARENT, 0).apply {
                        weight = 2f
                    })
                }, LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))

            }, DrawerLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        }
    }

    private fun setBackgroundProp() {
        window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE)

        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
    }
    
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        //Log.e("PlayerActivity", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(createView())
        //setContentView(R.layout.activity_player)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        setBackgroundProp()
        registerReceiver()
    }
    
    private fun registerReceiver() {
        registerReceiver(if (::broadcastReceiver.isInitialized) {broadcastReceiver} else {object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?:return
                when (intent.action) {
                    SERVER_BROADCAST_ONSTART -> { playCont.isChecked = true }
                    SERVER_BROADCAST_ONPAUSE -> { playCont.isChecked = false }
                    SERVER_BROADCAST_MUSICCHANGE -> {
                        val musicInfo = Player.musicList[Player.currentMusic]
                        toolbar.title = musicInfo.title
                        toolbar.subtitle = musicInfo.artist
                        musicAlbum.setImageBitmap(musicInfo.albumPic())
                        timeTotal.text = getTime(musicInfo.duration)
                    }
                }
            }
        }.apply { broadcastReceiver = this }}, IntentFilter().apply {
            addAction(SERVER_BROADCAST_ONSTART)
            addAction(SERVER_BROADCAST_ONPAUSE)
            addAction(SERVER_BROADCAST_MUSICCHANGE)
        })
    }

    private fun unregisterReceiver() {
        unregisterReceiver(broadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        //Log.e("PlayerActivity", "onResume")
        registerReceiver()
    }

    override fun onPause() {
        //Log.e("PlayerActivity", "onPause")
        super.onPause()
        unregisterReceiver()
    }

    override fun onBackPressed() {
        //Log.e("PlayerActivity", "onBackPressed")
        //overridePendingTransition(0, R.anim.anim_top2down)
        MainApplication.playerForeground = false
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(R.anim.anim_down2top, R.anim.anim_no_action)
    }

    override fun finish() {
        onBackPressed()
    }

    override fun onDestroy() {
        //Log.e("PlayerActivity", "onDestroy")
        try {
            unregisterReceiver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
}
