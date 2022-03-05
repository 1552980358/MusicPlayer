package sakuraba.saki.player.music

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

open class BaseThemeAppCompatActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {

        delegate.installViewFactory()
        delegate.onCreate(savedInstanceState)

        super.onCreate(savedInstanceState, persistentState)
    }

}