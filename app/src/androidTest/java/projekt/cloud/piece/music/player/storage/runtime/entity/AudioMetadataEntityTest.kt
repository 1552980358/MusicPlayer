package projekt.cloud.piece.music.player.storage.runtime.entity


import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class AudioMetadataEntityTest {

    private val modelInstance = AudioMetadataEntity(
        "test_id",
        "test_title",
        "test_artist",
        "test_artist_name",
        "test_album",
        "test_album_title",
        35000L,
        3500000L
    )

    @Test
    fun validTestOfAudioMetadataEntityOnEquals() {
        assertTrue(
            "Valid Test of AudioMetadataEntity on override operator method equals()",
            modelInstance ==
                    AudioMetadataEntity(
                        "test_id",
                        "test_title",
                        "test_artist",
                        "test_artist_name",
                        "test_album",
                        "test_album_title",
                        35000L,
                        3500000L
                    )
        )
    }

    @Test
    fun invalidTestOfAudioMetadataEntityOnEquals() {
        assertFalse(
            "Invalid Test of AudioMetadataEntity on override operator method equals()",
            modelInstance ==
                    AudioMetadataEntity(
                        "modified_id",
                        "modified_title",
                        "modified_artist",
                        "modified_artist_name",
                        "modified_album",
                        "modified_album_title",
                        35000L,
                        3500000L
                    )
        )
    }

}