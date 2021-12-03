package sakuraba.saki.player.music.ui.audioDetail.netease

import androidx.preference.PreferenceFragmentCompat
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.ui.audioDetail.base.BaseLyricFetchDialogFragment
import sakuraba.saki.player.music.util.Lyric
import sakuraba.saki.player.music.util.NetEaseUtil.netEaseLyric

class NetEaseDialog(audioId: String, parentFragment: PreferenceFragmentCompat):
        BaseLyricFetchDialogFragment(audioId, parentFragment) {

    private companion object {
        const val TAG = "NetEaseDialog"
    }

    override fun getDialogTitle() = R.string.audio_detail_netease_dialog

    override fun failedMessage() = R.string.audio_detail_netease_dialog_failed

    override fun getLyric(text: String): Lyric = text.netEaseLyric

    override fun getLaunchTag() = TAG

}