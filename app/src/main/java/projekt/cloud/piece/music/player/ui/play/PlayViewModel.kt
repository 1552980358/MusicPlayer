package projekt.cloud.piece.music.player.ui.play

import androidx.lifecycle.ViewModel
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.ui.play.playControl.PlayControlFragment

class PlayViewModel: ViewModel() {

    val fragmentList = listOf<BaseFragment>(
        PlayControlFragment()
    )

}