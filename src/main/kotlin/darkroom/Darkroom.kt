package darkroom

import com.jhlabs.image.ContrastFilter
import com.jhlabs.image.LevelsFilter
import com.jhlabs.image.RotateFilter
import convertToGrayScale
import marvin.image.MarvinImage
import marvinplugins.MarvinPluginCollection.invertColors
import org.marvinproject.image.color.colorChannel.ColorChannel
import ui.SettingsPannelProperties
import ui.histograms.HistogramChartsForFilm
import ui.histograms.HistogramEqualizationProperties
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO

private val debugImage = ImageIO.read(File("prints/02_long_10.png"))

object Darkroom {
    var isPrinting = false

    fun makeTestPrint(): BufferedImage {
        val previewFrame: BufferedImage

        if (System.getenv("WITHOUT_SCANNER") == true.toString()) {
            previewFrame = debugImage
        } else {
            previewFrame = FilmScanner.scanInFullResolution()
        }

        return doImageProcessing(previewFrame)
    }

    fun printImage() {
        isPrinting = true
        try {
            val scan: BufferedImage

            if (System.getenv("WITHOUT_SCANNER") == true.toString()) {
                scan = debugImage
            } else {
                scan = FilmScanner.scanInFullResolution()
            }

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

                adjustedImage = colorfulImage.convertToGrayScale()
                var dataGray = Date()
                println("Grayscale time: ${dataGray.time - dataColors.time}ms")

                adjustedImage = doLuminosityEqualization(adjustedImage)
                var dataLumin = Date()
                println("Luminosity time: ${dataLumin.time - dataGray.time}ms")

                adjustedImage = adjustBrightnessAndContrast(adjustedImage)
                var dataBrightn = Date()
                println("Brightness time: ${dataBrightn.time - dataLumin.time}ms")

                adjustedImage = rotate(adjustedImage)
                var dataRotate = Date()
                println("Rotate time: ${dataRotate.time - dataBrightn.time}ms")

                HistogramChartsForFilm.buildHistogramsForBlackAndWhiteFilm(adjustedImage, colorfulImage)
                println("All processing time: ${Date().time - dataBefore.time}ms")
            }
            FilmTypes.COLOR_NEGATIVE -> {
                adjustedImage = invertNegativeImage(adjustedImage)
                adjustedImage = doColorChannelsEqualization(MarvinImage(adjustedImage)).bufferedImage
                adjustedImage = doLuminosityEqualization(adjustedImage)
                adjustedImage = adjustBrightnessAndContrast(adjustedImage)
                adjustedImage = rotate(adjustedImage)
                HistogramChartsForFilm.buildHistogramsForColorfulFilm(adjustedImage)
            }
            FilmTypes.POSITIVE -> {
                adjustedImage = doColorChannelsEqualization(MarvinImage(adjustedImage)).bufferedImage
                adjustedImage = doLuminosityEqualization(adjustedImage)
                adjustedImage = adjustBrightnessAndContrast(adjustedImage)
                adjustedImage = rotate(adjustedImage)
                HistogramChartsForFilm.buildHistogramsForColorfulFilm(adjustedImage)
            }
        }

        if (HistogramEqualizationProperties.highLightMaskEnabled() || HistogramEqualizationProperties.shadowsMaskEnabled()) {
            adjustedImage = createClippingMask(adjustedImage)
        }

        return adjustedImage
    }

    private fun invertNegativeImage(image: BufferedImage): BufferedImage {
        val marvinImage = MarvinImage(image)
        invertColors(marvinImage)
        marvinImage.update()
        return marvinImage.bufferedImage

//        val invertFilter = InvertFilter()
//        return invertFilter.filter(image, null)

        // TODO even longer than two above, try to parallel
        /*for (w in 0 until image.width) {
            for (h in 0 until image.height) {
                var p = image.getRGB(w, h)
                val a = p shr 24 and 0xff
                var r = p shr 16 and 0xff
                var g = p shr 8 and 0xff
                var b = p and 0xff

                r = 255 - r
                g = 255 - g
                b = 255 - b
                p = a shl 24 or (r shl 16) or (g shl 8) or b

                image.setRGB(w, h, p)
            }
        }
        return image;*/
    }

    private fun doLuminosityEqualization(image: BufferedImage): BufferedImage {
        if (HistogramEqualizationProperties.lowLumLevel.value == 0.0 && HistogramEqualizationProperties.highLumLevel.value == 1.0) {
            return image
        }

        val levelsFilter = LevelsFilter()

        levelsFilter.lowLevel = HistogramEqualizationProperties.lowLumLevel.floatValue()
        levelsFilter.highLevel = HistogramEqualizationProperties.highLumLevel.floatValue()

        return levelsFilter.filter(image, null)
    }

    private fun adjustBrightnessAndContrast(image: BufferedImage): BufferedImage {
        if (SettingsPannelProperties.contrast.value == 0.0 && SettingsPannelProperties.brightness.value == 0.0) {
            return image
        }

        val contrastFilter = ContrastFilter()
        contrastFilter.contrast = SettingsPannelProperties.contrast.floatValue() / 2 + 1
        contrastFilter.brightness = SettingsPannelProperties.brightness.floatValue() / 2 + 1
        return contrastFilter.filter(image, null)
    }

    private fun rotate(image: BufferedImage): BufferedImage {
        val degreeAngle = SettingsPannelProperties.rotation.value

        if (degreeAngle == 0.0) {
            return image
        }

        val radAngle = Math.toRadians(-degreeAngle).toFloat()
        val rotateFilter = RotateFilter(radAngle)
        return rotateFilter.filter(image, null)
    }

    // TODO try jhlabs library
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

    private fun createClippingMask(image: BufferedImage): BufferedImage {
        val mask = BufferedImage(image.width, image.height, image.type)
        val maskRaster = mask.raster
        val imageRaster = image.raster

        val maskForShadows = HistogramEqualizationProperties.shadowsMaskEnabled()
        val maskForHighlights = HistogramEqualizationProperties.highLightMaskEnabled()

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val pixel = IntArray(4)
                imageRaster.getPixel(x, y, pixel)

                maskRaster.setPixel(x, y, getNewPixelValueForClippingMask(pixel, maskForShadows, maskForHighlights))
            }
        }
        return mask
    }

    private fun getNewPixelValueForClippingMask(pixel: IntArray, maskForShadows: Boolean, maskForHighlights: Boolean): IntArray {
        return if (maskForHighlights) {
            if ((pixel[0] == 255 || pixel[1] == 255 || pixel[2] == 255)) {
                pixel
            } else {
                arrayOf(0, 0, 0, 255).toIntArray()
            }
        } else if (maskForShadows) {
            if (pixel[0] == 0 || pixel[1] == 0 || pixel[2] == 0) {
                pixel
            } else {
                arrayOf(255, 255, 255, 255).toIntArray()
            }
        } else {
            throw IllegalStateException()
        }
    }
}
