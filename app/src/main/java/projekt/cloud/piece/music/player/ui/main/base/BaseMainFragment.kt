package projekt.cloud.piece.music.player.ui.main.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import projekt.cloud.piece.music.player.ui.main.MainFragment
import projekt.cloud.piece.music.player.ui.main.MainViewModel

open class BaseMainFragment: Fragment() {
    
    protected lateinit var viewModel: MainViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            // Token from https://stackoverflow.com/a/63200538/11685230
            requireParentFragment()                         // NavHostFragment
                .requireParentFragment() as MainFragment    // MainFragment
        )[MainViewModel::class.java]
    }
    
}