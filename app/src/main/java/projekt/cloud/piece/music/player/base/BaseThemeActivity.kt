package projekt.cloud.piece.music.player.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows

open class BaseThemeActivity: AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {

        delegate.installViewFactory()
        delegate.onCreate(savedInstanceState)

        setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
    }
    
}