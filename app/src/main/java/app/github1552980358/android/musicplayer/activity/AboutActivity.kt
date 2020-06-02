package app.github1552980358.android.musicplayer.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.Constant.Companion.ABOUT_INTENT_EXTRA
import app.github1552980358.android.musicplayer.base.Constant.Companion.ABOUT_INTENT_LICENSE
import app.github1552980358.android.musicplayer.fragment.aboutActivity.OpenSourceLicenseFragment
import kotlinx.android.synthetic.main.activity_about.toolbar

/**
 * [AboutActivity]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/6/1
 * @time    : 13:45
 **/

class AboutActivity: AppCompatActivity() {
    
    /**
     * [onCreate]
     * @param savedInstanceState [Bundle]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_about)
        
        val fragment: Fragment
        when (intent?.getStringExtra(ABOUT_INTENT_EXTRA)) {
            ABOUT_INTENT_LICENSE -> {
                fragment = OpenSourceLicenseFragment()
                supportFragmentManager.beginTransaction()
                    .add(R.id.frameLayout, fragment)
                    .show(fragment)
                    .commit()
                toolbar.setTitle(R.string.aboutActivity_title_license)
            }
            
            else -> {
                finish()
                return
            }
            
        }
        
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        
        toolbar.setNavigationOnClickListener {
            finish()
        }
        
    }
    
}