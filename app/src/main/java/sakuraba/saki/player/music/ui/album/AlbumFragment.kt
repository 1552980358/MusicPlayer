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
import sakuraba.saki.player.music.ui.album.util.AlbumFragmentData
import sakuraba.saki.player.music.ui.album.util.RecyclerViewAdapterUtil
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArt
import java.util.concurrent.TimeUnit

class AlbumFragment: Fragment() {
    
    private var _fragmentAlbumBinding: FragmentAlbumBinding? = null
    private val fragmentAlbum get() = _fragmentAlbumBinding!!
    private var albumFragmentData: AlbumFragmentData? = null
    
    private lateinit var recyclerViewAdapter: RecyclerViewAdapterUtil
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _fragmentAlbumBinding = FragmentAlbumBinding.inflate(layoutInflater)
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        fragmentAlbum.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerViewAdapter = RecyclerViewAdapterUtil(albumFragmentData) { imageView, textView, mediaAlbum ->
            findNavController().navigate(
                AlbumFragmentDirections.actionNavAlbumToNavAlbumList(mediaAlbum),
                FragmentNavigatorExtras(imageView to "${mediaAlbum.albumId}_image", textView to "${mediaAlbum.albumId}_text")
            )
        }
        recyclerViewAdapter.setAdapterToRecyclerView(fragmentAlbum.recyclerView)
        fragmentAlbum.root.isRefreshing = true
        postponeEnterTransition(100, TimeUnit.MILLISECONDS)
        return fragmentAlbum.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (albumFragmentData?.hasData != true) {
            fragmentAlbum.root.isRefreshing = true
            CoroutineScope(Dispatchers.IO).launch {
                if (recyclerViewAdapter.mediaAlbumList.isEmpty()) {
                    AudioDatabaseHelper(requireContext()).queryMediaAlbum(recyclerViewAdapter.mediaAlbumList)
                    recyclerViewAdapter.mediaAlbumList.sortBy { mediaAlbum -> mediaAlbum.titlePinyin }
                    launch(Dispatchers.Main) { recyclerViewAdapter.notifyDataSetChanged() }
                    if (recyclerViewAdapter.mediaAlbumList.isNotEmpty()) {
                        var bitmap: Bitmap?
                        recyclerViewAdapter.mediaAlbumList.forEach { mediaAlbum ->
                            bitmap = null
                            if (recyclerViewAdapter.bitmapMap[mediaAlbum.albumId] == null) {
                                bitmap = tryRun { loadAlbumArt(mediaAlbum.albumId) }
                                if (bitmap != null) {
                                    recyclerViewAdapter.bitmapMap[mediaAlbum.albumId] = bitmap
                                }
                            }
                        }
                    }
                }
                launch(Dispatchers.Main) {
                    recyclerViewAdapter.notifyDataSetChanged()
                    fragmentAlbum.root.isRefreshing = false
                    fragmentAlbum.recyclerView.doOnPreDraw {
                        startPostponedEnterTransition()
                    }
                }
            }
        }
    }
    
    fun setAlbumFragmentData(albumFragmentData: AlbumFragmentData) {
        this.albumFragmentData = albumFragmentData
    }
    
    
}