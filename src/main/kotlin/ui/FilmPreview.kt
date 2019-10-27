package ui

import darkroom.Darkroom
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import toFxImage
import java.awt.image.BufferedImage
import java.util.*
import javax.imageio.ImageIO
import kotlin.concurrent.scheduleAtFixedRate

object FilmPreview : SimpleObjectProperty<Image>() {
    private val defaultProgressImage = ImageIO.read(javaClass.getResourceAsStream("/images/progress.gif"))

    private var currentFrame = defaultProgressImage
        set(newValue) {
            field = newValue
            set(newValue.toFxImage())
        }


    init {
        Timer(true).scheduleAtFixedRate(500, 200) {
            val newFrame = getPreviewFrame()
            if (currentFrame != newFrame) {
                currentFrame = newFrame
            }
        }
    }

    private fun getPreviewFrame(): BufferedImage {
        if (Darkroom.isPrinting) {
            return defaultProgressImage
        }

        return Darkroom.makeTestPrint()
    }
}