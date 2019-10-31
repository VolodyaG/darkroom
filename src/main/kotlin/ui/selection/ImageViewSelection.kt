package ui.selection

import javafx.scene.Group
import javafx.scene.image.ImageView
import tornadofx.add
import ui.SettingsPannelProperties
import kotlin.math.abs

/**
 * Inspired by https://github.com/imgeself/JavaFX-ImageCropper
 */
fun Group.imageviewselection(imageView: ImageView, op: ResizableRectangle.() -> Unit = {}): ResizableRectangle {
    val rectangle = ResizableRectangle(this)
    add(rectangle)

    imageView.setOnMousePressed { event ->
        if (event.isSecondaryButtonDown || !SettingsPannelProperties.isCropVisible.value) {
            return@setOnMousePressed
        }

        rectangle.isVisible = false
        rectangle.x = event.x
        rectangle.y = event.y
        rectangle.maxX = imageView.layoutBounds.maxX
        rectangle.maxY = imageView.layoutBounds.maxY
    }

    imageView.setOnMouseReleased { event ->
        if (event.isSecondaryButtonDown || !SettingsPannelProperties.isCropVisible.value) {
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
