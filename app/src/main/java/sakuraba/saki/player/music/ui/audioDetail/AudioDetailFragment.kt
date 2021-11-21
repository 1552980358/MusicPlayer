package sakuraba.saki.player.music.ui.audioDetail

import android.graphics.Color
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_BITRATE
import android.media.MediaMetadataRetriever.METADATA_KEY_MIMETYPE
import android.media.MediaMetadataRetriever.METADATA_KEY_SAMPLERATE
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import lib.github1552980358.ktExtension.androidx.coordinatorlayout.widget.shortSnack
import lib.github1552980358.ktExtension.androidx.fragment.app.findActivityViewById
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.databinding.FragmentAudioDetailBinding
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.service.util.mediaUriStr
import sakuraba.saki.player.music.service.util.parseAsUri
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArt
import sakuraba.saki.player.music.util.Constants.EXTRAS_DATA
import sakuraba.saki.player.music.util.CoroutineUtil.io
import sakuraba.saki.player.music.util.CoroutineUtil.ui
import sakuraba.saki.player.music.util.LyricUtil.decodeLine
import sakuraba.saki.player.music.util.LyricUtil.hasLyric
import sakuraba.saki.player.music.util.LyricUtil.removeLyric
import sakuraba.saki.player.music.util.LyricUtil.writeLyric
import sakuraba.saki.player.music.util.PreferenceUtil.preference
import sakuraba.saki.player.music.util.UnitUtil.PER
import sakuraba.saki.player.music.util.UnitUtil.UNIT_BITS
import sakuraba.saki.player.music.util.UnitUtil.UNIT_Hertz
import sakuraba.saki.player.music.util.UnitUtil.UNIT_SAMPLE
import sakuraba.saki.player.music.util.UnitUtil.UNIT_SEC
import sakuraba.saki.player.music.util.UnitUtil.asMiB
import sakuraba.saki.player.music.util.UnitUtil.getAsKilo
import sakuraba.saki.player.music.util.UnitUtil.toTimeFormat

class AudioDetailFragment: PreferenceFragmentCompat() {

    private lateinit var navController: NavController

    private lateinit var audioInfo: AudioInfo

    private var _fragmentAudioDetailBinding: FragmentAudioDetailBinding? = null
    private val fragmentAudioDetail get() = _fragmentAudioDetailBinding!!
    
