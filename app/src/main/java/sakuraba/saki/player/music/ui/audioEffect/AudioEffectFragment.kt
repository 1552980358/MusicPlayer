package sakuraba.saki.player.music.ui.audioEffect

import android.Manifest.permission.MODIFY_AUDIO_SETTINGS
import android.Manifest.permission.RECORD_AUDIO
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.media.audiofx.LoudnessEnhancer.PARAM_TARGET_GAIN_MB
import android.media.audiofx.Visualizer
import android.media.audiofx.Visualizer.OnDataCaptureListener
import android.media.audiofx.Visualizer.getCaptureSizeRange
import android.media.audiofx.Visualizer.getMaxCaptureRate
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat.checkSelfPermission
import lib.github1552980358.ktExtension.android.content.commit
import lib.github1552980358.ktExtension.android.os.bundle
import lib.github1552980358.ktExtension.androidx.coordinatorlayout.widget.shortSnack
import lib.github1552980358.ktExtension.androidx.fragment.app.findActivityViewById
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.base.BaseMainFragment
import sakuraba.saki.player.music.databinding.FragmentAudioEffectBinding
import sakuraba.saki.player.music.ui.audioEffect.util.RecyclerViewAdapterUtil
import sakuraba.saki.player.music.util.Constants.ACTION_AUDIO_SESSION
import sakuraba.saki.player.music.util.Constants.ACTION_EQUALIZER
import sakuraba.saki.player.music.util.Constants.ACTION_EXTRA
import sakuraba.saki.player.music.util.Constants.ACTION_LOUDNESS_ENHANCER
import sakuraba.saki.player.music.util.Constants.EQUALIZER_ENABLE
import sakuraba.saki.player.music.util.Constants.EQUALIZER_GET
import sakuraba.saki.player.music.util.Constants.EQUALIZER_SET
import sakuraba.saki.player.music.util.Constants.EXTRAS_DATA
import sakuraba.saki.player.music.util.Constants.LOUDNESS_ENHANCER_DISABLE
import sakuraba.saki.player.music.util.Constants.LOUDNESS_ENHANCER_ENABLE
import sakuraba.saki.player.music.util.Constants.LOUDNESS_ENHANCER_SET
import sakuraba.saki.player.music.util.DeviceEqualizer
import sakuraba.saki.player.music.util.SettingUtil.defaultSharedPreference
import sakuraba.saki.player.music.util.SettingUtil.getBooleanSetting
import sakuraba.saki.player.music.util.SettingUtil.getIntSetting
import sakuraba.saki.player.music.util.SettingUtil.getStringSetting
import kotlin.math.abs
import kotlin.math.hypot

class AudioEffectFragment: BaseMainFragment() {

    companion object {
        private const val UNIT_MB = "mB"
        @JvmStatic
        private val AUDIO_PERMISSIONS = arrayOf(RECORD_AUDIO, MODIFY_AUDIO_SETTINGS)
    }

    private lateinit var requestAudioPermissions: ActivityResultLauncher<Array<out String>>

    private var _fragmentAudioEffectBinding: FragmentAudioEffectBinding? = null
    private val fragmentAudioEffect get() = _fragmentAudioEffectBinding!!

    private lateinit var recyclerViewAdapter: RecyclerViewAdapterUtil

    private var visualizer: Visualizer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _fragmentAudioEffectBinding = FragmentAudioEffectBinding.inflate(inflater)
        return fragmentAudioEffect.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragmentAudioEffect.switchCompat.apply {
            isChecked = getBooleanSetting(R.string.loudness_enhancer_enable_key)
            setOnCheckedChangeListener { _, isChecked ->
                defaultSharedPreference.commit(getString(R.string.loudness_enhancer_enable_key), isChecked)
                activityInterface.sendCustomAction(ACTION_LOUDNESS_ENHANCER, bundle {
                    putInt(EXTRAS_DATA, if (isChecked) LOUDNESS_ENHANCER_ENABLE else LOUDNESS_ENHANCER_DISABLE)
                }) { _, _, _ -> }
            }
        }
        fragmentAudioEffect.valueSeekbarLoudnessEnhancer.apply {
            unit = UNIT_MB
            saveKey = R.string.loudness_enhancer_gain_key
            max = PARAM_TARGET_GAIN_MB
            if (max == 0) {
                fragmentAudioEffect.switchCompat.isEnabled = false
                isEnabled = false
            }
            cur = getIntSetting(R.string.loudness_enhancer_gain_key) ?: 0
            setSeekChangeListener { value ->
                activityInterface.sendCustomAction(ACTION_LOUDNESS_ENHANCER, bundle {
                    putInt(EXTRAS_DATA, LOUDNESS_ENHANCER_SET)
                    putInt(ACTION_EXTRA, value)
                }) { _, _, _ -> }
            }
        }

