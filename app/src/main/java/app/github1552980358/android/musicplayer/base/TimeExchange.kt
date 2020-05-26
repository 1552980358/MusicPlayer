package app.github1552980358.android.musicplayer.base

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
     * @param time [Int]
     * @return [String]
     * @author 1552980358
     * @since 0.1
     */
    fun getTimeText(time: Long?): String {
        time?:return "00:00"
        return getTimeText(time.toInt() / 1000)
    }
    
    /**
     * [getTimeText]
     * @param time [Int]
     * @return [String]
     * @author 1552980358
     * @since 0.1
     */
    fun getTimeText(time: Int?): String {
        time?:return "00:00"
        return "${(time / 60).run { if (this < 10) "0$this" else this }}:${(time % 60).run { if (this < 10) "0$this" else this }}"
    }
    
    /**
     * [getTimeText]
     * @param time [Int]
     * @return [String]
     * @author 1552980358
     * @since 0.1
     */
    fun getTimeText(time: Int) = "${(time / 60).run { if (this < 10) "0$this" else this }}:${(time % 60).run { if (this < 10) "0$this" else this }}"
    
    /**
     * [getDateText]
     * @param time [Long]
     * @return [String]
     * @author 1552980358
     * @since 0.1
     */
    fun getDateText(time: Long?) = kotlin.run { if (time != null) SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(time) else "" }
    
}