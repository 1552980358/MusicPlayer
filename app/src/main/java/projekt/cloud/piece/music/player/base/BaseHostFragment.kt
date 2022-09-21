package projekt.cloud.piece.music.player.base

import android.os.Bundle
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController

abstract class BaseHostFragment: BaseFragment() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (onBackPressed()) {
                findNavController().navigateUp()
            }
        }
    }

}