package projekt.cloud.piece.music.player.base

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import projekt.cloud.piece.music.player.MainActivityViewModel
import projekt.cloud.piece.music.player.database.Database.audioRoom

/**
 * Class [BaseDialogFragment], inherit to [DialogFragment]
 *
 * Variables
 *  [containerViewModel]
 *
 * Getters
 *  [audioRoom]
 *
 * Methods
 *  [onCreate]
 *
 **/
open class BaseDialogFragment: DialogFragment() {

    protected lateinit var containerViewModel: MainActivityViewModel

    protected val audioRoom get() = requireContext().audioRoom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        containerViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
    }

}