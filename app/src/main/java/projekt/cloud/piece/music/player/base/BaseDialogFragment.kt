package projekt.cloud.piece.music.player.base

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import projekt.cloud.piece.music.player.MainActivityViewModel

open class BaseDialogFragment: DialogFragment() {

    protected lateinit var activityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
    }

}
