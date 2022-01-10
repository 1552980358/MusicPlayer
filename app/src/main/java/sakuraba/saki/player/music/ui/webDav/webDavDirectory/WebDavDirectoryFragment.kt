package sakuraba.saki.player.music.ui.webDav.webDavDirectory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.thegrizzlylabs.sardineandroid.Sardine
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import com.thegrizzlylabs.sardineandroid.impl.SardineException
import kotlinx.coroutines.Job
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.databinding.FragmentWebDavDirectoryBinding
import sakuraba.saki.player.music.ui.webDav.webDavDirectory.fragment.DownloadDialogFragment
import sakuraba.saki.player.music.ui.webDav.webDavDirectory.util.RecyclerViewAdapterUtil
import sakuraba.saki.player.music.ui.webDav.webDavDirectory.util.WebDavUrl
import sakuraba.saki.player.music.util.CoroutineUtil.io
import sakuraba.saki.player.music.util.CoroutineUtil.ui

class WebDavDirectoryFragment: Fragment() {

    private var _fragmentWebDavDirectoryBinding: FragmentWebDavDirectoryBinding? = null
    private val layout get() = _fragmentWebDavDirectoryBinding!!
    private lateinit var recyclerViewAdapter: RecyclerViewAdapterUtil

    private lateinit var sardine: Sardine
    private lateinit var webDavUrl: WebDavUrl

    private var job: Job? = null

    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        navController = findNavController()
        _fragmentWebDavDirectoryBinding = FragmentWebDavDirectoryBinding.inflate(layoutInflater)
        return layout.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerViewAdapter = RecyclerViewAdapterUtil(layout.recyclerView) { index, item ->
            when {
                webDavUrl.forward(index) -> {
                    layout.root.apply { isEnabled = false; isRefreshing = true }
                    if (job == null || job?.isCompleted == true) {
                        job = io {
                            try {
                                @Suppress("BlockingMethodInNonBlockingContext")
                                webDavUrl.updateDavResources(sardine.list(webDavUrl.dir))
                            } catch (e: SardineException) {
                                webDavUrl.backward()
                                ui { layout.root.apply { isEnabled = true; isRefreshing = false } }
                            }
                        }
                    }
                }
                else -> {
                    if (item.isAudioFile) {
                        DownloadDialogFragment(sardine, webDavUrl.path(index), item.name, item.fileSize).show(parentFragmentManager)
                    }
                }
            }
        }

        webDavUrl = WebDavUrl(requireArguments().getString("url")!!, recyclerViewAdapter) {
            ui {
                layout.root.apply { isEnabled = true; isRefreshing = false }
                recyclerViewAdapter.notifyUpdate()
                val path = webDavUrl.path
                layout.textViewPath.text = path
                when (path) {
                    "/" -> {
                        layout.textViewBackward.isEnabled = false
                        layout.imageView.setImageResource(R.drawable.ic_folder_disabled)
                    }
                    else -> {
                        layout.textViewBackward.isEnabled = true
                        layout.imageView.setImageResource(R.drawable.ic_folder)
                    }
                }
            }
        }

        layout.relativeLayout.setOnClickListener {
            if (webDavUrl.backward()) {
                layout.root.apply { isEnabled = false; isRefreshing = true }
                if (job == null || job?.isCompleted == true) {
                    layout.root.apply { isEnabled = false; isRefreshing = true }
                    job = io { tryOnly { webDavUrl.updateDavResources(sardine.list(webDavUrl.dir)) } }
                }
            }
        }

        job = io {
            sardine = OkHttpSardine()
            sardine.setCredentials(requireArguments().getString("username"), requireArguments().getString("password"))
            tryOnly { webDavUrl.updateDavResources(sardine.list(webDavUrl.dir)) }
        }

        layout.root.setOnRefreshListener {
            if (job == null || job?.isCompleted == true) {
                job = io { tryOnly { webDavUrl.updateDavResources(sardine.list(webDavUrl.dir)) } }
            }
        }
    }

    fun onBackPressed() {
        when {
            webDavUrl.backward() -> {
                job?.cancel()
                layout.root.apply { isEnabled = false; isRefreshing = true }
                job = io { tryOnly { webDavUrl.updateDavResources(sardine.list(webDavUrl.dir)) } }
            }
            else -> navController.navigateUp()
        }
    }

}