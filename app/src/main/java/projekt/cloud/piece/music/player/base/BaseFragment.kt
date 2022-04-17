package projekt.cloud.piece.music.player.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import projekt.cloud.piece.music.player.MainActivity
import projekt.cloud.piece.music.player.MainActivityViewModel

/**
 * Class [BaseFragment], inherit to [Fragment]
 *
 * Variables:
 *   [containerViewModel]
 *
 * Methods:
 *   [onCreate]
 *
 **/
open class BaseFragment: Fragment() {

    protected lateinit var containerViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        containerViewModel = ViewModelProvider(requireActivity() as MainActivity)[MainActivityViewModel::class.java]
    }

}