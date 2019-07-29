package app.skynight.musicplayer.fragment.activity_intro

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import app.skynight.musicplayer.R

/**
 * @FILE:   SecondFragment
 * @AUTHOR: 1552980358
 * @DATE:   25 Jul 2019
 * @TIME:   7:37 AM
 **/

class SecondFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LinearLayout(context).apply {
            background =
                ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        }
    }
}