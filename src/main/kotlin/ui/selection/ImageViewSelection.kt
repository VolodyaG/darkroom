package ui.selection

import javafx.beans.value.ObservableValue
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.image.ImageView
import tornadofx.add
import kotlin.math.abs

/**
 * Inspired by https://github.com/imgeself/JavaFX-ImageCropper
 */
fun Group.imageviewselection(imageView: ImageView, op: ResizableRectangle.() -> Unit = {}): ResizableRectangle {
    val rectangle = ResizableRectangle(this)
    add(rectangle)

    rectangle.setOnMouseEntered { cursor = Cursor.HAND }
    rectangle.setOnMouseReleased { cursor = Cursor.HAND }
    rectangle.setOnMouseExited { cursor = Cursor.DEFAULT }
    rectangle.setOnMousePressed { event ->
        MouseClick.set(event.x, event.y)
        cursor = Cursor.MOVE
    }
    rectangle.setOnMouseDragged { event ->
        val newX = rectangle.x + event.x - MouseClick.x
        val newY = rectangle.y + event.y - MouseClick.y

        if (newX >= 0 && newX + rectangle.width <= imageView.boundsInLocal.width) {
            rectangle.x = newX
        }
        if (newY >= 0 && newY + rectangle.height <= imageView.boundsInLocal.height) {
            rectangle.y = newY
        }

        MouseClick.set(event.x, event.y)
    }
    rectangle.setOnAngleChanged { _, old, new ->
        val oldAngle = old.toInt()
        val newAngle = new.toInt()
        val angleDifference = oldAngle - newAngle

        val xOffset = imageView.layoutBounds.maxX - rectangle.endx()
        val yOffset = imageView.layoutBounds.maxY - rectangle.endy()

        if (abs(oldAngle - newAngle) == 180) {
            rectangle.x = xOffset
            rectangle.y = yOffset
        }

        if (angleDifference == -90 || angleDifference == 270) {
            rectangle.x = yOffset.also { rectangle.y = rectangle.x }
            rectangle.width = rectangle.height.also { rectangle.height = rectangle.width }
        }

        if (angleDifference == 90 || angleDifference == -270) {
            rectangle.x = rectangle.y.also { rectangle.y = xOffset }
            rectangle.width = rectangle.height.also { rectangle.height = rectangle.width }
        }
    }

    imageView.setOnMousePressed { event ->
        if (event.isSecondaryButtonDown) {
            return@setOnMousePressed
        }

        rectangle.isVisible = false
        rectangle.x = event.x
        rectangle.y = event.y
    }

    imageView.setOnMouseReleased { event ->
        if (event.isSecondaryButtonDown) {
            return@setOnMouseReleased
        }

        val imageWidth = imageView.layoutBounds.maxX
        val imageHeight = imageView.layoutBounds.maxY
        rectangle.width = if (event.x >= imageWidth - 5) imageWidth - rectangle.x else abs(event.x - rectangle.x)
        rectangle.height = if (event.y >= imageHeight - 5) imageHeight - rectangle.y else abs(event.y - rectangle.y)

        if (event.x < rectangle.x) {
            rectangle.x = if (event.x < 0) 0.0 else event.x
        }
        if (event.y < rectangle.y) {
            rectangle.y = if (event.y < 0) 0.0 else event.y
        }
        rectangle.isVisible = true
    }

    rectangle.op()
    return rectangle
}

private object MouseClick {
    var x: Double = 0.0
    var y: Double = 0.0

    fun set(x: Double, y: Double) {
        this.x = x
        this.y = y
    }
}