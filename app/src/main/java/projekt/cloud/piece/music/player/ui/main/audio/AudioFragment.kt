package projekt.cloud.piece.music.player.ui.main.audio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.TAG_AUDIO_LIST
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.ui.main.audio.util.RecyclerViewAdapter

/**
 * Class [AudioFragment], inherit to [BaseFragment]
 *
 * Variables:
 *   [_recyclerView]
 *   @type @Nullable [RecyclerView]
 *   [recyclerView]
 *   @type [RecyclerView]
 *
 *   [recyclerViewAdapter]
 *   @type [RecyclerViewAdapter]
 *
 * Methods:
 *   [onCreateView]
 *   [onViewCreated]
 *
 **/
class AudioFragment: BaseFragment() {

    companion object {
        private const val TAG = "AudioFragment"
    }

    private var _recyclerView: RecyclerView? = null
    private val recyclerView get() = _recyclerView!!

    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _recyclerView = RecyclerView(requireContext())
        with(recyclerView) {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
            layoutManager = LinearLayoutManager(requireContext())
            recyclerViewAdapter = RecyclerViewAdapter(this)
        }
        return recyclerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        containerViewModel.register<List<AudioItem>>(TAG, TAG_AUDIO_LIST) {
            recyclerViewAdapter.audioList = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _recyclerView = null
    }

}