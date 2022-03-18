package projekt.cloud.piece.music.player.ui.main

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import projekt.cloud.piece.music.player.database.item.AlbumItem

class MainViewModel: ViewModel() {

    var isExtended = false

    var isDestroyed = false

    var isAlbumListLoaded = false
    lateinit var albumList: List<AlbumItem>

    lateinit var defaultAlbumCover: Bitmap

}