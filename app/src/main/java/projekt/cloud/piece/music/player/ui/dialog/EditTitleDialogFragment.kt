package projekt.cloud.piece.music.player.ui.dialog

import android.os.Bundle
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseInputDialogFragment
import projekt.cloud.piece.music.player.database.item.AudioItem

class EditTitleDialogFragment: BaseInputDialogFragment() {

    private var onChange: ((AudioItem, String?) -> Unit)? = null

    private lateinit var audioItem: AudioItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hasDefault = true
        originValue = audioItem.nickname ?: audioItem.title
    }

    override fun setHint() = R.string.edit_title_hint

    override fun setTitle() = R.string.edit_title_title

    override fun setNeutralButtonText() = R.string.edit_title_neutral

    override fun onPositiveClick(newValue: String?) {
        if (!newValue.isNullOrBlank()) {
            onChange?.let { it(audioItem, newValue) }
        }
    }

    override fun onDefaultClick(defaultValue: String?) {
        onChange?.let { it(audioItem, defaultValue) }
    }

    fun setOnChange(onChange: (AudioItem, String?) -> Unit) = apply {
        this.onChange = onChange
    }

    fun setAudioItem(audioItem: AudioItem) = apply {
        this.audioItem = audioItem
    }

}