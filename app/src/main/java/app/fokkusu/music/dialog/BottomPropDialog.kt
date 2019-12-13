package app.fokkusu.music.dialog

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.text.TextUtils
import app.fokkusu.music.R
import app.fokkusu.music.base.Constants.Companion.Dir_Cover
import app.fokkusu.music.base.Constants.Companion.Dir_Lyric
import app.fokkusu.music.base.Constants.Companion.Ext_Cover
import app.fokkusu.music.base.Constants.Companion.Ext_Lyric
import app.fokkusu.music.base.Constants.Companion.Hyphen_STR
import app.fokkusu.music.service.PlayService
import kotlinx.android.synthetic.main.dialog_bottom_prop.imageView
import kotlinx.android.synthetic.main.dialog_bottom_prop.textView_artist
import kotlinx.android.synthetic.main.dialog_bottom_prop.textView_id
import kotlinx.android.synthetic.main.dialog_bottom_prop.textView_path_file
import kotlinx.android.synthetic.main.dialog_bottom_prop.textView_path_img
import kotlinx.android.synthetic.main.dialog_bottom_prop.textView_path_lyric
import kotlinx.android.synthetic.main.dialog_bottom_prop.textView_title
import java.io.File

/**
 * @File    : BottomOptDialog
 * @Author  : 1552980358
 * @Date    : 2019/12/3
 * @TIME    : 18:09
 **/

class BottomPropDialog : BaseBottomSheetDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setContentView(R.layout.dialog_bottom_prop)
            
            PlayService.getCurrentMusicInfo().also { music ->
                textView_title.apply {
                    setSingleLine()
                    setHorizontallyScrolling(true)
                    marqueeRepeatLimit = -1
                    ellipsize = TextUtils.TruncateAt.MARQUEE
                    isSelected = true
                    text = music?.title()
                }
                
                textView_artist.apply {
                    setSingleLine()
                    setHorizontallyScrolling(true)
                    marqueeRepeatLimit = -1
                    ellipsize = TextUtils.TruncateAt.MARQUEE
                    isSelected = true
                    text = music?.artist()
                }
                
                textView_id.text = music?.id()
                
                textView_path_file.text = music?.path()
                
                textView_path_lyric.text =
                    File(
                        context.externalCacheDir!!.absolutePath.plus('/').plus(Dir_Lyric),
                        music?.id().plus(Ext_Lyric)
                    ).run {
                        if (exists()) absolutePath else Hyphen_STR
                    }
                
                textView_path_img.text =
                    File(
                        context.externalCacheDir!!.absolutePath.plus('/').plus(Dir_Cover),
                        music?.id().plus(Ext_Cover)
                    ).run {
                        if (exists()) absolutePath else Hyphen_STR
                    }
            }
            
            val cover = PlayService.getCurrentBitmap()//info.albumCover() ?: return this
            
            cover ?: return this
            
            if (cover.width == cover.height) {
                imageView.setImageBitmap(
                    Bitmap.createBitmap(
                        cover, 0, 0, cover.width, cover.height, Matrix().apply {
                            (resources.getDimensionPixelSize(R.dimen.dp_50) / cover.width.toFloat()).apply {
                                setScale(this, this)
                            }
                        }, true
                    )
                )
                return this
            }
            
            if (cover.height < cover.width) {
                imageView.setImageBitmap(
                    Bitmap.createBitmap(
                        cover,
                        0,
                        0,
                        (cover.width - cover.height) / 2,
                        cover.height,
                        Matrix().apply {
                            (resources.getDimensionPixelSize(R.dimen.dp_50) / cover.height.toFloat()).apply {
                                setScale(this, this)
                            }
                        },
                        true
                    )
                )
                return this
            }
            
            imageView.setImageBitmap(
                Bitmap.createBitmap(
                    cover,
                    0,
                    0,
                    0,
                    (cover.height - cover.width) / 2,
                    Matrix().apply {
                        (resources.getDimensionPixelSize(R.dimen.dp_50) / cover.width.toFloat()).apply {
                            setScale(this, this)
                        }
                    },
                    true
                )
            )
            
        }
    }
}