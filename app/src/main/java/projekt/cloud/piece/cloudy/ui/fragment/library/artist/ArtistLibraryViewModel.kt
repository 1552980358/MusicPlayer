package projekt.cloud.piece.cloudy.ui.fragment.library.artist

import android.content.Context
import androidx.annotation.MainThread
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import projekt.cloud.piece.cloudy.storage.audio.AudioDatabase.AudioDatabaseUtil.audioDatabase
import projekt.cloud.piece.cloudy.storage.audio.view.ArtistView
import projekt.cloud.piece.cloudy.util.CoroutineUtil.defaultBlocking
import projekt.cloud.piece.cloudy.util.LifecycleOwnerUtil.main
import projekt.cloud.piece.cloudy.util.helper.NullableHelper
import projekt.cloud.piece.cloudy.util.helper.NullableHelper.NullableHelperUtil.nullable

/**
 * [ArtistLibraryViewModel]
 * @extends [androidx.lifecycle.ViewModel]
 **/
class ArtistLibraryViewModel: ViewModel() {

    companion object ArtistLibraryViewModelUtil {

        /**
         * [ArtistLibraryViewModel.artistLibraryViewModel]
         * @extends [ArtistLibraryFragment]
         * @return [Lazy]<[ArtistLibraryViewModel]>
         **/
        fun ArtistLibraryFragment.artistLibraryViewModel(): Lazy<ArtistLibraryViewModel> {
            return viewModels()
        }

    }

    /**
     * [ArtistLibraryViewModel._artistList]
     * @wrap [NullableHelper]
     * @type [List]<[ArtistView]>
     **/
    private val _artistList = nullable<List<ArtistView>>()

    /**
     * [ArtistLibraryViewModel.requireArtistList]
     * @param fragment [androidx.fragment.app.Fragment]
     * @param preRequest [kotlin.jvm.functions.Function1]<[List]<[ArtistView]>, [Unit]>
     * @param onRequested [kotlin.jvm.functions.Function1]<[List]<[ArtistView]>, [Unit]>
     **/
    @UiThread
    fun requireArtistList(
        fragment: Fragment,
        preRequest: (List<ArtistView>?) -> Unit,
        onRequested: (List<ArtistView>) -> Unit
    ) {
        startPreRequestArtistList(
            fragment, _artistList.nullable(), preRequest, onRequested
        )
    }

    /**
     * [ArtistLibraryViewModel.startPreRequestArtistList]
     * @param fragment [androidx.fragment.app.Fragment]
     * @param artistList [List]<[ArtistView]>
     * @param preRequest [kotlin.jvm.functions.Function1]<[List]<[ArtistView]>, [Unit]>
     * @param onRequested [kotlin.jvm.functions.Function1]<[List]<[ArtistView]>, [Unit]>
     **/
    @UiThread
    private fun startPreRequestArtistList(
        fragment: Fragment,
        artistList: List<ArtistView>?,
        preRequest: (List<ArtistView>?) -> Unit,
        onRequested: (List<ArtistView>) -> Unit
    ) {
        preRequest.invoke(artistList)
        if (artistList == null) {
            setupArtistList(fragment, onRequested)
        }
    }

    /**
     * [ArtistLibraryViewModel.setupArtistList]
     * @param fragment [androidx.fragment.app.Fragment]
     * @param onRequested [kotlin.jvm.functions.Function1]<[List]<[ArtistView]>, [Unit]>
     **/
    @MainThread
    private fun setupArtistList(
        fragment: Fragment,
        onRequested: (List<ArtistView>) -> Unit
    ) {
        fragment.main {
            setArtistList(
                queryArtistList(fragment.requireContext()),
                onRequested
            )
        }
    }

    /**
     * [ArtistLibraryViewModel.setArtistList]
     * @param artistList [List]<[ArtistView]>
     * @param onRequested [kotlin.jvm.functions.Function1]<[List]<[ArtistView]>, [Unit]>
     **/
    private fun setArtistList(
        artistList: List<ArtistView>,
        onRequested: (List<ArtistView>) -> Unit
    ) {
        // Store list
        _artistList valued artistList
        // Update list to RecyclerView
        onRequested.invoke(artistList)
    }

    /**
     * [ArtistLibraryViewModel.queryArtistList]
     * @param context [android.content.Context]
     * @return [List]<[ArtistView]>
     **/
    private suspend fun queryArtistList(context: Context): List<ArtistView> {
        return defaultBlocking {
            context.audioDatabase
                .library
                .queryArtists()
        }
    }

}