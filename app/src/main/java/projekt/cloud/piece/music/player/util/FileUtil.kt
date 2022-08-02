package projekt.cloud.piece.music.player.util

import projekt.cloud.piece.music.player.util.FileUtil.FileUtilImpl.Companion.instance

object FileUtil {
    
    class FileUtilImpl private constructor() {
        
        companion object {
            init {
                System.loadLibrary("fileutil")
            }
            
            val instance = FileUtilImpl()
        }
        
        external fun write(path: String, data: ByteArray)
        
    }
    
    fun writeByteArray(path: String, data: ByteArray) = instance.write(path, data)
    
}