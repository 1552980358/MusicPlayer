package sakuraba.saki.player.music.service.util

import android.support.v4.media.MediaMetadataCompat.Builder
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE

object MediaMetadataUtil {
    
    fun Builder.setMediaMetadata(audioInfo: AudioInfo): Builder {
        putString(METADATA_KEY_MEDIA_ID, audioInfo.audioId)
        putString(METADATA_KEY_TITLE, audioInfo.audioTitle)
        putString(METADATA_KEY_ARTIST, audioInfo.audioArtist)
        putString(METADATA_KEY_ALBUM, audioInfo.audioAlbum)
        putLong(METADATA_KEY_DURATION, audioInfo.audioDuration)
        return this
    }
    
}