package app.skynight.musicplayer.base

import java.lang.Exception

/**
 * @File    : InitNotAllowedException
 * @Author  : 1552980358
 * @Date    : 31 Jul 2019
 * @TIME    : 5:55 PM
 **/
class InitNotAllowedException: Exception{
    constructor(): super()
    constructor(info: String): super(info)

    init {

    }
}