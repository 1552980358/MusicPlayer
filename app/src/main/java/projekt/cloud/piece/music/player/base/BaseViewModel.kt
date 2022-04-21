package projekt.cloud.piece.music.player.base

import androidx.lifecycle.ViewModel

abstract class BaseViewModel<F: BaseFragment>: ViewModel() {

    @Suppress("LeakingThis")
    private val _fragmentList = arrayListOf(*setFragments())

    val fragmentList get() = _fragmentList as List<F>

    abstract fun setFragments(): Array<F>

    operator fun get(index: Int) = _fragmentList[index]

    val size get() = _fragmentList.size

}