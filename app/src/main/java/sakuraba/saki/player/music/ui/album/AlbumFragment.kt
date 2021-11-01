package sakuraba.saki.player.music.ui.album

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import sakuraba.saki.player.music.databinding.FragmentAlbumBinding
import sakuraba.saki.player.music.ui.album.util.RecyclerViewAdapterUtil
import java.util.concurrent.TimeUnit
import sakuraba.saki.player.music.base.BaseMainFragment

class AlbumFragment: BaseMainFragment() {
    
    private var _fragmentAlbumBinding: FragmentAlbumBinding? = null
    private val fragmentAlbum get() = _fragmentAlbumBinding!!
    
    private lateinit var recyclerViewAdapter: RecyclerViewAdapterUtil
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _fragmentAlbumBinding = FragmentAlbumBinding.inflate(layoutInflater)
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        fragmentAlbum.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerViewAdapter = RecyclerViewAdapterUtil(activityInterface) { imageView, textView, mediaAlbum ->
            findNavController().navigate(
                AlbumFragmentDirections.actionNavAlbumToNavAlbumList(mediaAlbum),
                FragmentNavigatorExtras(imageView to "${mediaAlbum.albumId}_image", textView to "${mediaAlbum.albumId}_text")
            )
        }
        recyclerViewAdapter.setAdapterToRecyclerView(fragmentAlbum.recyclerView)
        postponeEnterTransition(100, TimeUnit.MILLISECONDS)
        return fragmentAlbum.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}