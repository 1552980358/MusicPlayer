package projekt.cloud.piece.music.player.ui.lyricPlay

import android.annotation.SuppressLint
import android.graphics.Color.parseColor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BasePlayFragment
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.FragmentLyricPlayBinding
import projekt.cloud.piece.music.player.util.ActivityUtil.pixelHeight
import projekt.cloud.piece.music.player.util.LyricUtil.decodeLyric
import projekt.cloud.piece.music.player.util.LyricUtil.loadLyric
import projekt.cloud.piece.music.player.util.LyricUtil.writeLyric

class LyricPlayFragment: BasePlayFragment() {
    
    private var _binding: FragmentLyricPlayBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var currentAudioItem: AudioItem
    private lateinit var selectedAudioItem: AudioItem
    
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_lyric_play, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    
        activityInterface.setLyricPlayFragmentListener(
            updateAudioItem = { updateLyric(it) },
            updateColor = { primaryColor, secondaryColor ->
                binding.primaryColor = primaryColor
                binding.secondaryColor = secondaryColor
            },
            updateProgress = { progress -> binding.progress = progress }
        )
        
        binding.recyclerLyricView.paddings = requireActivity().pixelHeight / 2
        
        activityInterface.requestColors()?.split(' ')?.let {
            binding.recyclerLyricView.primaryColor = parseColor(it.first())
            binding.recyclerLyricView.secondaryColor = parseColor(it.last())
        }
    }
    
    @SuppressLint("RestrictedApi")
    override fun onResume() {
        super.onResume()
    
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            binding.toolbar.setNavigationOnClickListener { finish() }
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        
        updateLyric(activityInterface.requestMetadata())
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_lyric_play, menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_lyric -> {
                selectedAudioItem = currentAudioItem
                activityInterface.getLyric { uri ->
                    requireContext().contentResolver.openInputStream(uri)
                        ?.bufferedReader()
                        ?.use { bufferedReader -> bufferedReader.readLines() }
                        ?.decodeLyric
                        ?.let {
                            requireContext().writeLyric(selectedAudioItem.id, it)
                            if (selectedAudioItem.id == currentAudioItem.id) {
                                binding.recyclerLyricView.lyric = it
                            }
                        }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun updateLyric(audioItem: AudioItem) {
        if (!::currentAudioItem.isInitialized || currentAudioItem != audioItem) {
            currentAudioItem = audioItem
            io {
                with(requireContext().loadLyric(audioItem.id)) {
                    ui { binding.recyclerLyricView.lyric = this@with }
                }
            }
        }
    }

}