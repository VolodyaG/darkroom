package darkroom

enum class FilmTypes(val displayName: String) {
    COLOR_NEGATIVE("Color Negative"),
    BLACK_AND_WHITE("Black and White"),
    POSITIVE("Positive");

    companion object {
        fun fromString(str: String): FilmTypes {
            return values().first { it.displayName == str }
        }
    }
}