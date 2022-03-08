package projekt.cloud.piece.music.player.util

import androidx.recyclerview.widget.RecyclerView.ViewHolder

object ViewHolderUtil {

    @JvmStatic
    fun <VH: ViewHolder> VH.bind(block: VH.() -> Unit) = block(this)

}