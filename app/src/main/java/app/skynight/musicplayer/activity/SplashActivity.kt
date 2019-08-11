package app.skynight.musicplayer.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.util.log

/**
 * @FILE:   SplashActivity
 * @AUTHOR: 1552980358
 * @DATE:   21 Jul 2019
 * @TIME:   9:42 AM
 **/

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        log("SplashActivity", "onCreate")
        super.onCreate(savedInstanceState)
        startActivity(
            Intent(
                this,
                if (MainApplication.sharedPreferences.getBoolean(
                        "init",
                        false
                    )
                ) {
                    log("SplashActivity", "startIntroActivity\n==========")
                    IntroActivity::class.java
                } else {
                    log("SplashActivity", "startCheckPermissionActivity\n==========")
                    CheckPermissionActivity::class.java
                }
            )
        )
        finish()
    }
}