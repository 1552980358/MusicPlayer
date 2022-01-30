package sakuraba.saki.player.music.ui.setting.fragment

import android.app.Dialog
import android.media.MediaCodecInfo
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.databinding.DialogFragmentCodecInfoBinding

class CodecInfoDialogFragment(private val mediaCodecInfo: MediaCodecInfo): DialogFragment() {

    private companion object {
        const val TAG = "CodecInfoDialogFragment"
    }

    private var _dialogFragmentCodecInfo: DialogFragmentCodecInfoBinding? = null
    private val layout get() = _dialogFragmentCodecInfo!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _dialogFragmentCodecInfo = DialogFragmentCodecInfoBinding.inflate(layoutInflater)
        layout.textInputType.editText?.setText(if (mediaCodecInfo.isEncoder) R.string.codec_type_encoder else R.string.codec_type_decoder)
        if (SDK_INT >= Q) {
            layout.textInputHardwareAcceleration.editText?.setText(
                if (mediaCodecInfo.isHardwareAccelerated) R.string.codec_hardware_acceleration_supported
                else R.string.codec_hardware_acceleration_not_supported
            )
            layout.textInputSupportedMethod.editText?.setText(
                if (mediaCodecInfo.isSoftwareOnly) R.string.codec_supported_method_software
                else R.string.codec_supported_method_hardware
            )
            layout.textInputVendor.editText?.setText(
                if (mediaCodecInfo.isVendor) R.string.codec_vendor_android
                else R.string.codec_vendor_vendor
            )
        }
        layout.textInputSupportedMime.editText?.setText(mediaCodecInfo.supportedTypes.joinToString(separator = ", "))
        return AlertDialog.Builder(requireContext())
            .setTitle(mediaCodecInfo.name)
            .setView(layout.root)
            .setPositiveButton(R.string.dialog_okay) { _, _ -> }
            .create()
    }

    fun show(fragmentManager: FragmentManager) = show(fragmentManager, TAG)

}