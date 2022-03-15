package projekt.cloud.piece.music.player.base

import androidx.fragment.app.Fragment

open class BackPressedTerminationFragment: Fragment() {

    open fun onBackPressed() = true

    val canBackStack get() = onBackPressed()

}