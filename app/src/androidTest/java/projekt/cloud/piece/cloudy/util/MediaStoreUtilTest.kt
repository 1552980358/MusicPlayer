package projekt.cloud.piece.cloudy.util

import android.Manifest.permission.READ_MEDIA_AUDIO
import android.content.Context
import android.database.Cursor
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import projekt.cloud.piece.cloudy.util.MediaStoreUtil.musicAlbumId
import projekt.cloud.piece.cloudy.util.MediaStoreUtil.musicAlbumTitle
import projekt.cloud.piece.cloudy.util.MediaStoreUtil.musicArtistId
import projekt.cloud.piece.cloudy.util.MediaStoreUtil.musicArtistName
import projekt.cloud.piece.cloudy.util.MediaStoreUtil.musicCursor
import projekt.cloud.piece.cloudy.util.MediaStoreUtil.musicDuration
import projekt.cloud.piece.cloudy.util.MediaStoreUtil.musicId
import projekt.cloud.piece.cloudy.util.MediaStoreUtil.musicSize
import projekt.cloud.piece.cloudy.util.MediaStoreUtil.musicTitle

class MediaStoreUtilTest {

    private companion object {
        const val TAG = "MediaStoreUtilTest"
    }

    @JvmField
    @Rule
    val permission: GrantPermissionRule = GrantPermissionRule.grant(READ_MEDIA_AUDIO)

    private val context: Context
        get() = InstrumentationRegistry.getInstrumentation()
            .context

    @Test
    fun testQueryMusic() {
        log("===== Call testQueryMusic() =====")
        context.musicCursor {
            logCursor(it)
        }
        log("===== End testQueryMusic() =====")
    }

    private fun logCursor(cursor: Cursor) {
        log(
            "id=${cursor.musicId} " +
                    "title=${cursor.musicTitle} " +
                    "artistId=${cursor.musicArtistId} " +
                    "artist=${cursor.musicArtistName} " +
                    "albumId=${cursor.musicAlbumId} " +
                    "album=${cursor.musicAlbumTitle} " +
                    "duration=${cursor.musicDuration} " +
                    "size=${cursor.musicSize}"
        )
    }

    private fun log(message: String) {
        Log.d(TAG, message)
    }

}