package app.github1552980358.android.musicplayer.service

import java.io.Serializable

/**
 * @file    : [CycleMode]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/13
 * @time    : 21:59
 **/

enum class CycleMode(@Suppress("UNUSED_PARAMETER") mode: String): Serializable {
    LIST_CYCLE("LIST_CYCLE"),
    SINGLE_CYCLE("SINGLE_CYCLE"),
    RANDOM_ACCESS("RANDOM_ACCESS")
}