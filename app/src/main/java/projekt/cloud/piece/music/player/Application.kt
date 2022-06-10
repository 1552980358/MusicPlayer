package projekt.cloud.piece.music.player

import android.app.Application
import projekt.cloud.piece.music.player.service.WebService
import projekt.cloud.piece.music.player.service.web.Extra.ACTION_START_COMMAND
import projekt.cloud.piece.music.player.service.web.Extra.ACTION_START_SERVER
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui
import projekt.cloud.piece.music.player.util.SharedPreferencesUtil.boolPrefsUnblock
import projekt.cloud.piece.music.player.util.ServiceUtil.serviceIntent

class Application: Application() {
    
    override fun onCreate() {
        super.onCreate()
    
        boolPrefsUnblock(R.string.key_web_server_switch) {
            ui {
                startService(serviceIntent<WebService> {
                    putExtra(ACTION_START_COMMAND, ACTION_START_SERVER)
                })
            }
        }
    }
    
}