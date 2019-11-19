package darkroom

import com.jhlabs.image.*
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.awt.image.BufferedImage
import kotlin.math.abs
import kotlin.math.min

fun BufferedImage.toFxImage(): Image {
    return SwingFXUtils.toFXImage(this, null)
}

fun BufferedImage.invert(): BufferedImage {
    val processCallback = fun(pixels: IntArray) {
        pixels.forEachIndexed { index, pixelValue -> pixels[index] = 255 - pixelValue }
    }
    return splitAndRunAsync(PixelsImageSplitter(this), processCallback)
}

// Taken from org.marvinproject.image.color.ColorChannel
fun BufferedImage.adjustColors(red: Int, green: Int, blue: Int): BufferedImage {
    val redFactor = getColorAdjFactor(red)
    val greenFactor = getColorAdjFactor(green)
    val blueFactor = getColorAdjFactor(blue)

    val processCallback = fun(pixels: IntArray) {
        for (i in pixels.indices step 3) {
            pixels[i] = min(pixels[i] * redFactor, 255.0).toInt()
            pixels[i + 1] = min(pixels[i + 1] * greenFactor, 255.0).toInt()
            pixels[i + 2] = min(pixels[i + 2] * blueFactor, 255.0).toInt()
        }
    }

    return splitAndRunAsync(PixelsImageSplitter(this), processCallback)
}

private fun getColorAdjFactor(adjustTo: Int): Double {
    val factor = 1 + abs(adjustTo / 100.0 * 2.5)

    if (adjustTo > 0) {
        return factor
    }
    return 1 / factor
}

// https://www.tannerhelland.com/3643/grayscale-image-algorithm-vb6/
fun BufferedImage.convertToGrayScale(): BufferedImage {
    val processCallback = fun(pixels: IntArray) {
        for (i in pixels.indices step 3) {
            val gray = PixelUtils.clamp((pixels[i] * 0.299 + pixels[i + 1] * 0.587 + pixels[i + 2] * 0.114).toInt())
            pixels[i] = gray
            pixels[i + 1] = gray
            pixels[i + 2] = gray
        }
    }

    return splitAndRunAsync(PixelsImageSplitter(this), processCallback)
}

// https://www.dfstudios.co.uk/articles/programming/image-programming-algorithms/image-processing-algorithms-part-4-brightness-adjustment/
// https://www.dfstudios.co.uk/articles/programming/image-programming-algorithms/image-processing-algorithms-part-5-contrast-adjustment/
fun BufferedImage.adjustBrightnessAndContrast(brightness: Int, contrast: Int): BufferedImage {
    val contrastFactor = 259F / 255F * (contrast + 255F) / (259F - contrast)
    val contrastTable = IntArray(256) {
        val brightened = PixelUtils.clamp(it + brightness)
        val contrasted = contrastFactor * (brightened - 128) + 128
        return@IntArray PixelUtils.clamp(contrasted.toInt())
    }

    val processCallback = fun(pixels: IntArray) {
        pixels.forEachIndexed { index, pixelValue -> pixels[index] = contrastTable[pixelValue] }
    }

    return splitAndRunAsync(PixelsImageSplitter(this), processCallback)
}

// Taken from com.jhlabs.image.LevelsFilter
fun BufferedImage.adjustLevels(lowLevel: Float = 0F, highLevel: Float = 255F): BufferedImage {
    val luminosityTable = IntArray(256) {
        return@IntArray PixelUtils.clamp((255 * (it - lowLevel) / (highLevel - lowLevel)).toInt())
    }

    val processCallback = fun(pixels: IntArray) {
        pixels.forEachIndexed { index, pixelValue -> pixels[index] = luminosityTable[pixelValue] }
    }

    return splitAndRunAsync(PixelsImageSplitter(this), processCallback)
}

fun BufferedImage.rotate(degreeAngle: Double): BufferedImage {
    val radAngle = Math.toRadians(-degreeAngle).toFloat()
    val rotateFilter = RotateFilter(radAngle)
    return rotateFilter.filter(this, null)
}

fun BufferedImage.crop(x: Int, y: Int, width: Int, height: Int): BufferedImage {
    val cropFilter = CropFilter(x, y, width, height)
    return cropFilter.filter(this, null)
}

fun BufferedImage.createClippingMask(
    shadowsMask: Boolean = false,
    highlightsMask: Boolean = false
): BufferedImage {
    val processCallback = fun(pixels: IntArray) {
        for (i in pixels.indices step 3) {
            if (highlightsMask) {
                if (!(pixels[i] == 255 || pixels[i + 1] == 255 || pixels[i + 2] == 255)) {
                    pixels[i] = 0
                    pixels[i + 1] = 0
                    pixels[i + 2] = 0
                }
                continue
            }

            if (shadowsMask) {
                if (!(pixels[i] == 0 || pixels[i + 1] == 0 || pixels[i + 2] == 0)) {
                    pixels[i] = 255
                    pixels[i + 1] = 255
                    pixels[i + 2] = 255
                }
                continue
            }
        }
    }

    return splitAndRunAsync(PixelsImageSplitter(this), processCallback)
}
