package ui.histograms

import marvin.image.MarvinImage
import marvin.statistic.MarvinHistogram
import marvin.statistic.MarvinHistogramEntry
import org.jfree.chart.ChartFactory
import org.jfree.chart.plot.DefaultDrawingSupplier
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.StandardXYBarPainter
import org.jfree.chart.renderer.xy.XYBarRenderer
import org.jfree.data.statistics.HistogramDataset
import ui.FILM_PREVIEW_WINDOW_HEIGHT
import ui.LEFT_AND_RIGHT_WINDOWS_WIDTH
import java.awt.Color
import java.awt.Paint
import java.awt.image.BufferedImage
import java.awt.image.WritableRaster

private const val histogramWidth = LEFT_AND_RIGHT_WINDOWS_WIDTH
private const val histogramHeight = FILM_PREVIEW_WINDOW_HEIGHT / 3

object HistogramDrawer {

    fun createHistogram(image: MarvinImage, isGrayscale: Boolean): BufferedImage {
        return if (isGrayscale) {
            createGrayscaleHisto(image)
        } else {
            createColorHisto(image.bufferedImageNoAlpha)
        }
    }

    private fun createGrayscaleHisto(image: MarvinImage): BufferedImage {
        val histogram = MarvinHistogram("")
        histogram.barWidth = 1

        val grayPixelsCounter = IntArray(256)

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                grayPixelsCounter[image.getIntComponent0(x, y)]++
            }
        }

        for (i in 0 until 256) {
            val entry = createMarvinGrayHistoEntry(i, grayPixelsCounter[i])
            histogram.addEntry(entry)
        }

        return histogram.getImage(histogramWidth.toInt(), histogramHeight.toInt())
    }

    private fun createColorHisto(image: BufferedImage): BufferedImage {
        val raster = image.raster
        val dataset = createColorHistogramDataset(raster)

        val chart = ChartFactory.createHistogram(
            "", "", "",
            dataset, PlotOrientation.VERTICAL, false, true, false
        )
        val plot = chart.plot as XYPlot
        val renderer = plot.renderer as XYBarRenderer
        renderer.barPainter = StandardXYBarPainter()

        // translucent blue, green & red (reverse order when legend in true)
        val paintArray = arrayOf<Paint>(Color(-0x7fffff01, true), Color(-0x7fff0100, true), Color(-0x7f010000, true) )
        plot.drawingSupplier = DefaultDrawingSupplier(
            paintArray,
            DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE
        )

        return chart.createBufferedImage(histogramWidth.toInt(), histogramHeight.toInt())
    }

    private fun createColorHistogramDataset(raster: WritableRaster): HistogramDataset {
        val width = raster.width
        val height = raster.height

        val dataset = HistogramDataset()
        var pixelsCounter = DoubleArray(width * height)

        pixelsCounter = raster.getSamples(0, 0, width, height, 0, pixelsCounter)
        pixelsCounter[0] = 256.0 // Hack to disable histo rescale when all values less than 255

        dataset.addSeries("Red", pixelsCounter, 256)

        pixelsCounter = raster.getSamples(0, 0, width, height, 1, pixelsCounter)
        dataset.addSeries("Green", pixelsCounter, 256)

        pixelsCounter = raster.getSamples(0, 0, width, height, 2, pixelsCounter)
        dataset.addSeries("Blue", pixelsCounter, 256)

        return dataset
    }

    private fun createMarvinGrayHistoEntry(pixelValue: Int, pixelsCount: Int): MarvinHistogramEntry {
        val entryColor = if (pixelValue > 230) 230 else if (pixelValue < 30) 30 else pixelValue

        return MarvinHistogramEntry(
            pixelValue.toDouble(),
            pixelsCount.toDouble(),
            Color(entryColor, entryColor, entryColor)
        )
    }
}