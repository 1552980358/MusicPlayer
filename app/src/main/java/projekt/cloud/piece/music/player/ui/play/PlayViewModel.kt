package projekt.cloud.piece.music.player.ui.play

import android.graphics.Point
import androidx.lifecycle.ViewModel
import projekt.cloud.piece.music.player.ui.play.palyLyric.PlayLyricFragment
import projekt.cloud.piece.music.player.ui.play.playControl.PlayControlFragment

class PlayViewModel: ViewModel() {

    val fragmentList = listOf(
        PlayControlFragment(), PlayLyricFragment()
    )

    var isPointSet = false
    val circularRevelPoint = Point()

}