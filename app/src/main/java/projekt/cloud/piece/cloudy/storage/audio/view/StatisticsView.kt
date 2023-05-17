package projekt.cloud.piece.cloudy.storage.audio.view

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.AUDIO_ALBUM
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.AUDIO_ARTIST
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.AUDIO_ID
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.TABLE_AUDIO
import projekt.cloud.piece.cloudy.storage.audio.view.StatisticsView.StatisticsViewConstant.STATISTICS_ALBUM
import projekt.cloud.piece.cloudy.storage.audio.view.StatisticsView.StatisticsViewConstant.STATISTICS_ARTIST
import projekt.cloud.piece.cloudy.storage.audio.view.StatisticsView.StatisticsViewConstant.STATISTICS_AUDIO
import projekt.cloud.piece.cloudy.storage.audio.view.StatisticsView.StatisticsViewConstant.VIEW_STATISTICS

@DatabaseView(
    value = "SELECT " +
        "COUNT(DISTINCT $AUDIO_ID) AS $STATISTICS_AUDIO, " +
        "COUNT(DISTINCT $AUDIO_ARTIST) AS $STATISTICS_ARTIST, " +
        "COUNT(DISTINCT $AUDIO_ALBUM) AS $STATISTICS_ALBUM " +
        "FROM $TABLE_AUDIO",
    viewName = VIEW_STATISTICS
)
data class StatisticsView(
    @ColumnInfo(STATISTICS_AUDIO)
    val audio: Int,
    @ColumnInfo(STATISTICS_ARTIST)
    val artist: Int,
    @ColumnInfo(STATISTICS_ALBUM)
    val album: Int
) {

    companion object StatisticsViewConstant {

        const val VIEW_STATISTICS = "statistics"

        const val STATISTICS_AUDIO = "audio"
        const val STATISTICS_ARTIST = "artist"
        const val STATISTICS_ALBUM = "album"

    }

}