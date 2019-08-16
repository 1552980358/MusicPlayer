package app.skynight.musicplayer.util

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_MUSICCHANGE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_ONPAUSE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_ONSTART
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_ONSTOP
import app.skynight.musicplayer.R
import app.skynight.musicplayer.service.PlayService

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
                    //mediaPlayer.prepare()
                    if (!mediaPlayer.isLooping) {
                        mediaPlayer.isLooping = true
                        onStart()
                    }
                }
                Companion.PlayingType.RANDOM -> {
                    playChange(
                        (0..when (currentList) {
                            LIST_ALL -> {
                                MusicClass.getMusicClass.fullList.size
                            }
                            else -> {
                                PlayList.playListList[currentList].getPlayList().size
                            }
                        }).random()
                    )
                }
            }
        }
    }

    @Suppress("unused")
    @Synchronized
    fun onStart() {
        try {
            changeMusic()
            mediaPlayer.seekTo(paused)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        log("player", "onStart")
        MainApplication.sendBroadcast(SERVER_BROADCAST_ONSTART)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MainApplication.getMainApplication().startForegroundService(
                Intent(
                    MainApplication.getMainApplication(),
                    PlayService::class.java
                )
            )
            return
        }
        MainApplication.getMainApplication()
            .startService(Intent(MainApplication.getMainApplication(), PlayService::class.java))
    }

    private var paused = 0
    @Suppress("unused")
    @Synchronized
    fun onPause() {
        try {
            paused = mediaPlayer.currentPosition
            mediaPlayer.pause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        log("player", "onPause")
        MainApplication.sendBroadcast(SERVER_BROADCAST_ONPAUSE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MainApplication.getMainApplication().startForegroundService(
                Intent(
                    MainApplication.getMainApplication(),
                    PlayService::class.java
                )
            )
            return
        }
        MainApplication.getMainApplication()
            .startService(Intent(MainApplication.getMainApplication(), PlayService::class.java))
    }

    @Suppress("unused")
    @Synchronized
    fun onStop() {
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        MainApplication.sendBroadcast(SERVER_BROADCAST_ONSTOP)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MainApplication.getMainApplication().startForegroundService(
                Intent(
                    MainApplication.getMainApplication(),
                    PlayService::class.java
                )
            )
            return
        }
        MainApplication.getMainApplication()
            .startService(Intent(MainApplication.getMainApplication(), PlayService::class.java))
    }

    @Suppress("unused")
    @Synchronized
    fun setWakeMode(context: Context, mode: Int) {
        mediaPlayer.setWakeMode(context, mode)
    }

    private var playingType = PlayingType.CYCLE

    @Suppress("unused")
    @Synchronized
    fun setPlayingType(playingType: PlayingType = Companion.PlayingType.CYCLE) {
        this.playingType = playingType
        mediaPlayer.isLooping = playingType == Companion.PlayingType.SINGLE
    }

    fun getPlayingType(): PlayingType {
        return playingType
    }

    @Synchronized
    fun playNext() {
        if (currentMusic == when (currentList) {
                LIST_ALL -> {
                    MusicClass.getMusicClass.fullList.lastIndex
                }
                else -> {
                    PlayList.playListList[currentList].getPlayList().lastIndex
                }
            }
        ) {
            currentMusic = 0
        } else {
            currentMusic++
        }
        changeMusic()
    }

    @Synchronized
    fun playLast() {
        if (currentMusic == 0) {
            currentMusic = when (currentList) {
                LIST_ALL -> {
                    MusicClass.getMusicClass.fullList.lastIndex
                }
                else -> {
                    PlayList.playListList[currentList].getPlayList().lastIndex
                }
            }
        } else {
            currentMusic--
        }
        changeMusic()
    }

    @Synchronized
    fun playChange(index: Int) {
        playChange(currentList, index)
    }

    @Synchronized
    fun playChange(list: Int, index: Int) {
        paused = 0
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

            mediaPlayer.setDataSource(
                when (currentList) {
                    LIST_ALL -> {
                        getCurrentMusicInfo().path
                    }
                    LIST_HEART -> {
                        throw Exception("NotImplemented")
                    }
                    else -> {
                        getCurrentMusicInfo().path
                    }
                }
            )
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        MainApplication.sendBroadcast(SERVER_BROADCAST_MUSICCHANGE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MainApplication.getMainApplication().startForegroundService(
                Intent(
                    MainApplication.getMainApplication(),
                    PlayService::class.java
                )
            )
            return
        }
        MainApplication.getMainApplication()
            .startService(Intent(MainApplication.getMainApplication(), PlayService::class.java))
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    fun getCurrent(): Int {
        return if (mediaPlayer.isPlaying) {
            mediaPlayer.currentPosition / 1000
        } else {
            paused / 1000
        }
    }

    @Suppress("unused")
    fun getIndexMusic(index: Int): MusicInfo {
        return MusicClass.getMusicClass.fullList[index]
    }

    fun onSeekChange(pos: Int) {
        try {
            mediaPlayer.seekTo(pos * 1000)
        } catch (e: Exception) {
            //
        }
    }
}