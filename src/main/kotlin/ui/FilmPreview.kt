package ui

import darkroom.Darkroom
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import toFxImage
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO
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

//        val image = Darkroom.makeTestPrint()
        val image = debugImage()
        HistogramChartsForFilm.update(image) // ToDo move to timer after set() is called
        return image.toFxImage()
    }

    fun debugImage(): BufferedImage { // TODO For debug
        return ImageIO.read(File("prints/02_long_10.png"))
    }
}