package app.github1552980358.android.musicplayer.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.DEFAULT_VALUE_INT
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_SONG_LIST_COVER
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_SONG_LIST_INFO
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_SONG_LIST_POS
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListCoverDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListFile
import app.github1552980358.android.musicplayer.base.SongListCover
import app.github1552980358.android.musicplayer.base.SongListInfo
import app.github1552980358.android.musicplayer.base.SongListInfo.Companion.songListInfoList
import app.github1552980358.android.musicplayer.base.TimeExchange
import kotlinx.android.synthetic.main.activity_song_list_editing.editTextDescription
import kotlinx.android.synthetic.main.activity_song_list_editing.editTextTitle
import kotlinx.android.synthetic.main.activity_song_list_editing.imageViewCover
import kotlinx.android.synthetic.main.activity_song_list_editing.relativeLayoutImage
import kotlinx.android.synthetic.main.activity_song_list_editing.textViewDateContent
import kotlinx.android.synthetic.main.activity_song_list_editing.toolbar
import kotlinx.android.synthetic.main.activity_song_list_editing.view_backgroundColour
import kotlinx.android.synthetic.main.activity_song_list_editing.view_subtitleColour
import kotlinx.android.synthetic.main.activity_song_list_editing.view_titleColour
import lib.github1552980358.ktExtension.jvm.javaClass.readObjectAs
import lib.github1552980358.ktExtension.jvm.javaClass.writeObject
import mkaflowski.mediastylepalette.MediaNotificationProcessor
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * [SongListEditingActivity]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/25
 * @time    : 12:41
 **/

class SongListEditingActivity: AppCompatActivity(), TimeExchange {
    
    /**
     * [bitmap]
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var bitmap: Bitmap
    
    /**
     * [songListCover]
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var songListCover: SongListCover
    /**
     * [songListInfo]
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var songListInfo: SongListInfo
    
    /**
     * [pos]
     * @author 1552980358
     * @since 0.1
     **/
    private var pos = DEFAULT_VALUE_INT
    
    /**
     * [bitmap]
     * @author 1552980358
     * @since 0.1
     **/
    private val bitmapSize by lazy { resources.getDimension(R.dimen.songListEditingActivity_bitmap_cutting_size) }
    
