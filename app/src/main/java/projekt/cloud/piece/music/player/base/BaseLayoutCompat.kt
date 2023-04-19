package projekt.cloud.piece.music.player.base

import androidx.viewbinding.ViewBinding

abstract class BaseLayoutCompat<VB: ViewBinding>(private var _binding: VB?) {

    protected val binding: VB
        get() = _binding!!

    fun onDestroy() {
        _binding = null
        onRecycleInstance()
    }

    open fun onRecycleInstance() = Unit

}