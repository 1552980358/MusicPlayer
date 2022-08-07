package projekt.cloud.piece.music.player.ui.main.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.music.player.databinding.FragmentHomeBinding
import projekt.cloud.piece.music.player.room.AudioDatabase.Companion.audioDatabase
import projekt.cloud.piece.music.player.ui.main.base.BaseMainFragment
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui

class HomeFragment: BaseMainFragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!
    
    private val root get() = binding.root
    private val recyclerView: RecyclerView
        get() = binding.recyclerView
    
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    
    @SuppressLint("NewApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerViewAdapter = RecyclerViewAdapter(recyclerView) { audioMetadata, audioMetadataList ->
            playAudio(audioMetadata.id, audioMetadataList)
            setBottomPlayBarEnable()
        }
        
        when (viewModel.isInitialized.value) {
            true -> io {
                audioDatabase.audioMetadataDao().query().let {
                    ui { recyclerViewAdapter.audioMetadataList = it }
                }
            }
            else -> {
                viewModel.isInitialized.observe(viewLifecycleOwner) {
                    if (it) {
                        io {
                            audioDatabase.audioMetadataDao().query().let {
                                ui {
                                    recyclerViewAdapter.audioMetadataList = it
                                    viewModel.isInitialized.removeObservers(viewLifecycleOwner)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    override fun onDestroyView() {
        viewModel.isInitialized.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }

}