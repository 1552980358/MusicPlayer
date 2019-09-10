package app.skynight.musicplayer.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.skynight.musicplayer.R
import app.skynight.musicplayer.base.AppCompatActivity
import app.skynight.musicplayer.util.Player
import app.skynight.musicplayer.util.log
import java.lang.Exception
import kotlin.system.exitProcess

/**
 * @FILE:   LoadingActivity
 * @AUTHOR: 1552980358
 * @DATE:   21 Jul 2019
 * @TIME:   9:16 PM
 **/

class LoadingActivity : AppCompatActivity() {
    @Suppress("PrivatePropertyName")
    private lateinit var textView_state: TextView

    @Suppress("PrivatePropertyName")
    override fun onCreate(savedInstanceState: Bundle?) {
        log("LoadingActivity", "onCreate")
        super.onCreate(savedInstanceState)
        @Suppress("LocalVariableName")
        val textView_timer: TextView
        setContentView(RelativeLayout(this).apply {
            background =
                ContextCompat.getDrawable(this@LoadingActivity, R.color.activity_background)
            log("LoadingActivity", "setContentView")
            val imgId: Int
            addView(AppCompatImageView(this@LoadingActivity).apply {
                setImageBitmap(BitmapFactory.decodeStream(assets.open("huli.png")))
                id = View.generateViewId()
                imgId = id
            }, RelativeLayout.LayoutParams(600, 600).apply {
                addRule(RelativeLayout.CENTER_VERTICAL)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
            })

            addView(TextView(this@LoadingActivity).apply {
                textView_state = this
                id = View.generateViewId()
                setTextColor(ContextCompat.getColor(this@LoadingActivity, R.color.black))
                textSize = resources.getDimension(R.dimen.checkPermissionActivity_state)
                setText(R.string.checkPermission_checking)
            }, RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                addRule(RelativeLayout.BELOW, imgId)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
            })

            addView(LinearLayout(this@LoadingActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                addView(TextView(this@LoadingActivity).apply {
                    textView_timer = this
                    text = "0"
                    setTextColor(
                        ContextCompat.getColor(
                            this@LoadingActivity,
                            R.color.black
                        )
                    )
                    textSize = resources.getDimension(R.dimen.checkPermissionActivity_timer)
                })
                addView(TextView(this@LoadingActivity).apply {
                    textSize = resources.getDimension(R.dimen.checkPermissionActivity_timer)
                    setTextColor(
                        ContextCompat.getColor(
                            this@LoadingActivity,
                            R.color.black
                        )
                    )
                    text = "s"
                })
            }, RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                addRule(RelativeLayout.BELOW, textView_state.id)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
            })
            /*
            addView(TextView(this@LoadingActivity).apply {
                textView_timer = this
                text = "0"
                id = View.generateViewId()
                setTextColor(ContextCompat.getColor(this@LoadingActivity, R.color.black))
                textSize = resources.getDimension(R.dimen.checkPermissionActivity_timer)
            }, RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                addRule(RelativeLayout.BELOW, textView_state.id)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
            })
            addView(TextView(this@LoadingActivity).apply {
                textSize = resources.getDimension(R.dimen.checkPermissionActivity_timer)
                setTextColor(ContextCompat.getColor(this@LoadingActivity, R.color.black))
                text = "s"
            }, RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                addRule(RelativeLayout.RIGHT_OF, textView_timer.id)
                addRule(RelativeLayout.BELOW, textView_state.id)
            })
             */
        })

        Thread {
            var s = 0
            while (!Player.launchDone) {
                try {
                    Thread.sleep(1000)
                } catch (e: Exception) {
                    //
                }
                s++
                runOnUiThread { textView_timer.text = s.toString() }
            }
        }.start()

        Thread {
            log("LoadingActivity", "CheckPermission")
            if (ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                loadSettings()
            } else {
                checkPermission()
            }
        }.start()
    }

    private fun checkPermission() {
        runOnUiThread { textView_state.setText(R.string.checkPermission_authorizing) }
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 0
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        textView_state.setText(R.string.checkPermission_checking)
        log("LoadingActivity", "onRequestPermissionsResult")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            loadSettings()
        } else {
            checkPermission()
        }
    }

    private fun loadSettings() {
        Thread {
            runOnUiThread { textView_state.setText(R.string.checkPermission_loadingMusic) }
            log("LoadingActivity", "startPlayer")
            Player.getPlayer
            while (!Player.launchDone) {
                try {
                    Thread.sleep(10)
                } catch (e: Exception) {
                    //
                }
            }
            log("LoadingActivity", "startMainActivity\n==========")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }.start()
    }
}