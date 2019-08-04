package app.skynight.musicplayer.base

/**
 * @File    : InitConstructorNotAllowedException
 * @Author  : 1552980358
 * @Date    : 4 Aug 2019
 * @TIME    : 9:47 AM
 **/
class InitConstructorNotAllowedException: Exception {
    constructor(): super()
    constructor(text: String): super(text)
    init {

    }
}