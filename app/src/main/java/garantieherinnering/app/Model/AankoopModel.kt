package Model

import java.util.Date

data class AankoopModel (

    val aankoopID: Int,
    val productNaam: String,
    val categorie: String,
    val aankoopBedrag: Double,
    val aankoopDatum: Date,
    val dagenGarantie: Int
)
