package app.fokkusu.music.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.text.TextUtils
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toDrawable
import app.fokkusu.music.Application
import app.fokkusu.music.R
import app.fokkusu.music.base.Constants.Companion.SERVICE_BROADCAST_CHANGED
import app.fokkusu.music.base.Constants.Companion.SERVICE_BROADCAST_PAUSE
import app.fokkusu.music.base.Constants.Companion.SERVICE_BROADCAST_PLAY
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_CONTENT
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_LAST
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_NEXT
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_PAUSE
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_PLAY
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_PLAY_FORM
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_PLAY_FORM_CONTENT
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_SEEK_CHANGE
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_SEEK_CHANGE_POSITION
import app.fokkusu.music.base.getStack
import app.fokkusu.music.base.getTime
import app.fokkusu.music.base.interfaces.OnRequestAlbumCoverListener
import app.fokkusu.music.service.PlayService
import kotlinx.android.synthetic.main.activity_player.checkBox_playControl
import kotlinx.android.synthetic.main.activity_player.drawerLayout_container
import kotlinx.android.synthetic.main.activity_player.imageButton_last
import kotlinx.android.synthetic.main.activity_player.imageButton_next
import kotlinx.android.synthetic.main.activity_player.imageButton_playForm
import kotlinx.android.synthetic.main.activity_player.lyricView
import kotlinx.android.synthetic.main.activity_player.relativeLayout_container
import kotlinx.android.synthetic.main.activity_player.seekBar
import kotlinx.android.synthetic.main.activity_player.textView_timePass
import kotlinx.android.synthetic.main.activity_player.textView_timeTotal
import kotlinx.android.synthetic.main.activity_player.toolbar
import java.io.File

/**
 * @File    : PlayerActivity
 * @Author  : 1552980358
 * @Date    : 4 Oct 2019
 * @TIME    : 6:01 PM
 **/

class PlayerActivity : AppCompatActivity(), OnRequestAlbumCoverListener {
    
    @Suppress("PrivatePropertyName")
    private lateinit var imageView_album: ImageView
    
    /* SeekBar Controlled by User */
    private var seekBarFree = true
    
    /* Thread and flag */
    private var timeCount: Thread? = null
    private var threadStop = false
    
    /* Pre-Set Size */
    private val albumSize by lazy { resources.displayMetrics.widthPixels * 2 / 3 }
    private val screenHei by lazy { resources.displayMetrics.heightPixels }
    private val screenWid by lazy { resources.displayMetrics.widthPixels }
    
