package projekt.cloud.piece.music.player.ui.play.playDetail.util

import androidx.annotation.StringRes

data class DetailItem(@StringRes val title: Int) {
    companion object {
        private const val EMPTY_STR = ""
    }
    var onClick: (() -> Unit)? = null
    var content = EMPTY_STR
}