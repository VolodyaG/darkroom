package ui.histograms

import convertToGrayScale
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import marvin.image.MarvinImage
import marvin.statistic.MarvinHistogram
import marvin.statistic.MarvinHistogramEntry
import toFxImage
import tornadofx.runAsync
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

private const val histogramWidth = 400
private const val histogramHeight = 200

object HistogramChartsForFilm {
    val colorHistogramView = SimpleObjectProperty<Image>()
    val greyHistogramView = SimpleObjectProperty<Image>()

    fun buildHistogramsForBlackAndWhiteFilm(grayImage: MarvinImage, colorImage: MarvinImage) {
        runAsync(true) {
            val regionToAnalise = crop10PercentOfTheImage(grayImage)
            val grayHisto = getGrayscaleHistogram(regionToAnalise)
            greyHistogramView.set(grayHisto.toFxImage())
        }

        runAsync(true) {
            val regionToAnalise = crop10PercentOfTheImage(colorImage)
            val colorHisto = getColorHistogram(regionToAnalise)
            colorHistogramView.set(colorHisto.toFxImage())
        }
    }

    fun buildHistogramsForColorfulFilm(image: MarvinImage) {
        val regionToAnalise = crop10PercentOfTheImage(image)

        runAsync(true) {
            val grayHisto = getGrayscaleHistogram(regionToAnalise.convertToGrayScale())
            greyHistogramView.set(grayHisto.toFxImage())
        }

        runAsync(true) {
            val colorHisto = getColorHistogram(regionToAnalise)
            colorHistogramView.set(colorHisto.toFxImage())
        }
    }

    private fun getGrayscaleHistogram(image: MarvinImage): BufferedImage {
        val histogram = MarvinHistogram("Gray Intensity")
        histogram.barWidth = 1

        val grayPixelsCounter = IntArray(256)

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                grayPixelsCounter[image.getIntComponent0(x, y)]++
            }
        }

//        println("max gray: px ${grayPixelsCounter.indexOf(grayPixelsCounter.max()!!)} value${grayPixelsCounter.max()}")

        for (i in 0 until 256) {
            val entry = createHistoEntry(Color.gray, i, grayPixelsCounter)
            histogram.addEntry(entry)
        }

        return histogram.getImage(histogramWidth, histogramHeight)

    }

    private fun getColorHistogram(image: MarvinImage): BufferedImage {
        val histogram = MarvinHistogram("Colors")
        histogram.barWidth = 1

        val colorChannelsPixelsCounter = countPixelValuesInImage(image)
//        printMaxValues(colorChannelsPixelsCounter)

        (0..255).forEach {

            val redEntry = createHistoEntry(
                Color.red,
                it,
                colorChannelsPixelsCounter.getValue(Color.red)
            )
            histogram.addEntry(redEntry)

            val greenEntry = createHistoEntry(
                Color.green,
                it,
                colorChannelsPixelsCounter.getValue(Color.green)
            )
            histogram.addEntry(greenEntry)

            val blueEntry = createHistoEntry(
                Color.blue,
                it,
                colorChannelsPixelsCounter.getValue(Color.blue)
            )
            histogram.addEntry(blueEntry)
        }

        return histogram.getImage(histogramWidth, histogramHeight)
    }

    private fun countPixelValuesInImage(image: MarvinImage): Map<Color, IntArray> {
        val redPixelsCounter = IntArray(256)
        val greenPixelsCounter = IntArray(256)
        val bluePixelsCounter = IntArray(256)

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val redPixelValue = getPixelValue(image, x, y, Color.red)
                redPixelsCounter[redPixelValue]++

                val greenPixelValue = getPixelValue(image, x, y, Color.green)
                greenPixelsCounter[greenPixelValue]++

                val bluePixelValue = getPixelValue(image, x, y, Color.blue)
                bluePixelsCounter[bluePixelValue]++
            }
        }

        return mapOf(Color.red to redPixelsCounter, Color.green to greenPixelsCounter, Color.blue to bluePixelsCounter)
    }

    private fun createHistoEntry(color: Color, pixelValue: Int, pixelsCounter: IntArray): MarvinHistogramEntry {
        val entryColor = if (pixelValue > 230) 230 else if (pixelValue < 30) 30 else pixelValue

        return MarvinHistogramEntry(
            pixelValue.toDouble(),
            pixelsCounter[pixelValue].toDouble(),
            getRgbColorForSingleChannelPixel(color, entryColor)
        )
    }

    private fun getPixelValue(image: MarvinImage, x: Int, y: Int, color: Color): Int {
        return when (color) {
            Color.red -> image.getIntComponent0(x, y)
            Color.green -> image.getIntComponent1(x, y)
            Color.blue -> image.getIntComponent2(x, y)
            else -> throw RuntimeException("Invalid color channel")
        }
    }


    private fun getRgbColorForSingleChannelPixel(channel: Color, value: Int): Color {
        return when (channel) {
            Color.red -> Color(value, 0, 0)
            Color.green -> Color(0, value, 0)
            Color.blue -> Color(0, 0, value)
            Color.gray -> Color(value, value, value)
            else -> throw RuntimeException("Invalid color channel")
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

    private fun printMaxValues(colorChannelsPixelsCounter: Map<Color, IntArray>) { // ToDo delete. Just for debug
        println(
            "max red: px ${colorChannelsPixelsCounter.getValue(Color.red).indexOf(
                colorChannelsPixelsCounter.getValue(
                    Color.red
                ).max()!!
            )} value${colorChannelsPixelsCounter.getValue(Color.red).max()}"
        )
        println(
            "max green: px ${colorChannelsPixelsCounter.getValue(Color.green).indexOf(
                colorChannelsPixelsCounter.getValue(
                    Color.green
                ).max()!!
            )} value${colorChannelsPixelsCounter.getValue(Color.green).max()}"
        )
        println(
            "max blue: px ${colorChannelsPixelsCounter.getValue(Color.blue).indexOf(
                colorChannelsPixelsCounter.getValue(
                    Color.blue
                ).max()!!
            )} value${colorChannelsPixelsCounter.getValue(Color.blue).max()}"
        )
    }
}
