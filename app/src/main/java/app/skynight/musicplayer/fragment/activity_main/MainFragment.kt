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
            when (SimpleDateFormat("HH", Locale.getDefault()).format(Date(System.currentTimeMillis())).toInt()) {
                6,7,8, 12, 13, 14, 18, 19, 20 -> {
                    textView_status.text = "~ 每一天的营养从好好把自己次了开始 ~"
                    imageView_status.setImageBitmap(BitmapFactory.decodeStream(context!!.assets.open("ic_eating.jpg")))
                }
                22, 23, 24, 0, 1, 2, 3, 4, 5-> {
                    textView_status.text = "~ 去睡觉觉咯 ~"
                    imageView_status.setImageBitmap(BitmapFactory.decodeStream(context!!.assets.open("ic_sleep.jpg")))
                }
                else -> {
                    textView_status.text = "~ 早睡晚起身体好 ~"
                    imageView_status.setImageBitmap(BitmapFactory.decodeStream(context!!.assets.open("ic_touch_fish.jpg")))
                }
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