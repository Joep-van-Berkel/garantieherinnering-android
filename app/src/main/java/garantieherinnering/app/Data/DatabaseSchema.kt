package garantieherinnering.app.Data

object DatabaseSchema {

    const val SQL_CREATE_AANKOOP =
        "CREATE TABLE ${DataContract.Aankoop.TABLE_NAME} (" +
                "${DataContract.Aankoop.COLUMN_NAME_AANKOOP_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${DataContract.Aankoop.COLUMN_NAME_PRODUCTNAAM} TEXT, " +
                "${DataContract.Aankoop.COLUMN_NAME_CATEGORIE} TEXT, " +
                "${DataContract.Aankoop.COLUMN_NAME_AANKOOPBEDRAG} REAL, " +
                "${DataContract.Aankoop.COLUMN_NAME_AANKOOPDATUM} TEXT, " +
                "${DataContract.Aankoop.COLUMN_NAME_DAGENGARANTIE} INTEGER)"

    const val SQL_CREATE_AANKOOPBON =
        "CREATE TABLE ${DataContract.AankoopBon.TABLE_NAME} (" +
                "${DataContract.AankoopBon.COLUMN_NAME_AANKOOP_ID} INTEGER, " +
                "${DataContract.AankoopBon.COLUMN_NAME_IMAGEPATH} TEXT, " +
                "PRIMARY KEY(${DataContract.AankoopBon.COLUMN_NAME_AANKOOP_ID}, ${DataContract.AankoopBon.COLUMN_NAME_IMAGEPATH}), " +
                "FOREIGN KEY(${DataContract.AankoopBon.COLUMN_NAME_AANKOOP_ID}) REFERENCES ${DataContract.Aankoop.TABLE_NAME}(${DataContract.Aankoop.COLUMN_NAME_AANKOOP_ID}))"

    const val SQL_CREATE_GARANTIEHERINNERING =
        "CREATE TABLE ${DataContract.GarantieHerinnering.TABLE_NAME} (" +
                "${DataContract.GarantieHerinnering.COLUMN_NAME_AANKOOP_ID} INTEGER, " +
                "${DataContract.GarantieHerinnering.COLUMN_NAME_HERINNERINGSDATUM} TEXT, " +
                "PRIMARY KEY(${DataContract.GarantieHerinnering.COLUMN_NAME_AANKOOP_ID}, ${DataContract.GarantieHerinnering.COLUMN_NAME_HERINNERINGSDATUM}), " +
                "FOREIGN KEY(${DataContract.GarantieHerinnering.COLUMN_NAME_AANKOOP_ID}) REFERENCES ${DataContract.Aankoop.TABLE_NAME}(${DataContract.Aankoop.COLUMN_NAME_AANKOOP_ID}))"

    const val SQL_DELETE_AANKOOP = "DROP TABLE IF EXISTS ${DataContract.Aankoop.TABLE_NAME}"
    const val SQL_DELETE_AANKOOPBON = "DROP TABLE IF EXISTS ${DataContract.AankoopBon.TABLE_NAME}"
    const val SQL_DELETE_GARANTIEHERINNERING = "DROP TABLE IF EXISTS ${DataContract.GarantieHerinnering.TABLE_NAME}"
}