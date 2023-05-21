package projekt.cloud.piece.cloudy.ui.fragment.home

import android.content.Context
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.cloudy.databinding.HomeRecyclerLayoutBinding
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView
import projekt.cloud.piece.cloudy.util.GlideUtil.roundCorners
import projekt.cloud.piece.cloudy.util.ViewBindingInflater

class HomeRecyclerAdapter(
    private val fragment: Fragment,
    private val onClicked: (Int) -> Unit
): BaseRecyclerViewAdapter<HomeRecyclerLayoutBinding>() {

    /**
     * [BaseRecyclerViewAdapter.viewBindingInflater]
     * @type [ViewBindingInflater]
     **/
    override val viewBindingInflater: ViewBindingInflater<HomeRecyclerLayoutBinding>
        get() = HomeRecyclerLayoutBinding::inflate

    /**
     * [HomeRecyclerAdapter.metadataList]
     * @type [List]<[MetadataView]>
     **/
    private var _metadataList: List<MetadataView>? = null
        set(value) {
            field = value
            if (value != null) {
                @Suppress("NotifyDataSetChanged")
                notifyDataSetChanged()
            }
        }
    private val metadataList: List<MetadataView>
        get() = _metadataList!!

    /**
     * [BaseRecyclerViewAdapter.getItemCount]
     * @return [Int]
     **/
    override fun getItemCount() = _metadataList?.size ?: ITEM_EMPTY

    /**
     * [BaseRecyclerViewAdapter.onViewBindingCreated]
     * @param binding [HomeRecyclerLayoutBinding]
     **/
    override fun onViewBindingCreated(binding: HomeRecyclerLayoutBinding) {
         binding.onClicked = onClicked
    }

    /**
     * [BaseRecyclerViewAdapter.onBindViewHolder]
     * @param context [android.content.Context]
     * @param binding [HomeRecyclerLayoutBinding]
     * @param position [Int]
     **/
    override fun onBindViewHolder(context: Context, binding: HomeRecyclerLayoutBinding, position: Int) {
        setMetadata(fragment, binding, metadataList[position], position)
    }

    /**
     * [HomeRecyclerAdapter.setMetadata]
     * @param fragment [androidx.fragment.app.Fragment]
     * @param binding [HomeRecyclerLayoutBinding]
     * @param metadata [MetadataView]
     *
     * Set metadata to binding
     **/
    private fun setMetadata(
        fragment: Fragment,
        binding: HomeRecyclerLayoutBinding,
        metadata: MetadataView,
        position: Int
    ) {
        binding.metadata = metadata
        binding.position = position
        setImage(fragment, binding.appCompatImageViewLeading, metadata)
    }

    /**
     * [HomeRecyclerAdapter.setImage]
     * @param fragment [androidx.fragment.app.Fragment]
     * @param metadata [MetadataView]
     *
     * Set image of audio
     **/
    private fun setImage(fragment: Fragment, imageView: ImageView, metadata: MetadataView) {
        Glide.with(fragment)
            .load(metadata.albumUri)
            .roundCorners(fragment.requireContext(), R.dimen.md_spec_round_radius)
            .into(imageView)
    }

    /**
     * [HomeRecyclerAdapter.updateMetadataList]
     * @param metadataList [List]<[MetadataView]>
     * @return [Boolean]
     **/
    fun updateMetadataList(metadataList: List<MetadataView>?): Boolean {
        _metadataList = metadataList
        return metadataList != null
    }

}