package projekt.cloud.piece.cloudy.storage.audio.view

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import projekt.cloud.piece.cloudy.storage.audio.entity.ArtistEntity.ArtistEntityConstants.ARTIST_ID
import projekt.cloud.piece.cloudy.storage.audio.entity.ArtistEntity.ArtistEntityConstants.ARTIST_NAME
import projekt.cloud.piece.cloudy.storage.audio.entity.ArtistEntity.ArtistEntityConstants.TABLE_ARTIST
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.AUDIO_ARTIST
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.AUDIO_DURATION
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.AUDIO_ID
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.TABLE_AUDIO
import projekt.cloud.piece.cloudy.storage.audio.view.ArtistView.ArtistViewConstant.ARTIST_COUNT
import projekt.cloud.piece.cloudy.storage.audio.view.ArtistView.ArtistViewConstant.ARTIST_DURATION
import projekt.cloud.piece.cloudy.storage.audio.view.ArtistView.ArtistViewConstant.VIEW_ARTIST
import projekt.cloud.piece.cloudy.util.DurationFormat.DurationFormatUtil.format
import projekt.cloud.piece.cloudy.util.DurationFormat.SHORT

@DatabaseView(
    value = "SELECT " +
        "$TABLE_ARTIST.$ARTIST_ID AS $ARTIST_ID," +
        "$TABLE_ARTIST.$ARTIST_NAME AS $ARTIST_NAME," +
        "COUNT($TABLE_AUDIO.$AUDIO_ID) AS $ARTIST_COUNT," +
        "SUM($TABLE_AUDIO.$AUDIO_DURATION) AS $ARTIST_DURATION " +
        "FROM $TABLE_ARTIST " +
        "INNER JOIN $TABLE_AUDIO ON $TABLE_AUDIO.$AUDIO_ARTIST = $TABLE_ARTIST.$ARTIST_ID " +
        "GROUP BY $TABLE_ARTIST.$ARTIST_ID",
    viewName = VIEW_ARTIST
)
data class ArtistView(
    @ColumnInfo(ARTIST_ID)
    val id: String,
    @ColumnInfo(ARTIST_NAME)
    val name: String,
    @ColumnInfo(ARTIST_COUNT)
    val count: Int,
    @ColumnInfo(ARTIST_DURATION)
    val duration: Long
) {

    companion object ArtistViewConstant {

        const val VIEW_ARTIST = "artist_view"

        const val ARTIST_COUNT = "count"
        const val ARTIST_DURATION = "duration"

    }

    /**
     * [ArtistView.durationStr]
     * @type [String]
     **/
    val durationStr: String
        get() = duration.format(SHORT)

}