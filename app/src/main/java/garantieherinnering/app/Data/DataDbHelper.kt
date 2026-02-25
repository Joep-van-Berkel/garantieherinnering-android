package garantieherinnering.app.Data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(DatabaseSchema.SQL_CREATE_AANKOOP)
        db?.execSQL(DatabaseSchema.SQL_CREATE_AANKOOPBON)
        db?.execSQL(DatabaseSchema.SQL_CREATE_GARANTIEHERINNERING)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DatabaseSchema.SQL_DELETE_GARANTIEHERINNERING)
        db?.execSQL(DatabaseSchema.SQL_DELETE_AANKOOPBON)
        db?.execSQL(DatabaseSchema.SQL_DELETE_AANKOOP)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DatabaseSchema.SQL_DELETE_GARANTIEHERINNERING)
        db?.execSQL(DatabaseSchema.SQL_DELETE_AANKOOPBON)
        db?.execSQL(DatabaseSchema.SQL_DELETE_AANKOOP)
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "garantieherinnering.db"
    }
}