package projekt.cloud.piece.music.player.ui.dialog

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import lib.github1552980358.ktExtension.android.content.commit
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseValueSelectDialogFragment

class LyricTextSizeDialogFragment: BaseValueSelectDialogFragment() {

    private companion object {
        const val MIN = 16
        const val MAX = 30
        const val DEFAULT = "24"
    }

    private var sharedPreferences: SharedPreferences

    init {
        hasDefaultButton = true
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        initialValue =
            sharedPreferences.getString(getString(R.string.key_lyric_text_size), DEFAULT)
                ?: DEFAULT
    }

    override fun setDialogTitle() = R.string.lyric_text_size_title

    override fun setSelectionTitle() = R.string.lyric_text_size_unit

    override fun setSelectionList() = arrayListOf<String>().apply {
        (MIN .. MAX).forEach { add(it.toString()) }
    }

    override fun onPositiveClick(newValue: String?) {
        sharedPreferences.commit(getString(R.string.key_lyric_text_size), newValue)
    }

    override fun onDefaultClick() = onPositiveClick(DEFAULT)

}