package ui

import darkroom.FilmTypes
import darkroom.PrintSettings
import javafx.beans.property.*
import javafx.scene.shape.Rectangle
import java.io.File

object SettingsPanelProperties {
    val saveInProgress  = SimpleBooleanProperty(false)

    var printsFolder = SimpleStringProperty(PrintSettings.folderToSave.canonicalPath)
        private set

    val filmType = SimpleObjectProperty<FilmTypes>()

    val rotation = SimpleDoubleProperty()

    val isCropVisible = SimpleBooleanProperty()
    val cropArea = SimpleObjectProperty<Rectangle>()
    val cropAreaAngle: DoubleProperty
        get() = cropArea.value.rotateProperty()

    val brightness = SimpleDoubleProperty()
    val contrast = SimpleDoubleProperty()

    init {
        setInitialValues()
    }

    fun resetAll() {
        setInitialValues()
    }

    fun resetCropArea() {
        cropArea.value = Rectangle(7.0, 15.0, 800 - 7.0 - 7.0, 600 - 15.0 - 25.0)
    }

    fun changePrintsLocation(newLocation: File) {
        PrintSettings.folderToSave = newLocation
        printsFolder.set(newLocation.canonicalPath)
    }

    private fun setInitialValues() {
        filmType.value = FilmTypes.BLACK_AND_WHITE
        rotation.value = 0.0
        isCropVisible.value = false
        brightness.value = 0.0
        contrast.value = 0.0

        resetCropArea()
    }
}