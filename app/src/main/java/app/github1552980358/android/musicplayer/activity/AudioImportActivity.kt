package app.github1552980358.android.musicplayer.activity

import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.Constant
import app.github1552980358.android.musicplayer.base.Constant.Companion.BackgroundThread
import app.github1552980358.android.musicplayer.base.AudioData
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
                            
                            AudioData.audioData.clear()
                            do {
                                if (AudioData.ignoredData
                                        .contains(
                                            getString(getColumnIndex(MediaStore.Audio.AudioColumns._ID)))
                                ) {
                                    continue
                                }
                                
                                AudioData.audioData.add(AudioData().apply {
                                    id = getString(getColumnIndex(MediaStore.Audio.AudioColumns._ID))
                                    title = getString(getColumnIndex(MediaStore.Audio.AudioColumns.TITLE))
                                    artist = getString(getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST))
                                    album = getString(getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM))
                                    duration = getLong(getColumnIndex(MediaStore.Audio.AudioColumns.DURATION))
                                })
                            } while (moveToNext())
                            
                            close()
                        }
                    
                    File(getExternalFilesDir(Constant.AudioDataFile), Constant.AudioDataFile).apply {
                        // Make a empty content
                        // 使文件为空
                        if (exists()) {
                            //delete()
                            writeText("")
                        }
                        
                        createNewFile()
                        
                        outputStream().use { os ->
                            ObjectOutputStream(os).use { oos ->
                                oos.writeObject(AudioData.audioData)
                            }
                        }
                    }
                    
                    handler?.post {
                        textView.text =
                            String.format(getString(R.string.mediaSearchActivity_searched), AudioData.audioData.size)
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