package darkroom

import com.jhlabs.image.ContrastFilter
import com.jhlabs.image.GrayscaleFilter
import com.jhlabs.image.InvertFilter
import com.jhlabs.image.LevelsFilter
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
        var adjustedImage = image

        when (SettingsPannelProperties.filmType.value!!) {
            FilmTypes.BLACK_AND_WHITE -> {
                var dataBefore = Date()

                adjustedImage = invertNegativeImage(adjustedImage)
                var dataInvert = Date()
                println("Invert time: ${dataInvert.time - dataBefore.time}ms")

                val colorfulImage = doColorChannelsEqualization(MarvinImage(adjustedImage)).bufferedImage
                var dataColors = Date()
                println("Colors time: ${dataColors.time - dataInvert.time}ms")

                adjustedImage = convertToGrayscale(colorfulImage)
                var dataGray = Date()
                println("Grayscale time: ${dataGray.time - dataColors.time}ms")

                adjustedImage = doLuminosityEqualization(adjustedImage)
                var dataLumin = Date()
                println("Luminosity time: ${dataLumin.time - dataGray.time}ms")

                adjustedImage = adjustBrightnessAndContrast(adjustedImage)
                var dataBrightn = Date()
                println("Brightness time: ${dataBrightn.time - dataLumin.time}ms")

                HistogramChartsForFilm.buildHistogramsForBlackAndWhiteFilm(MarvinImage(adjustedImage), MarvinImage(colorfulImage))
                println("All processing time: ${Date().time - dataBefore.time}ms")
            }
            FilmTypes.COLOR_NEGATIVE -> {
//                invertNegativeImage(adjustedImage)
//                adjustedImage = doColorChannelsEqualization(adjustedImage)
//                doLuminosityEqualization(adjustedImage)
//                adjustBrightnessAndContrast(adjustedImage)
//                HistogramChartsForFilm.buildHistogramsForColorfulFilm(adjustedImage)
            }
            FilmTypes.POSITIVE -> {
//                adjustedImage = doColorChannelsEqualization(adjustedImage)
//                doLuminosityEqualization(adjustedImage)
//                adjustBrightnessAndContrast(adjustedImage)
//                HistogramChartsForFilm.buildHistogramsForColorfulFilm(adjustedImage)
            }
        }

        return adjustedImage
    }

    private fun invertNegativeImage(image: BufferedImage): BufferedImage {
//        val marvinImage = MarvinImage(image)
//        invertColors(marvinImage)
//        marvinImage.update()
//        return marvinImage.bufferedImage

        val invertFilter = InvertFilter()
        return invertFilter.filter(image, null)
    }

    private fun convertToGrayscale(image: BufferedImage): BufferedImage {
        val grayscaleFilter = GrayscaleFilter()
        return grayscaleFilter.filter(image, null)
    }

    private fun doLuminosityEqualization(image: BufferedImage): BufferedImage  {
        return image
    }

    private fun adjustBrightnessAndContrast(image: BufferedImage): BufferedImage {
        val contrastFilter = ContrastFilter()
        contrastFilter.contrast = SettingsPannelProperties.contrast.floatValue()
        contrastFilter.brightness = SettingsPannelProperties.brightness.floatValue()
        return contrastFilter.filter(image, null)
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
