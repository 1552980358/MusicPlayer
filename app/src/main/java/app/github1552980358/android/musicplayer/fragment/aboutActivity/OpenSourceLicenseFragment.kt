package app.github1552980358.android.musicplayer.fragment.aboutActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.github1552980358.android.musicplayer.R
import kotlinx.android.synthetic.main.fragment_open_source_license.textViewContent

/**
 * [OpenSourceLicenseFragment]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/6/1
 * @time    : 14:10
 **/

class OpenSourceLicenseFragment: Fragment() {
    
    /**
     * [onCreateView]
     * @param inflater [LayoutInflater]
     * @param container [ViewGroup]?
     * @param savedInstanceState [Bundle]?
     * @return [View]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_open_source_license, container, false)
    }
    
    /**
     * [onViewCreated]
     * @param view [View]
     * @param savedInstanceState [Bundle]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        resources.assets.open("LICENSE").use { `is` ->
            `is`.bufferedReader().use { br ->
                textViewContent.text = br.readText()
            }
        }
    }
    
}