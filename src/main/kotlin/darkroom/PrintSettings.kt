package darkroom

import FilmTypes
import java.io.File

object PrintSettings {
    var typeOfFilm = FilmTypes.NEGATIVE
    var folderToSave = File("./prints")
    var rotationAngle = 0
}