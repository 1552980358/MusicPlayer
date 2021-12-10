package sakuraba.saki.player.music.ui.audioEffect

import android.media.audiofx.LoudnessEnhancer.PARAM_TARGET_GAIN_MB
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import lib.github1552980358.ktExtension.android.content.commit
import lib.github1552980358.ktExtension.android.os.bundle
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.base.BaseMainFragment
import sakuraba.saki.player.music.databinding.FragmentAudioEffectBinding
import sakuraba.saki.player.music.util.Constants.ACTION_EXTRA
import sakuraba.saki.player.music.util.Constants.ACTION_LOUDNESS_ENHANCER
import sakuraba.saki.player.music.util.Constants.EXTRAS_DATA
import sakuraba.saki.player.music.util.Constants.LOUDNESS_ENHANCER_DISABLE
import sakuraba.saki.player.music.util.Constants.LOUDNESS_ENHANCER_ENABLE
import sakuraba.saki.player.music.util.Constants.LOUDNESS_ENHANCER_SET
import sakuraba.saki.player.music.util.SettingUtil.defaultSharedPreference
import sakuraba.saki.player.music.util.SettingUtil.getBooleanSetting
import sakuraba.saki.player.music.util.SettingUtil.getIntSetting

class AudioEffectFragment: BaseMainFragment() {

    companion object {
        private const val UNIT_MB = "mB"
    }

    private var _fragmentAudioEffectBinding: FragmentAudioEffectBinding? = null
    private val fragmentAudioEffect get() = _fragmentAudioEffectBinding!!

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
            cur = requireContext().getIntSetting(R.string.loudness_enhancer_gain_key) ?: 0
            setSeekChangeListener { value ->
                activityInterface.sendCustomAction(ACTION_LOUDNESS_ENHANCER, bundle {
                    putInt(EXTRAS_DATA, LOUDNESS_ENHANCER_SET)
                    putInt(ACTION_EXTRA, value)
                }) { _, _, _ -> }
            }
        }
    }

}