package darkroom

import convertToGrayScale
import marvin.image.MarvinImage
import org.marvinproject.image.color.colorChannel.ColorChannel
import org.marvinproject.image.color.invert.Invert
import ui.SettingsPannelProperties
import ui.histograms.HistogramChartsForFilm
import ui.histograms.HistogramEqualizationProperties
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

private val debugImage = ImageIO.read(File("prints/02_long_10.png"));

object Darkroom {
    var isPrinting = false

    fun makeTestPrint(): BufferedImage {
//        val previewFrame = FilmScanner.scanInFullResolution()
        val previewFrame = debugImage // TODO For debug without scanner

        return doImageProcessing(previewFrame)
    }

    fun printImage() {
        isPrinting = true
        try {
            val scan = FilmScanner.scanInFullResolution()
            val print = doImageProcessing(scan)
            val filePath = "${PrintSettings.folderToSave.path}/${getPrintName()}"
            ImageIO.write(print, "PNG", File(filePath))
        } finally {
            isPrinting = false
        }
    }

    private fun doImageProcessing(image: BufferedImage): BufferedImage {
        var adjustedImage = MarvinImage(image)

        when (SettingsPannelProperties.filmType.value!!) {
            FilmTypes.BLACK_AND_WHITE -> {
                invertNegativeImage(adjustedImage)
                val colorfulImage = doColorChannelsEqualization(adjustedImage)
                adjustedImage = colorfulImage.convertToGrayScale()
                doLuminosityEqualization(adjustedImage)
                HistogramChartsForFilm.buildHistogramsForBlackAndWhiteFilm(adjustedImage, colorfulImage)
            }
            FilmTypes.COLOR_NEGATIVE -> {
                invertNegativeImage(adjustedImage)
                adjustedImage = doColorChannelsEqualization(adjustedImage)
                doLuminosityEqualization(adjustedImage)
                HistogramChartsForFilm.buildHistogramsForColorfulFilm(adjustedImage)
            }
            FilmTypes.POSITIVE -> {
                adjustedImage = doColorChannelsEqualization(adjustedImage)
                doLuminosityEqualization(adjustedImage)
                HistogramChartsForFilm.buildHistogramsForColorfulFilm(adjustedImage)
            }
        }

        adjustBrightnessAndContrast(adjustedImage)

        return adjustedImage.bufferedImage
    }

    private fun invertNegativeImage(image: MarvinImage) {
        Invert().process(image, image)
        image.update()
    }

    private fun doLuminosityEqualization(image: MarvinImage) {
    }

    private fun adjustBrightnessAndContrast(image: MarvinImage) {
    }

    private fun doColorChannelsEqualization(image: MarvinImage): MarvinImage {
        val adjustmentPlugin = ColorChannel()

        adjustmentPlugin.setAttributes(
            "red", HistogramEqualizationProperties.redChannelAdjustment.value.toInt(),
            "green", HistogramEqualizationProperties.greenChannelAdjustment.value.toInt(),
            "blue", HistogramEqualizationProperties.blueChannelAdjustment.value.toInt()
        )

        val adjustedImage = MarvinImage(image.width, image.height)
        adjustmentPlugin.process(image, adjustedImage)
        adjustedImage.update()
        return adjustedImage
    }

    private fun getPrintName(): String {
        return "test${Math.random()}.png" // TODO: Replace with date
    }
}
