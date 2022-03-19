package projekt.cloud.piece.music.player.ui.play

import android.graphics.Point
import androidx.lifecycle.ViewModel
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.ui.play.playControl.PlayControlFragment

class PlayViewModel: ViewModel() {

    val fragmentList = listOf<BaseFragment>(
        PlayControlFragment()
    )

    var isPointSet = false
    val circularRevelPoint = Point()

}