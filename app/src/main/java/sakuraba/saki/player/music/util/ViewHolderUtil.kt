package sakuraba.saki.player.music.util

import androidx.recyclerview.widget.RecyclerView.ViewHolder

object ViewHolderUtil {
    
    fun <T: ViewHolder> T.bindHolder(block: T.() -> Unit) = block(this)
    
}