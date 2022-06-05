package projekt.cloud.piece.music.player.base

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceFragmentCompat
import projekt.cloud.piece.music.player.MainActivity
import projekt.cloud.piece.music.player.MainActivityViewModel

abstract class BasePreferenceFragment: PreferenceFragmentCompat() {
    
    protected lateinit var containerViewModel: MainActivityViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        containerViewModel = ViewModelProvider(requireActivity() as MainActivity)[MainActivityViewModel::class.java]
    }
    
}