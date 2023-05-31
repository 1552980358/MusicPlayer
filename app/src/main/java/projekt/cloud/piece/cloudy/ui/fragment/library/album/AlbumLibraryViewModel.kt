package projekt.cloud.piece.cloudy.ui.fragment.library.album

import android.content.Context
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import projekt.cloud.piece.cloudy.storage.audio.AudioDatabase.AudioDatabaseUtil.audioDatabase
import projekt.cloud.piece.cloudy.storage.audio.view.AlbumView
import projekt.cloud.piece.cloudy.util.CoroutineUtil.defaultBlocking
import projekt.cloud.piece.cloudy.util.LifecycleOwnerUtil.main
import projekt.cloud.piece.cloudy.util.helper.NullableHelper
import projekt.cloud.piece.cloudy.util.helper.NullableHelper.NullableHelperUtil.nullable

/**
 * [AlbumLibraryViewModel]
 * @extends [androidx.lifecycle.ViewModel]
 **/
class AlbumLibraryViewModel: ViewModel() {

    companion object AlbumLibraryViewModelUtil {

        /**
         * [AlbumLibraryFragment.albumLibraryViewModel]
         * @extends [AlbumLibraryFragment]
         * @return [Lazy]<[AlbumLibraryViewModel]>
         **/
        fun AlbumLibraryFragment.albumLibraryViewModel(): Lazy<AlbumLibraryViewModel> {
            return viewModels()
        }

    }

    /**
     * [AlbumLibraryViewModel._albumList]
     * @wrap [NullableHelper]
     * @type [List]<[AlbumView]>
     **/
    private val _albumList = nullable<List<AlbumView>>()

    /**
     * [AlbumLibraryViewModel.requireAlbumList]
     * @param fragment [androidx.fragment.app.Fragment]
     * @param preRequest [kotlin.jvm.functions.Function1]<[List]<[AlbumView]>, [Unit]>
     * @param onRequested [kotlin.jvm.functions.Function1]<[List]<[AlbumView]>, [Unit]>
     *
     * Get [_albumList] instance, query list of [AlbumView] from database and store
     * if [_albumList] is null
     **/
    @MainThread
    fun requireAlbumList(
        fragment: Fragment,
        preRequest: (List<AlbumView>?) -> Unit,
        onRequested: (List<AlbumView>) -> Unit
    ) {
        if (preRequestAlbumList(_albumList.nullable(), preRequest)) {
            requestAlbumList(fragment, onRequested)
        }
    }

    /**
     * [AlbumLibraryViewModel.preRequestAlbumList]
     * @param albumList [List]<[AlbumView]>
     * @param preRequest [kotlin.jvm.functions.Function1]<[List]<[AlbumView]>, [Unit]>
     * @return [Boolean]
     *
     * Call [preRequest] and check if null
     * Return true if param [albumList] is null
     **/
    @MainThread
    private fun preRequestAlbumList(
        albumList: List<AlbumView>?, preRequest: (List<AlbumView>?) -> Unit
    ): Boolean {
        preRequest.invoke(albumList)
        return albumList == null
    }

    /**
     * [AlbumLibraryViewModel.requestAlbumList]
     * @param fragment
     * @param onRequested [kotlin.jvm.functions.Function1]<[List]<[AlbumView]>, [Unit]>
     **/
    @MainThread
    private fun requestAlbumList(fragment: Fragment, onRequested: (List<AlbumView>) -> Unit) {
        fragment.main {
            setupAlbumList(
                queryAlbumList(fragment.requireContext()),
                onRequested
            )
        }
    }

    /**
     * [AlbumLibraryViewModel.setupAlbumList]
     * @param albumList [List]<[AlbumView]>
     * @param onRequested [kotlin.jvm.functions.Function1]<[List]<[AlbumView]>, [Unit]>
     **/
    private fun setupAlbumList(
        albumList: List<AlbumView>, onRequested: (List<AlbumView>) -> Unit
    ) {
        _albumList valued albumList
        onRequested.invoke(albumList)
    }

    /**
     * [AlbumLibraryViewModel.queryAlbumList]
     * @param context [android.content.Context]
     * @return [List]<[AlbumView]>
     **/
    private suspend fun queryAlbumList(context: Context): List<AlbumView> {
        return defaultBlocking {
            context.audioDatabase
                .library
                .queryAlbums()
        }
    }

}