package projekt.cloud.piece.music.player.ui.play.util

import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import projekt.cloud.piece.music.player.R

object AudioUtil {
    
    val AudioManager.deviceDrawableId get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) outputDeviceApi23Impl()
        else outputDeviceImpl
    
    @RequiresApi(Build.VERSION_CODES.M)
    private fun AudioManager.outputDeviceApi23Impl(): Int {
        var currentDevice = AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
        getDevices(AudioManager.GET_DEVICES_OUTPUTS).forEach { audioDeviceInfo ->
            when (val type = audioDeviceInfo.type) {
                AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> currentDevice = type
                AudioDeviceInfo.TYPE_WIRED_HEADSET -> currentDevice = type
                AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> currentDevice = type
                AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> currentDevice = type
                AudioDeviceInfo.TYPE_USB_HEADSET -> currentDevice = type
            }
        }
        return when (currentDevice) {
            AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> R.drawable.ic_speaker
            AudioDeviceInfo.TYPE_WIRED_HEADSET -> R.drawable.ic_headset
            AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> R.drawable.ic_headset
            AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> R.drawable.ic_bluetooth
            AudioDeviceInfo.TYPE_USB_HEADSET -> R.drawable.ic_usb_headset
            else -> R.drawable.ic_speaker
        }
    }
    
    private val AudioManager.outputDeviceImpl get() = when {
        @Suppress("DEPRECATION")
        isBluetoothA2dpOn -> R.drawable.ic_bluetooth
        @Suppress("DEPRECATION")
        isWiredHeadsetOn -> R.drawable.ic_headset
        isSpeakerphoneOn -> R.drawable.ic_speaker
        else -> R.drawable.ic_speaker
    }
    
}