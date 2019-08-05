package app.skynight.musicplayer.broadcast

import java.lang.Exception

/**
 * @FILE:   BroadcastList
 * @AUTHOR: 1552980358
 * @DATE:   19 Jul 2019
 * @TIME:   8:50 AM
 **/

@Suppress("SpellCheckingInspection")
class BroadcastList private constructor() {
    @Suppress("MemberVisibilityCanBePrivate")
    companion object {

        /**
         * Sender will send music change broadcast first.
         * Then, wait for global music change broadcast
         * from the music util as well as the music player start
         * preparing the playback.
         * When the receive of the broadcast from music util,
         * players will get required data from that.
         *
         * 发送者先发送音乐切换广播，等待音乐类准备播放音乐，并且发送开始广播。
         * 当接收者接收到广播后，接收者将会获取音乐信息并更新UI。
         **/

        //const val SENDER = "SENDER"

        /* Player Broadcast */
        //const val SENDER_MAINPLAYER = "MAINPLAYER"
        // START
        const val PLAYER_BROADCAST_ONSTART = "app.skynight.musicplayer.player.onstart"
        // PAUSE
        const val PLAYER_BROADCAST_ONPAUSE = "app.skynight.musicplayer.player.onpause"
        // STOP
        const val PLAYER_BROADCAST_ONSTOP = "app.skynight.musicplayer.player.onstop"
        // CHANGE MUSIC
        const val PLAYER_BROADCAST_NEXT = "app.skynight.musicplayer.player.next"
        const val PLAYER_BROADCAST_LAST = "app.skynight.musicplayer.player.last"
        // PLAY TYPE
        const val PLAYER_BROADCAST_SINGLE = "app.skynight.musicplayer.player.single"
        const val PLAYER_BROADCAST_RANDOM = "app.skynight.musicplayer.player.random"
        const val PLAYER_BROADCAST_CYCLE = "app.skynight.musicplayer.player.cycle"
        // SEEK CHANGE
        //const val PLAYER_BROADCAST_ONSEEKCHANGE = "app.skynight.musicplayer.mainplayer.onseekchange"


        /* NotificationPlayer Broadcast */
        //const val SENDER_NOTIFICATIONPLAYER = "NOTIFICATIONPLAYER"
        // START
        //const val NOTIFICATION_BROADCAST_ONSTART = "app.skynight.musicplayer.notification.onstart"
        // PAUSE
        //const val NOTIFICATION_BROADCAST_ONPAUSE = "app.skynight.musicplayer.notification.onpause"
        // STOP
        //const val NOTIFICATION_BROADCAST_ONSTOP = "app.skynight.musicplayer.notification.onstop"
        // CHANGE MUSIC
        //const val NOTIFICATION_BROADCAST_NEXT = "app.skynight.musicplayer.notification.next"
        //const val NOTIFICATION_BROADCAST_LAST = "app.skynight.musicplayer.notification.last"


        /* SmallPlayer Broadcast */
        //const val SENDER_SMALLPLAYER = "SMALLPLAYER"
        // START
        //const val SMALLPLAYER_BROADCAST_ONSTART = "app.skynight.musicplayer.smallplayer.onstart"
        // PAUSE
        //const val SMALLPLAYER_BROADCAST_ONPAUSE = "app.skynight.musicplayer.smallplayer.onpause"
        // CHANGE MUSIC
        //const val SMALLPLAYER_BROADCAST_NEXT = "app.skynight.musicplayer.smallplayer.next"
        //const val SMALLPLAYER_BROADCAST_LAST = "app.skynight.musicplayer.smallplayer.last"

        /* Server Broad */
        // START
        const val SERVER_BROADCAST_ONSTART = "app.skynight.musicplayer.server.onstart"
        // PAUSE
        const val SERVER_BROADCAST_ONPAUSE = "app.skynight.musicplayer.server.onpause"
        // STOP
        const val SERVER_BROADCAST_ONSTOP = "app.skynight.musicplayer.server.onstop"
        // CHANGE MUSIC
        const val SERVER_BROADCAST_MUSICCHANGE = "app.skynight.musicplayer.server.onmusicchange"
        // PREPARING MUSIC LIST
        const val SERVER_BROADCAST_PREPAREDONE = "app.skynight.musicplayer.server.onpreparedone"

        val BroadcastSignalList = arrayListOf(
                /* MAIN PLAYER */
                PLAYER_BROADCAST_ONSTART, PLAYER_BROADCAST_ONSTOP, PLAYER_BROADCAST_ONPAUSE,
                PLAYER_BROADCAST_LAST, PLAYER_BROADCAST_NEXT,
                PLAYER_BROADCAST_SINGLE, PLAYER_BROADCAST_CYCLE, PLAYER_BROADCAST_RANDOM
                //PLAYER_BROADCAST_ONSEEKCHANGE
                /* NOTIFICATION PLAYER */
                //NOTIFICATION_BROADCAST_ONSTART, NOTIFICATION_BROADCAST_ONSTOP, NOTIFICATION_BROADCAST_ONPAUSE,
                //NOTIFICATION_BROADCAST_LAST, NOTIFICATION_BROADCAST_NEXT,
                /* SMALL PLAYER */
                //SMALLPLAYER_BROADCAST_ONSTART, SMALLPLAYER_BROADCAST_ONPAUSE,
                //SMALLPLAYER_BROADCAST_LAST, SMALLPLAYER_BROADCAST_NEXT
        )
    }
    init {
        throw Exception("InternalClassInitNotAllowed")
    }
}