    /**
     * [onCreate]
     * @param savedInstanceState [Bundle]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_list_editing)
        
        setSupportActionBar(toolbar)
        
        pos = intent?.getIntExtra(INTENT_SONG_LIST_POS, DEFAULT_VALUE_INT)!!
        (intent?.getSerializableExtra(INTENT_SONG_LIST_INFO) as SongListInfo).apply {
            songListInfo = SongListInfo.copy(this)
    
            editTextTitle.setText(listTitle)
            textViewDateContent.text = getDateText(createDate)
            editTextDescription.setText(description)
            
            if (!hasCoverImage) {
                Log.e("hasCoverImage", "hasNoImage")
                songListCover = SongListCover()
                return@apply
            }
            
            File(getExternalFilesDir(SongListCoverDir), listTitle).apply {
                if (!exists()) {
                    @Suppress("LABEL_NAME_CLASH")
                    return@apply
                }
    
                readObjectAs<SongListCover>()?.apply {
                    songListCover = this
                    imageViewCover.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.size))
                    view_titleColour.setBackgroundColor(primaryTextColour)
                    view_subtitleColour.setBackgroundColor(secondaryTextColour)
                    view_backgroundColour.setBackgroundColor(backgroundColour)
                }
                /**
                 * inputStream().use { `is` ->
                 *     ObjectInputStream(`is`).use { ois ->
                 *         songListCover = (ois.readObject() as SongListCover)
                 *         imageViewCover.setImageBitmap(BitmapFactory.decodeByteArray(songListCover.image, 0, songListCover.image.size))
                 *         view_titleColour.setBackgroundColor(songListCover.primaryTextColour)
                 *         view_subtitleColour.setBackgroundColor(songListCover.secondaryTextColour)
                 *         view_backgroundColour.setBackgroundColor(songListCover.backgroundColour)
                 *     }
                 * }
                 **/
    
            }
        }
        
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    
        relativeLayoutImage.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK).setType("image/*"), 0)
        }
        
        editTextTitle.addTextChangedListener { editable ->
            editable?:return@addTextChangedListener
            
            if (editable.isEmpty()) {
                Toast.makeText(this, R.string.songListEditingActivity_toast, Toast.LENGTH_SHORT).show()
                editTextTitle.setText(songListInfo.listTitle.first().toString())
                return@addTextChangedListener
            }
            
            songListInfo.listTitle = editable.toString()
        }
        
        editTextDescription.addTextChangedListener { editable ->
            songListInfo.description = (editable ?: "").toString()
        }
        
    }
    
    /**
     * [onActivityResult]
     * @param requestCode [Int]
     * @param resultCode [Int]
     * @param data [Intent]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?:return
    
        // Cut image
        // 裁剪图片
        BitmapFactory.decodeStream(contentResolver.openInputStream(data.data!!)).run {
            MediaNotificationProcessor(this@SongListEditingActivity, this).apply {
                songListCover.backgroundColour = backgroundColor
                songListCover.primaryTextColour = primaryTextColor
                songListCover.secondaryTextColour = secondaryTextColor
                songListCover.isLight = isLight
            }
            view_backgroundColour.setBackgroundColor(songListCover.backgroundColour)
            view_titleColour.setBackgroundColor(songListCover.primaryTextColour)
            view_subtitleColour.setBackgroundColor(songListCover.secondaryTextColour)
            
            bitmap = when {
                width == height -> {
                    Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply {
                        (bitmapSize / width).apply { setScale(this, this) }
                    }, true)
                }
                width > height -> {
                    Bitmap.createBitmap(this, (width - height) / 2, 0, height, height, Matrix().apply {
                        (bitmapSize / height).apply { setScale(this, this) }
                    }, true)
                }
                else -> {
                    Bitmap.createBitmap(this, 0, (height - width) / 2, width, width, Matrix().apply {
                        (bitmapSize / width).apply { setScale(this, this) }
                    }, true)
                }
            }
            songListInfo.hasCoverImage = true
            imageViewCover.setImageBitmap(bitmap)
        }
        
    }
    
    /**
     * [finish]
     * @author 1552980358
     * @since 0.1
     **/
    override fun finish() {
        if (pos == DEFAULT_VALUE_INT) {
            super.finish()
            return
        }
        
        songListInfoList[pos] = songListInfo
        File(getExternalFilesDir(AudioDataDir), SongListFile).apply {
            delete()
            createNewFile()
    
            writeObject(songListInfoList)
    
            /**
             * outputStream().os { os ->
             *     ObjectOutputStream(os).os { oos ->
             *         oos.writeObject(songListInfoList)
             *     }
             * }
             **/
        }
        
        if (!::bitmap.isInitialized) {
            super.finish()
            return
        }
        
        ByteArrayOutputStream().use { baos ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            songListCover.image = baos.toByteArray()
        }
        
        File(getExternalFilesDir(SongListCoverDir), songListInfo.listTitle).apply {
            if (exists())
                delete()
            createNewFile()
    
            writeObject(songListCover)
            /**
             * outputStream().os { os ->
             *     ObjectOutputStream(os).os { oos ->
             *         oos.writeObject(songListCover)
             *     }
             * }
             **/
        }
    
        setResult(
            Activity.RESULT_OK,
            Intent().putExtra(INTENT_SONG_LIST_INFO, songListInfo)
                .putExtra(INTENT_SONG_LIST_COVER, songListCover)
        )
        super.finish()
    }
    
    /**
     * [onBackPressed]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onBackPressed() {
        finish()
    }
    
}