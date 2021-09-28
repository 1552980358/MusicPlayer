package sakuraba.saki.player.music.ui.album

import android.graphics.Bitmap
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import sakuraba.saki.player.music.database.AudioDatabaseHelper
import sakuraba.saki.player.music.databinding.FragmentAlbumBinding
import sakuraba.saki.player.music.ui.album.util.RecyclerViewAdapter
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArt
import sakuraba.saki.player.music.util.MediaAlbum
import java.util.concurrent.TimeUnit

class AlbumFragment: Fragment() {
    
    private var _fragmentAlbumBinding: FragmentAlbumBinding? = null
    private val fragmentAlbum get() = _fragmentAlbumBinding!!
    
    private val mediaAlbumList = ArrayList<MediaAlbum>()
    private val bitmapMap = mutableMapOf<Long, Bitmap>()
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _fragmentAlbumBinding = FragmentAlbumBinding.inflate(layoutInflater)
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        fragmentAlbum.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        fragmentAlbum.recyclerView.adapter = RecyclerViewAdapter(mediaAlbumList) { imageView, textView, mediaAlbum ->
            findNavController().navigate(
                AlbumFragmentDirections.actionNavAlbumToNavAlbumList(mediaAlbum),
                FragmentNavigatorExtras(imageView to "${mediaAlbum.albumId}_image", textView to "${mediaAlbum.albumId}_text")
            )
        }
        fragmentAlbum.root.isRefreshing = true
        postponeEnterTransition(100, TimeUnit.MILLISECONDS)
        return fragmentAlbum.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            if (mediaAlbumList.isEmpty()) {
                AudioDatabaseHelper(requireContext()).queryMediaAlbum(mediaAlbumList)
                mediaAlbumList.sortBy { mediaAlbum -> mediaAlbum.titlePinyin }
                launch(Dispatchers.Main) { fragmentAlbum.recyclerView.adapter?.notifyDataSetChanged() }
                if (mediaAlbumList.isNotEmpty()) {
                    var bitmap: Bitmap?
                    mediaAlbumList.forEach { mediaAlbum ->
                        bitmap = null
                        if (bitmapMap[mediaAlbum.albumId] == null) {
                            bitmap = tryRun { loadAlbumArt(mediaAlbum.albumId) }
                            if (bitmap != null) {
                                bitmapMap[mediaAlbum.albumId] = bitmap!!
                            }
                        }
                    }
                }
            }
            launch(Dispatchers.Main) {
                (fragmentAlbum.recyclerView.adapter as RecyclerViewAdapter).updateBitmapMap(bitmapMap)
                fragmentAlbum.root.isRefreshing = false
                fragmentAlbum.recyclerView.doOnPreDraw {
                    startPostponedEnterTransition()
                }
            }
        }
    }
    
}