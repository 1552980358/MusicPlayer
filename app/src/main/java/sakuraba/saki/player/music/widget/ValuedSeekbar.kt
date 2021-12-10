package sakuraba.saki.player.music.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.annotation.MainThread
import androidx.appcompat.widget.LinearLayoutCompat
import lib.github1552980358.ktExtension.android.content.commit
import lib.github1552980358.ktExtension.android.view.getString
import sakuraba.saki.player.music.databinding.LayoutValuedSeekbarBinding
import sakuraba.saki.player.music.util.SettingUtil.defaultSharedPreference

class ValuedSeekbar: LinearLayoutCompat {

    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet)

    private var _layoutValuedSeekbarBinding: LayoutValuedSeekbarBinding? = null
    private val layoutValuedSeekbar get() = _layoutValuedSeekbarBinding!!

    private fun interface SeekChangeListener {
        fun onChange(value: Int)
    }

    private var listener: SeekChangeListener? = null

    lateinit var unit: String

    var max = 0
        @MainThread
        set(value) {
            field = value
            layoutValuedSeekbar.seekbar.max = value - min
            @Suppress("SetTextI18n")
            layoutValuedSeekbar.textViewMax.text = "$value $unit"
        }

    var min = 0
        @MainThread
        set(value) {
            field = value
            layoutValuedSeekbar.seekbar.max = max - value
            @Suppress("SetTextI18n")
            layoutValuedSeekbar.textViewMin.text = "$value $unit"
        }

    var cur = 0
        @MainThread
        set(value) {
            if (field != value) {
                field = value
                layoutValuedSeekbar.seekbar.progress = field + layoutValuedSeekbar.seekbar.max / 2
                @Suppress("SetTextI18n")
                layoutValuedSeekbar.textViewCur.text = "$value $unit"
            }
        }

    var saveKey = 0

    init {
        _layoutValuedSeekbarBinding = LayoutValuedSeekbarBinding.inflate(LayoutInflater.from(context), this, true)
        layoutValuedSeekbar.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    cur = progress - layoutValuedSeekbar.seekbar.max / 2
                    listener?.onChange(cur)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (saveKey != 0) {
                    context.defaultSharedPreference.commit(getString(saveKey), cur.toString())
                }
            }
        })
        @Suppress("SetTextI18n")
        layoutValuedSeekbar.textViewMax.text = "$max dB"
        @Suppress("SetTextI18n")
        layoutValuedSeekbar.textViewMin.text = "$min dB"
        @Suppress("SetTextI18n")
        layoutValuedSeekbar.textViewCur.text = "$cur dB"
    }

    override fun setEnabled(enabled: Boolean) {
        layoutValuedSeekbar.seekbar.isEnabled = enabled
    }

    fun setSeekChangeListener(block: (Int) -> Unit) {
        listener = SeekChangeListener(block)
    }

}