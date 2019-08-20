package app.skynight.musicplayer.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_MUSICCHANGE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_ONPAUSE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_ONSTART
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_ONSTOP
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

        const val LIST_HEART = -10

        var launchDone = false

        var currentMusic = 0
        var currentList = LIST_ALL

        /* Cycle / Single / Random */
        enum class PlayingType {
            CYCLE, SINGLE, RANDOM
        }

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

        var bgColor = false
        var buttons = false
        var rmFilter = false
        var blackStatusBar = false

        var wiredPlugIn = false
        var wiredPullOut = false
        var wirelessConn = false
        var wirelessDis = false

        var state = 0
    }

    init {
        //Thread {
        MusicClass.getMusicClass
        //}.start()
        //Thread {
        /*
        PlayList.loadAllPlayLists()
        playList = true
         */
        MainApplication.getMainApplication()
            .getSharedPreferences("app.skynight.musicplayer_preferences", MODE_PRIVATE).apply {
                bgColor = getBoolean("settingPreference_bgAlbum", false)
                buttons = getBoolean("settingPreference_buttons", false)
                rmFilter = getBoolean("settingPreference_filter", false)
                blackStatusBar = getBoolean("settingPreference_statusBar", false)

                wiredPlugIn = getBoolean("settingPreference_wired_plugin", false)
                wiredPullOut = getBoolean("settingPreference_wired_pullout", false)
                wirelessDis = getBoolean("settingPreference_wireless_disconnected", false)
            }
        //}.start()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener {
            paused = 0
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
                                MusicClass.getMusicClass.fullList.lastIndex
                            }
                            else -> {
                                PlayList.playListList[currentList].getPlayList().lastIndex
                            }
                        }).random()
                    )
                }
            }
        }
        PlayingControlUtil.getPlayingControlUtil
        launchDone = true
    }

    private var paused = -1

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

    @Suppress("unused")
    @Synchronized
    fun onStart() {
        when (state) {
            0 -> {
                changeMusic()
            }
            2 -> {
                mediaPlayer.start()
                if (paused != -1) {
                    mediaPlayer.seekTo(paused)
                    paused = -1
                }
            }
        }
        /*
        try {
            changeMusic()
            mediaPlayer.seekTo(paused)
            paused = 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        log("player", "onStart")
         */
        MainApplication.sendBroadcast(SERVER_BROADCAST_ONSTART)
    }

    @Suppress("unused")
    @Synchronized
    fun onPause() {
        mediaPlayer.pause()
        state = 2
        MainApplication.sendBroadcast(SERVER_BROADCAST_ONPAUSE)
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
    }

    @Synchronized
    fun playNext() {
        paused = -1
        if (playingType == Companion.PlayingType.RANDOM) {
            playChange(
                (0..when (currentList) {
                    LIST_ALL -> {
                        MusicClass.getMusicClass.fullList.lastIndex
                    }
                    else -> {
                        PlayList.playListList[currentList].getPlayList().lastIndex
                    }
                }).random()
            )
            return
        }
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
        paused = -1
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
        paused = -1
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
            state = 1
        } catch (e: Exception) {
            e.printStackTrace()
        }
        MainApplication.sendBroadcast(SERVER_BROADCAST_MUSICCHANGE)
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

    @Synchronized
    fun onSeekChange(pos: Int) {
        try {
            if (mediaPlayer.isPlaying) mediaPlayer.seekTo(pos * 1000)
            else paused = pos * 1000
        } catch (e: Exception) {
            //
        }
    }

    @Suppress("unused")
    fun getMediaPlayer(): MediaPlayer {
        return mediaPlayer
    }
}