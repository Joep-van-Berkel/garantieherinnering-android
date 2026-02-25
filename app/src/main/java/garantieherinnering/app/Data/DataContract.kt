package garantieherinnering.app.Data

import android.provider.BaseColumns

object DataContract{

    object Aankoop : BaseColumns{
        const val TABLE_NAME = "Aankoop"
        const val COLUMN_NAME_AANKOOP_ID = "Aankoop_ID"
        const val COLUMN_NAME_PRODUCTNAAM = "ProductNaam"
        const val COLUMN_NAME_CATEGORIE = "Categorie"
        const val COLUMN_NAME_AANKOOPBEDRAG = "AankoopBedrag"
        const val COLUMN_NAME_AANKOOPDATUM = "AankoopDatum"
        const val COLUMN_NAME_DAGENGARANTIE = "DagenGarantie"
    }

    object AankoopBon : BaseColumns{
        const val TABLE_NAME = "AankoopBon"
        const val COLUMN_NAME_AANKOOP_ID = "Aankoop_ID"
        const val COLUMN_NAME_IMAGEPATH = "ImagePath"
    }

    object GarantieHerinnering : BaseColumns{
        const val TABLE_NAME = "GarantieHerinnering"
        const val COLUMN_NAME_AANKOOP_ID = "Aankoop_ID"
        const val COLUMN_NAME_HERINNERINGSDATUM = "HerinneringsDatum"
    }
}