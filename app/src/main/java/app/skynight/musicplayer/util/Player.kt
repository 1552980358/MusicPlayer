package app.skynight.musicplayer.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import androidx.core.content.ContextCompat
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

    @Suppress("unused")
    companion object {
        fun changeSort(method: String) {
            MainApplication.getMainApplication().getSharedPreferences(
                "app.skynight.musicplayer_preferences", MODE_PRIVATE
            ).edit().putString("settingPreference_arrangement", method).apply()
            val info = getCurrentMusicInfo()
            settings[Arrangement] = method
            when (currentList) {
                LIST_ALL -> {
                    MusicClass.sortList()
                    for ((j, i) in MusicClass.getMusicClass.fullList.withIndex()) {
                        if (i == info) {
                            currentList = j
                        }
                    }
                }
            }
        }

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

        val settings = mutableMapOf<String, Any>()

        const val BgColor = "BgColor"
        const val Button = "Button"
        const val Filter = "Filter"
        const val StatusBar = "StatusBar"
        const val Pulse = "Pulse"
        const val PulseType = "PulseType"
        const val PulseType_CompatWave = "PulseType_CompatWave"
        const val PulseType_ElectricCurrent = "PulseType_ElectricCurrent"
        const val PulseType_VerticalColumn = "PulseType_VerticalColumn"
        const val PulseDensity = "PulseDensity"
        const val PulseColor = "PulseColor"
        const val Theme = "Theme"
        const val Theme_0 = "Theme_0"
        const val Theme_1 = "Theme_1"
        const val SimpleMode = "SimpleMode"
        const val Lyric = "Lyric"
        const val LyricColor = "LyricColor"
        const val LyricSupport = "LyricSupport"
        const val LyricSupport_NetEase = "LyricSupport_NetEase"
        const val LyricSupport_KuWo = "LyricSupport_KuWo"

        var ThemeTextColor = Color.BLACK
        var ThemeBgColor = Color.WHITE

        const val WiredPlugIn = "WiredPlugIn"
        const val WiredPullOut = "WiredPullOut"
        const val WirelessCon = "WirelessCon"
        const val WirelessDis = "WirelessDis"

        // 歌曲排列
        // 0: 标题, 1: 艺术家, 2: 专辑, 3: 原始排序
        const val Arrangement = "Arrangement"

        @Suppress("unused")
        var state = 0
    }

    private var playedList = mutableListOf<Music>()
    private var pointer = 0

    private class Music(val list: Int, val musicInfo: MusicInfo)

    init {
        MainApplication.sharedPreferences.apply {
            settings[BgColor] = getBoolean("settingPreference_bgAlbum", false)
            settings[Button] = getBoolean("settingPreference_buttons", false)
            settings[Filter] = getBoolean("settingPreference_filter", false)
            settings[StatusBar] = getBoolean("settingPreference_statusBar", false)

            settings[WiredPlugIn] = getBoolean("settingPreference_wired_plugin", false)
            settings[WiredPullOut] = getBoolean("settingPreference_wired_pullout", false)
            settings[WirelessDis] = getBoolean("settingPreference_wireless_disconnected", false)

            settings[Arrangement] = getString("settingPreference_arrangement", "TITLE")!!
            settings[Pulse] = getBoolean("settingPreference_pulse", true)
            settings[PulseType] =
                getString("settingPreference_pulse_type", PulseType_CompatWave)!!
            settings[PulseDensity] = getBoolean("settingPreference_pulse_density", false)
            settings[PulseColor] = getBoolean("settingPreference_pulse_color", false)

            settings[Theme] = getString("settingPreference_theme", Theme_0)!!.apply {
                if (this != Theme_0) {
                    ThemeTextColor = Color.WHITE
                    ThemeBgColor = ContextCompat.getColor(MainApplication.getMainApplication(), R.color.theme1_colorPrimary)
                }
            }

            settings[SimpleMode] = getBoolean("settingPreference_extremeSimple", false)
            settings[Lyric] = getBoolean("settingPreference_lyric", true)
            settings[LyricSupport] = getString("settingPreference_lyricSupplier", LyricSupport_NetEase)!!
            settings[LyricColor] = getBoolean("settingPreference_lyricColor", false)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                settings.forEach { (string, any) ->
                    log(string, any)
                }
            }
        }
        MusicClass.getMusicClass

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
                    playNext()
                }
            }
        }
        PlayingControlUtil.getPlayingControlUtil
        playedList.add(Music(LIST_ALL, getCurrentMusicInfo()))
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

    @Suppress("unused")
    fun getPlayingType(): PlayingType {
        return playingType
    }

    @Suppress("unused")
    @Synchronized
    fun onStart() {
        //mediaPlayer.setWakeMode(MainApplication.getMainApplication(), PowerManager.PARTIAL_WAKE_LOCK)
        when (state) {
            0 -> {
                changeMusic()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                        setAudioAttributes(AudioAttributes.Builder().run {
                            setUsage(AudioAttributes.USAGE_MEDIA)
                            setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            build()
                        })
                        setAcceptsDelayedFocusGain(true)
                        setOnAudioFocusChangeListener { changeType: Int ->
                            when (changeType) {
                                AudioManager.AUDIOFOCUS_GAIN -> {

                                }
                                AudioManager.AUDIOFOCUS_LOSS -> {

                                }
                            }
                        }
                        build()
                    }
                }
            }

            1, 2 -> {
                //pointer = 0
                mediaPlayer.start()
                if (mediaPlayer.currentPosition - paused > 1000/* != -1*/) {
                    mediaPlayer.seekTo(paused)
                }
                paused = -1
                mediaPlayer.setVolume(1f, 1f)
            }
        }
        state = 1
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
        paused = mediaPlayer.currentPosition
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

    @Suppress("unused")
    @Synchronized
    fun playNext() {
        state = 1
        paused = -1

        if (pointer != playedList.lastIndex) {
            pointer++
            currentList = playedList[pointer].list
            for ((j, i) in when (currentList) {
                LIST_ALL -> {
                    MusicClass.getMusicClass.fullList
                }
                else -> {
                    PlayList.playListList[currentList].getPlayList()
                }
            }.withIndex()) {
                if (i == playedList[pointer].musicInfo) {
                    currentMusic = j
                    break
                }
            }
            changeMusic()
            return
        }

        if (playingType == Companion.PlayingType.RANDOM) {
            currentMusic = (0..when (currentList) {
                LIST_ALL -> {
                    MusicClass.getMusicClass.fullList.lastIndex
                }
                else -> {
                    PlayList.playListList[currentList].getPlayList().lastIndex
                }
            }).random()

            playedList.add(Music(currentList, getCurrentMusicInfo()))
            pointer++
            changeMusic()
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
        playedList.add(Music(currentList, getCurrentMusicInfo()))
        pointer++
        changeMusic()
    }

    @Suppress("unused")
    @Synchronized
    fun playLast() {
        paused = -1
        state = 1
        log("playLast", pointer)
        if (pointer != 0) {
            pointer--
            currentList = playedList[pointer].list
            for ((j, i) in when (currentList) {
                LIST_ALL -> {
                    MusicClass.getMusicClass.fullList
                }
                else -> {
                    PlayList.playListList[currentList].getPlayList()
                }
            }.withIndex()) {
                if (i == playedList[pointer].musicInfo) {
                    currentMusic = j
                    break
                }
            }
            changeMusic()
            log("playLast", pointer)
            return
        }

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

        playedList.add(Music(currentList, getCurrentMusicInfo()))
        pointer++
        changeMusic()
    }

    @Suppress("unused")
    @Synchronized
    fun playChange(list: Int, index: Int) {
        paused = -1
        if (list == ERROR_CODE || index == ERROR_CODE) {
            makeToast(R.string.abc_player_unExpected_intent)
            return
        }
        currentList = list
        currentMusic = index
        playedList.add(Music(currentList, getCurrentMusicInfo()))
        changeMusic()
    }

    @Suppress("unused")
    @Synchronized
    fun changeMusic() {
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()

            mediaPlayer.setDataSource(
                when (currentList) {
                    LIST_ALL -> {
                        getCurrentMusicInfo().path()
                    }
                    LIST_HEART -> {
                        throw Exception("NotImplemented")
                    }
                    else -> {
                        getCurrentMusicInfo().path()
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

    @Suppress("unused")
    fun getCurrent(): Int {
        return if (mediaPlayer.isPlaying) {
            mediaPlayer.currentPosition
        } else {
            if (paused != -1) {
                paused
            } else {
                -1
            }
        }
    }

    @Suppress("unused")
    fun getIndexMusic(index: Int): MusicInfo {
        return MusicClass.getMusicClass.fullList[index]
    }

    @Suppress("unused")
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