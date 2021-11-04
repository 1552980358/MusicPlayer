package sakuraba.saki.player.music.ui.audioDetail

import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_BITRATE
import android.media.MediaMetadataRetriever.METADATA_KEY_MIMETYPE
import android.media.MediaMetadataRetriever.METADATA_KEY_SAMPLERATE
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import lib.github1552980358.ktExtension.androidx.coordinatorlayout.widget.shortSnack
import lib.github1552980358.ktExtension.androidx.fragment.app.findActivityViewById
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.service.util.mediaUriStr
import sakuraba.saki.player.music.service.util.parseAsUri
import sakuraba.saki.player.music.util.Constants.EXTRAS_DATA
import sakuraba.saki.player.music.util.CoroutineUtil.io
import sakuraba.saki.player.music.util.CoroutineUtil.ui
import sakuraba.saki.player.music.util.LyricUtil.decodeLine
import sakuraba.saki.player.music.util.LyricUtil.hasLyric
import sakuraba.saki.player.music.util.LyricUtil.removeLyric
import sakuraba.saki.player.music.util.LyricUtil.writeLyric

class AudioDetailFragment: PreferenceFragmentCompat() {

    private companion object {
        const val KEY_TITLE = "key_title"
        const val KEY_ARTIST = "key_artist"
        const val KEY_ALBUM = "key_album"
        const val KEY_DURATION = "key_duration"
        const val KEY_FORMAT = "key_format"
        const val KEY_BIT_RATE = "key_bit_rate"
        const val KEY_SAMPLE_RATE = "key_sample_rate"
        const val KEY_BIT_DEPTH = "key_bit_depth"
        const val KEY_LYRIC_IMPORT = "key_lyric_import"
        const val KEY_LYRIC_VIEW = "key_lyric_view"
        const val KEY_LYRIC_REMOVE = "key_lyric_remove"
        
        private const val UNIT_BITS = "bits"
        private const val UNIT_KILO = "K"
        private const val UNIT_Hertz = "Hz"
        private const val UNIT_SEC = "s"
        private const val UNIT_SAMPLE = "sample"
        private const val PER = "/"
    }

    private lateinit var navController: NavController

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.xml_audio_detail, rootKey)
        (requireActivity().intent.getSerializableExtra(EXTRAS_DATA) as AudioInfo).apply {
            findPreference<Preference>(KEY_TITLE)?.summary = audioTitle
            findPreference<Preference>(KEY_ARTIST)?.summary = audioArtist
            findPreference<Preference>(KEY_ALBUM)?.summary = audioAlbum
            findPreference<Preference>(KEY_DURATION)?.summary = audioDuration.toTimeFormat
            navController = findNavController()
            io {

                val mediaExtractorAsync = async(Dispatchers.IO) {
                    MediaExtractor().apply { setDataSource(requireContext(), audioId.mediaUriStr.parseAsUri, null) }
                }

                var sampleRateStr: String? = null
                MediaMetadataRetriever().apply {
                    setDataSource(context, audioId.mediaUriStr.parseAsUri)

                    ui {
                        findPreference<Preference>(KEY_FORMAT)?.summary =
                            extractMetadata(METADATA_KEY_MIMETYPE)?.run { substring(indexOf('/') + 1) }
                    }

                    ui {
                        findPreference<Preference>(KEY_BIT_RATE)?.summary = extractMetadata(METADATA_KEY_BITRATE)?.getAsKilo + "$UNIT_BITS$PER$UNIT_SEC"
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
                    findPreference<Preference>(KEY_SAMPLE_RATE)?.summary = sampleRate.getAsKilo + UNIT_Hertz
                }

                val bitPerSample = bitPerSampleAsync.await() ?: 16  // Default for almost audio file
                ui {
                    findPreference<Preference>(KEY_BIT_DEPTH)?.summary = "$bitPerSample $UNIT_BITS$PER$UNIT_SAMPLE"
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
                    findPreference<Preference>(KEY_LYRIC_VIEW)?.isEnabled = true
                    findPreference<Preference>(KEY_LYRIC_REMOVE)?.isEnabled = true
                    return@registerForActivityResult
                }
                findActivityViewById<CoordinatorLayout>(R.id.coordinator_layout)
                    ?.shortSnack(getString(R.string.audio_detail_lyric_import_incorrect_format) + uri.toString())
            }
            findPreference<Preference>(KEY_LYRIC_IMPORT)?.apply {
                setOnPreferenceClickListener {
                    pickLyric.launch("*/*")
                    return@setOnPreferenceClickListener true
                }
            }
            val hasLyric = requireContext().hasLyric(audioId)
            findPreference<Preference>(KEY_LYRIC_VIEW)?.apply {
                if (!hasLyric) {
                    isEnabled = false
                }
                setOnPreferenceClickListener {
                    navController.navigate(AudioDetailFragmentDirections.actionNavAudioDetailToNavLyricView(audioId))
                    return@setOnPreferenceClickListener true
                }
            }
            findPreference<Preference>(KEY_LYRIC_REMOVE)?.apply {
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
                            findPreference<Preference>(KEY_LYRIC_VIEW)?.isEnabled = false
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

    private val String.getAsKilo get() = toInt().getAsKilo
    private val Int.getAsKilo get() = "${(this / 1000)} $UNIT_KILO"
    private val Long.addZero get() = if (this > 9) this.toString() else "0${this}"
    private val Long.toTimeFormat get() = (this / 60000).toString() + ':' + (this / 1000 % 60).addZero

}