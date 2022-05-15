package projekt.cloud.piece.music.player.util

class MP3Decoder private constructor() {
    
    companion object {
        init {
            System.loadLibrary("mp3decoder")
        }
        
        @JvmStatic
        val mp3Decoder by lazy { MP3Decoder() }
        
    }
    
    private external fun isMp3File(mp3ByteArray: ByteArray): Boolean
    private external fun decodeMp3(pointer: Long, mp3ByteArray: ByteArray): Long
    private external fun getBitrate(pointer: Long): Int
    private external fun getSampleRate(pointer: Long): Int
    
    private var pointer = 0L
    
    var isMp3File = false
    
    fun decode(byteArray: ByteArray): Boolean {
        isMp3File = isMp3File(byteArray)
        if (isMp3File) {
            pointer = decodeMp3(pointer, byteArray)
        }
        return isMp3File;
    }
    
    val bitrate get() = getBitrate(pointer)
    
    val sampleRate get() = getSampleRate(pointer)
    
}