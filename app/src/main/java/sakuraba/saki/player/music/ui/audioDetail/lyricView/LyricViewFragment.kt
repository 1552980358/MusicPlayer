package sakuraba.saki.player.music.ui.audioDetail.lyricView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.ui.audioDetail.lyricView.util.RecyclerViewAdapterUtil
import sakuraba.saki.player.music.databinding.FragmentLyricViewBinding
import sakuraba.saki.player.music.ui.audioDetail.lyricView.dialogFragment.LyricEditDialogFragment
import sakuraba.saki.player.music.ui.audioDetail.lyricView.dialogFragment.LyricEditDialogFragment.Companion.CREATE
import sakuraba.saki.player.music.ui.audioDetail.lyricView.dialogFragment.LyricEditDialogFragment.Companion.CREATE_LYRIC
import sakuraba.saki.player.music.ui.audioDetail.lyricView.dialogFragment.LyricEditDialogFragment.Companion.MODIFY
import sakuraba.saki.player.music.ui.audioDetail.lyricView.dialogFragment.LyricEditDialogFragment.Companion.NO_CHANGE
import sakuraba.saki.player.music.ui.audioDetail.lyricView.dialogFragment.LyricEditDialogFragment.Companion.REMOVE
import sakuraba.saki.player.music.util.Constants.EXTRAS_DATA
import sakuraba.saki.player.music.util.Lyric
import sakuraba.saki.player.music.util.LyricUtil.readLyric
import sakuraba.saki.player.music.util.LyricUtil.writeLyric

class LyricViewFragment: Fragment() {

    private var _fragmentLyricViewBinding: FragmentLyricViewBinding? = null
    private val fragmentLyricView get() = _fragmentLyricViewBinding!!

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    private lateinit var lyric: Lyric
    private lateinit var audioId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _fragmentLyricViewBinding = FragmentLyricViewBinding.inflate(inflater, container, false)
        audioId = requireArguments().getString(EXTRAS_DATA)!!
        setHasOptionsMenu(true)
        return fragmentLyricView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lyric = readLyric(audioId)
        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(fragmentLyricView.recyclerView, lyric) { index ->
            LyricEditDialogFragment(index, lyric, parentFragmentManager) { action, _, timeLong, lyricStr ->
                when (action) {
                    NO_CHANGE -> return@LyricEditDialogFragment
                    CREATE -> lyric.add(timeLong!!, lyricStr!!)
                    MODIFY -> lyric.set(index, timeLong, lyricStr)
                    REMOVE -> lyric.remove(index)
                }
                recyclerViewAdapterUtil.notifyDataSetChanged()
            }.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_lyric_edit, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_cancel -> findNavController().navigateUp()
            R.id.menu_save -> writeLyric(audioId, lyric)
            R.id.menu_add -> LyricEditDialogFragment(CREATE_LYRIC, lyric, parentFragmentManager) { action, _, timeLong, lyricStr ->
                when (action) {
                    NO_CHANGE -> return@LyricEditDialogFragment
                    CREATE -> lyric.add(timeLong!!, lyricStr!!)
                }
                recyclerViewAdapterUtil.notifyDataSetChanged()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onBackPressed() = writeLyric(audioId, lyric)

}