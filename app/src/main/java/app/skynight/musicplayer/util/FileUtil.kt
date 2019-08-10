package app.skynight.musicplayer.util

import android.annotation.SuppressLint
import android.os.Environment
import java.io.File

/**
 * @File    : FileUtil
 * @Author  : 1552980358
 * @Date    : 9 Aug 2019
 * @TIME    : 4:41 PM
 **/

class FileUtil {
    companion object {
        @SuppressLint("SdCardPath")
        fun checkAndCreateExternalStorageFile(file: String): Boolean {
            return checkAndCreateExternalStorageFile(File(file))
        }
        fun checkAndCreateExternalStorageFile(file: File): Boolean {
            if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                return false
            }
            if (file.parentFile.exists()) {
                return try {
                    file.parentFile.mkdir()
                    file.mkdir()
                    true
                } catch (e: Exception) {
                    false
                }
            }
            if (!file.exists()) {
                return try {
                    file.createNewFile()
                    true
                } catch (e: Exception) {
                    false
                }
            }
            return true
        }
    }
}