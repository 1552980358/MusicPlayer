package app.skynight.musicplayer.base

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.RelativeLayout
import app.skynight.musicplayer.R
import app.skynight.musicplayer.activity.PlayerActivity
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_MUSICCHANGE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_ONPAUSE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_ONSTART
import app.skynight.musicplayer.util.Player
import app.skynight.musicplayer.view.BottomPlayerView
import java.lang.Exception

/**
 * @FILE:   BaseSmallPlayerActivity
 * @AUTHOR: 1552980358
 * @DATE:   20 Jul 2019
 * @TIME:   4:47 PM
 **/

@SuppressLint("Registered")
open class BaseSmallPlayerActivity : BaseAppCompatActivity() {

    private lateinit var smallPlayerBroadcastReceiver: BroadcastReceiver
    lateinit var bottomPlayerView: BottomPlayerView
    lateinit var relativeLayout: RelativeLayout

    override fun setContentView(layoutResID: Int) {
        setContentView(LayoutInflater.from(this).inflate(layoutResID, null, false))
    }

    /* Execute after setContentView when needed, otherwise useless */
    @Suppress("unused")
    fun setFitSystemWindows() {
        try {
            relativeLayout.fitsSystemWindows = true
        } catch (e: Exception) {
            throw Exception("LayoutNotInitException")
        }
    }

    /* Filter the origin method with */
    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        throw Exception("SetViewGroupLayoutParamsNotAllowed")
    }

    override fun setContentView(view: View?) {
        val layoutParams: RelativeLayout.LayoutParams

        super.setContentView(RelativeLayout(this).apply {
            addView(BottomPlayerView(this@BaseSmallPlayerActivity).also {
                relativeLayout = this
                bottomPlayerView = it
                it.id = View.generateViewId()
                bottomPlayerView.setRootOnClickListener(View.OnClickListener {
                    bottomPlayerView.linearLayout_Root.isClickable = false
                    //MainApplication.playerForeground = true
                    startActivity(
                        Intent(
                            this@BaseSmallPlayerActivity,
                            PlayerActivity::class.java
                        )
                    )
                    //overridePendingTransition(R.anim.anim_player_down2top, R.anim.anim_last_down2top)
                })
                layoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
                    addRule(RelativeLayout.ABOVE, it.id)
                }
            }, RelativeLayout.LayoutParams(
                MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.bottomPlayerView_height)
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            })
            addView(view!!, layoutParams)
        })

        Thread {
            smallPlayerBroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    intent ?: return
                    when (intent.action) {
                        SERVER_BROADCAST_ONSTART -> {
                            bottomPlayerView.checkBox_controller.isChecked = true
                        }
                        SERVER_BROADCAST_ONPAUSE -> {
                            bottomPlayerView.checkBox_controller.isChecked = false
                        }
                        SERVER_BROADCAST_MUSICCHANGE -> {
                            val info = Player.getCurrentMusicInfo()
                            bottomPlayerView.checkBox_controller.isChecked = Player.getPlayer.isPlaying()
                            bottomPlayerView.textView_title.text = info.title()
                            bottomPlayerView.textView_subTitle.text = info.artist()
                            bottomPlayerView.imageView_album.setImageBitmap(info.albumPic())

                        }
                    }
                }
            }
        }.start()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(smallPlayerBroadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        //Log.e("SmallPlayerRegister", "$taskId")
        registerReceiver(smallPlayerBroadcastReceiver, IntentFilter().apply {
            addAction(SERVER_BROADCAST_MUSICCHANGE)
            addAction(SERVER_BROADCAST_ONSTART)
            addAction(SERVER_BROADCAST_ONPAUSE)
        })
        try {
            val info = Player.getCurrentMusicInfo()
            bottomPlayerView.textView_title.text = info.title()
            bottomPlayerView.textView_subTitle.text = info.artist()
            bottomPlayerView.imageView_album.setImageBitmap(info.albumPic())
            bottomPlayerView.checkBox_controller.isChecked = Player.getPlayer.isPlaying()
            bottomPlayerView.linearLayout_Root.isClickable = true
        } catch (e: Exception) {
            //
        }
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(smallPlayerBroadcastReceiver)
            //Log.e("SmallPlayerUnRegister", "$taskId")
        } catch (e: Exception) {
            //e.printStackTrace()
            //Log.e("SmallPlayerUnRegister", "$taskId")
        }
        super.onDestroy()
    }
}