package sakuraba.saki.player.music.ui.webDav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import sakuraba.saki.player.music.databinding.FragmentWebDavBinding

class WebDavFragment: Fragment() {

    private var _fragmentWebDavBinding: FragmentWebDavBinding? = null
    private val layout get() = _fragmentWebDavBinding!!
    private val url get() = layout.textInputUrl.editText!!
    private val username get() = layout.textInputUsername.editText!!
    private val password get() = layout.textInputPassword.editText!!

    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _fragmentWebDavBinding = FragmentWebDavBinding.inflate(inflater)
        return layout.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = findNavController()
        layout.button.setOnClickListener {
            val url = url.text
            val username = username.text
            val password = password.text
            if (!url.isNullOrBlank() && !username.isNullOrBlank() && !password.isNullOrBlank()) {
                navController.navigate(
                    WebDavFragmentDirections.actionNavWebDavToNavWebDavDirectory(url.toString(), username.toString(), password.toString())
                )
            }
        }
    }

    override fun onDestroyView() {
        _fragmentWebDavBinding = null
        super.onDestroyView()
    }

}