package garantieherinnering.app.Enumeration

enum class DagenGarantieEnum(val days: Int, val description: String) {
    NUL_DAGEN(0, "Geen garantie"),
    VEERTIEN_DAGEN(14, "14 dagen"),
    ZES_MAANDEN(180, "6 maanden"),
    EEN_JAAR(365, "1 jaar"),
    TWEE_JAAR(730, "2 jaar"),
    DRIE_JAAR(1095, "3 jaar"),
    VIER_JAAR(1460, "4 jaar"),
    VIJF_JAAR(1825, "5 jaar"),
    ZES_JAAR(2190, "6 jaar"),
    ZEVEN_JAAR(2555, "7 jaar"),
    ACHT_JAAR(2920, "8 jaar"),
    NEGEN_JAAR(3285, "9 jaar"),
    TIEN_JAAR(3650, "10 jaar");

    companion object {
        fun fromDescription(description: String): DagenGarantieEnum {
            return values().first { it.description == description }
        }
    }
}