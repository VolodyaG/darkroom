import marvin.image.MarvinImage
import org.marvinproject.image.color.grayScale.GrayScale

object MarvinPlugins {
    val grayScalePlugin = GrayScale()
    init{
        grayScalePlugin.load()
    }
}

fun MarvinImage.convertToGrayScale(): MarvinImage {
    val grayImage = MarvinImage(width, height)
    MarvinPlugins.grayScalePlugin.process(this, grayImage)
    grayImage.update()

    return grayImage
}