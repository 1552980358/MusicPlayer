package app.skynight.musicplayer.base

/**
 * @File    : BaseInfoClassInitNotAllowedException
 * @Author  : 1552980358
 * @Date    : 7 Aug 2019
 * @TIME    : 2:15 AM
 **/
class BaseInfoClassInitNotAllowedException: Exception {
    constructor(info: String): super(info)
    constructor(): super()
    init {

    }
}