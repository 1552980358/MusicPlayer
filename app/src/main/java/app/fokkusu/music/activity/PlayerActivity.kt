package app.fokkusu.music.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toDrawable
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
import app.fokkusu.music.base.getTime
import app.fokkusu.music.service.PlayService
import com.google.gson.JsonParser
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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * @File    : PlayerActivity
 * @Author  : 1552980358
 * @Date    : 4 Oct 2019
 * @TIME    : 6:01 PM
 **/

class PlayerActivity : AppCompatActivity() {
    
    @Suppress("PrivatePropertyName")
    private lateinit var imageView_album: ImageView
    
    /* SeekBar Controlled by User */
    private var seekBarFree = true
    
    /* Thread and flag */
    private var timeCount: Thread? = null
    private var threadStop = false
    
    private var lyricFetch = false
    
    /* Pre-Set Size */
    private val albumSize by lazy { resources.displayMetrics.widthPixels * 2 / 3 }
    private val screenHei by lazy { resources.displayMetrics.heightPixels }
    private val screenWid by lazy { resources.displayMetrics.widthPixels }
    
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
                    getThreadStart()
                }
            }
        }
    }
    
    private val intentFilter = IntentFilter().apply {
        addAction(SERVICE_BROADCAST_PAUSE)
        addAction(SERVICE_BROADCAST_PLAY)
        addAction(SERVICE_BROADCAST_CHANGED)
    }
    
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
            RelativeLayout.LayoutParams(albumSize, albumSize)
        )
        
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
    }
    
    @SuppressLint("SetTextI18n")
    @Synchronized
    private fun changeMusic() {
        try {
            PlayService.getCurrentMusicInfo().apply {
                this ?: return
                (duration() / 1000).apply {
                    textView_timeTotal.text = getTime(this)
                    seekBar.max = this
                }
                toolbar.title = title()
                toolbar.subtitle = "${artist()} - ${album()}"
            }
            getLyric()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        try {
            PlayService.getCurrentMusicInfo()?.let {
                it.albumCover().apply {
                    this ?: return
                    if (width == height) {
                        imageView_album.setImageDrawable(RoundedBitmapDrawableFactory.create(
                            resources,
                            Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply {
                                (albumSize / width).toFloat().apply {
                                    setScale(this, this)
                                }
                            }, true)
                        ).apply { isCircular = true })
                        
                        drawerLayout_container.background =
                            Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply {
                                (screenHei / height).toFloat().apply {
                                    setScale(this, this)
                                }
                            }, true).run {
                                Bitmap.createBitmap(
                                    this, (width - screenWid) / 2, 0, screenWid, height
                                )
                            }.toDrawable(resources)
                        
                        return
                    }
                    
                    if (width > height) {
                        
                        imageView_album.setImageDrawable(RoundedBitmapDrawableFactory.create(
                            resources,
                            Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply {
                                (albumSize / height).toFloat().apply {
                                    setScale(this, this)
                                }
                            }, true).run {
                                Bitmap.createBitmap(
                                    this, (width - albumSize) / 2, 0, albumSize, albumSize
                                )
                            })
                        )
                        
                        Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply {
                            (screenHei / height).toFloat().apply {
                                setScale(this, this)
                            }
                        }, true).run {
                            Bitmap.createBitmap(
                                this, (width - screenWid) / 2, 0, screenWid, screenHei
                            )
                        }.toDrawable(resources)
                        
                        return
                    }
                    
                    imageView_album.setImageDrawable(RoundedBitmapDrawableFactory.create(resources,
                        Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply {
                            (albumSize / width).toFloat().apply {
                                setScale(this, this)
                            }
                        }, true).run {
                            Bitmap.createBitmap(
                                this, 0, (width - albumSize) / 2, albumSize, albumSize
                            )
                        })
                    )
                    
                    Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply {
                        (screenWid / width).toFloat().apply {
                            setScale(this, this)
                        }
                    }, true).run {
                        Bitmap.createBitmap(
                            this, 0, (width - screenHei) / 2, screenWid, screenHei
                        )
                    }.toDrawable(resources)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun getThreadStart() {
        timeCount = Thread {
        
            threadStop = false
            while (PlayService.playerState == PlayService.Companion.PlayState.PLAY && !threadStop) {
                if (seekBarFree) {
                    (PlayService.getCurrentPosition()).apply {
                        lyricView.updateLyricLine(this)
                        (this / 1000).apply {
                            runOnUiThread {
                                seekBar.progress = this
                                textView_timePass.text = getTime(this)
                            }
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
        }.apply { start() }
    }
    
    @Synchronized
    private fun getLyric() {
        if (lyricFetch) return
        
        Thread {
            lyricFetch = true
            val info = PlayService.getCurrentMusicInfo()
            
            info ?: return@Thread
            
            try {
                Request.Builder().header(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36"
                ).url("http://music.163.com/api/search/pc").post(FormBody.Builder().apply {
                    add("s", info.title())
                    add("offset", "0")
                    add("limit", "1")
                    add("type", "1")
                }.build()).build().apply {
                    OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
                        .writeTimeout(5, TimeUnit.SECONDS).build().newCall(this)
                        .enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                /* Error */
                                e.printStackTrace()
                            }
                            
                            override fun onResponse(call: Call, response: Response) {
                                response.body ?: return
                                
                                /* Decode JSON code */
                                JsonParser().parse(response.body!!.string())
                                    .asJsonObject.get("result").asJsonObject.get("songs")
                                    .asJsonArray.apply {
                                    if (this.size() == 0) {
                                        return
                                    }
                                    
                                    var text: String
                                    
                                    /* Decode JSON code */
                                    URL(
                                        "http://music.163.com/api/song/media?id=${first().asJsonObject.get(
                                            "id"
                                        ).asString}"
                                    ).openStream().apply {
                                        bufferedReader().apply {
                                            text = JsonParser().parse(readText())
                                                .asJsonObject.get("lyric").asString
                                            close()
                                        }
                                        close()
                                    }
                                    
                                    if (text.isEmpty()) return
                                    
                                    /* Remove empty lines */
                                    lyricView.updateMusicLyric(
                                        ArrayList(
                                            listOf(
                                                *text.split(("\n").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                            )
                                        )
                                    )
                                    
                                }
                            }
                        })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            lyricFetch = false
        }.start()
    }
    
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
    
    override fun onPause() {
        super.onPause()
        
        // Stop Thread
        threadStop = true
        timeCount = null
        
        System.gc()
        
        // Remove receiver
        unregisterReceiver(broadcastReceiver)
    }
    
    override fun onDestroy() {
        
        // Stop Thread
        threadStop = false
        timeCount = null
        
        // Remove receiver
        try {
            unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        System.gc()
        super.onDestroy()
    }
    
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_stay, R.anim.anim_top2bottom)
    }
    
}