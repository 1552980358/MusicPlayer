package projekt.cloud.piece.music.player.base

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {

    private lateinit var getContent: ActivityResultLauncher<String>
    private var onGetContent: ((Uri?) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getContent = registerForActivityResult(GetContent()) {
            onGetContent?.invoke(it)
        }
    }

    fun getContent(mime: String, onGetContent: (Uri?) -> Unit) {
        this.onGetContent = onGetContent
        getContent.launch(mime)
    }

}