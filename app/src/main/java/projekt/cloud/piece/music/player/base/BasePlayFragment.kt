package projekt.cloud.piece.music.player.base

import projekt.cloud.piece.music.player.ui.play.PlayFragment
import projekt.cloud.piece.music.player.ui.play.util.FragmentManager

open class BasePlayFragment(protected val playFragmentManager: FragmentManager)
    : BackPressedTerminationFragment()