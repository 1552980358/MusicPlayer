package projekt.cloud.piece.cloudy.ui.fragment.import_audio

import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import projekt.cloud.piece.cloudy.storage.audio.view.StatisticsView

class ImportAudioViewModel: ViewModel() {

    companion object {

        /**
         * [ImportAudioViewModel.importAudioViewModel]
         * @return [Lazy]<[ImportAudioViewModel]>
         *
         * Create [ImportAudioViewModel] for [ImportAudioFragment]
         **/
        fun ImportAudioFragment.importAudioViewModel(): Lazy<ImportAudioViewModel> {
            return viewModels()
        }

    }

    /**
     * [ImportAudioViewModel.loadingStatus]
     * @type [androidx.lifecycle.MutableLiveData]
     * @value true => loading; false => end
     **/
    private val _loadingStatus = MutableLiveData(true)
    val loadingStatus: LiveData<Boolean>
        get() = _loadingStatus

    /**
     * [ImportAudioViewModel.isLoadEnd]
     * @type [Boolean]
     **/
    inline val isLoadEnd: Boolean
        get() = loadingStatus.value == false

    /**
     * [ImportAudioViewModel.statistics]
     * @type [MutableLiveData]<[StatisticsView]>
     **/
    private val _statistics = MutableLiveData<StatisticsView?>()
    val statistics: LiveData<StatisticsView?>
        get() = _statistics

    /**
     * [ImportAudioViewModel.updateStatistics]
     * @param statistics [StatisticsView]
     *
     * Update [ImportAudioViewModel.statistics] content,
     * with update of [ImportAudioViewModel.loadingStatus]
     **/
    fun updateStatistics(statistics: StatisticsView?) {
        _statistics.value = statistics
        _loadingStatus.value = statistics == null
    }

}