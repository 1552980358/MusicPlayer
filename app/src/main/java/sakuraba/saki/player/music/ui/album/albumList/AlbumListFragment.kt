package sakuraba.saki.player.music.ui.album.albumList

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lib.github1552980358.ktExtension.android.graphics.toBitmap
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import sakuraba.saki.player.music.MainActivity.Companion.INTENT_ACTIVITY_FRAGMENT_INTERFACE
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.database.AudioDatabaseHelper
import sakuraba.saki.player.music.databinding.FragmentAlbumListBinding
import sakuraba.saki.player.music.ui.album.albumList.util.RecyclerViewAdapterUtil
import sakuraba.saki.player.music.util.ActivityFragmentInterface
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArt
import sakuraba.saki.player.music.util.Constants.EXTRAS_DATA
import sakuraba.saki.player.music.util.MediaAlbum
import java.util.concurrent.TimeUnit

class AlbumListFragment: Fragment() {
    
    private var _fragmentAlbumListBinding: FragmentAlbumListBinding? = null
    private val fragmentAlbumList get() = _fragmentAlbumListBinding!!
    private lateinit var behavior: BottomSheetBehavior<RecyclerView>
    private lateinit var recyclerViewAdapter: RecyclerViewAdapterUtil
    private var _activityFragmentInterface: ActivityFragmentInterface? = null
    private val activityNotifier get() = _activityFragmentInterface!!
    private lateinit var mediaAlbum: MediaAlbum
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _fragmentAlbumListBinding = FragmentAlbumListBinding.inflate(inflater)
        mediaAlbum = arguments!!.getSerializable(EXTRAS_DATA) as MediaAlbum
        fragmentAlbumList.imageView.transitionName = "${mediaAlbum.albumId}_image"
        fragmentAlbumList.textViewTitle.transitionName = "${mediaAlbum.albumId}_text"
        
        _activityFragmentInterface = requireActivity().intent.getSerializableExtra(INTENT_ACTIVITY_FRAGMENT_INTERFACE) as ActivityFragmentInterface
        
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        postponeEnterTransition(250, TimeUnit.MILLISECONDS)
        return fragmentAlbumList.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        fragmentAlbumList.imageView.setImageBitmap(tryRun { loadAlbumArt(mediaAlbum.albumId) } ?: resources.getDrawable(R.drawable.ic_music, null).toBitmap())
        fragmentAlbumList.textViewTitle.text = mediaAlbum.title
    
        behavior = BottomSheetBehavior.from(fragmentAlbumList.recyclerView)
        
        recyclerViewAdapter = RecyclerViewAdapterUtil(fragmentAlbumList.recyclerView) { pos ->
            activityNotifier.onFragmentListItemClick(pos, recyclerViewAdapter.audioInfoList[pos], recyclerViewAdapter.audioInfoList)
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            AudioDatabaseHelper(requireContext()).queryAudioForMediaAlbum(recyclerViewAdapter.audioInfoList, mediaAlbum.albumId)
            launch(Dispatchers.Main) {
                behavior.peekHeight = fragmentAlbumList.root.height - resources.getDimensionPixelSize(R.dimen.album_list_header_height)
                recyclerViewAdapter.notifyDataUpdated()
                behavior.state = STATE_COLLAPSED
            }
        }
    }
    
}