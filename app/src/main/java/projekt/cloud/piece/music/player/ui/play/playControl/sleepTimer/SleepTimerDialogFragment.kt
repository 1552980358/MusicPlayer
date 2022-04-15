package projekt.cloud.piece.music.player.ui.play.playControl.sleepTimer

import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseValueSelectDialogFragment

class SleepTimerDialogFragment: BaseValueSelectDialogFragment() {

    override fun setTitle() = R.string.sleep_timer_title

    override fun setSelectionTitle() = R.string.sleep_timer_unit

    override fun setPositiveButtonText() = R.string.sleep_timer_start

    private lateinit var onPositive: (String?) -> Unit
    private lateinit var onStopClick: () -> Unit

    override fun setSelectionList() = resources.getStringArray(R.array.sleep_timer_time).toList().apply {
        initialValue = this[2]
    }

    override fun onPositiveClick(newValue: String?) = onPositive(newValue)

    override fun setNeutralButtonText() = R.string.sleep_timer_stop

    override fun onDefaultClick() = onStopClick()

    fun setPositiveClick(onPositive: (String?) -> Unit) = apply {
        this.onPositive = onPositive
    }

    fun setStop(onStopClick: () -> Unit) = apply {
        this.onStopClick = onStopClick
    }

    fun setEnableStop(enable: Boolean) = apply {
        hasDefault = enable
    }

}