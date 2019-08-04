package app.skynight.musicplayer.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import app.skynight.musicplayer.MainApplication
import kotlinx.android.synthetic.main.activity_musiclist.*
import app.skynight.musicplayer.R
import app.skynight.musicplayer.base.BaseSmallPlayerActivity

class MusicListActivity : BaseSmallPlayerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_musiclist)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        toolbar.overflowIcon = ContextCompat.getDrawable(this, if (MainApplication.customize) R.drawable.ic_more_cust else R.drawable.ic_more_def)
        try {
            (toolbar::class.java.getDeclaredField("mTitleTextView").apply { isAccessible = true }.get(toolbar) as TextView).apply {
                setHorizontallyScrolling(true)
                marqueeRepeatLimit = -1
                ellipsize = TextUtils.TruncateAt.MARQUEE
                isSelected = true
            }
        } catch (e: Exception) {
            //
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_musiclist, menu)
        menu!![2].setIcon(if (MainApplication.customize) R.drawable.ic_search_cust else R.drawable.ic_search_def)
        return super.onCreateOptionsMenu(menu)
    }
}
