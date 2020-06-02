package app.github1552980358.android.musicplayer.fragment.mainActivity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.activity.AboutActivity
import app.github1552980358.android.musicplayer.activity.AudioImportActivity
import app.github1552980358.android.musicplayer.base.Constant.Companion.ABOUT_INTENT_EXTRA
import app.github1552980358.android.musicplayer.base.Constant.Companion.ABOUT_INTENT_LICENSE
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * @file    : [SettingFragment]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/9
 * @time    : 15:55
 **/

class SettingFragment: PreferenceFragmentCompat() {
    
    /**
     * [onCreateView]
     * @param inflater [LayoutInflater]
     * @param container [ViewGroup]?
     * @param savedInstanceState [Bundle]?
     * @return [View]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState).apply {
            this?:return this
            
            //setOnTouchListener { _, _ ->
            //    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            //        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            //    return@setOnTouchListener true
            //}
            //layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
            //    setMargins(0, 0, 0, 216)
            //
        }
    }
    
    /**
     * [onCreatePreferences]
     * @param savedInstanceState [Bundle]
     * @param rootKey [String]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.fragment_setting)
        
        findPreference<Preference>("import_media")?.setOnPreferenceClickListener {
            startActivity(Intent(context, AudioImportActivity::class.java))
            return@setOnPreferenceClickListener true
        }
    
        findPreference<Preference>("license")?.setOnPreferenceClickListener {
            startActivity(
                Intent(context, AboutActivity::class.java)
                    .putExtra(ABOUT_INTENT_EXTRA, ABOUT_INTENT_LICENSE)
            )
            return@setOnPreferenceClickListener true
        }
        
    }
}