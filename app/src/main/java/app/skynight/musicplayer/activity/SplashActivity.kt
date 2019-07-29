package app.skynight.musicplayer.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.skynight.musicplayer.MainApplication

/**
 * @FILE:   SplashActivity
 * @AUTHOR: 1552980358
 * @DATE:   21 Jul 2019
 * @TIME:   9:42 AM
 **/

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(
            Intent(
                this,
                if (MainApplication.sharedPreferences.getBoolean(
                        "init",
                        false
                    )
                ) IntroActivity::class.java else CheckPermissionActivity::class.java
            )
        )
        finish()
    }
}