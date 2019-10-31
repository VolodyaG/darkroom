package ui.selection

import javafx.scene.Group
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import tornadofx.add

/**
 * Inspired by https://github.com/imgeself/JavaFX-ImageCropper
 */
fun Pane.imageviewselection(imageView: ImageView, op: ResizableRectangle.() -> Unit = {}) {
    val group = imageView.parent as Group
    val rectangle = ResizableRectangle(group)
    group.add(rectangle)

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

        rectangle.width = event.x - rectangle.x
        rectangle.height = event.y - rectangle.y
        rectangle.isVisible = true
    }

    rectangle.op()
}
