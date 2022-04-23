package projekt.cloud.piece.music.player.base

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
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

    protected lateinit var navController: NavController
        private set

    protected lateinit var containerViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        containerViewModel = ViewModelProvider(requireActivity() as MainActivity)[MainActivityViewModel::class.java]
    }

    val canReturn get() = onBackPressed()
    protected open fun onBackPressed() = true

    protected fun sendCustomAction(action: String, vararg extras: Pair<String, Any>) {
        requireActivity().mediaController
            .transportControls
            .sendCustomAction(action, bundleOf(*extras))
    }

}