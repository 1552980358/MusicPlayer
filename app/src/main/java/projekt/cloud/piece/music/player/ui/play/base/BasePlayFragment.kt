package projekt.cloud.piece.music.player.ui.play.base

import projekt.cloud.piece.music.player.base.BaseFragment

open class BasePlayFragment: BaseFragment() {
    
    protected open val isSleepTimerEnabled: Boolean
        get() = (parentFragment as? BasePlayFragment)?.isSleepTimerEnabled == true
    
    protected open val sleepTimerMillis: String?
        get() = (parentFragment as? BasePlayFragment)?.sleepTimerMillis

    protected open fun startSleepTimer(millis: String) {
        (parentFragment as? BasePlayFragment)?.startSleepTimer(millis)
    }
    
    protected open fun stopSleepTimer() {
        (parentFragment as? BasePlayFragment)?.stopSleepTimer()
    }
    
    open fun onSleepTimerStop() = Unit
    
}