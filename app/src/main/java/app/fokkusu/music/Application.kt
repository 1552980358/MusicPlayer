package app.fokkusu.music

import android.app.Application
import android.content.Intent
import android.provider.MediaStore
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_CONTENT
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_INIT
import app.fokkusu.music.service.PlayService

/**
 * @File    : Application
 * @Author  : 1552980358
 * @Date    : 4 Oct 2019
 * @TIME    : 5:23 PM
 **/

class Application : Application() {
    companion object {
        var getApplication: app.fokkusu.music.Application? = null
            private set
            get() = field as app.fokkusu.music.Application
        
        fun getContext() = getApplication!!
    }
    
    init {
        getApplication = this
    }
    
    override fun onCreate() {
        super.onCreate()
        startService(Intent(this, PlayService::class.java).putExtra(SERVICE_INTENT_CONTENT, SERVICE_INTENT_INIT))
    }
}