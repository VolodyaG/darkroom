package ui

import darkroom.PrintSettings
import javafx.beans.property.SimpleStringProperty
import java.io.File

object UiProperties {
    var printsFolder = SimpleStringProperty(PrintSettings.folderToSave.canonicalPath)
        private set

    fun changePrintsLocation(newLocation: File) {
        PrintSettings.folderToSave = newLocation
        printsFolder.set(newLocation.canonicalPath)
    }
}