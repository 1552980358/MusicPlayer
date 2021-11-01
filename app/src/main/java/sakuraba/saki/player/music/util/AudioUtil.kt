package sakuraba.saki.player.music.util

import android.media.AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
import android.media.AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
import android.media.AudioDeviceInfo.TYPE_USB_HEADSET
import android.media.AudioDeviceInfo.TYPE_WIRED_HEADPHONES
import android.media.AudioDeviceInfo.TYPE_WIRED_HEADSET
import android.media.AudioManager
import android.media.AudioManager.GET_DEVICES_OUTPUTS
import android.os.Build
import androidx.annotation.RequiresApi

object AudioUtil {

    enum class AudioDevice {
        UNKNOWN, BUILD_IN_SPEAKER, HEADSET, HEADPHONE, BLUETOOTH_A2DP, USB_DEVICE
    }

    val AudioManager.getOutputDevice get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) outputDeviceApi23Impl() else outputDeviceImpl

    @RequiresApi(Build.VERSION_CODES.M)
    private fun AudioManager.outputDeviceApi23Impl(): AudioDevice {
        var currentDevice = TYPE_BUILTIN_SPEAKER
        getDevices(GET_DEVICES_OUTPUTS).forEach { audioDeviceInfo ->
            when (val type = audioDeviceInfo.type) {
                TYPE_BUILTIN_SPEAKER -> currentDevice = type
                TYPE_WIRED_HEADSET -> currentDevice = type
                TYPE_WIRED_HEADPHONES -> currentDevice = type
                TYPE_BLUETOOTH_A2DP -> currentDevice = type
                TYPE_USB_HEADSET -> currentDevice = type
            }
        }
        return when (currentDevice) {
            TYPE_BUILTIN_SPEAKER -> AudioDevice.BUILD_IN_SPEAKER
            TYPE_WIRED_HEADSET -> AudioDevice.HEADSET
            TYPE_WIRED_HEADPHONES -> AudioDevice.HEADPHONE
            TYPE_BLUETOOTH_A2DP -> AudioDevice.BLUETOOTH_A2DP
            TYPE_USB_HEADSET -> AudioDevice.USB_DEVICE
            else -> AudioDevice.UNKNOWN
        }
    }

    private val AudioManager.outputDeviceImpl get() = when {
        @Suppress("DEPRECATION")
        isBluetoothA2dpOn -> AudioDevice.BLUETOOTH_A2DP
        @Suppress("DEPRECATION")
        isWiredHeadsetOn -> AudioDevice.HEADSET
        isSpeakerphoneOn -> AudioDevice.BUILD_IN_SPEAKER
        else -> AudioDevice.UNKNOWN
    }


}