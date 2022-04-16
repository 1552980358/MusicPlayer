package projekt.cloud.piece.music.player.preference

import android.content.Context
import android.content.res.TypedArray
import android.text.InputType.TYPE_CLASS_TEXT
import android.util.AttributeSet
import androidx.fragment.app.FragmentActivity
import androidx.preference.Preference
import lib.github1552980358.ktExtension.android.content.commit
import lib.github1552980358.ktExtension.androidx.fragment.app.show
import projekt.cloud.piece.music.player.base.BaseInputDialogFragment.Companion.setInputType
import projekt.cloud.piece.music.player.preference.textInput.TextInputPreferenceDialogFragment

class TextInputPreference(context: Context, attributeSet: AttributeSet?): Preference(context, attributeSet) {

    companion object {
        private const val EMPTY_STR = ""
    }

    private var dialogTitle = 0
    private var hint = 0

    private var prefix: String? = null
        get() {
            return field ?: EMPTY_STR
        }
    private var suffix: String? = null
        get() {
            return field ?: EMPTY_STR
        }

    private var onChange: ((String?) -> Unit)? = null

    private val defaultValue get() = Preference::class.java
        .getDeclaredField("mDefaultValue").apply { isAccessible = true }
        .get(this) as String?

    private var inputType = TYPE_CLASS_TEXT

    private var dataValue: String? = null
        set(value) {
            field = value
            updateSummary()
        }

    init {
        setOnPreferenceClickListener {
            TextInputPreferenceDialogFragment()
                .setTitle(dialogTitle)
                .setHint(hint)
                .setSuffix(suffix)
                .setPrefix(prefix)
                .setOriginValue(dataValue)
                .setDefaultValue(defaultValue)
                .setOnChange { newValue ->
                    dataValue = newValue
                    commitValue(newValue)
                    onChange?.let { it(newValue) }
                }
                .setInputType(inputType)
                .show(context as FragmentActivity)
            true
        }
    }

    private fun commitValue(newValue: String?) =
        sharedPreferences?.commit(key, newValue)

    fun setDialogTitle(dialogTitle: Int) {
        this.dialogTitle = dialogTitle
    }

    fun setHint(hint: Int) {
        this.hint = hint
    }

    fun setPrefix(prefix: String?) {
        this.prefix = prefix
        updateSummary()
    }

    fun setSuffix(suffix: String?) {
        this.suffix = suffix
        updateSummary()
    }

    fun setOnChange(onChange: (String?) -> Unit) {
        this.onChange = onChange
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int) = a.getString(index)

    fun setInputType(inputType: Int) {
        this.inputType = inputType
    }

    private fun updateSummary() {
        dataValue?.let { summary = prefix + it + suffix }
    }

    override fun onAttached() {
        super.onAttached()
        /**
         * Look at source code [getSharedPreferences], a null will be gotten before attached
         **/
        dataValue = sharedPreferences?.getString(key, defaultValue) ?: defaultValue
    }

}