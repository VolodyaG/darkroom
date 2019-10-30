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

    fun buildHistogramsForBlackAndWhiteFilm(grayImage: BufferedImage, colorImage: BufferedImage) {
        runAsync(true) {
            val regionToAnalise = crop10PercentOfTheImage(MarvinImage(grayImage))
            val grayHisto = HistogramDrawer.createHistogram(regionToAnalise, true)
            greyHistogramView.set(grayHisto.toFxImage())
        }

        runAsync(true) {
            val regionToAnalise = crop10PercentOfTheImage(MarvinImage(colorImage))
            val colorHisto = HistogramDrawer.createHistogram(regionToAnalise, false)
            colorHistogramView.set(colorHisto.toFxImage())
        }
    }

    fun buildHistogramsForColorfulFilm(image: MarvinImage) {
        val regionToAnalise = crop10PercentOfTheImage(image)

        runAsync(true) {
            val grayHisto = HistogramDrawer.createHistogram(regionToAnalise.convertToGrayScale(), true)
            greyHistogramView.set(grayHisto.toFxImage())
        }

        runAsync(true) {
            val colorHisto = HistogramDrawer.createHistogram(regionToAnalise, false)
            colorHistogramView.set(colorHisto.toFxImage())
        }
    }

    private fun crop10PercentOfTheImage(image: MarvinImage): MarvinImage {
        return image.subimage(
            (image.width * 0.1).roundToInt(),
            (image.height * 0.1).roundToInt(),
            (image.width * 0.8).roundToInt(),
            (image.height * 0.8).roundToInt()
        )
    }
}
