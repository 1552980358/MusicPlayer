package projekt.cloud.piece.cloudy.storage.audio.view

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import projekt.cloud.piece.cloudy.storage.audio.entity.AlbumEntity.AlbumEntityConstants.ALBUM_ID
import projekt.cloud.piece.cloudy.storage.audio.entity.AlbumEntity.AlbumEntityConstants.ALBUM_TITLE
import projekt.cloud.piece.cloudy.storage.audio.entity.AlbumEntity.AlbumEntityConstants.TABLE_ALBUM
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.AUDIO_ALBUM
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.AUDIO_DURATION
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.AUDIO_ID
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity.AudioEntityConstant.TABLE_AUDIO
import projekt.cloud.piece.cloudy.storage.audio.view.AlbumView.AlbumViewConstants.ALBUM_COUNT
import projekt.cloud.piece.cloudy.storage.audio.view.AlbumView.AlbumViewConstants.ALBUM_DURATION
import projekt.cloud.piece.cloudy.storage.audio.view.AlbumView.AlbumViewConstants.VIEW_ALBUM
import projekt.cloud.piece.cloudy.util.DurationFormat.DurationFormatUtil.format
import projekt.cloud.piece.cloudy.util.DurationFormat.SHORT

@DatabaseView(
    value = "SELECT " +
        "$TABLE_ALBUM.$ALBUM_ID AS $ALBUM_ID, " +
        "$TABLE_ALBUM.$ALBUM_TITLE AS $ALBUM_TITLE, " +
        "COUNT($TABLE_AUDIO.$AUDIO_ID) AS $ALBUM_COUNT," +
        "SUM($TABLE_AUDIO.$AUDIO_DURATION) AS $ALBUM_DURATION " +
        "FROM $TABLE_ALBUM " +
        "INNER JOIN $TABLE_AUDIO ON $TABLE_AUDIO.$AUDIO_ALBUM = $TABLE_ALBUM.$ALBUM_ID " +
        "GROUP BY $TABLE_ALBUM.$ALBUM_ID",
    viewName = VIEW_ALBUM
)
data class AlbumView(
    @ColumnInfo(ALBUM_ID)
    val id: String,
    @ColumnInfo(ALBUM_TITLE)
    val title: String,
    @ColumnInfo(ALBUM_COUNT)
    val count: Int,
    @ColumnInfo(ALBUM_DURATION)
    val duration: Long
) {

    companion object AlbumViewConstants {

        const val VIEW_ALBUM = "album_view"

        const val ALBUM_COUNT = "count"
        const val ALBUM_DURATION = "duration"

    }

    /**
     * [AlbumView.durationStr]
     * @type [String]
     **/
    val durationStr: String
        get() = duration.format(SHORT)

}