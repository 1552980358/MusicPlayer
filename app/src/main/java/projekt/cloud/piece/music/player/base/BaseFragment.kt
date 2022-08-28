package projekt.cloud.piece.music.player.base

import android.media.session.MediaController
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import projekt.cloud.piece.music.player.item.AudioMetadata
import projekt.cloud.piece.music.player.service.play.ServiceConstants.EXTRA_AUDIO_METADATA_LIST

open class BaseFragment: Fragment() {
    
    private val transportControls: MediaController.TransportControls
        get() = requireActivity().mediaController.transportControls
    
    protected fun play() = transportControls.play()
    
    protected fun pause() = transportControls.pause()
    
    protected fun skipToPrevious() = transportControls.skipToPrevious()
    
    protected fun skipToNext() = transportControls.skipToNext()
    
    protected fun seekTo(position: Long) = transportControls.seekTo(position)
    
    protected fun playAudio(audioId: String, audioMetadataList: ArrayList<AudioMetadata>) =
        transportControls.playFromMediaId(audioId, bundleOf(EXTRA_AUDIO_METADATA_LIST to audioMetadataList))
    
    protected fun playAudio(audioId: String) = transportControls.playFromMediaId(audioId, null)
    
    protected fun sendCustomAction(action: String, vararg pair: Pair<String, Any?>) =
        transportControls.sendCustomAction(action, bundleOf(*pair))
    
}