package app.skynight.musicplayer.base

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.R
import app.skynight.musicplayer.activity.PlayerActivity
import app.skynight.musicplayer.util.UnitUtil
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.SERVER_BROADCAST_MUSICCHANGE
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.SERVER_BROADCAST_ONPAUSE
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.SERVER_BROADCAST_ONSTART
import app.skynight.musicplayer.view.BottomPlayerView
import java.io.File
import java.lang.Exception

/**
 * @FILE:   BaseSmallPlayerActivity
 * @AUTHOR: 1552980358
 * @DATE:   20 Jul 2019
 * @TIME:   4:47 PM
 **/

@SuppressLint("Registered")
open class BaseSmallPlayerActivity : BaseAppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var smallPlayerBroadcastReceiver: BroadcastReceiver
    lateinit var bottomPlayerView: BottomPlayerView

    override fun setContentView(layoutResID: Int) {
        setContentView(LayoutInflater.from(this).inflate(layoutResID, null))
    }

    override fun setContentView(view: View?) {
        val layoutParams: RelativeLayout.LayoutParams

        super.setContentView(DrawerLayout(this).apply {
            drawerLayout = this
            background = MainApplication.bgDrawable!!
            //background =
            addView(RelativeLayout(this@BaseSmallPlayerActivity).apply {
                fitsSystemWindows = true
                addView(RelativeLayout(this@BaseSmallPlayerActivity).apply {
                    background = ColorDrawable(if (MainApplication.customize) ContextCompat.getColor(this@BaseSmallPlayerActivity, android.R.color.transparent) else ContextCompat.getColor(this@BaseSmallPlayerActivity, R.color.activity_background))
                    addView(BottomPlayerView(this@BaseSmallPlayerActivity).also {
                        bottomPlayerView = it
                        it.id = View.generateViewId()
                        bottomPlayerView.setOnClickListener {
                            MainApplication.playerForeground = true
                            startActivity(Intent(this@BaseSmallPlayerActivity, PlayerActivity::class.java))
                            overridePendingTransition(R.anim.anim_down2top, R.anim.anim_no_action)
                        }
                        layoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
                            addRule(RelativeLayout.ABOVE, it.id)
                        }
                    }, RelativeLayout.LayoutParams(MATCH_PARENT, UnitUtil.getPx(50)).apply {
                        addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    })
                    addView(view!!, layoutParams)
                }, RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
            }, DrawerLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        })

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

                    }
                }
            }
        }
    }

    @Suppress("unused")
    fun setBackground(drawable: Drawable) {
        drawerLayout.background = drawable
    }

    @Suppress("unused")
    fun getBackground(): Drawable {
        return drawerLayout.background
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
    }

    override fun onStart() {
        if (MainApplication.playerForeground) {
            startActivity(Intent(this, PlayerActivity::class.java))
            overridePendingTransition(R.anim.anim_down2top, R.anim.anim_no_action)
        }
        super.onStart()
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(smallPlayerBroadcastReceiver)
            Log.e("SmallPlayerUnRegister", "$taskId")
        } catch (e: Exception) {
            //Log.e("SmallPlayerUnRegister", "$taskId")
        }
        super.onDestroy()
    }
}