package ui.histograms

import convertToGrayScale
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import marvin.image.MarvinImage
import toFxImage
import tornadofx.runAsync
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

object HistogramChartsForFilm {
    val colorHistogramView = SimpleObjectProperty<Image>()
    val greyHistogramView = SimpleObjectProperty<Image>()

    fun buildHistograms(image: BufferedImage, colorImage: BufferedImage? = null) {
        if (colorImage == null) {
            return buildHistogramsForColorfulFilm(image)
        }
        return buildHistogramsForBlackAndWhiteFilm(image, colorImage)
    }

    fun buildHistogramsForBlackAndWhiteFilm(grayImage: BufferedImage, colorImage: BufferedImage) {
        runAsync(true) {
            val regionToAnalise = crop10PercentOfTheImage(grayImage)
            val grayHisto = HistogramDrawer.createGrayscaleHisto(regionToAnalise)
            greyHistogramView.set(grayHisto.toFxImage())
        }

        runAsync(true) {
            val regionToAnalise = crop10PercentOfTheImage(colorImage)
            val colorHisto = HistogramDrawer.createColorHisto(regionToAnalise)
            colorHistogramView.set(colorHisto.toFxImage())
        }
    }

    fun buildHistogramsForColorfulFilm(image: BufferedImage) {
        val regionToAnalise = crop10PercentOfTheImage(image)

        runAsync(true) {
            val grayHisto = HistogramDrawer.createGrayscaleHisto(regionToAnalise.convertToGrayScale())
            greyHistogramView.set(grayHisto.toFxImage())
        }

        runAsync(true) {
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
