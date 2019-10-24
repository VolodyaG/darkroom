package darkroom

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
        val adjustedImage = doHistogramEqualization(image)
        return adjustedImage
    }

    private fun doHistogramEqualization(image: BufferedImage): BufferedImage {
        if (HistogramEqualizationProperties.redChannelAdjustment.value != 0.0) {
            println("new red: ${HistogramEqualizationProperties.redChannelAdjustment.value}")
        }

        return image
    }

    private fun getPrintName(): String {
        return "test${Math.random()}.png" // TODO: Replace with date
    }
}