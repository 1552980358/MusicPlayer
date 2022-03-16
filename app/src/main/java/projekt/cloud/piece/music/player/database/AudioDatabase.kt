package projekt.cloud.piece.music.player.database

import androidx.room.Database
import androidx.room.RoomDatabase
import projekt.cloud.piece.music.player.database.itemDao.AlbumItemDao
import projekt.cloud.piece.music.player.database.itemDao.ArtistItemDao
import projekt.cloud.piece.music.player.database.itemDao.AudioItemDao
import projekt.cloud.piece.music.player.database.item.AlbumItem
import projekt.cloud.piece.music.player.database.item.ArtistItem
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.database.item.ColorItem
import projekt.cloud.piece.music.player.database.item.PlaylistContentItem
import projekt.cloud.piece.music.player.database.item.PlaylistItem
import projekt.cloud.piece.music.player.database.itemDao.ColorItemDao
import projekt.cloud.piece.music.player.database.itemDao.PlaylistContentItemDao
import projekt.cloud.piece.music.player.database.itemDao.PlaylistItemDao

@Database(entities = [
    AudioItem::class,
    AlbumItem::class,
    ArtistItem::class,
    ColorItem::class,
    PlaylistItem::class,
    PlaylistContentItem::class],
    version = 1)
abstract class AudioDatabase: RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "AudioDatabase"
    }

    abstract fun audioItem(): AudioItemDao
    val audio get() = audioItem()

    abstract fun albumItem(): AlbumItemDao
    val album get() = albumItem()

    abstract fun artistItem(): ArtistItemDao
    val artist get() = artistItem()
    
    abstract fun colorItem(): ColorItemDao
    val color get() = colorItem()

    abstract fun playlistItem(): PlaylistItemDao
    val playlist get() = playlistItem()

    abstract fun playlistContentItem(): PlaylistContentItemDao
    val playlistContent get() = playlistContentItem()

}