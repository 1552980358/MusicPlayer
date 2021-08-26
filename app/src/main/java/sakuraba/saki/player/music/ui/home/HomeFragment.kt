package sakuraba.saki.player.music.ui.home

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.net.Uri.fromParts
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly
import sakuraba.saki.player.music.BuildConfig.APPLICATION_ID
import sakuraba.saki.player.music.database.AudioDatabaseHelper
import sakuraba.saki.player.music.database.AudioDatabaseHelper.Companion.TABLE_AUDIO
import sakuraba.saki.player.music.databinding.FragmentHomeBinding
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.ui.home.util.DividerItemDecoration
import sakuraba.saki.player.music.ui.home.util.RecyclerViewAdapter
import sakuraba.saki.player.music.util.ActivityFragmentInterface

class HomeFragment: Fragment() {
    
    companion object {
        private const val TAG = "HomeFragment"
        const val INTENT_ACTIVITY_FRAGMENT_INTERFACE = "ActivityFragmentInterface"
        private val URI = Uri.parse("content://media/external/audio/albumart")
    }
    
    private lateinit var viewModel: HomeViewModel
    
    private var _fragmentHomeBinding: FragmentHomeBinding? = null
    private val fragmentHome get() = _fragmentHomeBinding!!
    
    private lateinit var registerRequestReadPermission: ActivityResultLauncher<String>
    
    private lateinit var audioDatabaseHelper: AudioDatabaseHelper
    
    private val isReadExternalStoragePermissionGained get() =
        ActivityCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    
    private var _activityFragmentInterface: ActivityFragmentInterface? = null
    private val activityFragmentInterface get() = _activityFragmentInterface!!
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.e(TAG, "onCreateView")
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        
        _fragmentHomeBinding = FragmentHomeBinding.inflate(inflater)
        
        _activityFragmentInterface = requireActivity().intent.getSerializableExtra(INTENT_ACTIVITY_FRAGMENT_INTERFACE) as ActivityFragmentInterface
        
        fragmentHome.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        fragmentHome.recyclerView.adapter = RecyclerViewAdapter { pos ->
            activityFragmentInterface.onFragmentChanged(pos, viewModel.audioInfoList[pos], viewModel.audioInfoList)
        }
        findNavController().currentDestination
        fragmentHome.recyclerView.addItemDecoration(DividerItemDecoration())
        
        fragmentHome.root.isRefreshing = true
        fragmentHome.root.setOnRefreshListener {  }
        
        audioDatabaseHelper = AudioDatabaseHelper(requireContext())
        return fragmentHome.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e(TAG, "onViewCreated")
        if (!isReadExternalStoragePermissionGained) {
            registerRequestReadPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGained ->
                if (isGained) {
                    //viewModel.snackbar.dismiss()
                    CoroutineScope(Dispatchers.IO).launch {
                        readAudioSystemDatabase(viewModel.audioInfoList)
                        if (viewModel.audioInfoList.isNotEmpty()) {
                            loadBitmaps(viewModel.audioInfoList, viewModel.bitmaps)
                        }
                    }
                } else {
                    viewModel.initSnackBar(requireActivity()) {
                        requireContext().startActivity(Intent(ACTION_APPLICATION_DETAILS_SETTINGS).setData(fromParts("package", APPLICATION_ID, null)))
                    }
                    viewModel.snackbar.show()
                }
            }
            registerRequestReadPermission.launch(READ_EXTERNAL_STORAGE)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                readDatabase(viewModel.audioInfoList)
                if (viewModel.audioInfoList.isNotEmpty()) {
                    loadBitmaps(viewModel.audioInfoList, viewModel.bitmaps)
                }
            }
        }
    }
    
    private fun readAudioSystemDatabase(audioInfoList: ArrayList<AudioInfo>) {
        Log.e(TAG, "readAudioSystemDatabase")
        // Delete all data in table
        audioDatabaseHelper.clearTable(TABLE_AUDIO)
        
        audioInfoList.clear()
        
        requireContext().contentResolver
            .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.AudioColumns.IS_MUSIC)?.apply {
                var id: String?
                var title: String?
                var artist: String?
                var album: String?
                var albumId: Long?
                var duration: Long?
                while (moveToNext()) {
                    id = getStringOrNull(getColumnIndex(MediaStore.Audio.AudioColumns._ID))
                    title = getStringOrNull(getColumnIndex(MediaStore.Audio.AudioColumns.TITLE))
                    @Suppress("InlinedApi")
                    artist = getStringOrNull(getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST))
                    @Suppress("InlinedApi")
                    album = getStringOrNull(getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM))
                    albumId = getLongOrNull(getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID))
                    @Suppress("InlinedApi")
                    duration = getLongOrNull(getColumnIndex(MediaStore.Audio.AudioColumns.DURATION))
                    if (id != null && title != null && artist != null && album != null && albumId != null && duration != null) {
                        audioInfoList.add(AudioInfo(id, title, artist, album, albumId, duration))
                    }
                }
                close()
            }
        // Re-arrange
        if (audioInfoList.isNotEmpty()) {
            // audioInfoList.removeAll { audioInfo -> audioInfo.audioDuration < 3000 }
            audioInfoList.sortBy { it.audioTitlePinyin }
            audioDatabaseHelper.insertAudio(TABLE_AUDIO, audioInfoList)
            CoroutineScope(Dispatchers.Main).launch { (fragmentHome.recyclerView.adapter as RecyclerViewAdapter).setAudioInfoList(audioInfoList) }
        }
    }
    
    private fun readDatabase(audioInfoList: ArrayList<AudioInfo>) {
        Log.e(TAG, "readDatabase")
        audioInfoList.clear()
        audioDatabaseHelper.queryAll(audioInfoList)
        CoroutineScope(Dispatchers.Main).launch { (fragmentHome.recyclerView.adapter as RecyclerViewAdapter).setAudioInfoList(audioInfoList) }
    }
    
    private fun loadBitmaps(audioInfoList: ArrayList<AudioInfo>, bitmaps: MutableMap<Long, Bitmap?>) {
        Log.e(TAG, "loadBitmaps")
        bitmaps.clear()
        
        audioInfoList.forEach { audioInfo ->
            tryOnly {
                bitmaps[audioInfo.audioAlbumId] =
                    BitmapFactory.decodeFileDescriptor(
                        requireContext().contentResolver
                            .openFileDescriptor(ContentUris.withAppendedId(URI, audioInfo.audioAlbumId), "r")
                            ?.fileDescriptor
                    )
            }
        }
        
        CoroutineScope(Dispatchers.Main).launch { (fragmentHome.recyclerView.adapter as RecyclerViewAdapter).setBitmaps(bitmaps) }
    }
    
    override fun onDestroyView() {
        Log.e(TAG, "onDestroyView")
        super.onDestroyView()
        _fragmentHomeBinding = null
    }
    
}