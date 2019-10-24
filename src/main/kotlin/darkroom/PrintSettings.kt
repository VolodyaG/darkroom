package darkroom

import java.io.File

object PrintSettings {
    var typeOfFilm = FilmTypes.COLOR_NEGATIVE
    var folderToSave = File("./prints")
    var rotationAngle = 0
    var brightness = 0.0
    var contrast = 0.0
}