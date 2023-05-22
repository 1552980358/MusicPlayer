package projekt.cloud.piece.cloudy.ui.fragment.home

import android.content.Context
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import projekt.cloud.piece.cloudy.storage.audio.AudioDatabase
import projekt.cloud.piece.cloudy.storage.audio.AudioDatabase.AudioDatabaseUtil.audioDatabase
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView
import projekt.cloud.piece.cloudy.util.CoroutineUtil.defaultBlocking
import projekt.cloud.piece.cloudy.util.CoroutineUtil.ioBlocking
import projekt.cloud.piece.cloudy.util.LifecycleOwnerUtil.main

class HomeViewModel: ViewModel() {

    companion object HomeViewModelUtil {

        /**
         * [HomeFragment.homeViewModel]
         * @return [Lazy]<[HomeViewModel]>
         **/
        @MainThread
        fun HomeFragment.homeViewModel(): Lazy<HomeViewModel> {
            return viewModels()
        }

    }

    /**
     * [HomeViewModel.metadataList]
     * @type [List]<[MetadataView]>
     **/
    private var _metadataList: List<MetadataView>? = null
    private val metadataList: List<MetadataView>
        get() = _metadataList!!

    /**
     * [HomeViewModel.requireMetadataList]
     * @param fragment [androidx.fragment.app.Fragment]
     * @param onComplete [kotlin.jvm.functions.Function1]<[List]<[MetadataView]>>
     *
     * Check, request [List]<[MetadataView]>, store and set to UI component through [onComplete]
     **/
    @MainThread
    fun requireMetadataList(fragment: Fragment, onComplete: (List<MetadataView>) -> Unit) {
        when (val metadataList = _metadataList) {
            null -> { setupMetadataList(fragment, onComplete) }
            else -> { onComplete.invoke(metadataList) }
        }
    }

    /**
     * [HomeViewModel.setupMetadataList]
     * @param fragment [androidx.fragment.app.Fragment]
     * @param onComplete [kotlin.jvm.functions.Function1]<[List]<[MetadataView]>>
     *
     * Setup metadata list from [AudioDatabase]
     **/
    private fun setupMetadataList(
        fragment: Fragment, onComplete: (List<MetadataView>) -> Unit
    ) {
        fragment.main {
            completeRequireMetadataList(
                queryMetadataList(fragment.requireContext()),
                onComplete
            )
        }
    }

    /**
     * [HomeViewModel.queryMetadataList]
     * @param context [android.content.Context]
     * @return [List]<[MetadataView]>
     *
     * Query metadata list from [AudioDatabase]
     **/
    private suspend fun queryMetadataList(context: Context): List<MetadataView> {
        return ioBlocking {
            context.audioDatabase
                .metadata
                .query()
        }
    }

    /**
     * [HomeViewModel.completeRequireMetadataList]
     * @param metadataList [List]<[MetadataView]>
     * @param onComplete [kotlin.jvm.functions.Function1]<[List]<[MetadataView]>>
     *
     * Store [metadataList] instance and call [onComplete]
     **/
    @MainThread
    private fun completeRequireMetadataList(
        metadataList: List<MetadataView>, onComplete: (List<MetadataView>) -> Unit
    ) {
        _metadataList = metadataList
        onComplete.invoke(metadataList)
    }

    /**
     * [HomeViewModel.playAudioAtPos]
     * @param fragment [androidx.fragment.app.Fragment]
     * @param mediaController [androidx.media3.session.MediaController]
     * @param pos [Int]
     **/
    fun playAudioAtPos(
        fragment: Fragment,
        mediaController: MediaController,
        pos: Int
    ) {
        fragment.main {
            mediaController.setMediaItems(getMediaItemList())
            mediaController.seekToDefaultPosition(pos)
            mediaController.prepare()
            mediaController.play()
        }
    }

    /**
     * [HomeViewModel.getMediaItemList]
     * @return [List]<[MediaItem]>
     *
     * Map all [MetadataView] into [MediaItem] in [metadataList]
     **/
    private suspend fun getMediaItemList(): List<MediaItem> {
        return defaultBlocking {
            /**
             * [metadataList] should be non-null,
             * otherwise [HomeViewModel.getMediaItemList] will never be triggered
             **/
            metadataList.map { metadataView ->
                metadataView.mediaItem
            }
        }
    }

}