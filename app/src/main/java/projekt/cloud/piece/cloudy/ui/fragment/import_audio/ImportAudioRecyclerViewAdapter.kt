package projekt.cloud.piece.cloudy.ui.fragment.import_audio

import android.content.Context
import projekt.cloud.piece.cloudy.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.cloudy.databinding.ImportAudioRecyclerLayoutBinding
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView
import projekt.cloud.piece.cloudy.util.ViewBindingInflater

class ImportAudioRecyclerViewAdapter: BaseRecyclerViewAdapter<ImportAudioRecyclerLayoutBinding>() {

    override val viewBindingInflater: ViewBindingInflater<ImportAudioRecyclerLayoutBinding>
        get() = ImportAudioRecyclerLayoutBinding::inflate

    private var _metadataList: List<MetadataView>? = null
    private val metadataList: List<MetadataView>
        get() = _metadataList!!

    override fun getItemCount() = _metadataList?.size ?: ITEM_EMPTY

    override fun onBindViewHolder(
        context: Context, binding: ImportAudioRecyclerLayoutBinding, position: Int
    ) {
        binding.count = position.inc().toString()
        binding.metadata = metadataList[position]
    }

    fun update(metadataList: List<MetadataView>) {
        _metadataList = metadataList
        @Suppress("NotifyDataSetChanged")
        notifyDataSetChanged()
    }

}