    private lateinit var behavior: BottomSheetBehavior<RelativeLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        audioInfo = (requireActivity().intent.getSerializableExtra(EXTRAS_DATA) as AudioInfo)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _fragmentAudioDetailBinding = FragmentAudioDetailBinding.inflate(layoutInflater, container, false)
        fragmentAudioDetail.imageView.apply {
            audioInfo.apply {
                transitionName = audioId + "_image"
                setImageBitmap(
                    tryRun { loadAlbumArt(audioAlbumId) } ?: ContextCompat.getDrawable(requireContext(), R.drawable.ic_music)!!.toBitmap()
                )
            }
        }
        fragmentAudioDetail.preferenceFragmentContainer.apply {
            addView(super.onCreateView(inflater, fragmentAudioDetail.preferenceFragmentContainer, savedInstanceState))
            setBackgroundColor(Color.WHITE)
        }
        behavior = BottomSheetBehavior.from(fragmentAudioDetail.preferenceFragmentContainer)
        fragmentAudioDetail.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                fragmentAudioDetail.preferenceFragmentContainer.apply {
                    layoutParams = layoutParams.apply {
                        height = fragmentAudioDetail.root.height
                    }
                }
                behavior.peekHeight = fragmentAudioDetail.root.height - resources.getDimensionPixelSize(R.dimen.audio_detail_relative_layout_height)
                behavior.state = STATE_COLLAPSED
                fragmentAudioDetail.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        return fragmentAudioDetail.root
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.xml_audio_detail, rootKey)
        audioInfo.apply {
            preference(R.string.audio_detail_title_key)?.summary = audioTitle
            preference(R.string.audio_detail_artist_key)?.summary = audioArtist
            preference(R.string.audio_detail_album_key)?.summary = audioAlbum
            preference(R.string.audio_detail_duration_key)?.summary = audioDuration.toTimeFormat
            preference(R.string.audio_detail_size_key)?.summary = audioSize.asMiB
            preference(R.string.audio_detail_path_key)?.summary = audioPath
            navController = findNavController()
            io {

                val mediaExtractorAsync = async(Dispatchers.IO) {
                    MediaExtractor().apply { setDataSource(requireContext(), audioId.mediaUriStr.parseAsUri, null) }
                }

                var sampleRateStr: String? = null
                MediaMetadataRetriever().apply {
                    setDataSource(context, audioId.mediaUriStr.parseAsUri)

                    ui {
                        preference(R.string.audio_detail_format_key)?.summary =
                            extractMetadata(METADATA_KEY_MIMETYPE)?.run { substring(indexOf('/') + 1) }
                    }

                    ui {
                        preference(R.string.audio_detail_bit_rate_key)?.summary = extractMetadata(METADATA_KEY_BITRATE)?.getAsKilo + "$UNIT_BITS$PER$UNIT_SEC"
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        sampleRateStr = extractMetadata(METADATA_KEY_SAMPLERATE)
                    }

                }

                val mediaExtractor = mediaExtractorAsync.await()

                if (mediaExtractor.trackCount == 0) {
                    return@io
                }
                val trackFormat = mediaExtractor.getTrackFormat(0)

                var sampleRateAsync: Deferred<Int?>? = null
                if (sampleRateStr == null) {
                    sampleRateAsync = async { trackFormat.readTrackFormat(MediaFormat.KEY_SAMPLE_RATE) }
                }
                val bitPerSampleAsync = async { trackFormat.readTrackFormat("bits-per-sample") }

                val sampleRate = sampleRateAsync?.await() ?: sampleRateStr!!.toInt()
                ui {
                    preference(R.string.audio_detail_sample_rate_key)?.summary = sampleRate.getAsKilo + UNIT_Hertz
                }

                val bitPerSample = bitPerSampleAsync.await() ?: 16  // Default for almost audio file
                ui {
                    preference(R.string.audio_detail_bit_rate_key)?.summary = "$bitPerSample $UNIT_BITS$PER$UNIT_SAMPLE"
                }
            }

            val pickLyric = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri == null) {
                    findActivityViewById<CoordinatorLayout>(R.id.coordinator_layout)?.shortSnack(R.string.audio_detail_lyric_import_no_file_selected)
                    return@registerForActivityResult
                }
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    findActivityViewById<CoordinatorLayout>(R.id.coordinator_layout)
                        ?.shortSnack(getString(R.string.audio_detail_lyric_import_cannot_open_file) + uri.toString())
                    return@registerForActivityResult
                }
                val lines: List<String>
                inputStream.use { stream -> lines = stream.bufferedReader().readLines() }

                val lyricList = arrayListOf<String>()
                val timeList = arrayListOf<Long>()
                lines.forEach { line -> line.decodeLine(lyricList, timeList) }
                if (lyricList.size == timeList.size && lyricList.isNotEmpty()) {
                    requireContext().writeLyric(audioId, lyricList, timeList)
                    findActivityViewById<CoordinatorLayout>(R.id.coordinator_layout)
                        ?.shortSnack(R.string.audio_detail_lyric_import_succeed)
                    preference(R.string.audio_detail_lyric_view_key)?.isEnabled = true
                    preference(R.string.audio_detail_lyric_remove_key)?.isEnabled = true
                    return@registerForActivityResult
                }
                findActivityViewById<CoordinatorLayout>(R.id.coordinator_layout)
                    ?.shortSnack(getString(R.string.audio_detail_lyric_import_incorrect_format) + uri.toString())
            }
            preference(R.string.audio_detail_lyric_import_key)?.apply {
                setOnPreferenceClickListener {
                    pickLyric.launch("*/*")
                    return@setOnPreferenceClickListener true
                }
            }
            val hasLyric = requireContext().hasLyric(audioId)
            preference(R.string.audio_detail_lyric_view_key)?.apply {
                if (!hasLyric) {
                    isEnabled = false
                }
                setOnPreferenceClickListener {
                    navController.navigate(AudioDetailFragmentDirections.actionNavAudioDetailToNavLyricView(audioId))
                    return@setOnPreferenceClickListener true
                }
            }
            preference(R.string.audio_detail_lyric_remove_key)?.apply {
                if (!hasLyric) {
                    isEnabled = false
                }
                setOnPreferenceClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle(R.string.audio_detail_lyric_remove_dialog_title)
                        .setMessage(R.string.audio_detail_lyric_remove_dialog_content)
                        .setPositiveButton(R.string.audio_detail_lyric_remove_dialog_confirm) { _, _ ->
                            requireContext().removeLyric(audioId)
                            findActivityViewById<CoordinatorLayout>(R.id.coordinator_layout)
                                ?.shortSnack(R.string.audio_detail_lyric_remove_removed)
                            preference(R.string.audio_detail_lyric_view_key)?.isEnabled = false
                            isEnabled = false
                        }
                        .setNegativeButton(R.string.audio_detail_lyric_remove_dialog_cancel) { _, _ -> }
                        .show()
                    return@setOnPreferenceClickListener true
                }
            }
        }
    }

    private fun MediaFormat.readTrackFormat(key: String): Int? =
        if (containsKey(key)) getInteger(key) else null
    
    fun onActivityBackPressed(activity: AppCompatActivity, needAnim: Boolean) {
        when (behavior.state) {
            STATE_COLLAPSED -> {
                activity.apply {
                    finishAfterTransition()
                    if (needAnim) {
                        overridePendingTransition(R.anim.translate_up_pop_enter, R.anim.translate_up_pop_exit)
                    }
                }
            }
            else -> behavior.state = STATE_COLLAPSED
        }
    }

}