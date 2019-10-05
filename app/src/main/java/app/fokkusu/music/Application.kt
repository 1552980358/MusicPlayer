package app.fokkusu.music

import android.app.Application
import android.provider.MediaStore
import app.fokkusu.music.service.PlayService

/**
 * @File    : Application
 * @Author  : 1552980358
 * @Date    : 4 Oct 2019
 * @TIME    : 5:23 PM
 **/

class Application : Application() {
    companion object {
        var getApplication = null as app.fokkusu.music.Application?
            private set
            get() = field as app.fokkusu.music.Application
        var loading = true
            private set
    }
    
    init {
        getApplication = this
    }
    
    override fun onCreate() {
        super.onCreate()
        
    }
}