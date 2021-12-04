package sakuraba.saki.player.music.util

import android.graphics.BitmapFactory
import com.google.gson.JsonParser
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import sakuraba.saki.player.music.util.LyricUtil.decodeLine
import java.net.URL

object NetEaseUtil {

    private const val API = "https://music.163.com/api/"

    private const val LYRIC_PREFIX = "${API}song/lyric?lv=1&id="
    private val String.lyricURL get() = URL(LYRIC_PREFIX + this)

    private val String.lyricJsonStr get() =
        tryRun { (if ("https" in this) musicId else this).lyricURL.openStream().bufferedReader().use { it.readText() } }

    private val String.musicId get() = substring(indexOf("?id=") + 4, indexOf("&"))

    /**
     * Example JSON
     * {
     *      "sgc": true,
     *      "sfy": false,
     *      "qfy": false,
     *      "lrc": {
     *          "version": 7,
     *          "lyric": "[00:00.000] 作词 : 蛋筒\n[00:01.000] 作曲 : YoGHurt阿瑶\n[00:07.446]阿瑶：白纸上的字句\n[00:14.946][00:14.428]\n[00:09.445]又被风所扬起\n[00:11.195]我该以何种词汇去描述幕布后的你\n[00:15.194]祝一可：蝴蝶堕在美丽\n[00:21.945]\n[00:16.946]燃烬于你眼睛\n[00:18.946]一并扼住我本就浅薄的呼吸\n[00:22.445]阿瑶：正逢时闲\n[00:29.445]\n[00:24.446]听曲消遣\n[00:26.195]环绕耳边 是来人自称少年\n[00:29.946]祝一可：相交视线\n[00:37.150]\n[00:31.945]十二月雪\n[00:33.695]仍不知是 降下哪路神仙\n[00:37.650]阿瑶：升薄暮\n[00:41.150]\n[00:38.650]初起雾\n[00:39.650]你抿嘴挥手退步\n[00:41.650]我迷路\n[00:44.900]\n[00:42.649]做猎物\n[00:43.400]落入你的国度\n[00:45.400]祝一可：随草木\n[00:48.649]\n[00:46.400]一步步\n[00:51.650]\n[00:47.399]融入绚烂夺目\n[00:49.150]我沦为你信徒\n[00:52.400]阿瑶：我知这个世界变化万千\n[00:59.899]\n[00:55.900]他一言又一语\n[00:57.900]不断侵犯我空间\n[01:00.400]祝一可：可我甘愿陷入你的谎言\n[01:07.351]\n[01:03.399]你一言又一语\n[01:05.602]炽热的犹如火焰\n[01:08.101]阿瑶：你刻在浮世绘\n[01:15.039]\n[01:09.852]守着年年岁岁\n[01:12.289]祝一可：支撑多少摇摇欲坠\n[01:15.788]阿瑶：这时间会不会\n[01:22.788]\n[01:23.288]\n[01:17.789]忘记了谁是谁\n[01:19.788]祝一可：拼凑起零零碎碎\n[01:23.539]阿瑶：曾想过跨越山河\n[01:25.289]在相遇时定格\n[01:30.539]\n[01:27.288]奈何我飞蛾扑火\n[01:28.789]在天鹅处坠落\n[01:30.787]伤口在渐渐愈合\n[01:32.788]我南辕北辙又重蹈覆辙\n[01:38.485]\n[01:34.788]你懂得抉择教我如何自得其乐\n[01:37.235]想我快乐\n[01:38.735]祝一可：正逢时闲\n[01:45.735]\n[01:40.485]听曲消遣\n[01:42.234]环绕耳边 是来人自称少年\n[01:46.234]阿瑶：相交视线\n[01:53.235]\n[01:47.983]十二月雪\n[01:49.984]仍不知是 降下哪路神仙\n[01:53.734]祝一可：升薄暮\n[01:57.485]\n[01:54.984]初起雾\n[01:55.984]你抿嘴挥手退步\n[01:57.735]我迷路\n[02:01.234]\n[01:58.983]做猎物\n[01:59.985]落入你的国度\n[02:01.735]阿瑶：随草木\n[02:05.188]\n[02:02.735]一步步\n[02:08.437]\n[02:03.734]融入绚烂夺目\n[02:05.438]我沦为你信徒\n[02:08.938]祝一可：我知这个世界变化万千\n[02:16.188]\n[02:12.186]他一言又一语\n[02:14.188]不断侵犯我空间\n[02:16.688]阿瑶：可我甘愿陷入你的谎言\n[02:24.187]\n[02:19.687]你一言又一语\n[02:21.687]炽热的犹如火焰\n[02:24.687]阿瑶：你刻在浮世绘\n[02:31.937]\n[02:26.437]守着年年岁岁\n[02:28.188]祝一可：支撑多少摇摇欲坠\n[02:32.437]阿瑶：这时间会不会\n[02:39.188]\n[02:33.937]忘记了谁是谁\n[02:35.688]祝一可：拼凑起零零碎碎\n[02:39.937]阿瑶：雪从枝头落下\n[02:47.146]\n[02:42.646]祝一可：融化出盛夏\n[02:44.647]不过一刹那\n[02:47.397]阿瑶：你在门前作画\n[02:54.397]\n[02:50.396]祝一可：我听雨落下\n[02:52.396]我听雨落下\n[02:54.646]祝一可（阿瑶）正逢时闲（这声音过于浮躁嘈杂）\n[03:02.397]\n[02:56.646]听曲消遣（不及你随心一句话）\n[02:58.647]环绕耳边 是来人自称少年（在广袤无界之地 马蹄声儿踢踏）\n[03:02.397]相交视线\n[03:09.146]\n[03:09.647]\n[03:04.396]十二月雪\n[03:06.146]降你落于凡间\n[03:10.345]阿瑶：我知这个世界变化万千\n[03:17.345]\n[03:13.094]他一言又一语\n[03:15.094]不断侵犯我空间\n[03:17.594]祝一可：可我甘愿陷入你的谎言\n[03:24.595]\n[03:20.595]你一言又一语\n[03:22.845]炽热的犹如火焰\n[03:25.094]阿瑶：你刻在浮世绘\n[03:32.844]\n[03:27.345]守着年年岁岁\n[03:29.343]祝一可：支撑多少摇摇欲坠\n[03:35.757]\n[03:33.094]阿瑶：这时间永不会\n[03:35.058]忘记了你是谁\n[03:36.944]祝一可：拼凑起零零碎碎\n[03:44.944]支撑多少摇摇欲坠\n[03:55.639]正逢时闲\n[03:57.640]听曲消遣\n[03:59.367]相交视线 十二月雪 拼凑起零零碎碎\n"
     *      },
     *      "code": 200
     * }
     **/
    private val String.lyricStr get() = lyricJsonStr?.run {
        JsonParser.parseString(this).asJsonObject.get("lrc").asJsonObject.get("lyric").asString
    }

    private val String.lyricList get() = lyricStr?.split('\n')?.toMutableList() as ArrayList?

    private val String.lyric get(): Lyric {
        val lyric = Lyric()
        val lyricList = lyricList ?: return lyric
        lyricList.forEach { line ->
            line.decodeLine(lyric.lyricList, lyric.timeList)
        }
        return lyric
    }

    val String.netEaseLyric get() = lyric

    private val String.DETAIL_STR get() = "${API}song/detail?id=$this&ids=%5B$this%5D"
    private val String.detailURL get() = URL(DETAIL_STR)

    private val String.coverJsonStr get() =
        tryRun { (if ("http" in this ) musicId else this).detailURL.openStream().bufferedReader().use { it.readText()} }

    private val String.coverURLStr get() = coverJsonStr?.run {
        JsonParser.parseString(this).asJsonObject.get("songs").asJsonArray.first().asJsonObject.get("album").asJsonObject.get("picUrl").asString
    }

    private val String.netEaseCoverBitmap get() =
        coverURLStr?.tryRun { URL(this).openStream().use { BitmapFactory.decodeStream(it) } }

    val String.netEaseCover get() = netEaseCoverBitmap

}