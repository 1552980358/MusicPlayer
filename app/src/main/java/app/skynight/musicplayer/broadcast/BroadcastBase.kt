package app.skynight.musicplayer.broadcast

import app.skynight.musicplayer.base.InitNotAllowedException

/**
 * @FILE:   BroadcastList
 * @AUTHOR: 1552980358
 * @DATE:   19 Jul 2019
 * @TIME:   8:50 AM
 **/

@Suppress("SpellCheckingInspection")
class BroadcastBase private constructor() {
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

        const val BROADCAST_APPLICATION_RESTART = "app.skynight.musicplayer.application.restart"

        const val BROADCAST_INTENT_PLAYLIST = "PLAYLIST"
        const val BROADCAST_INTENT_MUSIC = "MUSIC"

        /* Player Broadcast */
        // START
        const val CLIENT_BROADCAST_ONSTART = "app.skynight.musicplayer.player.onstart"
        // PAUSE
        const val CLIENT_BROADCAST_ONPAUSE = "app.skynight.musicplayer.player.onpause"
        // STOP
        const val CLIENT_BROADCAST_ONSTOP = "app.skynight.musicplayer.player.onstop"
        // CHANGE MUSIC
        const val CLIENT_BROADCAST_NEXT = "app.skynight.musicplayer.player.next"
        const val CLIENT_BROADCAST_LAST = "app.skynight.musicplayer.player.last"
        const val CLIENT_BROADCAST_CHANGE = "app.skynight.musicplayer.player.change"
        // PLAY TYPE
        const val CLIENT_BROADCAST_SINGLE = "app.skynight.musicplayer.player.single"
        const val CLIENT_BROADCAST_RANDOM = "app.skynight.musicplayer.player.random"
        const val CLIENT_BROADCAST_CYCLE = "app.skynight.musicplayer.player.cycle"
        // SEEK CHANGE
        //const val CLIENT_BROADCAST_ONSEEKCHANGE = "app.skynight.musicplayer.mainplayer.onseekchange"


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
        //const val SMALLCLIENT_BROADCAST_ONSTART = "app.skynight.musicplayer.smallplayer.onstart"
        // PAUSE
        //const val SMALLCLIENT_BROADCAST_ONPAUSE = "app.skynight.musicplayer.smallplayer.onpause"
        // CHANGE MUSIC
        //const val SMALLCLIENT_BROADCAST_NEXT = "app.skynight.musicplayer.smallplayer.next"
        //const val SMALLCLIENT_BROADCAST_LAST = "app.skynight.musicplayer.smallplayer.last"

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
        @Deprecated("")
        const val SERVER_BROADCAST_PREPAREDONE = "app.skynight.musicplayer.server.onpreparedone"

        val BroadcastSignalList = arrayListOf(
                /* CLIENT */
                CLIENT_BROADCAST_ONSTART, CLIENT_BROADCAST_ONSTOP, CLIENT_BROADCAST_ONPAUSE,
                CLIENT_BROADCAST_LAST, CLIENT_BROADCAST_NEXT,
                CLIENT_BROADCAST_SINGLE, CLIENT_BROADCAST_CYCLE, CLIENT_BROADCAST_RANDOM,
                CLIENT_BROADCAST_CHANGE
                //CLIENT_BROADCAST_ONSEEKCHANGE
                /* NOTIFICATION PLAYER */
                //NOTIFICATION_BROADCAST_ONSTART, NOTIFICATION_BROADCAST_ONSTOP, NOTIFICATION_BROADCAST_ONPAUSE,
                //NOTIFICATION_BROADCAST_LAST, NOTIFICATION_BROADCAST_NEXT,
                /* SMALL PLAYER */
                //SMALLCLIENT_BROADCAST_ONSTART, SMALLCLIENT_BROADCAST_ONPAUSE,
                //SMALLCLIENT_BROADCAST_LAST, SMALLCLIENT_BROADCAST_NEXT
        )
    }
    init {
        throw InitNotAllowedException("BroadcastBase")
    }
}