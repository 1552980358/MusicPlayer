package sakuraba.saki.player.music.ui.webDav.webDavDirectory.util

import com.thegrizzlylabs.sardineandroid.DavResource
import sakuraba.saki.player.music.ui.webDav.webDavDirectory.util.DirectoryItem.ItemTypes.AUDIO_FILE
import sakuraba.saki.player.music.ui.webDav.webDavDirectory.util.DirectoryItem.ItemTypes.DIR
import sakuraba.saki.player.music.ui.webDav.webDavDirectory.util.DirectoryItem.ItemTypes.FILE

class DirectoryItem(val name: String, val fileSize: Long, davResource: DavResource) {

    private enum class ItemTypes {
        DIR, FILE, AUDIO_FILE
    }

    private companion object {
        val array = arrayOf(
            "3gp", "mp3", "aac",
            "ts", "flac", "gsm",
            "mid", "xmf", "mxmf",
            "rtttl", "rtx", "ota",
            "imy", "mkv", "wav", "ogg"
        )
    }

    constructor(davResource: DavResource): this(davResource.name, davResource.contentLength, davResource)

    private var itemType: ItemTypes

    init {
        itemType = identifyFileType(davResource)
    }

    private fun identifyFileType(davResource: DavResource) = when {
        davResource.isDirectory -> DIR
        !name.contains('.') -> FILE
        array.contains(name.substring(name.lastIndexOf('.') + 1)) -> AUDIO_FILE
        else -> FILE
    }

    val isDirectory get() = itemType == DIR

    val isAudioFile get() = itemType == AUDIO_FILE

}