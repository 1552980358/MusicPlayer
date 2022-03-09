package projekt.cloud.piece.music.player.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseThemeActivity: AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.installViewFactory()
        delegate.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
    }
    
}