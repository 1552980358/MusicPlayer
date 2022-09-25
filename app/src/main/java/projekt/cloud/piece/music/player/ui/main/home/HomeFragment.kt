package projekt.cloud.piece.music.player.ui.main.home

import android.os.Bundle
import android.view.View
import projekt.cloud.piece.music.player.room.AudioDatabase.Companion.audioDatabase
import projekt.cloud.piece.music.player.ui.main.base.BaseMainFragment
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui

class HomeFragment: BaseMainFragment() {
    
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    
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