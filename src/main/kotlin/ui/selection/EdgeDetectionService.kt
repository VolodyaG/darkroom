package ui.selection

import darkroom.ImageResolutions
import darkroom.performancelog
import darkroom.scaleTo
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.shape.Rectangle
import marvin.image.MarvinImage
import marvin.util.MarvinAttributes
import org.marvinproject.image.corner.susan.Susan
import java.awt.image.BufferedImage
import java.util.*

object EdgeDetectionService {
    private const val EDGE_BAND_WIDTH = 30.0

    // TODO improve algorithm
    fun getRectangle(imageView: ImageView, rotation: Double): Rectangle = performancelog {
        val image = imageView.image
        val cornersMap = getAllEdgePixels(image)
        val edgePoints = ArrayList<EdgePoint>()

        val viewWidth = imageView.boundsInLocal.width
        val viewHeight = imageView.boundsInLocal.height
        val allowedRange = 0.0..EDGE_BAND_WIDTH

        for (i in 0 until cornersMap.size) {
            val row = cornersMap[i]

            for (j in 0 until row.size) {
                val value = row[j]

                if (value > 0) {
                    val x = i * viewWidth / ImageResolutions.AUTO_CROP.width
                    val y = j * viewHeight / ImageResolutions.AUTO_CROP.height
                    val side: EdgeSide

                    if (x in allowedRange) {
                        side = EdgeSide.LEFT

                    } else if (y in allowedRange) {
                        side = EdgeSide.TOP

                    } else if (viewWidth - x in allowedRange) {
                        side = EdgeSide.RIGHT

                    } else if (viewHeight - y in allowedRange) {
                        side = EdgeSide.BOTTOM

                    } else {
                        side = EdgeSide.CENTER
                    }

                    val point = EdgePoint(x, y, side)
                    edgePoints.add(point)
                }
            }
        }

        val rectangle = getMeanRectangle(edgePoints)
        rectangle.rotateProperty().set(rotation)
        return@performancelog rectangle
    }

    private fun getMeanRectangle(edgePoints: List<EdgePoint>): Rectangle {
        val x = getMean(edgePoints, EdgeSide.LEFT, EdgePoint::x)
        val y = getMean(edgePoints, EdgeSide.TOP, EdgePoint::y)
        val width = getMean(edgePoints, EdgeSide.RIGHT, EdgePoint::x) - x
        val height = getMean(edgePoints, EdgeSide.BOTTOM, EdgePoint::y) - y

        return Rectangle(x, y, width, height)
    }

    private fun getMean(points: List<EdgePoint>, side: EdgeSide, getter: EdgePoint.() -> Double): Double {
        val oneSidePoints = points.filter { point -> point.side == side }

        if (oneSidePoints.isEmpty()) {
            return 0.0
        }

        val sum = oneSidePoints.fold(0.0) { sum, point -> sum + point.getter() }
        return sum / oneSidePoints.size
    }

    private fun getAllEdgePixels(image: Image): List<IntArray> {
        val bufferedImage = SwingFXUtils.fromFXImage(
            image, BufferedImage(image.width.toInt(), image.height.toInt(), BufferedImage.TYPE_INT_RGB)
        )

        val adjustedImage = bufferedImage.scaleTo(ImageResolutions.AUTO_CROP.width, ImageResolutions.AUTO_CROP.height)

        val bwImage = MarvinImage(adjustedImage)
        val attr = MarvinAttributes()
        val edgeDetectionPlugin = Susan()
        edgeDetectionPlugin.load()
        edgeDetectionPlugin.process(bwImage, null, attr)

        return (attr.get("cornernessMap") as Array<IntArray>).toList()
    }
}

data class EdgePoint(val x: Double, val y: Double, val side: EdgeSide)

enum class EdgeSide {
    LEFT,
    TOP,
    RIGHT,
    BOTTOM,
    CENTER
}