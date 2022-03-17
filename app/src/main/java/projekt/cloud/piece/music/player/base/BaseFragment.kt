package projekt.cloud.piece.music.player.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import projekt.cloud.piece.music.player.MainActivityViewModel

open class BaseFragment: Fragment() {

    protected lateinit var activityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
    }

    open fun onBackPressed() = true

    val canBackStack get() = onBackPressed()

}