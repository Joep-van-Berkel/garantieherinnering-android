package garantieherinnering.app.Data.DB

import Model.AankoopBonModel
import android.content.ContentValues
import android.content.Context
import garantieherinnering.app.Data.DataContract
import garantieherinnering.app.Data.DataDbHelper

class AankoopBonDB(context: Context) {

    private val dbHelper = DataDbHelper(context)

    fun insertAankoopBon(aankoopBon: AankoopBonModel): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DataContract.AankoopBon.COLUMN_NAME_AANKOOP_ID, aankoopBon.aankoopID)
            put(DataContract.AankoopBon.COLUMN_NAME_IMAGEPATH, aankoopBon.imagePath)
        }

        return db.insert(DataContract.AankoopBon.TABLE_NAME, null, values)
    }

    fun getAankoopBonById(aankoopID: Int): List<AankoopBonModel> {
        val db = dbHelper.readableDatabase
        val selection = "${DataContract.AankoopBon.COLUMN_NAME_AANKOOP_ID} = ?"
        val selectionArgs = arrayOf(aankoopID.toString())
        val cursor = db.query(
            DataContract.AankoopBon.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val aankoopBonList = mutableListOf<AankoopBonModel>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DataContract.AankoopBon.COLUMN_NAME_AANKOOP_ID))
                val imagePath = getString(getColumnIndexOrThrow(DataContract.AankoopBon.COLUMN_NAME_IMAGEPATH))
                aankoopBonList.add(AankoopBonModel(id, imagePath))
            }
        }
        cursor.close()
        return aankoopBonList
    }

    fun deleteAankoopBon(aankoopID: Int): Int {
        val db = dbHelper.writableDatabase
        val selection = "${DataContract.AankoopBon.COLUMN_NAME_AANKOOP_ID} = ?"
        val selectionArgs = arrayOf(aankoopID.toString())
        return db.delete(DataContract.AankoopBon.TABLE_NAME, selection, selectionArgs)
    }

    fun close() {
        dbHelper.close()
    }
}