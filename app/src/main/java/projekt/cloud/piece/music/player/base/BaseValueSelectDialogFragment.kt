package projekt.cloud.piece.music.player.base

import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.DataBindingUtil
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.databinding.DialogFragmentBaseValueSelectionBinding

abstract class BaseValueSelectDialogFragment: BaseAlertDialogFragment() {

    abstract fun setSelectionList(): List<String>
    private lateinit var selectionList: List<String>

    abstract fun setSelectionTitle(): Int
    private val selectionTitle get() = setSelectionTitle()

    private var _binding: DialogFragmentBaseValueSelectionBinding? = null
    private val binding get() = _binding!!

    protected var prefix: String? = null
    protected var suffix: String? = null

    override fun createView(): View {
        _binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.dialog_fragment_base_value_selection,
            null,
            false
        )
        selectionList = setSelectionList()
        with(binding.textInputLayout) {
            (editText as? AutoCompleteTextView)?.let { autoCompleteTextView ->
                autoCompleteTextView.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        R.layout.layout_array_base_value_selection,
                        selectionList
                    )
                )
                originValue?.let { autoCompleteTextView.setText(it, false) }
            }
            setHint(selectionTitle)
            prefix?.let { prefixText = it }
            suffix?.let { suffixText = it }
        }
        return binding.root
    }

    override fun setPositiveClickValue() =
        binding.textInputLayout.editText?.text.toString()

}