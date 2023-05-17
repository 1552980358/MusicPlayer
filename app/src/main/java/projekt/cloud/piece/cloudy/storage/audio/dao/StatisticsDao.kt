package projekt.cloud.piece.cloudy.storage.audio.dao

import androidx.room.Dao
import androidx.room.Query
import projekt.cloud.piece.cloudy.storage.audio.view.StatisticsView
import projekt.cloud.piece.cloudy.storage.audio.view.StatisticsView.StatisticsViewConstant.VIEW_STATISTICS

@Dao
interface StatisticsDao {

    @Query("SELECT * FROM $VIEW_STATISTICS")
    suspend fun getStatistics(): StatisticsView

}