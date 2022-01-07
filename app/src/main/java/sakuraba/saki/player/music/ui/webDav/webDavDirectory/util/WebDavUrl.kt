package sakuraba.saki.player.music.ui.webDav.webDavDirectory.util

import com.thegrizzlylabs.sardineandroid.DavResource
import java.lang.StringBuilder

class WebDavUrl(private val baseUrl: String, recyclerViewAdapterUtil: RecyclerViewAdapterUtil, private val listener: (ArrayList<DirectoryItem>) -> Unit) {

    private var directories = arrayListOf<String>()

    private val directoryItems = arrayListOf<DirectoryItem>()

    init {
        recyclerViewAdapterUtil.updateList(directoryItems)
    }

    fun updateDavResources(davResourceList: List<DavResource>) {
        directoryItems.clear()
        davResourceList.forEach {
            directoryItems.add(DirectoryItem(it))
        }
        listener(directoryItems)
    }

    fun isDirectory(index: Int) = isDirectory(directoryItems[index])

    private fun isDirectory(item: DirectoryItem) = item.isDirectory

    fun forward(index: Int): Boolean {
        val directoryItem = directoryItems[index]
        if (!isDirectory(directoryItem)) {
            return false
        }
        directories.add(directoryItem.name + '/')
        return true
    }

    fun backward(): Boolean {
        if (directories.isEmpty()) {
            return false
        }
        directories.removeLast()
        return true
    }

    val root get() = directories.clear()

    val dir: String get() {
        val stringBuilder = StringBuilder(baseUrl)
        if (directories.isEmpty()) {
            return stringBuilder.toString()
        }
        directories.forEach { stringBuilder.append(it) }
        return stringBuilder.toString()
    }

    val path: String get() {
        val stringBuilder = StringBuilder("/")
        if (directories.isEmpty()) {
            return stringBuilder.toString()
        }
        directories.forEach { stringBuilder.append(it) }
        return stringBuilder.toString()
    }

    fun path(index: Int): String = dir + directoryItems[index].name

}