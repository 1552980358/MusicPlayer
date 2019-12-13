package app.fokkusu.music.activity.player

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import app.fokkusu.music.R
import app.fokkusu.music.base.Constants
import app.fokkusu.music.base.activity.BasePlayerActivity
import app.fokkusu.music.base.getStack
import app.fokkusu.music.base.getTime
import app.fokkusu.music.dialog.BottomPropDialog
import app.fokkusu.music.dialog.BottomPlaylistDialog
import app.fokkusu.music.service.PlayService
import kotlinx.android.synthetic.main.activity_player_dyn.checkBox_playControl
import kotlinx.android.synthetic.main.activity_player_dyn.imageButton_last
import kotlinx.android.synthetic.main.activity_player_dyn.imageButton_list
import kotlinx.android.synthetic.main.activity_player_dyn.imageButton_next
import kotlinx.android.synthetic.main.activity_player_dyn.imageButton_playForm
import kotlinx.android.synthetic.main.activity_player_dyn.imageView_album
import kotlinx.android.synthetic.main.activity_player_dyn.linearLayout_container
import kotlinx.android.synthetic.main.activity_player_dyn.seekBar
import kotlinx.android.synthetic.main.activity_player_dyn.textView_subTitle
import kotlinx.android.synthetic.main.activity_player_dyn.textView_timeDiv
import kotlinx.android.synthetic.main.activity_player_dyn.textView_timePass
import kotlinx.android.synthetic.main.activity_player_dyn.textView_timeTotal
import kotlinx.android.synthetic.main.activity_player_dyn.textView_title
import mkaflowski.mediastylepalette.MediaNotificationProcessor

/**
 * @File    : DynPlayerActivity
 * @Author  : 1552980358
 * @Date    : 2019/11/11
 * @TIME    : 17:28
 **/

class DynPlayerActivity : BasePlayerActivity()/*AppCompatActivity(), OnRequestAlbumCoverListener*/ {
    
    private val albumSize by lazy {
        resources.displayMetrics.widthPixels.toFloat()
    }
    
    // Stop Thread
    //private var threadStop = false
    //private var timeCount = null as Thread?
    
    private var matrix = Matrix()
    private lateinit var mediaNotificationProcessor: MediaNotificationProcessor
    private var seekBarFree = true
    
    private val albumDefault by lazy { ColorDrawable(Color.WHITE) }
    
    /* BroadcastReceiver */
    /*
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent ?: return
            
            when (intent.action) {
                Constants.SERVICE_BROADCAST_PLAY -> {
                    checkBox_playControl.isChecked = true
                    getThreadStart()
                }
                
                Constants.SERVICE_BROADCAST_PAUSE -> {
                    checkBox_playControl.isChecked = false
                    threadStop = true
                }
                
                Constants.SERVICE_BROADCAST_CHANGED -> {
                    Thread {
                        changeMusic()
                    }.start()
                }
            }
        }
    }
     */
    
    /* IntentFilter for BroadcastReceiver */
    /*
    private val intentFilter = IntentFilter().apply {
        addAction(Constants.SERVICE_BROADCAST_PAUSE)
        addAction(Constants.SERVICE_BROADCAST_PLAY)
        addAction(Constants.SERVICE_BROADCAST_CHANGED)
    }
     */
    
    @Suppress("DuplicatedCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        
        window.statusBarColor = Color.TRANSPARENT
        
        overridePendingTransition(R.anim.anim_bottom2top, R.anim.anim_stay)
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_player_dyn)
        
        textView_title.apply {
            setSingleLine()
            setHorizontallyScrolling(true)
            marqueeRepeatLimit = -1
            ellipsize = TextUtils.TruncateAt.MARQUEE
            isSelected = true
        }
        
        textView_subTitle.apply {
            setSingleLine()
            setHorizontallyScrolling(true)
            marqueeRepeatLimit = -1
            ellipsize = TextUtils.TruncateAt.MARQUEE
            isSelected = true
        }
        
        val cycle = ContextCompat.getDrawable(this, R.drawable.ic_player_dyn_cycle)
        val single = ContextCompat.getDrawable(this, R.drawable.ic_player_dyn_single)
        val random = ContextCompat.getDrawable(this, R.drawable.ic_player_dyn_random)
        
