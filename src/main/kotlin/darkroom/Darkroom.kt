package darkroom

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object Darkroom {
    var isPrinting = false

    fun makeTestPrint(): BufferedImage {
        val previewFrame = FilmScanner.getPreviewFrame()
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

    fun doImageProcessing(image: BufferedImage): BufferedImage {
        return image
    }

    private fun getPrintName(): String {
        return "test${Math.random()}.png" // TODO: Replace with date
    }
}