package projekt.cloud.piece.cloudy

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions

class Cloudy: Application() {

    override fun onCreate() {
        super.onCreate()
        applyDynamicColors()
    }

    private fun applyDynamicColors() {
        DynamicColors.applyToActivitiesIfAvailable(
            this, createDynamicColorsOptions()
        )
    }

    private fun createDynamicColorsOptions(): DynamicColorsOptions {
        return DynamicColorsOptions.Builder()
            .setThemeOverlay(R.style.Theme_Cloudy)
            .build()
    }

}