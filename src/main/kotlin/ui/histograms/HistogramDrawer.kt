package ui.histograms

import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.axis.NumberTickUnit
import org.jfree.chart.plot.DefaultDrawingSupplier
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.StandardXYBarPainter
import org.jfree.chart.renderer.xy.XYBarRenderer
import org.jfree.chart.ui.RectangleEdge
import org.jfree.data.Range
import org.jfree.data.statistics.HistogramDataset
import ui.FILM_PREVIEW_WINDOW_HEIGHT
import ui.LEFT_AND_RIGHT_WINDOWS_WIDTH
import ui.Styles
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Paint
import java.awt.geom.RectangularShape
import java.awt.image.BufferedImage
import java.awt.image.WritableRaster

private const val histogramWidth = LEFT_AND_RIGHT_WINDOWS_WIDTH
private const val histogramHeight = FILM_PREVIEW_WINDOW_HEIGHT / 3

object HistogramDrawer {
    private const val plotBackground = 235

    fun createGrayscaleHisto(image: BufferedImage): BufferedImage {
        val raster = image.raster

        var pixelsCounter = DoubleArray(raster.width * raster.height)
        pixelsCounter = raster.getSamples(0, 0, raster.width, raster.height, 0, pixelsCounter)
        pixelsCounter[0] = 256.0

        val chart = createEmptyChart()
        val plot = chart.plot as XYPlot

        plot.renderer = GrayScaleRenderer().also { it.barPainter = GrayScaleBarPainter() }
        (plot.dataset as HistogramDataset).addSeries("Gray", pixelsCounter, 256)

        return chart.createBufferedImage(histogramWidth.toInt(), histogramHeight.toInt())
    }

    fun createColorHisto(image: BufferedImage): BufferedImage {
        val chart = createEmptyChart()
        val plot = chart.plot as XYPlot

        // translucent blue, green & red (reverse order when legend in true)
        val paintArray = arrayOf<Paint>(Color(-0x7fffff01, true), Color(-0x7fff0100, true), Color(-0x7f010000, true))
        plot.drawingSupplier = DefaultDrawingSupplier(
            paintArray,
            DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE
        )
        plot.dataset = createColorHistogramDataset(image.raster)
        (plot.renderer as XYBarRenderer).barPainter = StandardXYBarPainter()

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

    private fun createEmptyChart(): JFreeChart {
        val dataset = HistogramDataset()
        val chart = ChartFactory.createHistogram(
            "", "", "", dataset, PlotOrientation.VERTICAL, false, true, false
        )
        val plot = chart.plot as XYPlot

        plot.backgroundPaint = Color(plotBackground, plotBackground, plotBackground)
        plot.rangeGridlinePaint = Styles.lightGray.toAwtPaint()
        plot.domainGridlinePaint = Styles.lightGray.toAwtPaint()

        plot.isOutlineVisible = false
        plot.isDomainZeroBaselineVisible = false

        plot.rangeAxis.isVisible = false
        plot.rangeAxis.isTickLabelsVisible = false

        plot.domainAxis.standardTickUnits = NumberAxis.createIntegerTickUnits()
        plot.domainAxis.range = Range(0.0, 256.0)
        (plot.domainAxis as NumberAxis).tickUnit = NumberTickUnit(16.0)

        return chart
    }

    private class GrayScaleRenderer : XYBarRenderer() {
        override fun getItemPaint(row: Int, column: Int): Paint {
            var entryColor = column

            if (column in (plotBackground - 5)..plotBackground) entryColor = 230

            if (column in (plotBackground + 1)..(plotBackground + 5)) entryColor = 240

            return Color(entryColor, entryColor, entryColor)
        }
    }

    private class GrayScaleBarPainter : StandardXYBarPainter() {
        override fun paintBarShadow(
            g2: Graphics2D?,
            renderer: XYBarRenderer?,
            row: Int,
            column: Int,
            bar: RectangularShape?,
            base: RectangleEdge?,
            pegShadow: Boolean
        ) {
        }
    }
}

private fun javafx.scene.paint.Color.toAwtPaint(): Paint {
    return Color(red.toFloat(), green.toFloat(), blue.toFloat(), opacity.toFloat())
}