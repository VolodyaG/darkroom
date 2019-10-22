package darkroom

import com.github.sarxos.webcam.Webcam
import driver.FilmScannerDriver
import java.awt.Dimension
import java.awt.image.BufferedImage

object FilmScanner {
    private val scanResolution = Dimension(2592, 1944)
    private val previewResolution = Dimension(640, 480)

    init {
        Webcam.setDriver(FilmScannerDriver())
    }

    private val filmScanner: Webcam = findFilmScanner()
    var isScanning = false
        private set

    fun getPreviewFrame(): BufferedImage {
        check(!isScanning) { "Cannot get preview image right now" }

        if (!filmScanner.isOpen) {
            filmScanner.viewSize = previewResolution
            filmScanner.open()
        }

        return filmScanner.image
    }

    fun scanInFullResolution(): BufferedImage {
        try {
            isScanning = true
            filmScanner.close()

            filmScanner.viewSize = scanResolution
            filmScanner.open()

            return filmScanner.image
        } finally {
            filmScanner.close()
            isScanning = false
        }
    }

    @Throws(NoSuchElementException::class)
    private fun findFilmScanner(): Webcam {
        val webcams = Webcam.getWebcams()

        return webcams.first {
            it.viewSizes.size == 2 && it.viewSizes.last() == scanResolution
        }
    }
}
