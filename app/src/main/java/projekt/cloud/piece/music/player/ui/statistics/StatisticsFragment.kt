package projekt.cloud.piece.music.player.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.databinding.FragmentStatisticsBinding
import projekt.cloud.piece.music.player.ui.statistics.util.RecyclerViewAdapter

class StatisticsFragment: BaseFragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val toolbar get() = binding.toolbar

    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerViewAdapter = RecyclerViewAdapter(binding.recyclerView)
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            toolbar.setNavigationOnClickListener { onBackPressed() }
        }
        ui {
            recyclerViewAdapter.list = withContext(IO) {
                activityViewModel.database.playRecord.queryItems()
            }
        }
    }

}