package app.fokkusu.music.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.fokkusu.music.base.activity.BaseAppCompatActivity
import app.fokkusu.music.R
import app.fokkusu.music.service.PlayService
import kotlinx.android.synthetic.main.activity_splash.imageView
import kotlinx.android.synthetic.main.activity_splash.textView_time
import java.lang.Exception
import kotlin.system.exitProcess

/**
 * @File    : SplashActivity
 * @Author  : 1552980358
 * @Date    : 5 Oct 2019
 * @TIME    : 7:11 PM
 **/

class SplashActivity : BaseAppCompatActivity() {
    
    private var complete = false
    
    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_splash)
        imageView.setImageBitmap(BitmapFactory.decodeStream(assets.open("huli.png")))
        
        /* Timer */
        var s = 0
        Thread {
            while (!complete) {
                try {
                    Thread.sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                s++
                runOnUiThread { textView_time.text = "$s" }
            }
            
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }.start()
        
        /* Check permissions */
        Thread {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    0
                )
                return@Thread
            }
            
            loadMusic()
        }.start()
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        /* Check Result */
        grantResults.forEach { if (it != PackageManager.PERMISSION_GRANTED) exitProcess(0) }
        loadMusic()
    }
    
    private fun loadMusic() {
        Thread {
            /* Scan Music from Android Database */
            try {
                contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    MediaStore.Audio.AudioColumns.IS_MUSIC
                ).apply {
                    this ?: return@Thread
                    
                    if (moveToFirst()) {
                        do {
                            @Suppress("DEPRECATION") val path =
                                getString(getColumnIndex(MediaStore.Audio.Media.DATA))
                            
                            if (path == "-1") {
                                continue
                            }
                            
                            PlayService.addMusic(
                                path,
                                getString(getColumnIndex(MediaStore.Audio.Media.TITLE)),
                                getString(getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                                getString(getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                                getInt(getColumnIndex(MediaStore.Audio.Media.DURATION))
                            )
                            
                        } while (moveToNext())
                    }
                    
                    close()
                }
                PlayService.sortMusic()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            complete = true
        }.start()
    }
}