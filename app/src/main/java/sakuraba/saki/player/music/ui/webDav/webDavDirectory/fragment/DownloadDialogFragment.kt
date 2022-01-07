package sakuraba.saki.player.music.ui.webDav.webDavDirectory.fragment

import android.app.Dialog
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_MUSIC
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.thegrizzlylabs.sardineandroid.Sardine
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.databinding.DialogFragmentDownloadBinding
import sakuraba.saki.player.music.util.CoroutineUtil.io
import sakuraba.saki.player.music.util.CoroutineUtil.ui
import java.io.File

class DownloadDialogFragment(private val sardine: Sardine, private val url: String, private val name: String, private val fileSize: Long): DialogFragment() {

    companion object {
        private const val TAG = "DownloadDialogFragment"
    }

    private var _dialogFragmentDownloadBinding: DialogFragmentDownloadBinding? = null
    private val layout get() = _dialogFragmentDownloadBinding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _dialogFragmentDownloadBinding = DialogFragmentDownloadBinding.inflate(layoutInflater)
        layout.textViewFrom.text = url
        layout.textViewMax.text = fileSize.toString()
        layout.progressBar.max = fileSize.toInt()
        io {
            runCatching { sardine.get(url) }.getOrNull()?.use { inputStream ->
                val byteArray = ByteArray(1024)
                val output = File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC), name)
                ui { layout.textViewTo.text = output.path }
                if (output.exists()) {
                    output.delete()
                }
                output.outputStream().use { fileOutputStream ->
                    var len = 0
                    var writeLen = 0
                    while (len != -1) {
                        runCatching {
                            len = inputStream.read(byteArray)
                            if (len != -1) {
                                fileOutputStream.write(byteArray, 0, len)
                                writeLen += len
                                ui {
                                    layout.textViewCur.text = writeLen.toString()
                                    layout.progressBar.progress = writeLen
                                }
                            }
                        }
                    }
                    tryOnly { fileOutputStream.flush() }
                }
            }
            ui { dismiss() }
        }
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.web_dav_download_title)
            .setView(layout.root)
            .setCancelable(false)
            .create()
    }

    fun show(manager: FragmentManager) = super.show(manager, TAG)

}