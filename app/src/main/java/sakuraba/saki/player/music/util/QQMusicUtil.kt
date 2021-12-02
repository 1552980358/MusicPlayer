package sakuraba.saki.player.music.util

import android.util.Base64
import android.util.Base64.DEFAULT
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly
import sakuraba.saki.player.music.util.LyricUtil.decodeLine
import java.net.HttpURLConnection
import java.net.URL

object QQMusicUtil {

    private const val LYRIC_PREFIX = "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric.fcg" +
            "?g_tk=5381&format=json&inCharset=utf-8&outCharset=utf-8&platform=h5&"

    private val String.url get() = LYRIC_PREFIX + this

    private val String.musicId get(): String? {
        return if ("songmid" in this) {
            // For
            // https://i.y.qq.com/v8/playsong.html?ADTAG=cbshare&_wv=1&appshare=android_qq&appsongtype=1&appversion=11000106&channelId=10036163&hosteuin=oK4kowEFoeokNn%2A%2A&openinqqmusic=1&platform=11&songmid=000t16oL2q4Z85&type=0
            "songmid=" + substring(indexOf("songmid=") + 8, indexOf("&type"))
        } else {
            // For
            // https://i.y.qq.com/v8/playsong.html?ADTAG=erweimashare&channelId=10036163&openinqqmusic=1&songid=273699826&songtype=0&source=wkframe#wechat_redirect
            "musicid=" + substring(indexOf("songid=") + 7, indexOf("&songtype"))
        }
    }

    /**
     * Example: Convert "https://c.y.qq.com/base/fcgi-bin/u?__=r3aBVsbg4EBP", and get "Location" header:
     *          "https://i.y.qq.com/v8/playsong.html?ADTAG=cbshare&_wv=1&appshare=android_qq&appsongtype=1&appversion=11000106&channelId=10036163&hosteuin=oK4kowEFoeokNn%2A%2A&openinqqmusic=1&platform=11&songmid=000t16oL2q4Z85&type=0"; or
     *          "https://i.y.qq.com/v8/playsong.html?ADTAG=erweimashare&channelId=10036163&openinqqmusic=1&songid=273699826&songtype=0&source=wkframe#wechat_redirect"
     */
    private val String.musicLocation get(): String? {
        var location: String? = null
        tryOnly {
            (URL(this).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                instanceFollowRedirects = false
                setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36")
                setRequestProperty("Accept", "*/*")
                setRequestProperty("Referer", "https://y.qq.com/portal/player.html")
                setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8")
                setRequestProperty("Host", "c.y.qq.com")
                location = getHeaderField("Location")
            }.disconnect()
        }
        return location
    }

    private val String.lyricResponse: String? get() {
        var response: String? = null
        tryOnly {
            (URL(this).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36")
                setRequestProperty("Accept", "*/*")
                setRequestProperty("Referer", "https://y.qq.com/portal/player.html")
                setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8")
                setRequestProperty("Host", "c.y.qq.com")
                inputStream.bufferedReader().use {
                    response = it.readText()
                }
            }.disconnect()
        }
        return response
    }

    private val String.lyricJson get() = JsonParser.parseString(substring(indexOf('{'), lastIndexOf(')'))).asJsonObject

    private val JsonObject.lyricBase64 get() = get("lyric").asString

    /**
     * Example: https://c.y.qq.com/base/fcgi-bin/u?__=r3aBVsbg4EBP; or
     *          Braska《武学秘笈》https://c.y.qq.com/base/fcgi-bin/u?__=r3aBVsbg4EBP @QQ音乐 ;
     *          and get "https://c.y.qq.com/base/fcgi-bin/u?__=r3aBVsbg4EBP"
     **/
    private val String.link get(): String {
        if (startsWith("https"))
            return this
        return substring(indexOf("https"), indexOf(" @"))
    }

    private val String.lyricStr get() = String(Base64.decode(this, DEFAULT))

    private val String.lyric: Lyric get() {
        val lyric = Lyric()
        link.musicLocation?.musicId?.url?.lyricResponse?.lyricJson?.lyricBase64?.lyricStr?.split('\n')?.forEach { line ->
            line.decodeLine(lyric.lyricList, lyric.timeList)
        }
        return lyric
    }

    val String.qqMusic get() = lyric

}