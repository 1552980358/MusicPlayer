package projekt.cloud.piece.music.player.base

import android.text.InputType.TYPE_CLASS_TEXT
import android.view.View
import projekt.cloud.piece.music.player.databinding.DialogFragmentBaseInputBinding

abstract class BaseInputDialogFragment: BaseAlertDialogFragment() {

    companion object {

        @JvmStatic
        fun <T: BaseInputDialogFragment> T.setInputType(inputType: Int) = apply {
            this.inputType = inputType
        }

    }

    abstract fun setHint(): Int
    private val hint get() = setHint()

    private var _binding: DialogFragmentBaseInputBinding? = null
    private val binding get() = _binding!!

    protected var prefix: String? = null
    protected var suffix: String? = null

    private var inputType = TYPE_CLASS_TEXT

    override fun createView(): View {
        _binding = DialogFragmentBaseInputBinding.inflate(layoutInflater)

        with(binding.textInputLayout) {
            setHint(this@BaseInputDialogFragment.hint)
            prefix?.let { prefixText = it }
            suffix?.let { suffixText = it }
        }

        binding.textInputEditText.setText(originValue)
        binding.textInputEditText.inputType = inputType

        return binding.root
    }

    override fun setPositiveClickValue() = binding.textInputEditText.text?.toString()

}