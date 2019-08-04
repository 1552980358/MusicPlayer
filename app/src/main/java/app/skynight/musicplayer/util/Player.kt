package app.skynight.musicplayer.util

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_TITLE
import android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST
import android.media.MediaMetadataRetriever.METADATA_KEY_ALBUM
import android.media.MediaMetadataRetriever.METADATA_KEY_BITRATE
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.PLAYER_BROADCAST_ONSTART
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.SERVER_BROADCAST_MUSICCHANGE
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.SERVER_BROADCAST_ONPAUSE
import app.skynight.musicplayer.broadcast.BroadcastList.Companion.SERVER_BROADCAST_ONSTOP
import java.io.File

/**
 * @FILE:   PlayerUtil
 * @AUTHOR: 1552980358
 * @DATE:   19 Jul 2019
 * @TIME:   2:40 PM
 **/

class Player private constructor() {
    @Suppress("JoinDeclarationAndAssignment")
    private var mediaPlayer: MediaPlayer

    companion object {
        const val TAG = "Player"
        val musicList = mutableListOf<MusicInfo>()
        fun createMusicInfo(path: String) {
            if (path.endsWith(".3gp") || path.endsWith(".m4a") || path.endsWith(".aac") || path.endsWith(
                    ".ts"
                ) || path.endsWith(
                    ".flac"
                ) || path.endsWith(".gsm") || path.endsWith(".mid") || path.endsWith(".xmf") || path.endsWith(
                    ".mxmf"
                ) || path.endsWith(".rtttl") || path.endsWith(".rtx") || path.endsWith(".mp3") || path.endsWith(
                    ".mkv"
                ) || path.endsWith(".wav") || path.endsWith(".ogg")
            ) {
                try {
                    val mediaMetadataRetriever = MediaMetadataRetriever()
                    mediaMetadataRetriever.setDataSource(path)

                    val musicInfo = MusicInfo(
                        path,
                        mediaMetadataRetriever.extractMetadata(METADATA_KEY_TITLE),
                        mediaMetadataRetriever.extractMetadata(METADATA_KEY_ARTIST),
                        mediaMetadataRetriever.extractMetadata(METADATA_KEY_ALBUM),
                        if (mediaMetadataRetriever.embeddedPicture.size > 1) BitmapFactory.decodeByteArray(
                            mediaMetadataRetriever.embeddedPicture,
                            0,
                            mediaMetadataRetriever.embeddedPicture.size
                        ) else null,
                        mediaMetadataRetriever.extractMetadata(METADATA_KEY_BITRATE),
                        mediaMetadataRetriever.extractMetadata(METADATA_KEY_DURATION).toInt()
                    )
                    addMusic(musicInfo)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        @Synchronized
        private fun addMusic(musicInfo: MusicInfo) {
            musicList.add(musicInfo)
        }

        var currentMusic = 0

        /* Cycle / Single / Random */
        enum class PlayingType {
            CYCLE, SINGLE, RANDOM
        }

        @Suppress("unused")
        @Deprecated("Replace with map")
        const val THREAD_SINGLE = 1
        @Suppress("unused")
        @Deprecated("Replace with map")
        const val THREAD_DOUBLE = 2
        @Suppress("MemberVisibilityCanBePrivate")
        @Deprecated("Replace with map")
        val THREAD_CORE = Runtime.getRuntime().availableProcessors()
        @Suppress("unused")
        @Deprecated("Replace with map")
        val THREAD_DOUBLE_CORE = THREAD_CORE * 2

        val THREAD_NO = mapOf(
            "SINGLE" to 1,
            "DOUBLE" to 2,
            "PROCESSOR" to Runtime.getRuntime().availableProcessors(),
            "SUPER" to Runtime.getRuntime().availableProcessors() * 2
        )

        val getPlayer by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Player()
        }
    }

    init {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener {
            when (playingType) {
                Companion.PlayingType.CYCLE -> {
                    playNext()
                }
                Companion.PlayingType.SINGLE -> {
                    mediaPlayer.prepare()
                    if (!mediaPlayer.isLooping) {
                        mediaPlayer.isLooping = true
                        onStart()
                    }
                }
                Companion.PlayingType.RANDOM -> {

                }
            }
        }
    }

    private var playingType = PlayingType.CYCLE

    @Suppress("unused")
    fun onStart() {
        try {
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        MainApplication.sendBroadcast(PLAYER_BROADCAST_ONSTART)
    }

    @Suppress("unused")
    fun onPause() {
        try {
            mediaPlayer.pause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        MainApplication.sendBroadcast(SERVER_BROADCAST_ONPAUSE)
    }

    @Suppress("unused")
    fun onStop() {
        try {
            mediaPlayer.stop()
            //mediaPlayer.release()
            mediaPlayer.reset()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        MainApplication.sendBroadcast(SERVER_BROADCAST_ONSTOP)
    }

    @Suppress("unused")
    fun setWakeMode(context: Context, mode: Int) {
        mediaPlayer.setWakeMode(context, mode)
    }

    @Suppress("unused")
    fun setPlayingType(playingType: PlayingType = Companion.PlayingType.CYCLE) {
        this.playingType = playingType
    }

    @Synchronized
    fun playNext() {
        currentMusic++
        changeMusic()
    }

    @Synchronized
    fun playLast() {
        currentMusic--
        changeMusic()
    }

    @Suppress("unused")
    @Synchronized
    fun playChange(index: Int) {
        currentMusic = index
        changeMusic()
    }

    @Synchronized
    fun changeMusic() {
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.setDataSource(musicList[currentMusic].path)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        MainApplication.sendBroadcast(SERVER_BROADCAST_MUSICCHANGE)
    }

    private fun updateMusicList(path: File) {
        if (!path.isDirectory) {
            createMusicInfo(path.toString())
            return
        }

        for (i in path.listFiles()) {
            if (i.isDirectory) {
                updateMusicList(i)
            } else {
                createMusicInfo(i.toString())
            }
        }
    }

    @Suppress("unused")
    fun onUpdateMusicList(thread: Int) {
        Log.e(TAG, "onUpdateMusicList($thread)")
        val file = Environment.getExternalStorageDirectory()

        if (thread == 1) {
            updateMusicList(file)
            return
        }

        if (thread < 1) {
            throw Exception("")
        }

        val fileList = file.listFiles().toMutableList()
        @Synchronized
        fun getFile(): File {
            val tmp = fileList.last()
            fileList.removeAt(fileList.lastIndex)
            return tmp
        }
        for (i in 0 until thread) {
            Thread {
                updateMusicList(getFile())
            }.start()
        }
    }

    @Suppress("unused")
    fun onSeekChange(pos: Int) {
        try {
            mediaPlayer.seekTo(pos * 1000)
        } catch (e: Exception) {
            //
        }
    }
}