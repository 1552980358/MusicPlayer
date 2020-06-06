package app.github1552980358.android.musicplayer.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.ArrayListUtil
import app.github1552980358.android.musicplayer.base.AudioData
import app.github1552980358.android.musicplayer.base.Colour
import app.github1552980358.android.musicplayer.base.Constant.Companion.AlbumColourDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.AlbumNormalDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.AlbumRoundDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataListFile
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataListRandomFile
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataMapFile
import app.github1552980358.android.musicplayer.base.Constant.Companion.BackgroundThread
import app.github1552980358.android.musicplayer.base.Constant.Companion.INITIALIZE
import app.github1552980358.android.musicplayer.base.Constant.Companion.INITIALIZE_EXTRA
import app.github1552980358.android.musicplayer.base.Constant.Companion.IgnoredFile
import app.github1552980358.android.musicplayer.service.PlayService
import app.github1552980358.android.musicplayer.service.PlayService.Companion.START_FLAG
import kotlinx.android.synthetic.main.activity_audio_import.imageView
import kotlinx.android.synthetic.main.activity_audio_import.progressBar
import kotlinx.android.synthetic.main.activity_audio_import.textView
import kotlinx.android.synthetic.main.activity_audio_import.toolbar
import lib.github1552980358.ktExtension.android.graphics.cutCircle
import lib.github1552980358.ktExtension.android.graphics.writePNGToFile
import lib.github1552980358.ktExtension.jvm.javaClass.writeObject
import lib.github1552980358.labourforce.LabourForce
import lib.github1552980358.labourforce.labours.work.LabourWorkBuilder
import mkaflowski.mediastylepalette.MediaNotificationProcessor
import java.io.File
import java.io.Serializable

/**
 * @file    : [AudioImportActivity]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/9
 * @time    : 9:18
 **/

class AudioImportActivity: AppCompatActivity(), ArrayListUtil {
    
    /**
     * [searching]
     * @author 1552980358
     * @since 0.1
     **/
    private var searching = false
    
    /**
     * [audioDataMap]
     * @author 1552980358
     * @since 0.1
     **/
    private var audioDataMap = hashMapOf<String, AudioData>()
    
    /**
     * [audioDataList]
     * @author 1552980358
     * @since 0.1
     **/
    private var audioDataList = ArrayList<AudioData>()
    
