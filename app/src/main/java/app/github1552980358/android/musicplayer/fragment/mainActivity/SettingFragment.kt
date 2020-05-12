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
import app.github1552980358.android.musicplayer.activity.AudioImportActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * @file    : [SettingFragment]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/9
 * @time    : 15:55
 **/

class SettingFragment: PreferenceFragmentCompat() {
    
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
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.fragment_setting)
    
        findPreference<PreferenceScreen>("root")
        
        findPreference<Preference>("import_media")?.setOnPreferenceClickListener {
            startActivity(Intent(context, AudioImportActivity::class.java))
            return@setOnPreferenceClickListener true
        }
        
    }
}