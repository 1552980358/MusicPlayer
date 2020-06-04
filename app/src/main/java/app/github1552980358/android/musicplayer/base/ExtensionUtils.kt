package app.github1552980358.android.musicplayer.base

import java.io.OutputStream

/**
 * [ExtensionUtils]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/6/4
 * @time    : 10:22
 **/

/**
 * [tryCatch]
 * @param block
 * @author 1552980358
 * @since 0.1
 **/
inline fun tryCatch(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * [tryCatch]
 * @param block
 * @return [T]
 * @author 1552980358
 * @since 0.1
 **/
inline fun <T> T.applyTry(block: T.() -> Unit): T {
    tryCatch { block(this) }
    return this
}

/**
 * [tryCatch]
 * @param block
 * @return [R]
 * @author 1552980358
 * @since 0.1
 **/
inline fun <T: OutputStream, R> T.os(block: (T) -> R): R {
    try {
        return block(this)
    } catch (e: Exception) {
        throw e
    } finally {
        tryCatch { flush() }
        tryCatch { close() }
    }
}