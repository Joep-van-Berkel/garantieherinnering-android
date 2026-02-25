package garantieherinnering.app.Data.DB

import Model.AankoopModel
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.icu.text.SimpleDateFormat
import garantieherinnering.app.Data.DataContract
import garantieherinnering.app.Data.DataDbHelper
import java.sql.Date
import java.util.Locale

class AankoopDB(context: Context) {

    private val dbHelper = DataDbHelper(context)

    fun insertAankoop(aankoop: AankoopModel): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DataContract.Aankoop.COLUMN_NAME_PRODUCTNAAM, aankoop.productNaam)
            put(DataContract.Aankoop.COLUMN_NAME_CATEGORIE, aankoop.categorie)
            put(DataContract.Aankoop.COLUMN_NAME_AANKOOPBEDRAG, aankoop.aankoopBedrag)
            put(DataContract.Aankoop.COLUMN_NAME_AANKOOPDATUM, aankoop.aankoopDatum.time)
            put(DataContract.Aankoop.COLUMN_NAME_DAGENGARANTIE, aankoop.dagenGarantie)
        }

        return db.insert(DataContract.Aankoop.TABLE_NAME, null, values)
    }

    fun getAllAankopen(): List<AankoopModel> {
        val aankoopList = mutableListOf<AankoopModel>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Aankoop", null)
        if (cursor.moveToFirst()) {
            do {
                val aankoop = AankoopModel(
                    aankoopID = cursor.getInt(cursor.getColumnIndexOrThrow("Aankoop_ID")),
                    productNaam = cursor.getString(cursor.getColumnIndexOrThrow("ProductNaam")),
                    categorie = cursor.getString(cursor.getColumnIndexOrThrow("Categorie")),
                    aankoopBedrag = cursor.getDouble(cursor.getColumnIndexOrThrow("AankoopBedrag")),
                    aankoopDatum = Date(cursor.getLong(cursor.getColumnIndexOrThrow("AankoopDatum"))),
                    dagenGarantie = cursor.getInt(cursor.getColumnIndexOrThrow("DagenGarantie"))
                )
                aankoopList.add(aankoop)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return aankoopList
    }
    fun getAankoopById(aankoopID: Int): AankoopModel? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DataContract.Aankoop.TABLE_NAME,
            null,
            "${DataContract.Aankoop.COLUMN_NAME_AANKOOP_ID} = ?",
            arrayOf(aankoopID.toString()),
            null,
            null,
            null
        )

        var aankoop: AankoopModel? = null
        if (cursor.moveToFirst()) {
            aankoop = AankoopModel(
                aankoopID = cursor.getInt(cursor.getColumnIndexOrThrow(DataContract.Aankoop.COLUMN_NAME_AANKOOP_ID)),
                productNaam = cursor.getString(cursor.getColumnIndexOrThrow(DataContract.Aankoop.COLUMN_NAME_PRODUCTNAAM)),
                categorie = cursor.getString(cursor.getColumnIndexOrThrow(DataContract.Aankoop.COLUMN_NAME_CATEGORIE)),
                aankoopBedrag = cursor.getDouble(cursor.getColumnIndexOrThrow(DataContract.Aankoop.COLUMN_NAME_AANKOOPBEDRAG)),
                aankoopDatum = Date(cursor.getLong(cursor.getColumnIndexOrThrow(DataContract.Aankoop.COLUMN_NAME_AANKOOPDATUM))),
                dagenGarantie = cursor.getInt(cursor.getColumnIndexOrThrow(DataContract.Aankoop.COLUMN_NAME_DAGENGARANTIE))
            )
        }
        cursor.close()
        db.close()
        return aankoop
    }

    fun deleteAankoop(aankoopID: Int): Int {
        val db = dbHelper.writableDatabase
        val selection = "${DataContract.Aankoop.COLUMN_NAME_AANKOOP_ID} = ?"
        val selectionArgs = arrayOf(aankoopID.toString())
        return db.delete(DataContract.Aankoop.TABLE_NAME, selection, selectionArgs)
    }

    fun close() {
        dbHelper.close()
    }
}