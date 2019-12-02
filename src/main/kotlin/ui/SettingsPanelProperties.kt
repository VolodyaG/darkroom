package ui

import darkroom.FilmTypes
import darkroom.PrintSettings
import javafx.application.Platform
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.image.Image
import javafx.scene.shape.Rectangle
import tornadofx.observableListOf
import tornadofx.onChange
import tornadofx.runAsync
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

object SettingsPanelProperties {
    val saveInProgress = SimpleBooleanProperty(false)

    var printsFolder = SimpleStringProperty(PrintSettings.folderToSave.canonicalPath)
        private set
    val previewImages: ObservableList<Image> = FXCollections.synchronizedObservableList(observableListOf())
    val previewLoadInProgress = SimpleBooleanProperty(false)

    val filmType = SimpleObjectProperty<FilmTypes>()

    val rotation = SimpleDoubleProperty()

    val isCropVisible = SimpleBooleanProperty()
    val cropArea = SimpleObjectProperty<Rectangle>()
    val cropAreaAngle: DoubleProperty
        get() = cropArea.value.rotateProperty()

    val brightness = SimpleDoubleProperty()
    val contrast = SimpleDoubleProperty()

    init {
        printsFolder.onChange { runAsync { loadPrintsFolderContents() } }

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

    @Synchronized
    fun loadPrintsFolderContents() {
        previewLoadInProgress.set(true)

        val allImages = getImageFiles()?.map(GalleryImageFile::toImage) ?: return

        Platform.runLater {
            previewImages.clear()
            previewImages.addAll(allImages)
        }

        previewLoadInProgress.set(false)
    }

    @Synchronized
    fun loadLastFromPrintsFolder() {
        previewLoadInProgress.set(true)

        val firstImage = getImageFiles()?.get(0)?.toImage() ?: return

        Platform.runLater {
            previewImages.add(0, firstImage)
        }

        previewLoadInProgress.set(false)
    }

    @Synchronized
    private fun getImageFiles(): List<GalleryImageFile>? {
        if (printsFolder.value.isEmpty()) {
            return null
        }

        val directory = File(printsFolder.value)
        val files = directory.listFiles() ?: return null

        return files
            .filter { !it.isDirectory }
            .filter { "png".equals(it.extension) }
            .map {
                val attributes = Files.readAttributes(it.toPath(), BasicFileAttributes::class.java)
                GalleryImageFile(it, attributes.creationTime().toMillis())
            }
            .sortedByDescending { it.createdTime }
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

private class GalleryImageFile(private val file: File, val createdTime: Long) {
    fun toImage(): Image {
        return Image(file.inputStream(), 80.0, 60.0, true, false)
    }
}