        fragmentAudioEffect.switchCompatEqualizer.apply {
            isChecked = getBooleanSetting(R.string.key_equalizer_enable)
            setOnCheckedChangeListener { _, isChecked ->
                defaultSharedPreference.commit(getString(R.string.key_equalizer_enable), isChecked)
                activityInterface.sendCustomAction(ACTION_EQUALIZER, bundle {
                    putInt(EXTRAS_DATA, if (isChecked) EQUALIZER_ENABLE else EQUALIZER_ENABLE)
                }) { _, _, _ -> }
                if (::recyclerViewAdapter.isInitialized) {
                    recyclerViewAdapter.enable = isChecked
                }
            }
        }

        activityInterface.sendCustomAction(
            ACTION_EQUALIZER,
            bundle { putInt(EXTRAS_DATA, EQUALIZER_GET) }) { _, _, resultBundle ->
            val deviceEqualizer = resultBundle?.getSerializable(EXTRAS_DATA) as DeviceEqualizer
            val bandLevels = bandLevels(deviceEqualizer.bands)
            recyclerViewAdapter = RecyclerViewAdapterUtil(
                fragmentAudioEffect.recyclerView,
                deviceEqualizer,
                bandLevels) { band, level ->
                activityInterface.sendCustomAction(
                    ACTION_EQUALIZER,
                    bundle {
                        putInt(EXTRAS_DATA, EQUALIZER_SET)
                        putString(ACTION_EXTRA, "$band $level")
                    }
                ) { _, _, _, ->}
            }
            recyclerViewAdapter.enable = fragmentAudioEffect.switchCompatEqualizer.isChecked
        }

        requestAudioPermissions = registerForActivityResult(RequestMultiplePermissions()) { result ->
            if (result.count { !it.value } != 0) {
                findActivityViewById<CoordinatorLayout>(R.id.coordinator_layout)
                    ?.shortSnack(R.string.visualizer_permission_not_gained)
                return@registerForActivityResult
            }
            initVisualizer()
        }

        when {
            checkSelfPermission(requireContext(), RECORD_AUDIO) == PERMISSION_GRANTED
                && checkSelfPermission(requireContext(), MODIFY_AUDIO_SETTINGS) == PERMISSION_GRANTED -> initVisualizer()
            shouldShowRequestPermissionRationale(RECORD_AUDIO)
                || shouldShowRequestPermissionRationale(MODIFY_AUDIO_SETTINGS) -> Unit
            else -> requestAudioPermissions.launch(AUDIO_PERMISSIONS)
        }
    }

    private fun initVisualizer() {
        activityInterface.sendCustomAction(ACTION_AUDIO_SESSION, null) { _, _, result ->
            result?.getInt(EXTRAS_DATA)?.let { visualizer = Visualizer(it) }
            visualizer?.captureSize = getCaptureSizeRange().last()
            visualizer?.setDataCaptureListener(object: OnDataCaptureListener {
                override fun onWaveFormDataCapture(p0: Visualizer?, p1: ByteArray?, p2: Int) = Unit
                override fun onFftDataCapture(p0: Visualizer?, p1: ByteArray?, p2: Int) {
                    p1?.let { byteArray ->
                        fragmentAudioEffect.visualizerView.visibleData = FloatArray(byteArray.size / 2).apply {
                            this[0] = abs(byteArray.first().toFloat())
                            (1 until byteArray.size / 2).forEach { i ->
                                this[i] = hypot(byteArray[2 * i].toFloat(), byteArray[2 * i + 1].toFloat())
                            }
                        }
                    }
                }
            }, getMaxCaptureRate() / 2, false, true)
            visualizer?.enabled = true
        }
    }

    private fun bandLevels(band: Short) = ArrayList<Short>().apply {
        (getStringSetting(R.string.key_equalizer_band_level)?.split(' ')
            ?.toMutableList()?.apply { removeAll { it.isEmpty() } }
            ?: ArrayList<String>().apply { (0 until band).forEach { _ -> add("0") } }
            ).forEach { add(it.toShort()) }
    }

    override fun onDestroyView() {
        visualizer?.enabled = false
        visualizer?.release()
        super.onDestroyView()
    }

}