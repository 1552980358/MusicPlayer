package projekt.cloud.piece.music.player.database

import android.content.Context

object Database {

    private var audioRoomInstance: AudioRoom? = null
    val Context.audioRoom: AudioRoom get() {
        if (audioRoomInstance == null) {
            audioRoomInstance = AudioRoom.get(this)
        }
        return audioRoomInstance!!
    }

}