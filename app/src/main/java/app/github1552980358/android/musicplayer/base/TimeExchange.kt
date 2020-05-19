package app.github1552980358.android.musicplayer.base

/**
 * @file    : [TimeExchange]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/13
 * @time    : 14:33
 **/

interface TimeExchange {
    
    /**
     * [getTimeText]
     * @author 1552980358
     * @since 0.1
     */
    fun getTimeText(time: Long?): String {
        time?:return "00:00"
        return getTimeText(time.toInt() / 1000)
    }
    
    /**
     * [getTimeText]
     * @author 1552980358
     * @since 0.1
     */
    fun getTimeText(time: Int?): String {
        time?:return "00:00"
        return "${(time / 60).run { if (this < 10) "0$this" else this }}:${(time % 60).run { if (this < 10) "0$this" else this }}"
    }
    
    /**
     * [getTimeText]
     * @author 1552980358
     * @since 0.1
     */
    fun getTimeText(time: Int) = "${(time / 60).run { if (this < 10) "0$this" else this }}:${(time % 60).run { if (this < 10) "0$this" else this }}"
    
}