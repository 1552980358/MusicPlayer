package projekt.cloud.piece.music.player.util

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import java.io.Serializable

object BundleUtil {
    
    inline fun <reified T: Serializable> Bundle.serializableOf(key: String) = when {
        SDK_INT >= TIRAMISU -> getSerializable(key, T::class.java) as T
        else -> @Suppress("DEPRECATION") getSerializable(key) as T
    }
    
}