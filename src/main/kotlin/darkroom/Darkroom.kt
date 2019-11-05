package darkroom

import com.jhlabs.image.ContrastFilter
import com.jhlabs.image.CropFilter
import com.jhlabs.image.LevelsFilter
import com.jhlabs.image.RotateFilter
import convertToGrayScale
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import marvin.image.MarvinImage
import org.marvinproject.image.color.colorChannel.ColorChannel
import ui.FILM_PREVIEW_WINDOW_WIDTH
import ui.SettingsPanelProperties
import ui.histograms.HistogramChartsForFilm
import ui.histograms.HistogramEqualizationProperties
import java.awt.image.BufferedImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO

private val debugImage = ImageIO.read(File("prints/02_long_10.png"))

object Darkroom {
    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    var isPrinting = false

    fun makeTestPrint(): BufferedImage {
        val previewFrame: BufferedImage

        if (System.getenv("WITHOUT_SCANNER") == true.toString()) {
            val newImage = BufferedImage(debugImage.width, debugImage.height, 5)
            newImage.graphics.drawImage(debugImage, 0, 0, null)
            previewFrame = newImage
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

            val filePath = "${PrintSettings.folderToSave.path}/${getPrintName()}"

            val adjustedImage = doImageProcessing(scan)
            var dataBefore = Date()
            val croppedImage = cropImage(adjustedImage, scan)
            println("Crop time: ${Date().time - dataBefore.time}ms")

            ImageIO.write(croppedImage, "PNG", File(filePath))
        } finally {
            isPrinting = false
        }
    }

    private fun doImageProcessing(image: BufferedImage): BufferedImage {
        var adjustedImage = image

        when (SettingsPanelProperties.filmType.value!!) {
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
        val newImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)

        val imageRaster = image.raster
        val newImageRaster = newImage.raster

        val halfOfTheImageWidth = image.width / 2
        val pixelsInHalfImage = (halfOfTheImageWidth * image.height) * 3

        val firstHalfWorker = GlobalScope.async {
            val pixels = imageRaster.getPixels(0, 0, halfOfTheImageWidth, image.height, IntArray(pixelsInHalfImage))
            pixels.forEachIndexed { index, i ->
                pixels[index] = 255 - i
            }
            newImageRaster.setPixels(0, 0, halfOfTheImageWidth, image.height, pixels)
        }

        val secondHalfWorker = GlobalScope.async {
            val pixels = imageRaster.getPixels(
                halfOfTheImageWidth,
                0,
                image.width - halfOfTheImageWidth,
                image.height,
                IntArray(pixelsInHalfImage)
            )
            pixels.forEachIndexed { index, i ->
                pixels[index] = 255 - i
            }
            newImageRaster.setPixels(halfOfTheImageWidth, 0, image.width - halfOfTheImageWidth, image.height, pixels)
        }

        runBlocking {
            secondHalfWorker.await()
            firstHalfWorker.await()
        }

        return newImage
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
        if (SettingsPanelProperties.contrast.value == 0.0 && SettingsPanelProperties.brightness.value == 0.0) {
            return image
        }

        val contrastFilter = ContrastFilter()
        contrastFilter.contrast = SettingsPanelProperties.contrast.floatValue() / 2 + 1
        contrastFilter.brightness = SettingsPanelProperties.brightness.floatValue() / 2 + 1
        return contrastFilter.filter(image, null)
    }

    private fun rotate(image: BufferedImage): BufferedImage {
        val degreeAngle = SettingsPanelProperties.rotation.value

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

    private fun cropImage(image: BufferedImage, originalImage: BufferedImage): BufferedImage {
        if (!SettingsPanelProperties.isCropVisible.value) {
            return image
        }

        val area = SettingsPanelProperties.cropArea.value
        val scaleFactor = originalImage.width / FILM_PREVIEW_WINDOW_WIDTH
        val x = (area.x * scaleFactor).toInt()
        val y = (area.y * scaleFactor).toInt()
        val width = (area.width * scaleFactor).toInt()
        val height = (area.height * scaleFactor).toInt()

        val cropFilter = CropFilter(x, y, width, height)
        return cropFilter.filter(image, null)
    }

    private fun getPrintName(): String {
        return "${formatter.format(Date())}.png"
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

    private fun getNewPixelValueForClippingMask(
        pixel: IntArray,
        maskForShadows: Boolean,
        maskForHighlights: Boolean
    ): IntArray {
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
