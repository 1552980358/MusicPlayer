package app.github1552980358.android.musicplayer.service

import java.io.Serializable

/**
 * @file    : [CycleMode]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/13
 * @time    : 21:59
 **/

enum class CycleMode(@Suppress("UNUSED_PARAMETER") mode: Int): Serializable {
    LIST_CYCLE(0),
    SINGLE_CYCLE(1),
    RANDOM_ACCESS(2)
}