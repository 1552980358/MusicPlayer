package projekt.cloud.piece.music.player.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import com.google.android.material.textview.MaterialTextView

class MetadataTextView(context: Context, attributeSet: AttributeSet?)
    : MaterialTextView(context, attributeSet) {

    companion object {
        
        @JvmStatic
        @BindingAdapter("artist")
        fun MetadataTextView.updateArtist(artist: String?) {
            this.artist = artist
        }
    
        @JvmStatic
        @BindingAdapter("album")
        fun MetadataTextView.updateAlbum(album: String?) {
            this.album = album
        }
        
        private const val EMPTY_STR = ""
        private const val DIVIDER = " - "
        
    }
    
    private var artist: String? = null
        set(value) {
            field = value
            updateText()
        }
    
    private var album: String? = null
        set(value) {
            field = value
            updateText()
        }
    
    init {
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
    }
    
    @Synchronized
    private fun updateText() {
        val artist = artist
        val album = album
        text = when {
            artist != null && album != null -> "$artist$DIVIDER$album"
            else -> EMPTY_STR
        }
    }
    
}