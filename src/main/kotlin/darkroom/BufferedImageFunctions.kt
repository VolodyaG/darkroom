package darkroom

import com.jhlabs.image.*
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import marvin.image.MarvinImage
import org.marvinproject.image.color.colorChannel.ColorChannel
import java.awt.image.BufferedImage

fun BufferedImage.toFxImage(): Image {
    return SwingFXUtils.toFXImage(this, null)
}

fun BufferedImage.invert(): BufferedImage {
    val newImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val newImageRaster = newImage.raster

    val halfOfTheImageWidth = width / 2
    val pixelsInHalfImage = (halfOfTheImageWidth * height) * 3

    val firstHalfWorker = GlobalScope.async {
        val pixels = raster.getPixels(0, 0, halfOfTheImageWidth, height, IntArray(pixelsInHalfImage))
        pixels.forEachIndexed { index, i ->
            pixels[index] = 255 - i
        }
        newImageRaster.setPixels(0, 0, halfOfTheImageWidth, height, pixels)
    }

    val secondHalfWorker = GlobalScope.async {
        val pixels = raster.getPixels(
            halfOfTheImageWidth,
            0,
            width - halfOfTheImageWidth,
            height,
            IntArray(pixelsInHalfImage)
        )
        pixels.forEachIndexed { index, i ->
            pixels[index] = 255 - i
        }
        newImageRaster.setPixels(halfOfTheImageWidth, 0, width - halfOfTheImageWidth, height, pixels)
    }

    runBlocking {
        secondHalfWorker.await()
        firstHalfWorker.await()
    }

    return newImage
}

// TODO try jhlabs library
fun BufferedImage.adjustColors(red: Int, green: Int, blue: Int): BufferedImage {
    val originalImage = MarvinImage(this)
    val adjustedImage = MarvinImage(width, height)
    val adjustmentPlugin = ColorChannel()

    adjustmentPlugin.setAttributes("red", red, "green", green, "blue", blue)
    adjustmentPlugin.process(originalImage, adjustedImage)
    adjustedImage.update()

    return adjustedImage.bufferedImage
}

fun BufferedImage.convertToGrayScale(): BufferedImage {
    val grayscaleFilter = GrayscaleFilter()
    return grayscaleFilter.filter(this, BufferedImage(width, height, this.type))
}

fun BufferedImage.adjustBrightnessAndContrast(brightness: Float, contrast: Float): BufferedImage {
    val contrastFilter = ContrastFilter()
    contrastFilter.contrast = contrast
    contrastFilter.brightness = brightness
    return contrastFilter.filter(this, BufferedImage(width, height, type))
}

fun BufferedImage.adjustLevels(lowLevel: Float = 0F, highLevel: Float = 255F): BufferedImage {
    val levelsFilter = LevelsFilter()

    levelsFilter.lowLevel = lowLevel / 255
    levelsFilter.highLevel = highLevel / 255

    return levelsFilter.filter(this, BufferedImage(width, height, type))
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

fun BufferedImage.createClippingMask(shadowsMask: Boolean = false, highlightsMask: Boolean = false): BufferedImage {
    val mask = BufferedImage(width, height, type)
    val maskRaster = mask.raster

    for (x in 0 until width) {
        for (y in 0 until height) {
            val pixel = IntArray(4)

            raster.getPixel(x, y, pixel)
            maskRaster.setPixel(x, y, getNewPixelValueForClippingMask(pixel, shadowsMask, highlightsMask))
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