package projekt.cloud.piece.music.player.base

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.databinding.DialogFragmentBaseValueSelectionBinding

abstract class BaseValueSelectDialogFragment: BaseDialogFragment() {

    abstract fun setDialogTitle(): Int
    private val dialogTitle get() = setDialogTitle()

    abstract fun setSelectionTitle(): Int
    private val selectionTitle get() = setSelectionTitle()

    abstract fun setSelectionList(): List<String>
    private lateinit var selectionList: List<String>

    private val positiveButtonText get() = setPositiveButtonText()

    private val negativeButtonText get() = setNegativeButtonText()

    private val neutralButtonText get() = setNeutralButtonText()

    var initialValue: String? = null

    protected var hasDefaultButton = false

    private var _binding: DialogFragmentBaseValueSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
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
                initialValue?.let { autoCompleteTextView.setText(it, false) }
            }
            setHint(selectionTitle)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(dialogTitle)
            .setView(binding.root)
            .setPositiveButton(positiveButtonText) { _, _ ->
                onPositiveClick(binding.textInputLayout.editText?.text.toString())
            }
            .setNegativeButton(negativeButtonText) { _, _ ->
                onNegativeClick(initialValue)
            }
            .apply {
                if (hasDefaultButton) {
                    setNeutralButton(neutralButtonText) { _, _ -> onDefaultClick() }
                }
            }.create()
    }

    abstract fun onPositiveClick(newValue: String?)

    open fun onNegativeClick(originValue: String?) = Unit

    open fun onDefaultClick() = Unit

    open fun setPositiveButtonText() = android.R.string.ok

    open fun setNegativeButtonText() = android.R.string.cancel

    open fun setNeutralButtonText() = R.string.neutral

}