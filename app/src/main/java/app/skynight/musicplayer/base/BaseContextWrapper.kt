package app.skynight.musicplayer.base

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

/**
 * @File    : BaseContextWrapper
 * @Author  : 1552980358
 * @Date    : 26 Aug 2019
 * @TIME    : 2:52 PM
 **/

class BaseContextWrapper private constructor(base: Context): ContextWrapper(base){
    companion object {
        fun getBaseContextWrapper(context: Context, locale: Locale): BaseContextWrapper {
            val resources = context.resources
            val configuration = Configuration(resources.configuration)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocale(locale)
                val localeList = LocaleList(locale)
                LocaleList.setDefault(localeList)
                configuration.setLocales(localeList)
                return BaseContextWrapper(context.createConfigurationContext(configuration))
            }
            configuration.setLocale(locale)
            @Suppress("DEPRECATION")
            resources.updateConfiguration(configuration, resources.displayMetrics)
            return BaseContextWrapper(context.createConfigurationContext(configuration))
        }
    }
}