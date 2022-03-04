package sakuraba.saki.player.music.ui.audioEffect.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import lib.github1552980358.ktExtension.android.content.commit
import lib.github1552980358.ktExtension.android.view.createViewHolder
import lib.github1552980358.ktExtension.android.view.getString
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.util.DeviceEqualizer
import sakuraba.saki.player.music.util.SettingUtil.defaultSharedPreference
import sakuraba.saki.player.music.util.ViewHolderUtil.bindHolder
import sakuraba.saki.player.music.widget.ValuedSeekbar

class RecyclerViewAdapterUtil(recyclerView: RecyclerView,
                              private val deviceEqualizer: DeviceEqualizer,
                              private val bandLevels: ArrayList<Short>,
                              private val listener: (Short, Short) -> Unit) {

    private companion object {
        const val UNIT_HZ = "Hz"
        const val UNIT_DB = "dB"
    }

    private class RecyclerViewHolder(view: View): ViewHolder(view) {
        val textView: AppCompatTextView = view.findViewById(R.id.text_view)
        val valuedSeekbar: ValuedSeekbar = view.findViewById(R.id.valued_seekbar)
    }

    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            parent.createViewHolder<RecyclerViewHolder>(R.layout.layout_audio_effect_band)

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) = holder.bindHolder {
            @Suppress("SetTextI18n")
            textView.text = "${deviceEqualizer.frequencies[position]} $UNIT_HZ"
            valuedSeekbar.apply {
                unit = UNIT_DB
                min = deviceEqualizer.minBandLevel
                max = deviceEqualizer.maxBandLevel
                cur = bandLevels[position].toInt()
                setSeekChangeListener { value ->
                    bandLevels[position] = value.toShort()
                    listener(position.toShort(), value.toShort())
                }
                " ".split(' ').toMutableList().removeAll { it.isEmpty() }
                setSaveListener { _ ->
                    context.defaultSharedPreference.commit(
                        getString(R.string.key_equalizer_band_level),
                        StringBuilder().apply { bandLevels.forEach { append("$it ") } }.toString()
                    )
                }
            }
            textView.isEnabled = enable
            valuedSeekbar.isEnabled = enable
        }

        override fun getItemCount() = deviceEqualizer.bands.toInt()

    }

    private val adapter = RecyclerViewAdapter()

    var enable = true
        set(value) {
            field = value
            adapter.notifyItemRangeChanged(0, deviceEqualizer.bands.toInt())
        }

    init {
        recyclerView.adapter = adapter
    }

}