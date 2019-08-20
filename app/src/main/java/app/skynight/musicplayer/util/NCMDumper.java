package app.skynight.musicplayer.util;

/**
 * @File : NCMDumper
 * @Author : 1552980358
 * @Date : 20 Aug 2019
 * @TIME : 3:44 PM
 **/
public class NCMDumper {
    static {
        System.loadLibrary("ncm");
    }
    public static native String dump(String ncmFilePath);
}