        imageButton_playForm.apply {
            background = when (PlayService.playForm) {
                PlayService.Companion.PlayForm.CYCLE -> {
                    cycle
                }
                PlayService.Companion.PlayForm.SINGLE -> {
                    single
                }
                PlayService.Companion.PlayForm.RANDOM -> {
                    random
                }
            }
            background.setTint(Color.BLACK)
            
            setOnClickListener {
                background = when (PlayService.playForm) {
                    PlayService.Companion.PlayForm.CYCLE -> {
                        startService(
                            Intent(this@DynPlayerActivity, PlayService::class.java)
                                .putExtra(Constants.SERVICE_INTENT_CONTENT, Constants.SERVICE_INTENT_PLAY_FORM)
                                .putExtra(
                                    Constants.SERVICE_INTENT_PLAY_FORM_CONTENT,
                                    PlayService.Companion.PlayForm.SINGLE
                                )
                        )
                        single
                    }
                    PlayService.Companion.PlayForm.SINGLE -> {
                        startService(
                            Intent(this@DynPlayerActivity, PlayService::class.java)
                                .putExtra(Constants.SERVICE_INTENT_CONTENT, Constants.SERVICE_INTENT_PLAY_FORM)
                                .putExtra(
                                    Constants.SERVICE_INTENT_PLAY_FORM_CONTENT,
                                    PlayService.Companion.PlayForm.RANDOM
                                )
                        )
                        random
                    }
                    PlayService.Companion.PlayForm.RANDOM -> {
                        startService(
                            Intent(this@DynPlayerActivity, PlayService::class.java)
                                .putExtra(Constants.SERVICE_INTENT_CONTENT, Constants.SERVICE_INTENT_PLAY_FORM)
                                .putExtra(
                                    Constants.SERVICE_INTENT_PLAY_FORM_CONTENT,
                                    PlayService.Companion.PlayForm.CYCLE
                                )
                        )
                        cycle
                    }
                }
                if (::mediaNotificationProcessor.isInitialized) {
                    background.setTint(mediaNotificationProcessor.primaryTextColor)
                } else {
                    background.setTint(Color.BLACK)
                }
            }
        }
        
        // Set size
        imageView_album.layoutParams.apply {
            height = albumSize.toInt()
            width = albumSize.toInt()
        }
        
