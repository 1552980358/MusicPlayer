package projekt.cloud.piece.music.player.base

import android.view.View
import projekt.cloud.piece.music.player.databinding.DialogFragmentBaseInputBinding

abstract class BaseInputDialogFragment: BaseAlertDialogFragment() {
    
    private var _binding: DialogFragmentBaseInputBinding? = null
    private val binding get() = _binding!!
    private val root get() = binding.root
    
    private val textInputLayout get() = binding.textInputLayout
    private val textInputEditText get() = binding.textInputEditText
    
    protected abstract val positiveClick: (String?) -> Unit
    protected abstract val negativeClick: ((String?) -> Unit)?
    protected abstract val neutralClick: ((String?) -> Unit)?
    
    override val onPositiveClick: () -> Unit get() = {
        positiveClick.invoke(textInputEditText.text?.toString())
    }
    
    override val onNegativeClick: (() -> Unit)? get() = when (val negativeClick = negativeClick) {
        null -> null
        else -> {{ negativeClick.invoke(currentValue) }}
    }
    
    override val onNeutralClick: (() -> Unit)? get() = when (val neutralClick = neutralClick) {
        null -> null
        else -> {{ neutralClick.invoke(defaultValue) }}
    }
    
    override val contentView: View get() {
        _binding = DialogFragmentBaseInputBinding.inflate(layoutInflater)
        with(textInputLayout) {
            hint = this@BaseInputDialogFragment.hint
            prefix?.let { prefixText = it }
            suffix?.let { suffixText = it }
        }
        textInputEditText.setText(currentValue ?: defaultValue)
        return root
    }
    
}