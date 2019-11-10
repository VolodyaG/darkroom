package ui.histograms

import convertToGrayScale
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import toFxImage
import tornadofx.runAsync
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

object HistogramChartsForFilm {
    val colorHistogramView = SimpleObjectProperty<Image>()
    val greyHistogramView = SimpleObjectProperty<Image>()

    fun buildGrayscaleHistogram(image: BufferedImage, isGrayScale: Boolean = false) {
        runAsync {
            var regionToAnalise = crop10PercentOfTheImage(image)

            if (!isGrayScale) {
                regionToAnalise = regionToAnalise.convertToGrayScale()
            }

            val grayHisto = HistogramDrawer.createGrayscaleHisto(regionToAnalise)
            greyHistogramView.set(grayHisto.toFxImage())
        }
    }

    fun updateColorHistogram(image: BufferedImage) {
        runAsync(true) {
            val regionToAnalise = crop10PercentOfTheImage(image)
            val colorHisto = HistogramDrawer.createColorHisto(regionToAnalise)
            colorHistogramView.set(colorHisto.toFxImage())
        }
    }

    private fun crop10PercentOfTheImage(image: BufferedImage): BufferedImage {
        return image.getSubimage(
            (image.width * 0.1).roundToInt(),
            (image.height * 0.1).roundToInt(),
            (image.width * 0.8).roundToInt(),
            (image.height * 0.8).roundToInt()
        )
    }
}
