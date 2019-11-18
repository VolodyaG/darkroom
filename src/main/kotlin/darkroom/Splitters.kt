package darkroom

import java.awt.image.BufferedImage

interface ImageSplitter<T> {
    val splits: List<T>

    fun split()

    fun join(): BufferedImage
}

class PixelsImageSplitter(val image: BufferedImage) : ImageSplitter<IntArray> {
    private val pixels: MutableList<IntArray> = mutableListOf()

    override val splits: List<IntArray>
        get() = pixels

    override fun split() {
        val halfWidth = image.width / 2
        val pixelsInHalf = (halfWidth * image.height) * 3

        pixels.add(
            image.raster.getPixels(0, 0, halfWidth, image.height, IntArray(pixelsInHalf))
        )
        pixels.add(
            image.raster.getPixels(halfWidth, 0, image.width - halfWidth, image.height, IntArray(pixelsInHalf))
        )
    }

    override fun join(): BufferedImage {
        val newImage = BufferedImage(image.width, image.height, image.type)
        val halfWidth = newImage.width / 2

        newImage.raster.setPixels(0, 0, halfWidth, newImage.height, pixels[0])
        newImage.raster.setPixels(halfWidth, 0, newImage.width - halfWidth, newImage.height, pixels[1])

        return newImage
    }
}
