package darkroom

import com.github.sarxos.webcam.Webcam
import driver.FilmScannerDriver
import java.awt.Dimension
import java.awt.image.BufferedImage

object FilmScanner {
    init {
        Webcam.setDriver(FilmScannerDriver())
    }

    private val fullScanResolution = Dimension(2592, 1944)
    private val previewResolution = Dimension(640, 480)
    private val filmScanner: Webcam = findFilmScanner()

    private var scanningForNewImageNow = false
    private var fetchedFrame: BufferedImage? = null

    fun getPreviewFrame(): BufferedImage {
        return scanImage(previewResolution)
    }

    fun scanInFullResolution(): BufferedImage {
        return scanImage(fullScanResolution)
    }

    private fun scanImage(resolution: Dimension): BufferedImage {
        if (scanningForNewImageNow && fetchedFrame != null) {
            return fetchedFrame!!
        }

        try {
            scanningForNewImageNow = true

            if (!filmScanner.isOpen) {
                filmScanner.viewSize = resolution
                filmScanner.open()
            }

            if (filmScanner.viewSize != resolution) {
                filmScanner.close()
                filmScanner.viewSize = fullScanResolution
                filmScanner.open()
            }

            fetchedFrame = filmScanner.image
            return fetchedFrame!!
        } finally {
            scanningForNewImageNow = false
        }
    }

    @Throws(NoSuchElementException::class)
    private fun findFilmScanner(): Webcam {
        val webcams = Webcam.getWebcams()

        return webcams.first {
            it.viewSizes.size == 2 && it.viewSizes.last() == fullScanResolution
        }
    }
}
