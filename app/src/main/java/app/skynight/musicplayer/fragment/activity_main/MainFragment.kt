package app.skynight.musicplayer.fragment.activity_main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
        super.onViewCreated(view, savedInstanceState)
        /*when (SimpleDateFormat("HH", Locale.getDefault()).format(Date(System.currentTimeMillis())).toInt()) {

            12, 13, 14, 18, 19, 20 -> {
                imageView_status.setImageDrawable(ContextCompat.getDrawable(activity!!, R.drawable.ic_eatting))
            }
            6,7,8 -> {
                imageView_status
            }
            else -> {
            }
        }*/
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