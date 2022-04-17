package projekt.cloud.piece.music.player.ui.main.audio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.music.player.base.BaseFragment
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

    override fun onDestroyView() {
        super.onDestroyView()
        _recyclerView = null
    }

}