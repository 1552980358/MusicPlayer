package app.github1552980358.android.musicplayer.base

import android.annotation.SuppressLint

/**
 * @file    : [SystemUtil]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/13
 * @time    : 17:18
 **/

interface SystemUtil {
    
    /**
     * [isMiUi12]
     * @return [Boolean]
     * @author 1552980358
     * @since 0.1
     **/
    fun isMiUi12(): Boolean {
        return getSystemProperty("ro.miui.ui.version.name") == "V12"
    }
    
    /**
     * [getSystemProperty]
     * @param key [String]
     * @return [String]?
     * @author 1552980358
     * @since 0.1
     **/
    @SuppressLint("PrivateApi")
    fun getSystemProperty(key: String): String? {
        return Class.forName("android.os.SystemProperties")
            .getMethod("get", String::class.java).invoke(null, key) as String?
    }
    
}