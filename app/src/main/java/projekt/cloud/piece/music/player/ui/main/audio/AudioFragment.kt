package projekt.cloud.piece.music.player.ui.main.audio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_AUDIO_LIST
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.service.play.Extras.EXTRA_AUDIO_ITEM
import projekt.cloud.piece.music.player.service.play.Extras.EXTRA_AUDIO_LIST
import projekt.cloud.piece.music.player.ui.main.audio.util.RecyclerViewAdapter
import projekt.cloud.piece.music.player.ui.main.base.BaseMainFragment

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
class AudioFragment: BaseMainFragment() {

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
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (recyclerViewAdapter.audioList?.isEmpty() == false
                        && !recyclerView.canScrollVertically(1)) {
                        return onScrolledToBottom()
                    }
                    onLeaveBottom()
                }
            })
        }
        return recyclerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerViewAdapter.setOnClick { audioItem, audioList ->
            playAudio(audioItem, audioList)
        }
        containerViewModel.register<List<AudioItem>>(TAG, LABEL_AUDIO_LIST) {
            recyclerViewAdapter.audioList = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _recyclerView = null
    }

}