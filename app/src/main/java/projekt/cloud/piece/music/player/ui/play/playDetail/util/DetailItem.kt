package projekt.cloud.piece.music.player.ui.play.playDetail.util

import androidx.annotation.StringRes

data class DetailItem(@StringRes val content: Int) {
    companion object {
        private const val EMPTY_STR = ""
    }
    var onClick: (() -> Unit)? = null
    var title = EMPTY_STR
}