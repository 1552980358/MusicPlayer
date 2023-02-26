package projekt.cloud.piece.music.player

import android.app.Application
import com.google.android.material.color.DynamicColors

class MusicPlayerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

}