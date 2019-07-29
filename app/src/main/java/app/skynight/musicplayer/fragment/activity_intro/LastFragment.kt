package app.skynight.musicplayer.fragment.activity_intro

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import android.widget.RelativeLayout.ABOVE
import android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import app.skynight.musicplayer.R
import app.skynight.musicplayer.activity.IntroActivity
import app.skynight.musicplayer.activity.MainActivity
import app.skynight.musicplayer.util.UnitUtil.Companion.getPx

/**
 * @FILE:   LastFragment
 * @AUTHOR: 1552980358
 * @DATE:   25 Jul 2019
 * @TIME:   4:53 PM
 **/
 
class LastFragment : Fragment() {
    lateinit var activity: IntroActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return RelativeLayout(context).apply {
            setOnClickListener { 
                startActivity(Intent(context, MainActivity::class.java))
                activity.finish()
            }
            background =
                ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            val textView: AppCompatTextView
            addView(AppCompatTextView(context).apply { 
                textView = this
                id = View.generateViewId()
                textSize = getPx(12f)
                setTextColor(ContextCompat.getColor(context, android.R.color.white))
            }, RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                addRule(ALIGN_PARENT_BOTTOM)
            })
            
            addView(AppCompatImageView(context).apply {

                setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_music))
            }, RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT).apply { 
                addRule(ABOVE, textView.id)
            })
        }
    }
}