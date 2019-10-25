package ui

import darkroom.FilmTypes
import darkroom.PrintSettings
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import java.io.File

object SettingsPannelProperties {
    var printsFolder = SimpleStringProperty(PrintSettings.folderToSave.canonicalPath)
        private set

    val filmType = SimpleObjectProperty(FilmTypes.BLACK_AND_WHITE)

    fun changePrintsLocation(newLocation: File) {
        PrintSettings.folderToSave = newLocation
        printsFolder.set(newLocation.canonicalPath)
    }
}