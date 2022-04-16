package projekt.cloud.piece.music.player.preference.textInput

import projekt.cloud.piece.music.player.base.BaseInputDialogFragment

class TextInputPreferenceDialogFragment: BaseInputDialogFragment() {

    private var hint = 0
    private var title = 0

    override fun setHint() = hint

    override fun setTitle() = title

    private lateinit var onChange: (String?) -> Unit

    override fun onPositiveClick(newValue: String?) = onChange(newValue)

    fun setTitle(title: Int) = apply {
        this.title = title
    }

    fun setHint(hint: Int) = apply {
        this.hint = hint
    }

    fun setOnChange(onChange: (String?) -> Unit) = apply {
        this.onChange = onChange
    }

    fun setSuffix(suffix: String?) = apply {
        this.suffix = suffix
    }

    fun setPrefix(prefix: String?) = apply {
        this.prefix = prefix
    }

    fun setOriginValue(originValue: String?) = apply {
        this.originValue = originValue
    }

    fun setDefaultValue(defaultValue: String?) = apply {
        hasDefault = defaultValue != null
        this.defaultValue = defaultValue
    }

}