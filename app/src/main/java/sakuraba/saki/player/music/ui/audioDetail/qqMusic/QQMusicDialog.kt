package sakuraba.saki.player.music.ui.audioDetail.qqMusic

import androidx.preference.PreferenceFragmentCompat
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.ui.audioDetail.base.BaseLyricFetchDialogFragment
import sakuraba.saki.player.music.util.QQMusicUtil.qqMusic

class QQMusicDialog(audioId: String, parentFragment: PreferenceFragmentCompat): BaseLyricFetchDialogFragment(audioId, parentFragment) {

    private companion object {
        const val TAG = "QQMusicDialog"
    }

    override fun getDialogTitle() = R.string.audio_detail_qqMusic_dialog

    override fun failedMessage() = R.string.audio_detail_qqMusic_dialog_failed

    override fun getLyric(text: String) = text.qqMusic

    override fun getLaunchTag() = TAG

}