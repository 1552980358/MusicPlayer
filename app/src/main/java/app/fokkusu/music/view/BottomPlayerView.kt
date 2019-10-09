package app.fokkusu.music.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.text.TextUtils.TruncateAt.MARQUEE
import android.util.AttributeSet
import android.view.LayoutInflater
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
import app.fokkusu.music.service.PlayService
import app.fokkusu.music.service.PlayService.Companion.getCurrentMusicInfo
import app.fokkusu.music.service.PlayService.Companion.playerState
import app.fokkusu.music.service.PlayService.Companion.PlayState.PLAY
import kotlinx.android.synthetic.main.view_bottom_player.view.checkBox_playControl
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
    
    private val albumSize by lazy { resources.getDimensionPixelSize(R.dimen.view_bottom_player_imageView) }
    
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
                    updateInfo()
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
        
        relativeLayout_container.setOnClickListener { context.startActivity(Intent(context, PlayerActivity::class.java)) }
    }
    
    @Suppress("SetTextI18n", "DuplicatedCode")
    @Synchronized
    fun updateInfo() {
        getCurrentMusicInfo().apply {
            this?:return
            
            textView_title.text = title()
            textView_info.text = "${artist()} - ${album()}"
            checkBox_playControl.isChecked = playerState == PLAY
            
            val cover = albumCover()
            if (cover != null) {
                /* Rescale Bitmap and cut into circle */
                if (cover.width == cover.height) {
                    imageView_album.setImageDrawable(RoundedBitmapDrawableFactory.create(
                        resources, Bitmap.createBitmap(
                            cover, 0, 0, cover.width, cover.height, Matrix().apply {
                                (albumSize / cover.width).toFloat().apply {
                                    setScale(this, this)
                                }
                            }, true
                        )
                    ).apply { isCircular = true })
                    return
                }
                
                /* Cut Height pixels */
                if (cover.width < cover.height) {
                    imageView_album.setImageDrawable(RoundedBitmapDrawableFactory.create(
                        resources, Bitmap.createBitmap(
                            cover,
                            0,
                            (cover.height - cover.width) / 2,
                            cover.width,
                            cover.width,
                            Matrix().apply {
                                (albumSize / cover.width).toFloat().apply {
                                    setScale(this, this)
                                }
                            },
                            true
                        )
                    ).apply { isCircular = true })
                    return
                }
                
                /* Cut Width pixels */
                imageView_album.setImageDrawable(RoundedBitmapDrawableFactory.create(
                    resources, Bitmap.createBitmap(
                        cover,
                        0,
                        (cover.width - cover.height) / 2,
                        cover.width,
                        cover.width,
                        Matrix().apply {
                            (albumSize / cover.width).toFloat().apply {
                                setScale(this, this)
                            }
                        },
                        true
                    )
                ).apply { isCircular = true })
                
                checkBox_playControl.isChecked = false
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