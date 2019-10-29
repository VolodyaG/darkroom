package darkroom

import convertToGrayScale
import marvin.image.MarvinImage
import marvinplugins.MarvinPluginCollection.brightnessAndContrast
import marvinplugins.MarvinPluginCollection.invertColors
import org.marvinproject.image.color.colorChannel.ColorChannel
import ui.SettingsPannelProperties
import ui.histograms.HistogramChartsForFilm
import ui.histograms.HistogramEqualizationProperties
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
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
                var dataBefore = Date()
                invertNegativeImage(adjustedImage)
                var dataInvert = Date()
                println("Invert time: ${dataInvert.time - dataBefore.time}ms")
                val colorfulImage = doColorChannelsEqualization(adjustedImage)
                var dataColors = Date()
                println("Colors time: ${dataColors.time - dataInvert.time}ms")
                adjustedImage = colorfulImage.convertToGrayScale()
                var dataGray = Date()
                println("Grayscale time: ${dataGray.time - dataColors.time}ms")
                doLuminosityEqualization(adjustedImage)
                var dataLumin = Date()
                println("Luminosity time: ${dataLumin.time - dataGray.time}ms")
                adjustBrightnessAndContrast(adjustedImage)
                var dataBrightn = Date()
                println("Brightness time: ${dataBrightn.time - dataLumin.time}ms")
                HistogramChartsForFilm.buildHistogramsForBlackAndWhiteFilm(adjustedImage, colorfulImage)
                println("All processing time: ${Date().time - dataBefore.time}ms")
            }
            FilmTypes.COLOR_NEGATIVE -> {
                invertNegativeImage(adjustedImage)
                adjustedImage = doColorChannelsEqualization(adjustedImage)
                doLuminosityEqualization(adjustedImage)
                adjustBrightnessAndContrast(adjustedImage)
                HistogramChartsForFilm.buildHistogramsForColorfulFilm(adjustedImage)
            }
            FilmTypes.POSITIVE -> {
                adjustedImage = doColorChannelsEqualization(adjustedImage)
                doLuminosityEqualization(adjustedImage)
                adjustBrightnessAndContrast(adjustedImage)
                HistogramChartsForFilm.buildHistogramsForColorfulFilm(adjustedImage)
            }
        }

        return adjustedImage.bufferedImage
    }

    private fun invertNegativeImage(image: MarvinImage) {
        invertColors(image)
        image.update()
    }

    private fun doLuminosityEqualization(image: MarvinImage) {
    }

    private fun adjustBrightnessAndContrast(image: MarvinImage) {
        brightnessAndContrast(
            image,
            SettingsPannelProperties.brightness.intValue(),
            SettingsPannelProperties.contrast.intValue()
        )
        image.update()
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
