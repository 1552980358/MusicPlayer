package app.skynight.musicplayer.activity

import android.os.Bundle
import android.os.Environment
import app.skynight.musicplayer.R
import app.skynight.musicplayer.base.BaseSmallPlayerActivity
import app.skynight.musicplayer.util.NCMDumper
import app.skynight.musicplayer.util.QMCDumper
import app.skynight.musicplayer.util.log
import app.skynight.musicplayer.util.makeToast
import java.io.File
import java.lang.Exception
import kotlinx.android.synthetic.main.activity_findencryptedmusic.*

/**
 * @File    : FindEncryptedMusicActivity
 * @Author  : 1552980358
 * @Date    : 20 Aug 2019
 * @TIME    : 4:22 PM
 **/

@Suppress("DEPRECATION")
class FindEncryptedMusicActivity: BaseSmallPlayerActivity() {
    private var finished = 0
    private var done = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findencryptedmusic)
        setPlayerActivityFitsSystemWindows()
        Thread {
            try {
                runOnUiThread { makeToast("我自己没qmc0/qmc3/qmcflac/ncm文件, 搜索不稳定还请提供文件让我测试") }
                Environment.getExternalStorageDirectory().absoluteFile.listFiles()!!.forEach { searchFile(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            done = true
            runOnUiThread { content.text = "找到共${finished}首歌曲。\n需要等待Android媒体库刷新后才能在本应用看得到\n未来会增加完成后立刻要求媒体库扫描文件" }
        }.start()
        toolbar.apply {
            setSupportActionBar(this)
            setNavigationOnClickListener {
                if (done) {
                    finish()
                }
            }
        }
    }

    private fun searchFile(file: File) {
        log("file", file.path)
        if (file.isFile) {
            if (file.endsWith("qmc0") || file.endsWith("qmc3") || file.endsWith("qmcflac")) {
                try {
                    if (QMCDumper.dump(file, Environment.getExternalStorageDirectory().absolutePath + File.separator + "Music")) {
                        finished++
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return
            }
            if (file.endsWith("ncm")) {
                try {
                    val no = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + "Music").listFiles()!!.size
                    NCMDumper.dump(file.toString())
                    if (File(Environment.getExternalStorageDirectory().absolutePath + File.separator + "Music").listFiles()!!.size > no) {
                        finished++
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return
            }
            return
        }

        for (i in file.listFiles()!!) {
            if (i.isFile) {
                log("file", i.path)
                if (i.endsWith("qmc0") || i.endsWith("qmc3") || i.endsWith("qmcflac")) {
                    try {
                        if (QMCDumper.dump(i, Environment.getExternalStorageDirectory().absolutePath + File.separator + "Music")) {
                            finished++
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    continue
                }
                if (i.endsWith("ncm")) {
                    try {
                        val no = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + "Music").listFiles()!!.size
                        NCMDumper.dump(i.toString())
                        if (File(Environment.getExternalStorageDirectory().absolutePath + File.separator + "Music").listFiles()!!.size > no) {
                            finished++
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    continue
                }
                continue
            }
            searchFile(i)
        }
    }
}