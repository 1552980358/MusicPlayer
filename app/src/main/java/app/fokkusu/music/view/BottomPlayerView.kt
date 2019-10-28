package app.fokkusu.music.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.text.TextUtils.TruncateAt.MARQUEE
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import app.fokkusu.music.base.Constants.Companion.SERVICE_BROADCAST_CHANGED
import app.fokkusu.music.base.Constants.Companion.SERVICE_BROADCAST_PAUSE
import app.fokkusu.music.base.Constants.Companion.SERVICE_BROADCAST_PLAY
import app.fokkusu.music.R
import app.fokkusu.music.activity.PlayerActivity
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_CONTENT
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_PAUSE
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_PLAY
import app.fokkusu.music.base.activity.BaseAppCompatActivity
import app.fokkusu.music.base.log
import app.fokkusu.music.dialog.BottomPlaylistDialog
import app.fokkusu.music.service.PlayService
import app.fokkusu.music.service.PlayService.Companion.getCurrentMusicInfo
import app.fokkusu.music.service.PlayService.Companion.playerState
import app.fokkusu.music.service.PlayService.Companion.PlayState.PLAY
import kotlinx.android.synthetic.main.view_bottom_player.view.checkBox_playControl
import kotlinx.android.synthetic.main.view_bottom_player.view.imageButton_list
import kotlinx.android.synthetic.main.view_bottom_player.view.imageView_album
import kotlinx.android.synthetic.main.view_bottom_player.view.relativeLayout_container
import kotlinx.android.synthetic.main.view_bottom_player.view.textView_info
import kotlinx.android.synthetic.main.view_bottom_player.view.textView_title

/**
 * @File    : BottomPlayerView
 * @Author  : 1552980358
 * @Date    : 6 Oct 2019
 * @TIME    : 10:13 AM
 **/

class BottomPlayerView(context: Context, attributeSet: AttributeSet) :
    LinearLayout(context, attributeSet) {
    
    private lateinit var parentActivity: BaseAppCompatActivity
    
    private val albumSize by lazy { resources.getDimensionPixelSize(R.dimen.view_bottom_player_imageView).toFloat() }
    
    private var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent ?: return
            
            when (intent.action) {
                SERVICE_BROADCAST_PLAY -> {
                    checkBox_playControl.isChecked = true
                }
                
                SERVICE_BROADCAST_PAUSE -> {
                    checkBox_playControl.isChecked = false
                }
                
                SERVICE_BROADCAST_CHANGED -> {
                    if (visibility == View.GONE) {
                        resources.getDimensionPixelSize(R.dimen.bottomPlayerView_height).apply {
                            visibility = View.VISIBLE
                            
                            log("scrollY", scrollY.toString())
                            scrollY = -this
                            
                            Thread {
                                for (i in 40 downTo -10 ) {
                                    post { scrollY = -(i * this / 40) }
                                    Thread.sleep(10)
                                }
                                for (i in -10 .. 0) {
                                    post { scrollY = -(i * this / 40) }
                                    Thread.sleep(10)
                                }
                            }.start()
                        }
                    }
                    Thread {
                        updateInfo()
                    }.start()
                }
            }
        }
    }
    
    private val intentFilter = IntentFilter().apply {
        addAction(SERVICE_BROADCAST_PAUSE)
        addAction(SERVICE_BROADCAST_PLAY)
        addAction(SERVICE_BROADCAST_CHANGED)
    }
    
    init {
        LayoutInflater.from(context).inflate(R.layout.view_bottom_player, this)
        
        setBackgroundColor(Color.TRANSPARENT)
        
        visibility = View.GONE
        
        textView_title.apply {
            setSingleLine()
            setHorizontallyScrolling(true)
            marqueeRepeatLimit = -1
            ellipsize = MARQUEE
            isSelected = true
        }
        
        checkBox_playControl.setOnClickListener {
            context.startService(
                Intent(context, PlayService::class.java).putExtra(
                    SERVICE_INTENT_CONTENT,
                    if (checkBox_playControl.isChecked) SERVICE_INTENT_PLAY else SERVICE_INTENT_PAUSE
                )
            )
        }
        
        imageButton_list.setOnClickListener {
            BottomPlaylistDialog.bottomPlaylistDialog.showNow(parentActivity.supportFragmentManager)
        }
        
        relativeLayout_container.setOnClickListener {
            context.startActivity(
                Intent(
                    context,
                    PlayerActivity::class.java
                )
            )
        }
    }
    
    fun setParentActivity(baseAppCompatActivity: BaseAppCompatActivity) {
        parentActivity = baseAppCompatActivity
    }
    
    @Suppress("SetTextI18n", "DuplicatedCode")
    @Synchronized
    fun updateInfo() {
        getCurrentMusicInfo().apply {
            this ?: return
            
            parentActivity.runOnUiThread {
                textView_title.text = title()
                textView_info.text = "${artist()} - ${album()}"
                checkBox_playControl.isChecked = playerState == PLAY
            }
            
            val cover = albumCover()
            val tmp: Drawable
            if (cover != null) {
                /* Rescale Bitmap and cut into circle */
                if (cover.width == cover.height) {
                    tmp = RoundedBitmapDrawableFactory.create(
                        resources, Bitmap.createBitmap(
                            cover, 0, 0, cover.width, cover.height, Matrix().apply {
                                (albumSize / cover.width).apply {
                                    postScale(this, this)
                                }
                            }, true
                        )
                    ).apply { isCircular = true }
                    parentActivity.runOnUiThread { imageView_album.setImageDrawable(tmp) }
                    return
                }
                
                /* Cut Height pixels */
                if (cover.width < cover.height) {
                    tmp = RoundedBitmapDrawableFactory.create(
                        resources, Bitmap.createBitmap(
                            cover,
                            0,
                            (cover.height - cover.width) / 2,
                            cover.width,
                            cover.width,
                            Matrix().apply {
                                (albumSize / cover.width).apply {
                                    postScale(this, this)
                                }
                            },
                            true
                        )
                    ).apply { isCircular = true }
                    
                    parentActivity.runOnUiThread { imageView_album.setImageDrawable(tmp) }
                    return
                }
                
                /* Cut Width pixels */
                tmp = RoundedBitmapDrawableFactory.create(
                    resources, Bitmap.createBitmap(
                        cover,
                        0,
                        (cover.width - cover.height) / 2,
                        cover.width,
                        cover.width,
                        Matrix().apply {
                            (albumSize / cover.width).apply {
                                postScale(this, this)
                            }
                        },
                        true
                    )
                ).apply { isCircular = true }
                
                parentActivity.runOnUiThread { imageView_album.setImageDrawable(tmp) }
            }
        }
    }
    
    @Synchronized
    fun onDestroy() {
        try {
            context.unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        updateInfo()
    }
    
    @Synchronized
    fun onResume() {
        try {
            context.registerReceiver(broadcastReceiver, intentFilter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        updateInfo()
    }
    
    @Synchronized
    fun onPause() {
        try {
            context.unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        updateInfo()
    }
}