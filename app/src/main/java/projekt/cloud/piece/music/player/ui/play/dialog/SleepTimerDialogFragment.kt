package projekt.cloud.piece.music.player.ui.play.dialog

import android.app.Dialog
import android.content.DialogInterface.BUTTON_NEUTRAL
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseItemSelectDialogFragment

class SleepTimerDialogFragment: BaseItemSelectDialogFragment() {
    
    companion object {
        const val EXTRA_VALUE = "value"
    }
    
    override val items: Array<String>
        get() = resources.getStringArray(R.array.sleep_timer_array)
    
    override val positiveClick: (String?) -> Unit get() = { millis ->
        millis?.let { onStart.invoke(millis) }
    }
    
    override val negativeClick: ((String?) -> Unit) get() = {
    
    }
    
    override val neutralClick: ((String?) -> Unit) get() = {
        onClose.invoke()
    }
    
    override val currentValue: String?
        get() = requireArguments().getString(EXTRA_VALUE)
    
    override val hint: String
        get() = getString(R.string.sleep_timer_hint)
    
    override val dialogTitle: String
        get() = getString(R.string.sleep_timer_title)
    
    override val positiveText: String
        get() = getString(R.string.sleep_timer_start)
    
    override val negativeText: String
        get() = getString(R.string.sleep_timer_cancel)
    
    override val neutralText: String
        get() = getString(R.string.sleep_timer_stop)
    
    private val neutralButton get() = alertDialog.getButton(BUTTON_NEUTRAL)
    private val positiveButton get() = alertDialog.getButton(BUTTON_POSITIVE)
    
    private lateinit var onStart: (String) -> Unit
    private lateinit var onClose: () -> Unit
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defaultValue = getString(R.string.sleep_timer_default)
        suffix = getString(R.string.sleep_timer_suffix)
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        textInputEditText.doOnTextChanged { text, _, _, _ ->
            positiveButton?.isEnabled = !text?.toString().isNullOrBlank()
        }
        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            isTextInputEnabled = position == autoCompleteTextView.adapter.count - 1
            if (!isTextInputEnabled) {
                positiveButton?.isEnabled = true
            }
        }
        return alertDialog
    }
    
    override fun onStart() {
        super.onStart()
        neutralButton?.isEnabled = currentValue != null
    }
    
    fun setOnStart(onStart: (String) -> Unit) = apply {
        this.onStart = onStart
    }
    
    fun setOnClose(onClose: () -> Unit) = apply {
        this.onClose = onClose
    }
    
}