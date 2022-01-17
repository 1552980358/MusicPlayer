package sakuraba.saki.player.music.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import sakuraba.saki.player.music.ui.webDav.util.WebDavData

class WebDavDatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "WebDavDatabase.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_WEB_DAV = "web_dav"
        private const val KEY_WEB_DAV_NAME = "name"
        private const val KEY_WEB_DAV_URL = "url"
        private const val KEY_WEB_DAV_USERNAME = "username"
        private const val KEY_WEB_DAV_PASSWORD = "password"
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(
            "create table if not exists $TABLE_WEB_DAV(" +
                "$KEY_WEB_DAV_NAME text not null, " +
                "$KEY_WEB_DAV_URL text not null, " +
                "$KEY_WEB_DAV_USERNAME text not null, " +
                "$KEY_WEB_DAV_PASSWORD text not null, " +
                "primary key ( $KEY_WEB_DAV_NAME )" +
                ")"
        )
    }

    fun insertWebDavInfo(webDavData: WebDavData) = writableDatabase.apply {
        insert(TABLE_WEB_DAV, null, ContentValues().apply {
            webDavData.apply {
                put(KEY_WEB_DAV_NAME, name)
                put(KEY_WEB_DAV_URL, url)
                put(KEY_WEB_DAV_USERNAME, username)
                put(KEY_WEB_DAV_PASSWORD, password)
            }
        })
    }.close()

    fun queryWebDavInfo(arrayList: ArrayList<WebDavData>) {
        readableDatabase.apply {
            rawQuery("select * from $TABLE_WEB_DAV", null).apply {
                if (!moveToFirst()) {
                    @Suppress("LABEL_NAME_CLASH")
                    return@apply
                }
                do {
                    arrayList.add(
                        WebDavData(
                            getString(getColumnIndexOrThrow(KEY_WEB_DAV_NAME)),
                            getString(getColumnIndexOrThrow(KEY_WEB_DAV_URL)),
                            getString(getColumnIndexOrThrow(KEY_WEB_DAV_USERNAME)),
                            getString(getColumnIndexOrThrow(KEY_WEB_DAV_PASSWORD))
                        )
                    )
                } while (moveToNext())
            }.close()
        }.close()
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) = Unit

}