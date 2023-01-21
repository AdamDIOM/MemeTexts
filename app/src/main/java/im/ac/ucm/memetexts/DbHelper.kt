package im.ac.ucm.memetexts

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context): SQLiteOpenHelper(context, "dbMemes", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val qry = """CREATE TABLE 'tblSavedMemes'(
            'Id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            'Title' NVARCHAR(100) NOT NULL,
            'PostLink' NVARCHAR(100) NOT NULL,
            'ImgUrl' NVARCHAR(100) NOT NULL
            )""".trimMargin()
        db?.execSQL(qry)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}