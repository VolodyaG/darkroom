package ui

import javafx.beans.property.SimpleObjectProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.io.File
import javax.imageio.ImageIO

object ImageProcessingPreview : SimpleObjectProperty<Image>() {
    init {
        val bufferedImage = ImageIO.read(File("prints/test0.9446859030045139.png"))
        set(SwingFXUtils.toFXImage(bufferedImage, null))
    }
}