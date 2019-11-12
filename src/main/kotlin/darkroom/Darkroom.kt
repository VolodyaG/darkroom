package darkroom

import isEnvTrue
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

    fun makeTestPrint(): BufferedImage {
        val previewFrame: BufferedImage

        if ("WITHOUT_SCANNER".isEnvTrue()) {
            val newImage = BufferedImage(debugImage.width, debugImage.height, 5)
            newImage.graphics.drawImage(debugImage, 0, 0, null)
            previewFrame = newImage
        } else {
            previewFrame = FilmScanner.scanInFullResolution()
        }

        return doImageProcessing(previewFrame)
    }

    fun printImage() {
        SettingsPanelProperties.saveInProgress.set(true)
        try {
            val scan: BufferedImage

            if ("WITHOUT_SCANNER".isEnvTrue()) {
                scan = debugImage
            } else {
                scan = FilmScanner.scanInFullResolution()
            }

            val filePath = "${PrintSettings.folderToSave.path}/${getPrintName()}"

            val adjustedImage = doImageProcessing(scan)
            val croppedImage = cropImage(adjustedImage, scan)

            ImageIO.write(croppedImage, "PNG", File(filePath))
        } finally {
            SettingsPanelProperties.saveInProgress.set(false)
        }
    }

    private fun doImageProcessing(image: BufferedImage): BufferedImage {
        var colorfulImage: BufferedImage? = null
        var adjustedImage = image

        when (SettingsPanelProperties.filmType.value!!) {
            FilmTypes.BLACK_AND_WHITE -> {
                colorfulImage = doColorChannelsEqualization(adjustedImage.invert())
                adjustedImage = colorfulImage.convertToGrayScale()
            }
            FilmTypes.COLOR_NEGATIVE -> {
                adjustedImage = doColorChannelsEqualization(adjustedImage.invert())
            }
            FilmTypes.POSITIVE -> {
                adjustedImage = doColorChannelsEqualization(adjustedImage)
            }
        }

        adjustedImage = adjustBrightnessAndContrast(adjustedImage)

        HistogramChartsForFilm.buildGrayscaleHistogram(adjustedImage, colorfulImage != null)

        adjustedImage = doLuminosityEqualization(adjustedImage)

        HistogramChartsForFilm.updateColorHistogram(if (colorfulImage == null) adjustedImage else colorfulImage)

        adjustedImage = applyClippingMask(adjustedImage)

        return rotate(adjustedImage)
    }

    private fun doLuminosityEqualization(image: BufferedImage): BufferedImage {
        if (!HistogramEqualizationProperties.applyLevelsAdjustment.value) {
            return image
        }
        if (HistogramEqualizationProperties.lowLumLevel.value == 0.0 && HistogramEqualizationProperties.highLumLevel.value == 255.0) {
            return image
        }

        return image.adjustLevels(
            HistogramEqualizationProperties.lowLumLevel.floatValue(),
            HistogramEqualizationProperties.highLumLevel.floatValue()
        )
    }

    private fun adjustBrightnessAndContrast(image: BufferedImage): BufferedImage {
        if (SettingsPanelProperties.contrast.value == 0.0 && SettingsPanelProperties.brightness.value == 0.0) {
            return image
        }

        return image.adjustBrightnessAndContrast(
            SettingsPanelProperties.brightness.floatValue() / 2 + 1,
            SettingsPanelProperties.contrast.floatValue() / 2 + 1
        )
    }

    private fun rotate(image: BufferedImage): BufferedImage {
        val degreeAngle = SettingsPanelProperties.rotation.value
        SettingsPanelProperties.cropAreaAngle.set(degreeAngle)

        if (degreeAngle == 0.0) {
            return image
        }

        return image.rotate(degreeAngle)
    }

    private fun doColorChannelsEqualization(image: BufferedImage): BufferedImage {
        if (!HistogramEqualizationProperties.applyColorsAdjustment.value) {
            return image
        }

        val red = HistogramEqualizationProperties.redChannelAdjustment.value.toInt()
        val green = HistogramEqualizationProperties.greenChannelAdjustment.value.toInt()
        val blue = HistogramEqualizationProperties.blueChannelAdjustment.value.toInt()

        if (red == 0 && green == 0 && blue == 0) {
            return image
        }

        return image.adjustColors(red, green, blue)
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

        return image.crop(x, y, width, height)
    }

    private fun applyClippingMask(image: BufferedImage): BufferedImage {
        if (!HistogramEqualizationProperties.highLightMaskEnabled() && !HistogramEqualizationProperties.shadowsMaskEnabled()) {
            return image
        }

        val maskForShadows = HistogramEqualizationProperties.shadowsMaskEnabled()
        val maskForHighlights = HistogramEqualizationProperties.highLightMaskEnabled()

        return image.createClippingMask(maskForShadows, maskForHighlights)
    }

    private fun getPrintName(): String {
        return "${formatter.format(Date())}.png"
    }
}
