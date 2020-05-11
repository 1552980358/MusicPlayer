package app.github1552980358.android.musicplayer.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.AudioData
import app.github1552980358.android.musicplayer.base.Constant.Companion.AlbumNormal
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataListFile
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataMapFile
import app.github1552980358.android.musicplayer.base.Constant.Companion.BackgroundThread
import app.github1552980358.android.musicplayer.base.Constant.Companion.SmallAlbumRound
import kotlinx.android.synthetic.main.activity_audio_import.imageView
import kotlinx.android.synthetic.main.activity_audio_import.progressBar
import kotlinx.android.synthetic.main.activity_audio_import.textView
import lib.github1552980358.labourforce.LabourForce
import lib.github1552980358.labourforce.labours.work.LabourWork
import java.io.File
import java.io.ObjectOutputStream

/**
 * @file    : [AudioImportActivity]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/9
 * @time    : 9:18
 **/

class AudioImportActivity : AppCompatActivity() {
    
    private var searching = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_import)
        
        textView.text = String.format(getString(R.string.mediaSearchActivity_searching), 0)
        
        LabourForce.onDuty.sendWork2Labour(
            BackgroundThread,
            object : LabourWork(Handler()) {
                override fun dutyEnd(workProduct: MutableMap<String, Any?>?, handler: Handler?) {
                
                }
                
                override fun workContent(workProduct: MutableMap<String, Any?>?, handler: Handler?) {
                    searching = true
                    
                    handler?.post {
                        textView.text = String.format(getString(R.string.mediaSearchActivity_none), 0)
                    }
                    
                    contentResolver
                        .query(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            null,
                            null,
                            null,
                            MediaStore.Audio.AudioColumns.IS_MUSIC
                        ).apply {
                            if (this == null) {
                                handler?.post {
                                    textView.setText(R.string.mediaSearchActivity_none)
                                }
                                return
                            }
        
                            if (!moveToFirst()) {
                                close()
                                handler?.post {
                                    textView.text = String.format(getString(R.string.mediaSearchActivity_none), 0)
                                }
                                return
                            }
        
                            AudioData.audioDataList.clear()
                            do {
                                if (AudioData.ignoredData
                                        .contains(
                                            getString(getColumnIndex(MediaStore.Audio.AudioColumns._ID))
                                        )
                                ) {
                                    continue
                                }
            
                                AudioData.audioDataList.add(AudioData().apply {
                                    id = getString(getColumnIndex(MediaStore.Audio.AudioColumns._ID))
                                    title = getString(getColumnIndex(MediaStore.Audio.AudioColumns.TITLE))
                                    artist = getString(getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST))
                                    album = getString(getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM))
                                    duration = getLong(getColumnIndex(MediaStore.Audio.AudioColumns.DURATION))
                                })
            
                                AudioData.audioDataMap[getString(getColumnIndex(MediaStore.Audio.AudioColumns._ID))] =
                                    AudioData.audioDataList.last()
            
                            } while (moveToNext())
        
                            close()
                        }
    
                    // Write to storage
                    // 写入存储
                    File(getExternalFilesDir(AudioDataDir), AudioDataListFile).apply {
                        if (!exists()) {
                            createNewFile()
                        }
        
                        writeText("")
        
                        // Write
                        // 写入
                        outputStream().use { os ->
                            ObjectOutputStream(os).use { oos ->
                                oos.writeObject(AudioData.audioDataList)
                            }
                        }
                    }
                    File(getExternalFilesDir(AudioDataDir), AudioDataMapFile).apply {
                        if (!exists()) {
                            createNewFile()
                        }
        
                        writeText("")
        
                        // Write
                        // 写入
                        outputStream().use { os ->
                            ObjectOutputStream(os).use { oos ->
                                oos.writeObject(AudioData.audioDataList)
                            }
                        }
                    }
    
                    val mediaMetadataRetriever = MediaMetadataRetriever()
                    var byteArray: ByteArray?
    
                    val smaller = resources.getDimension(R.dimen.mainActivity_bottom_sheet_icon_size)
                    val matrix1 = Matrix()
                    val matrix2 = Matrix()
    
                    for (i in AudioData.audioDataList) {
                        mediaMetadataRetriever.setDataSource(
                            this@AudioImportActivity, Uri.parse(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()
                                    + File.separator
                                    + i.id
                            )
                        )
                        byteArray = mediaMetadataRetriever.embeddedPicture
                        byteArray ?: continue
        
        
                        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size).run {
                            when {
                                width > height -> {
                                    Bitmap.createBitmap(this, (width - height) / 2, 0, height, height)
                                }
                                width < height -> {
                                    Bitmap.createBitmap(this, 0, (height - width), width, width)
                                }
                                else -> {
                                    this
                                }
                            }
                        }.apply {
                            // Extension is not given,
                            // prevent being scanned by system media
                            // 不添加后缀名, 防止被系统相册刷到
                            File(getExternalFilesDir(SmallAlbumRound), i.id).outputStream().use { ros ->
                                RoundedBitmapDrawableFactory.create(
                                    resources,
                                    //kotlin.run {
                                        Bitmap.createBitmap(
                                            this, 0, 0, width, height, matrix1.apply {
                                                (smaller / width).apply { preScale(this, this) }
                                            },
                                            true
                                        )//.apply {
                                         //   File(getExternalFilesDir(SmallAlbumRound), i.id).outputStream().use { os ->
                                         //       compress(Bitmap.CompressFormat.PNG, 100, os)
                                         //   }
                                        //}
                                    //}
                                ).apply { isCircular = true }.bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, ros)
                            }
            
                            File(getExternalFilesDir(AlbumNormal), i.id).outputStream().use { os ->
                                Bitmap.createBitmap(
                                    this, 0, 0, width, height, matrix2.apply {
                                        (resources.displayMetrics.widthPixels / width).toFloat().apply {
                                            setScale(this, this)
                                        }
                                    },
                                    true
                                ).compress(Bitmap.CompressFormat.PNG, 100, os)
                            }
            
                        }
        
        
                    }
    
                    handler?.post {
                        textView.text =
                            String.format(
                                getString(R.string.mediaSearchActivity_searched),
                                AudioData.audioDataList.size
                            )
                        imageView.visibility = View.VISIBLE
                        progressBar.visibility = View.INVISIBLE
                    }
    
                }
                
                override fun workDone(workProduct: MutableMap<String, Any?>?, handler: Handler?) {
                    searching = false
                }
                
                override fun workFail(e: Exception, workProduct: MutableMap<String, Any?>?, handler: Handler?) {
                    searching = false
                }
                
            }
        )
        
        
    }
    
    override fun onBackPressed() {
        if (!searching)
            super.onBackPressed()
    }
    
}