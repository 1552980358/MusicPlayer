package projekt.cloud.piece.cloudy.ui.fragment.import_audio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.base.BaseFragment
import projekt.cloud.piece.cloudy.base.BaseMultiLayoutFragment
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.databinding.FragmentImportAudioBinding
import projekt.cloud.piece.cloudy.storage.audio.AudioDatabase
import projekt.cloud.piece.cloudy.storage.audio.AudioDatabase.AudioDatabaseUtil.audioDatabase
import projekt.cloud.piece.cloudy.storage.audio.dao.MetadataDao
import projekt.cloud.piece.cloudy.storage.audio.dao.StatisticsDao
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView
import projekt.cloud.piece.cloudy.storage.audio.view.StatisticsView
import projekt.cloud.piece.cloudy.storage.util.PreferenceUtil.defaultSharedPreference
import projekt.cloud.piece.cloudy.ui.activity.main.MainActivity
import projekt.cloud.piece.cloudy.ui.activity.guide.GuideActivity
import projekt.cloud.piece.cloudy.ui.fragment.import_audio.ImportAudioViewModel.Companion.importAudioViewModel
import projekt.cloud.piece.cloudy.util.CoroutineUtil.defaultBlocking
import projekt.cloud.piece.cloudy.util.CoroutineUtil.ioBlocking
import projekt.cloud.piece.cloudy.util.LifecycleOwnerUtil.main
import projekt.cloud.piece.cloudy.util.MediaStoreUtil.musicAlbumId
import projekt.cloud.piece.cloudy.util.MediaStoreUtil.musicAlbumTitle
import projekt.cloud.piece.cloudy.util.MediaStoreUtil.musicArtistId
import projekt.cloud.piece.cloudy.util.MediaStoreUtil.musicArtistName
import projekt.cloud.piece.cloudy.util.MediaStoreUtil.musicCursor
import projekt.cloud.piece.cloudy.util.MediaStoreUtil.musicDuration
import projekt.cloud.piece.cloudy.util.MediaStoreUtil.musicId
import projekt.cloud.piece.cloudy.util.MediaStoreUtil.musicSize
import projekt.cloud.piece.cloudy.util.MediaStoreUtil.musicTitle
import projekt.cloud.piece.cloudy.util.ViewBindingInflater

class ImportAudioFragment: BaseMultiLayoutFragment<FragmentImportAudioBinding, ImportAudioLayoutAdapter>() {

    /**
     * [BaseFragment.viewBindingInflater]
     * @type [ViewBindingInflater]
     **/
    override val viewBindingInflater: ViewBindingInflater<FragmentImportAudioBinding>
        get() = FragmentImportAudioBinding::inflate

    /**
     * [BaseMultiLayoutFragment.layoutAdapterBuilder]
     * @type [LayoutAdapterBuilder]
     **/
    override val layoutAdapterBuilder: LayoutAdapterBuilder<FragmentImportAudioBinding, ImportAudioLayoutAdapter>
        get() = ImportAudioLayoutAdapter.builder

    /**
     * [ImportAudioFragment.viewModel]
     * @type [ImportAudioViewModel]
     **/
    private val viewModel by importAudioViewModel()

    /**
     * [ImportAudioFragment.sharedPreferences]
     * @type [android.content.SharedPreferences]
     **/
    private val sharedPreferences by defaultSharedPreference()

    /**
     * [BaseFragment.setupBinding]
     * @param binding [FragmentImportAudioBinding]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onSetupBinding(binding: FragmentImportAudioBinding, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        super.onSetupBinding(binding, savedInstanceState)
        binding.materialButtonComplete
            .setOnClickListener(::onCompleteButtonClicked)
    }

    /**
     * [BaseMultiLayoutFragment.onSetupLayoutAdapter]
     * @param layoutAdapter [ImportAudioFragment]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onSetupLayoutAdapter(layoutAdapter: ImportAudioLayoutAdapter, savedInstanceState: Bundle?) {
        layoutAdapter.setupBackgroundColor()
        layoutAdapter.setupRetryButton(this, viewModel, ::onRetryButtonClicked)
        main(::startQuerying)
    }

    /**
     * [ImportAudioFragment.startQuerying]
     * @param coroutineScope [CoroutineScope]
     **/
    private suspend fun startQuerying(
        @Suppress("UNUSED_PARAMETER")
        coroutineScope: CoroutineScope
    ) {
        queryAudio(requireContext().audioDatabase)
    }

