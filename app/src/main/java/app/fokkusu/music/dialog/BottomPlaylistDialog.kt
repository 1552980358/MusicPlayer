package app.fokkusu.music.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.text.TextUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import app.fokkusu.music.R
import app.fokkusu.music.service.PlayService
import kotlinx.android.synthetic.main.dialog_bottom_playlist.imageView
import kotlinx.android.synthetic.main.dialog_bottom_playlist.linearLayout_root
import kotlinx.android.synthetic.main.dialog_bottom_playlist.listMusicView
import kotlinx.android.synthetic.main.dialog_bottom_playlist.textView_artist
import kotlinx.android.synthetic.main.dialog_bottom_playlist.textView_title

/**
 * @File    : BottomPlaylistDialog
 * @Author  : 1552980358
 * @Date    : 13 Oct 2019
 * @TIME    : 7:12 PM
 **/

class BottomPlaylistDialog : BottomSheetDialogFragment() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setContentView(R.layout.dialog_bottom_playlist)
            linearLayout_root.layoutParams.height = resources.displayMetrics.heightPixels / 2
            
            textView_title.apply {
                setHorizontallyScrolling(true)
                marqueeRepeatLimit = -1
                ellipsize = TextUtils.TruncateAt.MARQUEE
                isSelected = true
            }
            
            val info = PlayService.getCurrentMusicInfo() ?: return this
            textView_title.text = info.title()
            textView_artist.text = info.artist()
            
            val cover = info.albumCover() ?: return this
            
            val albumSize = resources.getDimensionPixelSize(R.dimen.dp_50)
            
            if (cover.width == cover.height) {
                imageView.setImageBitmap(
                    Bitmap.createBitmap(
                        cover, 0, 0, cover.width, cover.height, Matrix().apply {
                            (albumSize / cover.width.toFloat()).apply {
                                setScale(this, this)
                            }
                        }, true
                    )
                )
                
                return this
            }
            
            if (cover.width > cover.height) {
                imageView.setImageBitmap(
                    Bitmap.createBitmap(
                        cover,
                        0,
                        0,
                        (cover.width - cover.height) / 2,
                        cover.height,
                        Matrix().apply {
                            (albumSize / cover.height.toFloat()).apply {
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
                        (albumSize / cover.width.toFloat()).apply {
                            setScale(this, this)
                        }
                    },
                    true
                )
            )
            
            listMusicView.setUpAdapterWithMusicList(PlayService.getPlayList(), 2)
        }
    }
}