    /* BroadcastReceiver */
    private val broadcastReceiver = object : BroadcastReceiver() {
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
                    changeMusic()
                }
            }
            getThreadStart()
        }
    }
    
    /* IntentFilter for BroadcastReceiver */
    private val intentFilter = IntentFilter().apply {
        addAction(SERVICE_BROADCAST_PAUSE)
        addAction(SERVICE_BROADCAST_PLAY)
        addAction(SERVICE_BROADCAST_CHANGED)
    }
    
    /* onCreate */
    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            (SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE)
        
        /* Set StatusBar & NavBar Color */
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        
        overridePendingTransition(R.anim.anim_bottom2top, R.anim.anim_stay)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        
        try {
            (toolbar.javaClass.getDeclaredField("mTitleTextView").apply { isAccessible = true }.get(
                toolbar
            ) as TextView).apply {
                setHorizontallyScrolling(true)
                marqueeRepeatLimit = -1
                ellipsize = TextUtils.TruncateAt.MARQUEE
                isSelected = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        imageButton_next.setOnClickListener {
            startService(
                Intent(this, PlayService::class.java).putExtra(
                    SERVICE_INTENT_CONTENT,
                    SERVICE_INTENT_NEXT
                )
            )
        }
        
        imageButton_last.setOnClickListener {
            startService(
                Intent(this, PlayService::class.java).putExtra(
                    SERVICE_INTENT_CONTENT,
                    SERVICE_INTENT_LAST
                )
            )
        }
        
        val cycle = ContextCompat.getDrawable(this, R.drawable.ic_player_cycle)
        val single = ContextCompat.getDrawable(this, R.drawable.ic_player_single)
        val random = ContextCompat.getDrawable(this, R.drawable.ic_player_random)
        
        when (PlayService.playForm) {
            PlayService.Companion.PlayForm.CYCLE -> {
                imageButton_playForm.background = cycle
            }
            PlayService.Companion.PlayForm.SINGLE -> {
                imageButton_playForm.background = single
            }
            PlayService.Companion.PlayForm.RANDOM -> {
                imageButton_playForm.background = random
            }
        }
        
        imageButton_playForm.setOnClickListener {
            when (PlayService.playForm) {
                PlayService.Companion.PlayForm.CYCLE -> {
                    imageButton_playForm.background = single
                    startService(
                        Intent(this, PlayService::class.java).putExtra(
                            SERVICE_INTENT_CONTENT,
                            SERVICE_INTENT_PLAY_FORM
                        ).putExtra(
                            SERVICE_INTENT_PLAY_FORM_CONTENT, PlayService.Companion.PlayForm.SINGLE
                        )
                    )
                }
                
                PlayService.Companion.PlayForm.SINGLE -> {
                    imageButton_playForm.background = random
                    startService(
                        Intent(this, PlayService::class.java).putExtra(
                            SERVICE_INTENT_CONTENT,
                            SERVICE_INTENT_PLAY_FORM
                        ).putExtra(
                            SERVICE_INTENT_PLAY_FORM_CONTENT, PlayService.Companion.PlayForm.RANDOM
                        )
                    )
                }
                
                PlayService.Companion.PlayForm.RANDOM -> {
                    imageButton_playForm.background = cycle
                    startService(
                        Intent(this, PlayService::class.java).putExtra(
                            SERVICE_INTENT_CONTENT,
                            SERVICE_INTENT_PLAY_FORM
                        ).putExtra(
                            SERVICE_INTENT_PLAY_FORM_CONTENT, PlayService.Companion.PlayForm.CYCLE
                        )
                    )
                }
            }
        }
        
        relativeLayout_container.addView(
            ImageView(this).apply { imageView_album = this },
            RelativeLayout.LayoutParams(albumSize, albumSize).apply {
                // Set to center of the RelativeLayout
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
        )
        
        val gesture = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                e ?: return false
                
                (screenWid.toFloat() / 3).apply {
                    if (e.y <= this) {
                        startService(
                            Intent(this@PlayerActivity, PlayService::class.java)
                                .putExtra(SERVICE_INTENT_CONTENT, SERVICE_INTENT_SEEK_CHANGE)
                                .putExtra(
                                    SERVICE_INTENT_SEEK_CHANGE_POSITION,
                                    (seekBar.progress * 1000 - 5000).run { if (this > 0) this else 0 })
                        )
                        return true
                    }
                    
                    if (e.y >= (this * 2)) {
                        startService(
                            Intent(this@PlayerActivity, PlayService::class.java)
                                .putExtra(SERVICE_INTENT_CONTENT, SERVICE_INTENT_SEEK_CHANGE)
                                .putExtra(
                                    SERVICE_INTENT_SEEK_CHANGE_POSITION,
                                    (seekBar.progress * 1000 + 5000).run { if (this <= PlayService.getMusicDuration()) this else 0 })
                        )
                        return true
                    }
                    
                    startService(
                        Intent(this@PlayerActivity, PlayService::class.java).putExtra(
                            SERVICE_INTENT_CONTENT,
                            if (checkBox_playControl.isChecked) SERVICE_INTENT_PAUSE else SERVICE_INTENT_PLAY
                        )
                    )
                    return true
                }
            }
        })
        
        relativeLayout_container.setOnTouchListener { _, event ->
            return@setOnTouchListener gesture.onTouchEvent(event)
        }
        
        checkBox_playControl.apply {
            setOnClickListener {
                startService(
                    Intent(this@PlayerActivity, PlayService::class.java).putExtra(
                        SERVICE_INTENT_CONTENT,
                        if (isChecked) SERVICE_INTENT_PLAY else SERVICE_INTENT_PAUSE
                    )
                )
            }
        }
        
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) {
                    return
                }
                textView_timePass.text = getTime(progress)
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                seekBarFree = false
            }
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar ?: return
                
                startService(
                    Intent(this@PlayerActivity, PlayService::class.java).putExtra(
                        SERVICE_INTENT_CONTENT, SERVICE_INTENT_SEEK_CHANGE
                    ).putExtra(
                        SERVICE_INTENT_SEEK_CHANGE_POSITION, seekBar.progress * 1000
                    )
                )
                
                seekBarFree = true
            }
        })
        
        lyricView.calculateHeight()
    }
    
    /* Set music into to the layout */
    @SuppressLint("SetTextI18n")
    @Synchronized
    private fun changeMusic() {
        try {
            PlayService.getCurrentMusicInfo()?.apply {
                
                (duration() / 1000).apply {
                    textView_timeTotal.text = getTime(this)
                    seekBar.max = this
                }
                toolbar.title = title()
                toolbar.subtitle = "${artist()} - ${album()}"
                
                Thread {
                    albumCover(this@PlayerActivity)
                }.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /* Start a thread updating position, lyric */
    private fun getThreadStart() {
        timeCount = Thread {
            threadStop = false
            
            while (PlayService.playerState == PlayService.Companion.PlayState.PLAY && !threadStop) {
                if (seekBarFree) {
                    (PlayService.getCurrentPosition()).apply {
                        lyricView.updateLyricLine(this)
                        runOnUiThread {
                            seekBar.progress = this
                            textView_timePass.text = getTime(this)
                        }
                        
                    }
                }
                
                try {
                    Thread.sleep(200)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                System.gc()
            }
            
            System.gc()
        }.apply { start() }
    }
    
    /* Update lyric */
    @Synchronized
    private fun getLyric() {
        PlayService.getCurrentMusicInfo().apply {
            this ?: return
            
            // Read lrc file
            File(Application.extDataDir_lyric, id().plus(".lrc")).apply {
                lyricView.updateMusicLyric(
                    // Local file does not exist or empty file
                    if (!exists() && length() == 0L) {
                        arrayListOf()
                    } else {
                        readLines() as ArrayList<String>
                    }
                )
            }
        }
    }
    
    /* onResult */
    @Suppress("DuplicatedCode")
    override fun onResult(bitmap: Bitmap) {
        bitmap.apply {
            if (width == height) {
                RoundedBitmapDrawableFactory.create(
                    resources,
                    Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply {
                        (albumSize.toFloat() / width).apply {
                            setScale(this, this)
                        }
                    }, true)
                ).apply {
                    isCircular = true
                    
                    runOnUiThread { imageView_album.setImageDrawable(this) }
                }
                
                // Cut width
                Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply {
                    (screenHei.toFloat() / height).apply {
                        setScale(this, this)
                    }
                }, true).run {
                    Bitmap.createBitmap(
                        this, (width - screenWid) / 2, 0, screenWid, height
                    )
                }.toDrawable(resources).apply { runOnUiThread { drawerLayout_container.background = this } }
                
                getLyric()
                return
            }
            
            // Cut width
            if (width > height) {
                RoundedBitmapDrawableFactory.create(
                    resources,
                    Bitmap.createBitmap(this, (width - height) / 2, 0, height, height, Matrix().apply {
                        (albumSize.toFloat() / height).apply {
                            setScale(this, this)
                        }
                    }, true)/*.run {
                        Bitmap.createBitmap(
                            this, (width - albumSize) / 2, 0, albumSize, albumSize
                        )
                    }*/
                ).apply {
                    isCircular = true
                    
                    runOnUiThread { imageView_album.setImageDrawable(this) }
                }
                
                Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply {
                    (screenHei.toFloat() / height).apply {
                        setScale(this, this)
                    }
                }, true).run {
                    Bitmap.createBitmap(
                        this, (width - screenWid) / 2, 0, screenWid, screenHei
                    )
                }.toDrawable(resources).apply { runOnUiThread { drawerLayout_container.background = this } }
                
                getLyric()
                return
            }
            
            // Cut height
            RoundedBitmapDrawableFactory.create(resources,
                Bitmap.createBitmap(this, 0, (height - height) / 2, width, width, Matrix().apply {
                    (albumSize.toFloat() / width).apply {
                        setScale(this, this)
                    }
                }, true)/*.run {
                    Bitmap.createBitmap(
                        this, 0, (height - albumSize) / 2, albumSize, albumSize
                    )
                }*/
            ).apply {
                isCircular = true
                
                runOnUiThread { imageView_album.setImageDrawable(this) }
            }
            
            Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply {
                (screenWid.toFloat() / width).apply {
                    setScale(this, this)
                }
            }, true).run {
                Bitmap.createBitmap(
                    this, 0, (height - screenHei) / 2, screenWid, screenHei
                )
            }.toDrawable(resources).apply { runOnUiThread { drawerLayout_container.background = this } }
            getLyric()
        }
    }
    
    /* onNullResult */
    override fun onNullResult() {
        getLyric()
    }
    
    /* onResume */
    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiver, intentFilter)
        changeMusic()
        checkBox_playControl.isChecked =
            PlayService.playerState == PlayService.Companion.PlayState.PLAY
        (PlayService.getCurrentPosition()).apply {
            lyricView.updateLyricLine(this)
            (this / 1000).apply {
                runOnUiThread {
                    seekBar.progress = this
                    textView_timePass.text = getTime(this)
                }
            }
        }
        getThreadStart()
    }
    
    /* onPause */
    override fun onPause() {
        super.onPause()
        
        // Stop Thread
        threadStop = true
        timeCount = null
        
        System.gc()
        
        // Remove receiver
        unregisterReceiver(broadcastReceiver)
    }
    
    /* onDestroy */
    override fun onDestroy() {
        // Stop Thread
        threadStop = false
        timeCount = null
        
        // Remove receiver
        try {
            unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
            e.getStack(false)
        }
        
        System.gc()
        super.onDestroy()
    }
    
    /* onBackPressed */
    override fun onBackPressed() {
        finish()
    }
    
    /* finish */
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.anim_stay, R.anim.anim_top2bottom)
    }
    
}