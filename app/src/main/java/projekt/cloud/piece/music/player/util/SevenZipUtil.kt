package projekt.cloud.piece.music.player.util

import java.io.File
import org.apache.commons.compress.archivers.sevenz.SevenZFile

object SevenZipUtil {
    
    @JvmStatic
    fun File.decompress7zTo(targetDir: File) {
        SevenZFile(this).use { sevenZipFile ->
            var currentFile: File
            var sevenZArchiveEntry = sevenZipFile.nextEntry
            var byteArray: ByteArray
            
            while (sevenZArchiveEntry != null) {
                currentFile = File(targetDir, sevenZArchiveEntry.name)
                when {
                    sevenZArchiveEntry.isDirectory -> {
                        if (!currentFile.exists()) {
                            currentFile.mkdirs()
                        }
                    }
                    else -> {
                        byteArray = ByteArray(sevenZArchiveEntry.size.toInt())
                        sevenZipFile.read(byteArray)
                        currentFile.parentFile?.let { parentFile ->
                            if (!parentFile.exists()) {
                                parentFile.mkdirs()
                            }
                        }
                        currentFile.writeBytes(byteArray)
                    }
                }
    
                sevenZArchiveEntry = sevenZipFile.nextEntry
            }
        }
    }
    
}