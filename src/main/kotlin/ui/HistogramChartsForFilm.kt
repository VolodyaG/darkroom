package ui

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import marvin.image.MarvinImage
import marvin.statistic.MarvinHistogram
import marvin.statistic.MarvinHistogramEntry
import toFxImage
import java.awt.Color
import java.awt.image.BufferedImage

object HistogramChartsForFilm : SimpleObjectProperty<Image>() {
    private const val histogramWidth = 400
    private const val histogramHeight = 200

    fun update(image: BufferedImage) {
        val histogram = getHistogram(Color.red, MarvinImage(image))
        set(histogram.toFxImage())
    }

    private fun getHistogram(color: Color, image: MarvinImage): BufferedImage {
        val histogram = MarvinHistogram(color.toString())
        histogram.barWidth = 1

        val pixelsWithColorCounter: IntArray = countPixelValuesInImage(image, color)

        (0..255).forEach {
            val entry = MarvinHistogramEntry(
                it.toDouble(),
                pixelsWithColorCounter[it].toDouble(),
                getSingleChannelPixelColor(color, it)
            )
            histogram.addEntry(entry)
        }

        return histogram.getImage(histogramWidth, histogramHeight)
    }

    private fun countPixelValuesInImage(image: MarvinImage, color: Color): IntArray {
        val colorPixelsCounter = IntArray(256)

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val redPixelValue = getRedPixelValueFromImage(image, x, y)
                colorPixelsCounter[redPixelValue]++
            }
        }

        return colorPixelsCounter
    }

    private fun getRedPixelValueFromImage(image: MarvinImage, x: Int, y: Int): Int {
        return image.getIntComponent0(x, y);
    }

    private fun getSingleChannelPixelColor(channel: Color, value: Int): Color {
        return Color(value, 0, 0)
    }
}
