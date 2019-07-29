package app.skynight.musicplayer.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
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
import app.skynight.musicplayer.base.BaseAppCompatActivity
import app.skynight.musicplayer.util.UnitUtil.Companion.getPx
import java.lang.Exception
import kotlin.system.exitProcess

/**
 * @FILE:   CheckPermissionActivity
 * @AUTHOR: 1552980358
 * @DATE:   21 Jul 2019
 * @TIME:   9:16 PM
 **/

class CheckPermissionActivity : BaseAppCompatActivity() {

    private fun createView(): View {
        return RelativeLayout(this).apply {
            //background = ColorDrawable(ContextCompat.getColor(this@CheckPermissionActivity, R.color.activity_background))
            addView(AppCompatImageView(this@CheckPermissionActivity).apply {
                setImageBitmap(BitmapFactory.decodeStream(assets.open("huli.png")))
            }, RelativeLayout.LayoutParams(600, 600).apply {
                addRule(RelativeLayout.CENTER_VERTICAL)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createView())

        Thread {
            try {
                Thread.sleep(2000)
            } catch (e: Exception) {
                //
            }

            if (ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                checkPermission()
            }
        }.start()
    }

    private fun createDialogView(): View {
        return ScrollView(this).apply {
            addView(LinearLayout(this@CheckPermissionActivity).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                addView(LinearLayout(this@CheckPermissionActivity).apply {
                    orientation = LinearLayout.HORIZONTAL
                    addView(AppCompatImageView(this@CheckPermissionActivity).apply {
                        setImageDrawable(
                            ContextCompat.getDrawable(
                                this@CheckPermissionActivity, R.drawable.ic_sd_card
                            )
                        )
                    }, LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
                    addView(TextView(this@CheckPermissionActivity).apply {
                        text = getString(R.string.checkPermission_read)
                        setSingleLine()
                    }, LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
                }, LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                    setMargins(0, getPx(15), 0, 0)
                })
                addView(LinearLayout(this@CheckPermissionActivity).apply {
                    orientation = LinearLayout.HORIZONTAL
                    addView(AppCompatImageView(this@CheckPermissionActivity).apply {
                        setImageDrawable(
                            ContextCompat.getDrawable(
                                this@CheckPermissionActivity, R.drawable.ic_sd_card
                            )
                        )
                    }, LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
                    addView(TextView(this@CheckPermissionActivity).apply {
                        text = getString(R.string.checkPermission_read)
                        setSingleLine()
                    }, LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
                }, LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                    setMargins(0, getPx(5), 0, 0)
                })
            })
        }
    }

    private fun checkPermission() {
        AlertDialog.Builder(this).setTitle(R.string.checkPermission_title)
            .setView(createDialogView()).setPositiveButton(
                R.string.checkPermission_allow
            ) { _, _ ->
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 0
                )
            }.setNegativeButton(R.string.cancel) { _, _ -> exitProcess(0) }.setCancelable(false).apply {
                this@CheckPermissionActivity.runOnUiThread { this.show() }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            checkPermission()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}