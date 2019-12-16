package darkroom

import java.awt.Dimension

enum class ImageResolutions(val width: Double, val height: Double) {
    FULL(2592.0, 1944.0),
    DISPLAY(800.0, 600.0),
    AUTO_CROP(324.0, 243.0),
    GALLERY(80.0, 60.0);

    fun toAwtDimension(): Dimension {
        return Dimension(width.toInt(), height.toInt())
    }
}