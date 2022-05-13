package projekt.cloud.piece.music.player.base

import android.view.View
import android.widget.ArrayAdapter
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.databinding.DialogFragmentBaseItemSelectBinding

abstract class BaseItemSelectDialogFragment: BaseAlertDialogFragment() {

    protected abstract val items: List<String>
    
    protected abstract val positiveClick: (String?) -> Unit
    protected abstract val negativeClick: ((String?) -> Unit)?
    protected abstract val neutralClick: ((String?) -> Unit)?
    
    private var _binding: DialogFragmentBaseItemSelectBinding? = null
    private val binding get() = _binding!!
    protected val root get() = binding.root
    protected val textInputLayoutMenu get() = binding.textInputLayoutMenu
    private val autoCompleteTextView get() = binding.autoCompleteTextView
    private val textInputLayoutInput get() = binding.textInputLayoutInput
    protected val textInputEditText get() = binding.textInputEditText
    
    protected var defaultValue: String? = null
    protected abstract val currentValue: String?
    
    protected abstract val hint: String
    protected var prefix: String? = null
    protected var suffix: String? = null
    
    protected var isTextInputEnabled = false
        set(value) {
            field = value
            textInputLayoutInput.isEnabled = value
        }
    
    override val contentView: View get() {
        _binding = DialogFragmentBaseItemSelectBinding.inflate(layoutInflater)
        with(textInputLayoutMenu) {
            hint = this@BaseItemSelectDialogFragment.hint
            prefix?.let { prefixText = it }
            suffix?.let { suffixText = it }
        }
        with(textInputLayoutInput) {
            hint = this@BaseItemSelectDialogFragment.hint
            prefix?.let { prefixText = it }
            suffix?.let { suffixText = it }
        }
        autoCompleteTextView.setAdapter(
            ArrayAdapter(requireContext(), R.layout.layout_base_item_select, items)
        )
        autoCompleteTextView.setText(currentValue ?: defaultValue)
        textInputEditText.setText(currentValue ?: defaultValue)
        return root
    }
    
    override val onPositiveClick: () -> Unit get() = {
        positiveClick.invoke(
            when {
                isTextInputEnabled -> textInputEditText.text ?: defaultValue
                else -> autoCompleteTextView.text
            }?.toString()
        )
    }
    
    override val onNegativeClick: (() -> Unit)? get() = when (val negativeClick = negativeClick) {
        null -> null
        else -> {{ negativeClick.invoke(currentValue) }}
    }
    
    override val onNeutralClick: (() -> Unit)? get() = when (val neutralClick = neutralClick) {
        null -> null
        else -> {{ neutralClick.invoke(defaultValue) }}
    }

}