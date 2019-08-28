package app.skynight.musicplayer.fragment.activity_main

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.skynight.musicplayer.util.log
import app.skynight.musicplayer.R
import kotlinx.android.synthetic.main.fragment_main.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * @File    : MusicFragment
 * @Author  : 1552980358
 * @Date    : 30 Jul 2019
 * @TIME    : 9:21 PM
 **/

class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        log("MainFragment", "- onCreateView")
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        log("MainFragment", "- onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        try {
            val hr = SimpleDateFormat("HH", Locale.getDefault()).format(Date(System.currentTimeMillis())).toInt()
            if ((hr in 6..8) || (hr in 12..14) || (hr in 18..20))  {
                textView_status.text = "~ 每一天的营养从好好把自己次了开始 ~"
                imageView_status.setImageBitmap(BitmapFactory.decodeStream(context!!.assets.open("ic_eating.jpg")))
            } else if ((hr in 22 .. 24) || (hr in 0 .. 5)) {
                textView_status.text = "~ 早睡晚起身体好 ~"
                imageView_status.setImageBitmap(BitmapFactory.decodeStream(context!!.assets.open("ic_sleep.jpg")))
            } else {
                textView_status.text = "~ 世界上最快乐的莫过于摸鱼鱼了 ~"
                imageView_status.setImageBitmap(BitmapFactory.decodeStream(context!!.assets.open("ic_touch_fish.jpg")))
            }
        } catch (e: Exception) {
            //
        }
    }

    override fun onStart() {
        log("MainFragment", "- onStart")
        super.onStart()
    }
    override fun onResume() {
        log("MainFragment", "- onResume")
        super.onResume()
    }
}