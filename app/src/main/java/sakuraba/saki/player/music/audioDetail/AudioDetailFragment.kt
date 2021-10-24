package sakuraba.saki.player.music.audioDetail

import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_BITRATE
import android.media.MediaMetadataRetriever.METADATA_KEY_MIMETYPE
import android.media.MediaMetadataRetriever.METADATA_KEY_SAMPLERATE
import android.os.Build
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.service.util.mediaUriStr
import sakuraba.saki.player.music.service.util.parseAsUri
import sakuraba.saki.player.music.util.Constants.EXTRAS_DATA

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
        
        private const val UNIT_BITS = "bits"
        private const val UNIT_KILO = "K"
        private const val UNIT_Hertz = "Hz"
        private const val UNIT_SEC = "s"
        private const val UNIT_SAMPLE = "sample"
        private const val PER = "/"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.xml_audio_detail, rootKey)
        (requireActivity().intent.getSerializableExtra(EXTRAS_DATA) as AudioInfo).apply {
            findPreference<Preference>(KEY_TITLE)?.summary = audioTitle
            findPreference<Preference>(KEY_ARTIST)?.summary = audioArtist
            findPreference<Preference>(KEY_ALBUM)?.summary = audioAlbum
            findPreference<Preference>(KEY_DURATION)?.summary = audioDuration.toTimeFormat

            CoroutineScope(Dispatchers.IO).launch {

                val mediaExtractorAsync = async(Dispatchers.IO) {
                    MediaExtractor().apply { setDataSource(requireContext(), audioId.mediaUriStr.parseAsUri, null) }
                }

                var sampleRateStr: String? = null
                MediaMetadataRetriever().apply {
                    setDataSource(context, audioId.mediaUriStr.parseAsUri)

                    launch(Dispatchers.Main) {
                        findPreference<Preference>(KEY_FORMAT)?.summary =
                            extractMetadata(METADATA_KEY_MIMETYPE)?.run { substring(indexOf('/') + 1) }
                    }

                    launch(Dispatchers.Main) {
                        findPreference<Preference>(KEY_BIT_RATE)?.summary = extractMetadata(METADATA_KEY_BITRATE)?.getAsKilo + "$UNIT_BITS$PER$UNIT_SEC"
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        sampleRateStr = extractMetadata(METADATA_KEY_SAMPLERATE)
                    }

                }

                val mediaExtractor = mediaExtractorAsync.await()

                if (mediaExtractor.trackCount == 0) {
                    return@launch
                }
                val trackFormat = mediaExtractor.getTrackFormat(0)

                var sampleRateAsync: Deferred<Int?>? = null
                if (sampleRateStr == null) {
                    sampleRateAsync = async { trackFormat.readTrackFormat(MediaFormat.KEY_SAMPLE_RATE) }
                }
                val bitPerSampleAsync = async { trackFormat.readTrackFormat("bits-per-sample") }

                val sampleRate = sampleRateAsync?.await() ?: sampleRateStr!!.toInt()
                launch(Dispatchers.Main) {
                    findPreference<Preference>(KEY_SAMPLE_RATE)?.summary = sampleRate.getAsKilo + UNIT_Hertz
                }

                val bitPerSample = bitPerSampleAsync.await() ?: 16  // Default for almost audio file
                launch(Dispatchers.Main) {
                    findPreference<Preference>(KEY_BIT_DEPTH)?.summary = "$bitPerSample $UNIT_BITS$PER$UNIT_SAMPLE"
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