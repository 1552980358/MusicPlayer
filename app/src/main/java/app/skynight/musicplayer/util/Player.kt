package app.skynight.musicplayer.util

import android.content.Context
import android.media.MediaPlayer
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_MUSICCHANGE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_ONPAUSE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_ONSTART
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_ONSTOP
import java.io.File
import app.skynight.musicplayer.R

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

        const val EXTRA_LIST = "MusicList"
        const val ERROR_CODE = Int.MIN_VALUE
        const val LIST_ALL = -1
        const val LIST_HEART = -2

        var fullList = false
        var playList = false

        var currentMusic = 0
        var currentList = LIST_ALL

        /* Cycle / Single / Random */
        enum class PlayingType {
            CYCLE, SINGLE, RANDOM
        }

        val THREAD_NO = mapOf(
            "HALF" to Runtime.getRuntime().availableProcessors() / 2,
            "PROCESSOR" to Runtime.getRuntime().availableProcessors(),
            "SUPER" to Runtime.getRuntime().availableProcessors() * 2
        )

        val getPlayer by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Player()
        }

        fun getCurrentMusicInfo(): MusicInfo {
            return when (currentList) {
                LIST_ALL -> MusicClass.getMusicClass.fullList[currentMusic]
                //LIST_HEART -> {}
                else -> PlayList.playListList[currentList].getPlayList()[currentMusic]
            }
        }
    }

    init {
        Thread {
            MusicClass.getMusicClass
            fullList = true
        }.start()
        Thread {
            PlayList.loadAllPlayLists()
            playList = true
        }.start()
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
    @Synchronized
    fun onStart() {
        try {
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        MainApplication.sendBroadcast(SERVER_BROADCAST_ONSTART)
    }

    @Suppress("unused")
    @Synchronized
    fun onPause() {
        try {
            mediaPlayer.pause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        MainApplication.sendBroadcast(SERVER_BROADCAST_ONPAUSE)
    }

    @Suppress("unused")
    @Synchronized
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
    @Synchronized
    fun setWakeMode(context: Context, mode: Int) {
        mediaPlayer.setWakeMode(context, mode)
    }

    @Suppress("unused")
    @Synchronized
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

    @Synchronized
    fun playChange(list: Int, index: Int) {
        if (list == ERROR_CODE || index == ERROR_CODE) {
            makeToast(R.string.abc_player_unExpected_intent)
            return
        }
        currentList = list
        currentMusic = index
        changeMusic()
    }

    @Synchronized
    fun changeMusic() {
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()

            mediaPlayer.setDataSource(when (currentList) {
                LIST_ALL -> { getCurrentMusicInfo().path }
                LIST_HEART -> { throw Exception("NotImplemented") }
                else -> { getCurrentMusicInfo().path }
            })
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        MainApplication.sendBroadcast(SERVER_BROADCAST_MUSICCHANGE)
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    fun getCurrent(): Int {
        return mediaPlayer.currentPosition / 1000
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