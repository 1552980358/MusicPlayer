package projekt.cloud.piece.music.player.base

import android.view.View
import projekt.cloud.piece.music.player.databinding.DialogFragmentBaseInputBinding

abstract class BaseInputDialogFragment: BaseAlertDialogFragment() {

    abstract fun setHint(): Int
    private val hint get() = setHint()

    private var _binding: DialogFragmentBaseInputBinding? = null
    private val binding get() = _binding!!

    protected var prefix: String? = null
    protected var suffix: String? = null

    override fun createView(): View {
        _binding = DialogFragmentBaseInputBinding.inflate(layoutInflater)

        with(binding.textInputLayout) {
            setHint(this@BaseInputDialogFragment.hint)
            prefix?.let { prefixText = it }
            suffix?.let { suffixText = it }
        }

        binding.textInputEditText.setText(originValue)

        return binding.root
    }

    override fun setPositiveClickValue() = binding.textInputEditText.text?.toString()

}