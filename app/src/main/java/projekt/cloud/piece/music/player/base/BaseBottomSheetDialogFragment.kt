package projekt.cloud.piece.music.player.base

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import projekt.cloud.piece.music.player.MainActivityViewModel
import projekt.cloud.piece.music.player.database.Database.audioRoom

open class BaseBottomSheetDialogFragment: BottomSheetDialogFragment() {

    protected lateinit var containerViewModel: MainActivityViewModel

    protected val audioRoom get() = requireContext().audioRoom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        containerViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
    }

}