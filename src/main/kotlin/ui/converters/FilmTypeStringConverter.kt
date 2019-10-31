package ui.converters

import darkroom.FilmTypes
import javafx.util.StringConverter

class FilmTypeStringConverter : StringConverter<FilmTypes>() {
    override fun toString(filmType: FilmTypes): String {
        return filmType.displayName
    }

    override fun fromString(string: String): FilmTypes {
        return FilmTypes.fromString(string)
    }
}