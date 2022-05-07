package projekt.cloud.piece.music.player.util

class MP3Decoder private constructor(byteArray: ByteArray) {
    
    companion object {
        init {
            System.loadLibrary("mp3decoder")
        }
        
        fun decode(byteArray: ByteArray) = MP3Decoder(byteArray).run {
            if (pointer != 0L) this else null
        }
        
    }
    
    private external fun decodeMp3(mp3ByteArray: ByteArray): Long
    private external fun getVersion(pointer: Long): Short
    private external fun getLayer(pointer: Long): Short
    private external fun getBitRate(pointer: Long): Int
    private external fun getSampleRate(pointer: Long): Int
    
    private var pointer = decodeMp3(byteArray)
    
    val version get() = getVersion(pointer)
    
    val layer get() = getLayer(pointer)
    
    val bitRate get() = getBitRate(pointer)
    
    val sampleRate get() = getSampleRate(pointer)
    
}