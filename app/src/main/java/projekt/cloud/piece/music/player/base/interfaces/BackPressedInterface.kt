package projekt.cloud.piece.music.player.base.interfaces

import androidx.fragment.app.Fragment

interface BackPressedInterface {

    fun onBackPressed(fragment: Fragment): Boolean

}