        seekBar.apply {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    //if (!fromUser) return
                    textView_timePass.text = getTime(progress)
                }
                
                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    seekBarFree = false
                }
                
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar ?: return
                    startService(
                        Intent(this@DynPlayerActivity, PlayService::class.java)
                            .putExtra(Constants.SERVICE_INTENT_CONTENT, Constants.SERVICE_INTENT_SEEK_CHANGE)
                            .putExtra(Constants.SERVICE_INTENT_SEEK_CHANGE_POSITION, seekBar.progress * 1000)
                    )
                    seekBarFree = true
                }
            })
        }
        
        checkBox_playControl.apply {
            setOnClickListener {
                startService(
                    Intent(this@DynPlayerActivity, PlayService::class.java).putExtra(
                        Constants.SERVICE_INTENT_CONTENT,
                        if (isChecked) Constants.SERVICE_INTENT_PLAY else Constants.SERVICE_INTENT_PAUSE
                    )
                )
            }
        }
        
        imageButton_list.setOnClickListener {
            BottomPlaylistDialog.bottomPlaylistDialog.showNow(supportFragmentManager)
        }
        
        checkBox_playControl.apply {
            setOnClickListener {
                startService(
                    Intent(this@DynPlayerActivity, PlayService::class.java).putExtra(
                        Constants.SERVICE_INTENT_CONTENT,
                        if (isChecked) Constants.SERVICE_INTENT_PLAY else Constants.SERVICE_INTENT_PAUSE
                    )
                )
            }
        }
        
        imageButton_next.setOnClickListener {
            startService(
                Intent(this, PlayService::class.java).putExtra(
                    Constants.SERVICE_INTENT_CONTENT,
                    Constants.SERVICE_INTENT_NEXT
                )
            )
        }
        
        imageButton_last.setOnClickListener {
            startService(
                Intent(this, PlayService::class.java).putExtra(
                    Constants.SERVICE_INTENT_CONTENT,
                    Constants.SERVICE_INTENT_LAST
                )
            )
        }
    }
    
    /* changeMusic */
    @Synchronized
    override fun changeMusic(onResume: Boolean) {
        try {
            PlayService.getCurrentMusicInfo()?.apply {
                (duration() / 1000).apply {
                    runOnUiThread {
                        textView_timeTotal.text = getTime(this)
                        seekBar.max = this
                        textView_title.text = title()
                        textView_subTitle.text = artist()
                    }
                }
                
                //albumCover(this@DynPlayerActivity)
                if (onResume) {
                    PlayService.getCurrentBitmap().apply {
                        if (this == null) {
                            onNullResult()
                            return
                        }
                        
                        onResult(this)
                    }
                }
            }
        } catch (e: Exception) {
            e.getStack(showLog = false, showToast = false)
        }
    }
    
    /* getThreadStart */
    @Synchronized
    override fun getThreadStart() {
        timeCount = Thread {
            threadStop = false
            
            do {
                if (seekBarFree) {
                    (PlayService.getCurrentPosition()).apply {
                        runOnUiThread {
                            seekBar.progress = this / 1000
                            //textView_timePass.text = getTime(this)
                        }
                    }
                }
                
                try {
                    Thread.sleep(200)
                } catch (e: Exception) {
                    e.getStack(showLog = false, showToast = false)
                }
                
                //System.gc()
            } while (PlayService.playerState == PlayService.Companion.PlayState.PLAY && !threadStop)
            
        }.apply { start() }
    }
    
    /* onResult */
    override fun onResult(@NonNull bitmap: Bitmap) {
        bitmap.apply {
            updateBackground(this)
            if (width == height) {
                Bitmap.createBitmap(this, 0, 0, width, height, matrix.apply {
                    (albumSize / width).apply {
                        postScale(this, this)
                    }
                }, true).apply { imageView_album.setImageBitmap(this) }
                return
            }
            if (width > height) {
                Bitmap.createBitmap(this, (width - height) / 2, 0, height, height, matrix.apply {
                    (albumSize / height).apply {
                        postScale(this, this)
                    }
                }, true).apply { imageView_album.setImageBitmap(this) }
                return
            }
            Bitmap.createBitmap(this, 0, (height - width) / 2, width, width, matrix.apply {
                (albumSize / width).apply {
                    postScale(this, this)
                }
            }, true).apply { imageView_album.setImageBitmap(this) }
        }
    }
    
    /* updateBackground */
    private fun updateBackground(bitmap: Bitmap) {
        mediaNotificationProcessor = MediaNotificationProcessor(this, bitmap).apply {
            runOnUiThread {
                linearLayout_container.setBackgroundColor(backgroundColor)
                imageButton_playForm.background.setTint(primaryTextColor)
                imageButton_last.background.setTint(primaryTextColor)
                checkBox_playControl.background.setTint(primaryTextColor)
                imageButton_next.background.setTint(primaryTextColor)
                imageButton_list.background.setTint(primaryTextColor)
                seekBar.thumb.setTint(primaryTextColor)
                seekBar.progressDrawable.setTint(primaryTextColor)
                seekBar.indeterminateDrawable.setTint(secondaryTextColor)
                textView_timePass.setTextColor(primaryTextColor)
                textView_timeTotal.setTextColor(primaryTextColor)
                textView_timeDiv.setTextColor(secondaryTextColor)
                textView_title.setTextColor(primaryTextColor)
                textView_subTitle.setTextColor(secondaryTextColor)
                if (isLight) {
                    window.decorView.systemUiVisibility =
                        (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                } else {
                    window.decorView.systemUiVisibility =
                        (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                }
            }
        }
    }
    
    override fun onReceivePlay() {
        checkBox_playControl.isChecked = true
        getThreadStart()
    }
    
    override fun onReceivePause() {
        checkBox_playControl.isChecked = false
        threadStop = true
    }
    
    //override fun onReceiveChange() {
        //Thread {
        //changeMusic()
        //}.start()
    //}
    
    /* onNullResult */
    override fun onNullResult() {
        runOnUiThread {
            imageView_album.setImageDrawable(albumDefault)
            linearLayout_container.setBackgroundColor(Color.WHITE)
            imageButton_playForm.background.setTint(Color.BLACK)
            imageButton_last.background.setTint(Color.BLACK)
            checkBox_playControl.background.setTint(Color.BLACK)
            imageButton_next.background.setTint(Color.BLACK)
            imageButton_list.background.setTint(Color.BLACK)
            seekBar.thumb.setTint(Color.BLACK)
            seekBar.progressDrawable.setTint(Color.BLACK)
            seekBar.indeterminateDrawable.setTint(Color.BLACK)
            textView_title.setTextColor(Color.BLACK)
            textView_subTitle.setTextColor(Color.BLACK)
            textView_timePass.setTextColor(Color.BLACK)
            textView_timeTotal.setTextColor(Color.BLACK)
            textView_timeDiv.setTextColor(Color.BLACK)
        }
    }
    
    /* onResume */
    override fun onResume() {
        checkBox_playControl.isChecked = PlayService.playerState == PlayService.Companion.PlayState.PLAY
        super.onResume()
        //registerReceiver(broadcastReceiver, intentFilter)
        //changeMusic(true)
        //getThreadStart()
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_play_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_opts -> {
                BottomPropDialog().show(this.supportFragmentManager, "DynPlayerActivity")
            }
            
            R.id.menu_cover_choose -> {
            
            }
            
            R.id.menu_lyric_choose -> {
            
            }
        }
        
        return super.onOptionsItemSelected(item)
    }
    
    /* onPause */
    //override fun onPause() {
    //    super.onPause()
    //
        // Stop Thread
    //    threadStop = true
    //    timeCount = null
        
    //    //unregisterReceiver(broadcastReceiver)
        
    //    System.gc()
    //}
    
    /* onBackPressed */
    //override fun onBackPressed() {
    //    finish()
    //}
    
    /* finish */
    //override fun finish() {
    // Stop Thread
    //    threadStop = true
    //    timeCount = null
    
    //    super.finish()
    //    overridePendingTransition(R.anim.anim_stay, R.anim.anim_top2bottom)
    //}
    
    /* onDestroy */
    //override fun onDestroy() {
    // make sure that sub-thread is removed
    //    timeCount = null
    // Confirm that receiver is removed
    //try {
    //    unregisterReceiver(broadcastReceiver)
    //} catch (e: Exception) {
    //    e.getStack(showLog = false, showToast = false)
    //}
    //    super.onDestroy()
    
    // Remove stack
    //    System.gc()
    //}
    
}