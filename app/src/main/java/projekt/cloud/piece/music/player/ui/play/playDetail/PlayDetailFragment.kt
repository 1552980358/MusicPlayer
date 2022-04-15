package projekt.cloud.piece.music.player.ui.play.playDetail

import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaFormat.KEY_SAMPLE_RATE
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_BITRATE
import android.media.MediaMetadataRetriever.METADATA_KEY_MIMETYPE
import android.media.MediaMetadataRetriever.METADATA_KEY_SAMPLERATE
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.FragmentPlayDetailBinding
import projekt.cloud.piece.music.player.service.play.MediaIdUtil.parseAsUri
import projekt.cloud.piece.music.player.ui.play.playDetail.util.DetailItem
import projekt.cloud.piece.music.player.ui.play.playDetail.util.RecyclerViewAdapterUtil
import projekt.cloud.piece.music.player.util.LyricUtil.timeStr
import projekt.cloud.piece.music.player.util.SizeUtil.asKilo
import projekt.cloud.piece.music.player.util.SizeUtil.asMegabyte

class PlayDetailFragment: BaseFragment() {

    companion object {
        private const val TAG = "PlayDetailFragment"
        private const val DEFAULT_SAMPLE_RATE = 44100
        private const val DEFAULT_BIT_PER_SAMPLE = 16
    }

    private var _binding: FragmentPlayDetailBinding? = null
    private val binding get() = _binding!!
    private val recyclerView get() = binding.recyclerView

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_play_detail, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val detailList = arrayListOf<DetailItem>()
        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(recyclerView, detailList)
        initializeList(detailList)
    }

    override fun onDestroyView() {
        activityViewModel.removeAllObservers(TAG)
        super.onDestroyView()
        _binding = null
    }

    private fun initializeList(detailList: ArrayList<DetailItem>) = io {
        with(detailList) {
            add(DetailItem(R.string.play_detail_item_title).apply { isExpanded = true })        // 0
            add(DetailItem(R.string.play_detail_item_artist).apply { isExpanded = true })       // 1
            add(DetailItem(R.string.play_detail_item_album).apply { isExpanded = true })        // 2
            add(DetailItem(R.string.play_detail_item_duration))     // 3
            add(DetailItem(R.string.play_detail_item_type))     // 4
            add(DetailItem(R.string.play_detail_item_size))         // 5
            add(DetailItem(R.string.play_detail_item_path))         // 6
            add(DetailItem(R.string.play_detail_item_bit_rate))     // 7
            add(DetailItem(R.string.play_detail_item_sample_rate))  // 8
            add(DetailItem(R.string.play_detail_item_bit_depth))    // 9
        }
        activityViewModel.setAudioItemObserver(TAG) {
            updateAudioItem(detailList, it)
        }
    }

    private fun updateAudioItem(detailList: List<DetailItem>, audioItem: AudioItem) = io {
        detailList[0].content = audioItem.nickname ?: audioItem.title
        detailList[1].content = audioItem.artistItem.title
        detailList[2].content = audioItem.albumItem.title
        detailList[3].content = audioItem.duration.timeStr
        detailList[5].content = "${audioItem.size.asMegabyte} ${getString(R.string.play_detail_item_size_unit)}"
        detailList[6].content = audioItem.path

        var sampleRateStr: String? = null
        MediaMetadataRetriever().apply {
            setDataSource(requireContext(), audioItem.id.parseAsUri)

            extractMetadata(METADATA_KEY_MIMETYPE)?.let {
                detailList[4].content = it.substring(it.indexOf('/') + 1)
            }

            extractMetadata(METADATA_KEY_BITRATE)?.let {
                detailList[7].content = "${it.toInt().asKilo} ${getString(R.string.play_detail_item_bit_rate_unit)}"
            }

            if (SDK_INT > VERSION_CODES.R) {
                sampleRateStr = extractMetadata(METADATA_KEY_SAMPLERATE)
            }
        }

        val mediaExtractor = MediaExtractor().apply {
            @Suppress("BlockingMethodInNonBlockingContext")
            setDataSource(requireContext(), audioItem.id.parseAsUri, null)
        }
        if (mediaExtractor.trackCount == 0) {
            detailList[8].content = "${(sampleRateStr?.toInt() ?: DEFAULT_SAMPLE_RATE).asKilo} ${getString(R.string.play_detail_item_sample_rate_unit)}"
            detailList[9].content = "$DEFAULT_BIT_PER_SAMPLE ${getString(R.string.play_detail_item_bit_depth_unit)}"
            ui { recyclerViewAdapterUtil.notifyUpdate() }
            return@io
        }

        val trackFormat = mediaExtractor.getTrackFormat(0)

        detailList[8].content = "${(sampleRateStr?.toInt() 
            ?: trackFormat.readTrackFormat(KEY_SAMPLE_RATE) 
            ?: DEFAULT_SAMPLE_RATE).asKilo} ${getString(R.string.play_detail_item_sample_rate_unit)}"

        detailList[9].content =
            "${trackFormat.readTrackFormat("bits-per-sample") 
                ?: DEFAULT_BIT_PER_SAMPLE} ${getString(R.string.play_detail_item_bit_depth_unit)}"

        ui { recyclerViewAdapterUtil.notifyUpdate() }
    }

    private fun MediaFormat.readTrackFormat(key: String): Int? =
        if (containsKey(key)) getInteger(key) else null

}