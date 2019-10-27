import marvin.image.MarvinImage
import org.marvinproject.image.color.grayScale.GrayScale
import java.awt.image.BufferedImage

object MarvinPlugins {
    val grayScalePlugin = GrayScale()
    init{
        grayScalePlugin.load()
    }
}

fun MarvinImage.convertToGrayScale(): MarvinImage {
    val grayImageBuffer = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
    val grayImage = MarvinImage(grayImageBuffer)
    MarvinPlugins.grayScalePlugin.process(this, grayImage)
    grayImage.update()

    return grayImage
}