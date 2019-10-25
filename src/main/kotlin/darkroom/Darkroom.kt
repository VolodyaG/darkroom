package darkroom

import convertToGrayScale
import marvin.image.MarvinImage
import org.marvinproject.image.color.colorChannel.ColorChannel
import org.marvinproject.image.color.invert.Invert
import ui.SettingsPannelProperties
import ui.histograms.HistogramEqualizationProperties
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

private val debugImage = ImageIO.read(File("prints/02_long_10.png"));

object Darkroom {
    var isPrinting = false

    fun makeTestPrint(): BufferedImage {
//        val previewFrame = FilmScanner.getPreviewFrame()
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
        var adjustedImage: BufferedImage

        when (SettingsPannelProperties.filmType.value!!) {
            FilmTypes.BLACK_AND_WHITE -> {
                adjustedImage = invertNegativeImage(image)
                adjustedImage = doColorChannelsEqualization(adjustedImage)
                adjustedImage = MarvinImage(adjustedImage).convertToGrayScale().bufferedImage
                adjustedImage = doLuminosityEqualization(adjustedImage)
            }
            FilmTypes.COLOR_NEGATIVE -> {
                adjustedImage = invertNegativeImage(image)
                adjustedImage = doLuminosityEqualization(adjustedImage)
                adjustedImage = doColorChannelsEqualization(adjustedImage)
            }
            FilmTypes.POSITIVE -> {
                adjustedImage = doLuminosityEqualization(image)
                adjustedImage = doColorChannelsEqualization(adjustedImage)
            }
        }


        return adjustedImage
    }

    private fun invertNegativeImage(image: BufferedImage): BufferedImage {
        val inImage = MarvinImage(image)
        Invert().process(inImage, inImage)
        inImage.update()
        return inImage.bufferedImage;
    }

    private fun doLuminosityEqualization(image: BufferedImage): BufferedImage {
        return image
    }

    private fun doColorChannelsEqualization(image: BufferedImage): BufferedImage {
        val adjustmentPlugin = ColorChannel()

        adjustmentPlugin.setAttributes(
            "red", HistogramEqualizationProperties.redChannelAdjustment.value.toInt(),
            "green", HistogramEqualizationProperties.greenChannelAdjustment.value.toInt(),
            "blue", HistogramEqualizationProperties.blueChannelAdjustment.value.toInt()
        )

        val adjustedImage = MarvinImage(image)
        adjustmentPlugin.process(adjustedImage, adjustedImage)
        adjustedImage.update()
        return adjustedImage.bufferedImage
    }

    private fun getPrintName(): String {
        return "test${Math.random()}.png" // TODO: Replace with date
    }
}
