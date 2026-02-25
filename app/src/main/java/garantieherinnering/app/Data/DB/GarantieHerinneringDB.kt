package garantieherinnering.app.Data.DB

import android.content.ContentValues
import android.content.Context
import garantieherinnering.app.Data.DataContract
import garantieherinnering.app.Data.DataDbHelper
import Model.GarantieHerinneringModel
import java.util.Date

class GarantieHerinneringDB(context: Context) {

    private val dbHelper = DataDbHelper(context)

    fun insertGarantieHerinnering(garantieHerinnering: GarantieHerinneringModel): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DataContract.GarantieHerinnering.COLUMN_NAME_AANKOOP_ID, garantieHerinnering.aankoopID)
            put(DataContract.GarantieHerinnering.COLUMN_NAME_HERINNERINGSDATUM, garantieHerinnering.herinneringsDatum.time)
        }

        return db.insert(DataContract.GarantieHerinnering.TABLE_NAME, null, values)
    }

    fun getHerinneringDatesByAankoopId(aankoopID: Int): List<Date> {
        val dates = mutableListOf<Date>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DataContract.GarantieHerinnering.TABLE_NAME,
            arrayOf(DataContract.GarantieHerinnering.COLUMN_NAME_HERINNERINGSDATUM),
            "${DataContract.GarantieHerinnering.COLUMN_NAME_AANKOOP_ID} = ?",
            arrayOf(aankoopID.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val dateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DataContract.GarantieHerinnering.COLUMN_NAME_HERINNERINGSDATUM))
                dates.add(Date(dateMillis))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return dates
    }

    fun deleteGarantieHerinnering(aankoopID: Int): Int {
        val db = dbHelper.writableDatabase
        val selection = "${DataContract.GarantieHerinnering.COLUMN_NAME_AANKOOP_ID} = ?"
        val selectionArgs = arrayOf(aankoopID.toString())
        return db.delete(DataContract.GarantieHerinnering.TABLE_NAME, selection, selectionArgs)
    }

    fun deleteHerinneringDate(aankoopID: Int, herinneringsDatum: Date): Int {
        val db = dbHelper.writableDatabase
        val selection = "${DataContract.GarantieHerinnering.COLUMN_NAME_AANKOOP_ID} = ? AND ${DataContract.GarantieHerinnering.COLUMN_NAME_HERINNERINGSDATUM} = ?"
        val selectionArgs = arrayOf(aankoopID.toString(), herinneringsDatum.time.toString())
        return db.delete(DataContract.GarantieHerinnering.TABLE_NAME, selection, selectionArgs)
    }

    fun addHerinneringDate(aankoopID: Int, herinneringsDatum: Date): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DataContract.GarantieHerinnering.COLUMN_NAME_AANKOOP_ID, aankoopID)
            put(DataContract.GarantieHerinnering.COLUMN_NAME_HERINNERINGSDATUM, herinneringsDatum.time)
        }
        return db.insert(DataContract.GarantieHerinnering.TABLE_NAME, null, values)
    }

    fun close() {
        dbHelper.close()
    }
}