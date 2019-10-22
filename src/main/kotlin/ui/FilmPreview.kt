package ui

import darkroom.Darkroom
import javafx.beans.property.SimpleObjectProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

private val defaultProgressImage = Image("images/progress.gif")

object FilmPreview: SimpleObjectProperty<Image>(defaultProgressImage) {

    init {
        Timer().scheduleAtFixedRate(500, 200) {
            val previewFrame = getPreviewFrame()
            if (value != previewFrame) {
                set(previewFrame)
            }
        }
    }

    private fun getPreviewFrame(): Image {
        if (Darkroom.isPrinting) {
            return defaultProgressImage
        }

        val image = Darkroom.makeTestPrint()
        return SwingFXUtils.toFXImage(image, null)
    }
}