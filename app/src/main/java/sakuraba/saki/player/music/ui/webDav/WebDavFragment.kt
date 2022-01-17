package sakuraba.saki.player.music.ui.webDav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import lib.github1552980358.ktExtension.jvm.util.addInstance
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.database.WebDavDatabaseHelper
import sakuraba.saki.player.music.databinding.FragmentWebDavBinding
import sakuraba.saki.player.music.ui.webDav.addHost.AddHostDialogFragment
import sakuraba.saki.player.music.ui.webDav.util.RecyclerViewAdapterUtil
import sakuraba.saki.player.music.util.CoroutineUtil.ui

class WebDavFragment: Fragment() {

    private var _fragmentWebDavBinding: FragmentWebDavBinding? = null
    private val layout get() = _fragmentWebDavBinding!!
    private val url get() = layout.textInputUrl.editText!!
    private val username get() = layout.textInputUsername.editText!!
    private val password get() = layout.textInputPassword.editText!!

    private lateinit var navController: NavController

    private lateinit var behavior: BottomSheetBehavior<CardView>

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    private lateinit var database: WebDavDatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _fragmentWebDavBinding = FragmentWebDavBinding.inflate(inflater)
        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(layout.recyclerView) { webDavData ->
            url.setText(webDavData.url)
            username.setText(webDavData.username)
            password.setText(webDavData.password)
        }
        database = WebDavDatabaseHelper(requireContext())
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

        layout.linearLayout.setOnClickListener {
            AddHostDialogFragment { name, url, username, password ->
                if (name != null) {
                    when (recyclerViewAdapterUtil.webDavDataList.indexOfFirst { it.name == name }) {
                        -1 -> {
                            recyclerViewAdapterUtil.webDavDataList.addInstance(name, url, username, password)
                            recyclerViewAdapterUtil.notifyUpdate()
                            database.insertWebDavInfo(recyclerViewAdapterUtil.webDavDataList.last())
                        }
                        else -> {

                        }
                    }
                }
            }.show(parentFragmentManager)
        }

        behavior = BottomSheetBehavior.from(layout.cardView)
        behavior.peekHeight = resources.getDimensionPixelSize(R.dimen.web_dav_bottom_sheet_height)
        behavior.isHideable = false
        behavior.state = STATE_COLLAPSED

        ui {
            layout.cardView.apply {
                layoutParams = layoutParams.apply { height = layout.root.measuredHeight / 2 }
            }
        }

        io {
            database.queryWebDavInfo(recyclerViewAdapterUtil.webDavDataList)
            ui { recyclerViewAdapterUtil.notifyUpdate() }
        }

    }

    override fun onDestroy() {
        database.close()
        super.onDestroy()
    }

    override fun onDestroyView() {
        _fragmentWebDavBinding = null
        super.onDestroyView()
    }

}