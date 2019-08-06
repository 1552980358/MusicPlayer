package app.skynight.musicplayer.util

import android.content.Context
import android.media.MediaPlayer
import android.os.Environment
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_MUSICCHANGE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_ONPAUSE
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_ONSTART
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_ONSTOP
import app.skynight.musicplayer.broadcast.BroadcastBase.Companion.SERVER_BROADCAST_PREPAREDONE
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import java.io.FileWriter
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
    private val searchThreadList = mutableListOf<Thread>()

    companion object {
        //const val TAG = "Player"
        val AllMusicSavedPath =
            MainApplication.getMainApplication().cacheDir.absolutePath + File.separator + "AllMusic.json"

        const val EXTRA_LIST = "MusicList"
        const val ERROR_CODE = Int.MIN_VALUE
        const val LIST_ALL = -1
        const val LIST_HEART = -2

        var prepareDone = true

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
                    addMusic(MusicInfo(path))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private val playListList = mutableListOf<PlayList>()
        @Synchronized
        fun addPlayList(playList: PlayList) {
            playListList.add(playList)
        }
        fun getPlayList(index: Int): PlayList {
            return playListList[index]
        }

        @Synchronized
        private fun addMusic(musicInfo: MusicInfo) {
            musicList.add(musicInfo)
        }

        var currentMusic = 0
        var currentList = -1

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
        @Suppress("unused", "DEPRECATION")
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
        Thread {
            prepareDone = false
            val file = File(AllMusicSavedPath)
            if (file.exists()) {
                try {
                    for (i in JsonParser().parse(file.inputStream().bufferedReader().readText()).asJsonObject.get(
                        "FullList"
                    ).asJsonArray) {
                        addMusic(MusicInfo(i.asString))
                    }
                    prepareDone = true
                    MainApplication.sendBroadcast(SERVER_BROADCAST_PREPAREDONE)
                    return@Thread
                } catch (e: Exception) {
                    //e.printStackTrace()
                    //file.delete()
                }
            }
            prepareDone = true
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
                LIST_ALL -> { musicList[currentMusic].path }
                LIST_HEART -> { throw Exception("NotImplemented") }
                else -> { playListList[currentList].getPlayList()[currentMusic].path }
            })
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
        prepareDone = false
        //Log.e(TAG, "onUpdateMusicList($thread)")
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
            searchThreadList.add(Thread {
                updateMusicList(getFile())
            }.apply { start() })
        }
        Thread {
            whileLoop@ while (!prepareDone) {
                try {
                    Thread.sleep(20)
                } catch (e: Exception) {
                    //
                }
                for (i in searchThreadList) {
                    if (i.isAlive) {
                        continue@whileLoop
                    }
                }
                // Complete flag
                prepareDone = true
            }
            //Log.e(TAG, "prepareDone: ${musicList.size}")
            makeToast("Complete")
            MainApplication.sendBroadcast(SERVER_BROADCAST_PREPAREDONE)

            try {
                FileWriter(File(AllMusicSavedPath).apply {
                    if (exists()) {
                        delete()
                    }
                    createNewFile()
                }).apply {
                    write(JsonObject().apply {
                        add("FullList", JsonArray().apply {
                            musicList.forEach { musicInfo ->
                                add(musicInfo.path)
                            }
                        })
                    }.toString())
                    flush()
                    close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }.start()
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