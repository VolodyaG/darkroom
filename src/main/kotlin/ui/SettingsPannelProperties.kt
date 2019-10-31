package ui

import darkroom.FilmTypes
import darkroom.PrintSettings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import java.io.File

// TODO fix naming
object SettingsPannelProperties {
    var printsFolder = SimpleStringProperty(PrintSettings.folderToSave.canonicalPath)
        private set

    val isFiltersApplied = SimpleBooleanProperty(true)

    val filmType = SimpleObjectProperty<FilmTypes>()
    val rotation = SimpleDoubleProperty()
    val brightness = SimpleDoubleProperty()
    val contrast = SimpleDoubleProperty()

    init {
        setInitialValues()
    }

    fun resetAll() {
        setInitialValues()
    }

    fun changePrintsLocation(newLocation: File) {
        PrintSettings.folderToSave = newLocation
        printsFolder.set(newLocation.canonicalPath)
    }

    private fun setInitialValues() {
        filmType.value = FilmTypes.BLACK_AND_WHITE
        rotation.value = 0.0
        brightness.value = 0.0
        contrast.value = 0.0
    }
}