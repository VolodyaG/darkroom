package ui

import darkroom.FilmTypes
import darkroom.PrintSettings
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import java.io.File

object SettingsPannelProperties {
    var printsFolder = SimpleStringProperty(PrintSettings.folderToSave.canonicalPath)
        private set

    val filmType = SimpleObjectProperty(FilmTypes.BLACK_AND_WHITE)

    val rotation = SimpleDoubleProperty(0.0)

    val brightness = SimpleDoubleProperty(0.0)
    val contrast = SimpleDoubleProperty(0.0)

    val lowLumLevel = SimpleDoubleProperty(0.0)
    val highLumLevel = SimpleDoubleProperty(1.0)

    fun changePrintsLocation(newLocation: File) {
        PrintSettings.folderToSave = newLocation
        printsFolder.set(newLocation.canonicalPath)
    }
}