    /**
     * [ImportAudioFragment.queryAudio]
     * @param audioDatabase [AudioDatabase]
     *
     * Query and update statistics
     **/
    private suspend fun queryAudio(audioDatabase: AudioDatabase) {
        viewModel.updateStatistics(
            queryAndGetStatistics(audioDatabase)
        )
    }

    /**
     * [ImportAudioFragment.queryAndGetStatistics]
     * @param audioDatabase [AudioDatabase]
     * @return [StatisticsView]
     *
     * Query and store metadata, and return statistics
     **/
    private suspend fun queryAndGetStatistics(
        audioDatabase: AudioDatabase
    ): StatisticsView {
        queryMusic(audioDatabase.metadata)
        return getStatistics(audioDatabase.statistics)
    }

    /**
     * [ImportAudioFragment.queryMusic]
     * @param metadataDao [MetadataDao]
     **/
    private suspend fun queryMusic(metadataDao: MetadataDao) {
        return ioBlocking {
            requireContext().musicCursor { cursor ->
                metadataDao.insert(
                    MetadataView(
                        id = cursor.musicId,
                        title = cursor.musicTitle,
                        artist = cursor.musicArtistId,
                        artistName = cursor.musicArtistName,
                        album = cursor.musicAlbumId,
                        albumTitle = cursor.musicAlbumTitle,
                        duration = cursor.musicDuration,
                        size = cursor.musicSize
                    )
                )
            }
        }
    }

    /**
     * [ImportAudioFragment.getStatistics]
     * @param statisticsDao [StatisticsDao]
     * @return [StatisticsView]
     **/
    private suspend fun getStatistics(statisticsDao: StatisticsDao): StatisticsView {
        return defaultBlocking {
            statisticsDao.getStatistics()
        }
    }

    /**
     * [ImportAudioFragment.onRetryButtonClicked]
     *
     * Triggered when button [R.id.material_button_retry] clicked
     **/
    private fun onRetryButtonClicked(
        @Suppress("UNUSED_PARAMETER")
        view: View
    ) {
        if (viewModel.isLoadEnd) {
            viewModel.updateStatistics(null)
            main(::clearAndQuery)
        }
    }

    /**
     * [ImportAudioFragment.clearAndQuery]
     * @param coroutineScope [CoroutineScope]
     *
     * Clear database and query metadata from content resolver
     **/
    private suspend fun clearAndQuery(
        @Suppress("UNUSED_PARAMETER")
        coroutineScope: CoroutineScope
    ) {
        requireContext().audioDatabase.let { audioDatabase ->
            clearAudioDatabase(audioDatabase)
            queryAudio(audioDatabase)
        }
    }

    /**
     * [ImportAudioFragment.clearAudioDatabase]
     * @param audioDatabase [AudioDatabase]
     *
     * Remove all tables in [AudioDatabase]
     **/
    private suspend fun clearAudioDatabase(audioDatabase: AudioDatabase) {
        return ioBlocking {
            audioDatabase.metadata
                .clear()
        }
    }

    /**
     * [ImportAudioFragment.onCompleteButtonClicked]
     * @param view [android.view.View]
     *
     * Triggered when button [R.id.material_button_complete] is clicked
     **/
    private fun onCompleteButtonClicked(
        @Suppress("UNUSED_PARAMETER")
        view: View
    ) {
        if (viewModel.isLoadEnd) {
            main(::completeSetup)
        }
    }

    /**
     * [ImportAudioFragment.completeSetup]
     * @param coroutineScope [CoroutineScope]
     *
     * To do some complete setup before enter [MainActivity]
     **/
    private suspend fun completeSetup(
        @Suppress("UNUSED_PARAMETER")
        coroutineScope: CoroutineScope
    ) {
        setCompleteFlag()
        startMainAndFinishGuide(requireActivity())
    }

    /**
     * [ImportAudioFragment.setCompleteFlag]
     *
     * Store a flag indicating setup are done
     **/
    private suspend fun setCompleteFlag() {
        return ioBlocking {
            sharedPreferences.edit(true) {
                putBoolean(getString(R.string.guide_setup), true)
            }
        }
    }

    /**
     * [ImportAudioFragment.startMainAndFinishGuide]
     * @param activity [android.app.Activity]
     *
     * Start [MainActivity] and finish [GuideActivity]
     **/
    private fun startMainAndFinishGuide(activity: Activity) {
        // Let activity to start Main
        activity.startActivity(Intent(requireContext(), MainActivity::class.java))
        // Finish Guide
        activity.finish()
    }

}