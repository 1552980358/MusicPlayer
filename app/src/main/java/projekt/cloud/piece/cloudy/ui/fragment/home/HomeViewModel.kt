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
import projekt.cloud.piece.cloudy.util.helper.NullableHelper
import projekt.cloud.piece.cloudy.util.helper.NullableHelper.NullableHelperUtil.nullable

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
     * [HomeViewModel._metadataList]
     * @type [NullableHelper]<[List]<[MetadataView]>>
     **/
    private val _metadataList = nullable<List<MetadataView>>()

    /**
     * [HomeViewModel.requireMetadataList]
     * @param fragment [androidx.fragment.app.Fragment]
     * @param onComplete [kotlin.jvm.functions.Function1]<[List]<[MetadataView]>>
     *
     * Check, request [List]<[MetadataView]>, store and set to UI component through [onComplete]
     **/
    @MainThread
    fun requireMetadataList(fragment: Fragment, onComplete: (List<MetadataView>) -> Unit) {
        when (val metadataList = _metadataList.nullable()) {
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
        _metadataList valued metadataList
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
     * Map all [MetadataView] into [MediaItem] in [_metadataList]
     **/
    private suspend fun getMediaItemList(): List<MediaItem> {
        return defaultBlocking {
            /**
             * [_metadataList] should be non-null,
             * otherwise [HomeViewModel.getMediaItemList] will never be triggered
             **/
            _metadataList.nonnull(::getMediaItemList)
        }
    }

    /**
     * [HomeViewModel.getMediaItemList]
     * @param metadataList [List]<[MetadataView]>
     * @return [List]<[MediaItem]>
     **/
    private fun getMediaItemList(
        metadataList: List<MetadataView>
    ): List<MediaItem> {
        return metadataList.map { it.mediaItem }
    }

}