    /**
     * [onCreate]
     * @param savedInstanceState [Bundle]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_import)
        
        textView.text = String.format(getString(R.string.audioImportActivity_searching), 0)
        
        toolbar.setTitle(R.string.audioImportActivity_toolbar_searching)
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        
        LabourForce.onDuty.sendWork2Labour(
            BackgroundThread,
            LabourWorkBuilder.getBuilder(Handler())
                .setWorkContent { _, handler ->
                    Log.e("setWorkContent", "startSearching")
                    
                    searching = true
                    
                    var ignoredAudioId = ArrayList<String>()
                    
                    File(getExternalFilesDir(AudioDataDir), IgnoredFile).apply {
                        if (!exists())
                            return@apply
                        
                        ignoredAudioId = (readLines().toMutableList() as ArrayList<String>)
                    }
                    
                    handler?.post {
                        textView.text = String.format(getString(R.string.audioImportActivity_searching), 0)
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
                                    toolbar.setTitle(R.string.audioImportActivity_toolbar_complete)
                                    textView.setText(R.string.audioImportActivity_none)
                                }
                                return@setWorkContent
                            }
                            
                            if (!moveToFirst()) {
                                close()
                                return@setWorkContent
                            }
                            
                            //audioDataList.clear()
                            //audioDataMap.clear()
                            
                            do {
                                AudioData().apply {
                                    id = getString(getColumnIndex(MediaStore.Audio.AudioColumns._ID))
                                    title = getString(getColumnIndex(MediaStore.Audio.AudioColumns.TITLE))
                                    artist = getString(getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST))
                                    album = getString(getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM))
                                    duration = getLong(getColumnIndex(MediaStore.Audio.AudioColumns.DURATION))
                                }.apply {
                                    audioDataMap[id] = this
                                    
                                    if (ignoredAudioId
                                            .contains(
                                                getString(getColumnIndex(MediaStore.Audio.AudioColumns._ID))
                                            )
                                    ) {
                                        @Suppress("LABEL_NAME_CLASH")
                                        return@apply
                                    }
                                    
                                    audioDataList.add(this)
                                }
                                
                            } while (moveToNext())
                            
                            close()
                        }
                    
                    // Sort by PinYin characters
                    // 以拼音字母排序
                    audioDataList.sortBy { it.titlePinYin }
                    
                    // Write to storage
                    // 写入存储
                    File(getExternalFilesDir(AudioDataDir), AudioDataListFile).apply {
                        //if (!exists()) {
                        //    createNewFile()
                        //}
                        //writeText("")
                        if (exists()) {
                            delete()
                        }
                        createNewFile()
    
                        // Write
                        // 写入
    
                        /**
                         * outputStream().os { os ->
                         *     ObjectOutputStream(os).os { oos ->
                         *         oos.writeObject(audioDataList)
                         *     }
                         * }
                         **/
                        writeObject(audioDataList)
                    }
                    File(getExternalFilesDir(AudioDataDir), AudioDataMapFile).apply {
                        //if (!exists()) {
                        //    createNewFile()
                        //}
                        //writeText("")
                        if (exists()) {
                            delete()
                        }
                        createNewFile()
    
                        // Write
                        // 写入
    
                        /**
                         * outputStream().os { os ->
                         *    ObjectOutputStream(os).os { oos ->
                         *        oos.writeObject(audioDataMap)
                         *    }
                         * }
                         **/
                        writeObject(audioDataMap)
                    }
                    File(getExternalFilesDir(AudioDataDir), AudioDataListRandomFile).apply {
                        if (exists()) {
                            delete()
                        }
                        createNewFile()
    
                        /**
                         * outputStream().os { os ->
                         *    ObjectOutputStream(os).os { oos ->
                         *        oos.writeObject(copyAndShuffle(audioDataList))
                         *    }
                         * }
                         **/
                        writeObject(copyAndShuffle(audioDataList))
                    }
    
                    val mediaMetadataRetriever = MediaMetadataRetriever()
                    var byteArray: ByteArray?
                    //var paint: Paint
                    //var canvas: Canvas
                    //val mode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                    val smaller = resources.getDimension(R.dimen.mainActivity_bottom_sheet_icon_size)
                    val matrix1 = Matrix()
                    val matrix2 = Matrix()
    
                    for ((i, j) in audioDataList.withIndex()) {
                        handler?.post {
                            toolbar.setTitle(R.string.audioImportActivity_toolbar_handling)
                            textView.text =
                                String.format(getString(R.string.audioImportActivity_handling), i, audioDataList.size)
                        }
                        
                        mediaMetadataRetriever.setDataSource(
                            this@AudioImportActivity, Uri.parse(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()
                                    + File.separator
                                    + j.id
                            )
                        )
                        byteArray = mediaMetadataRetriever.embeddedPicture
                        byteArray ?: continue
                        
                        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size).run {
                            // Bitmap colour palette treating
                            // 图片颜色进行调色板处理
                            // Credit:
                            // [https://github.com/mkaflowski/Media-Style-Palette/]
                            // com.github.mkaflowski:Media-Style-Palette:1.3
                            MediaNotificationProcessor(this@AudioImportActivity, this).apply {
                                File(getExternalFilesDir(AlbumColourDir), j.id).writeObject(
                                    Colour(
                                        backgroundColor,
                                        primaryTextColor,
                                        secondaryTextColor,
                                        isLight
                                    )
                                )
                                /**
                                 * .outputStream().os { os ->
                                 *     ObjectOutputStream(os).os { oos ->
                                 *         oos.writeObject(
                                 *             Colour(
                                 *                 backgroundColor,
                                 *                 primaryTextColor,
                                 *                 secondaryTextColor,
                                 *                 isLight
                                 *             )
                                 *         )
                                 *     }
                                 * }
                                 **/
    
                            }
                            
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
                            //File(getExternalFilesDir(AlbumRoundDir), j.id).outputStream().os { os ->
                            Bitmap.createBitmap(
                                this, 0, 0, width, width, matrix1.apply {
                                    (smaller / width).apply { setScale(this, this) }
                                },
                                true
                            ).cutCircle().writePNGToFile(File(getExternalFilesDir(AlbumRoundDir), j.id))
                            //.run {
                            // Apply canvas cutting bitmap into circle
                            // 利用canvas把图片剪裁成圆形
                            //Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888).apply {
                            //    canvas = Canvas(this)
                            //    paint = Paint()
                            //    paint.isAntiAlias = true
                            //    canvas.drawCircle(width / 2F, width / 2F, width / 2F, paint)
                            //    paint.xfermode = mode
                            //    canvas.drawBitmap(this@run, 0F, 0F, paint)
                            //}
                            //}.compress(Bitmap.CompressFormat.PNG, 100, os)
                            //}
                            //File(getExternalFilesDir(AlbumNormalDir), j.id).outputStream().os { os ->
                            Bitmap.createBitmap(
                                this, 0, 0, width, width, matrix2.apply {
                                    (resources.displayMetrics.widthPixels / width).toFloat().apply {
                                        setScale(this, this)
                                    }
                                },
                                true
                            ).writePNGToFile(
                                File(
                                    getExternalFilesDir(AlbumNormalDir),
                                    j.id
                                )
                            )//.compress(Bitmap.CompressFormat.PNG, 100, os)
                            //}
                        }
                        
                    }
                    
                    handler?.post {
                        toolbar.setTitle(R.string.audioImportActivity_toolbar_complete)
                        textView.text =
                            String.format(
                                getString(R.string.audioImportActivity_searched),
                                audioDataList.size
                            )
                        imageView.visibility = View.VISIBLE
                        progressBar.visibility = View.INVISIBLE
                    }
                }
                .setWorkDone { _, handler ->
                    searching = false
                    if (audioDataList.isEmpty()) {
                        handler?.post {
                            imageView.visibility = View.VISIBLE
                            progressBar.visibility = View.INVISIBLE
                            textView.text = String.format(getString(R.string.audioImportActivity_none), 0)
                            toolbar.setTitle(R.string.audioImportActivity_toolbar_complete)
                        }
                    }
                    runOnUiThread {
                        startService(
                            Intent(this@AudioImportActivity, PlayService::class.java)
                                .putExtra(START_FLAG, INITIALIZE)
                                .putExtra(INITIALIZE_EXTRA, audioDataMap as Serializable)
                        )
                    }
                }
                .setWorkFail { e, _, handler ->
                    searching = false
                    handler?.post {
                        toolbar.setTitle(R.string.audioImportActivity_toolbar_failed)
                        imageView.visibility = View.VISIBLE
                        progressBar.visibility = View.INVISIBLE
                        textView.text = getString(R.string.audioImportActivity_fail) + e.toString()
                    }
                }
                .build()
        )
        
    }
    
    /**
     * [onBackPressed]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onBackPressed() {
        if (!searching) {
            super.onBackPressed()
        }
    }
    
}