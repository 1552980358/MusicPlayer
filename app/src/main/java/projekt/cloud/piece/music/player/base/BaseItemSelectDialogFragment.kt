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
    private val root get() = binding.root
    private val textInputLayout get() = binding.textInputLayout
    private val autoCompleteTextView get() = binding.autoCompleteTextView
    
    override val contentView: View get() {
        _binding = DialogFragmentBaseItemSelectBinding.inflate(layoutInflater)
        with(textInputLayout) {
            hint = this@BaseItemSelectDialogFragment.hint
            prefix?.let { prefixText = it }
            suffix?.let { suffixText = it }
        }
        autoCompleteTextView.setAdapter(
            ArrayAdapter(requireContext(), R.layout.layout_base_item_select, items)
        )
        return root
    }
    
    override val onPositiveClick: () -> Unit get() = {
        positiveClick.invoke(autoCompleteTextView.text?.toString())
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