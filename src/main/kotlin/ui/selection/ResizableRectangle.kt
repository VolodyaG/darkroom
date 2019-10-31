package ui.selection

import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.add
import tornadofx.style

/**
 * Inspired by https://github.com/imgeself/JavaFX-ImageCropper
 */
class ResizableRectangle(
    val group: Group,
    x: Double = 0.0,
    y: Double = 0.0,
    width: Double = 0.0,
    height: Double = 0.0
) :
    Rectangle(x, y, width, height) {

    init {
        isVisible = false

        resizemark(Cursor.NW_RESIZE) { resizable ->
            bindLeftX(this)
            bindTopY(this)
            setOnMouseDragged { event ->
                moveLeftLine(resizable, event)
                moveTopLine(resizable, event)
            }
        }
        resizemark(Cursor.N_RESIZE) { resizable ->
            bindMiddleX(this)
            bindTopY(this)
            setOnMouseDragged { event -> moveTopLine(resizable, event) }
        }
        resizemark(Cursor.NE_RESIZE) { resizable ->
            bindRightX(this)
            bindTopY(this)
            setOnMouseDragged { event ->
                moveRightLine(resizable, event)
                moveTopLine(resizable, event)
            }
        }
        resizemark(Cursor.E_RESIZE) { resizable ->
            bindRightX(this)
            bindMiddleY(this)
            setOnMouseDragged { event -> moveRightLine(resizable, event) }
        }
        resizemark(Cursor.SE_RESIZE) {resizable ->
            bindRightX(this)
            bindBottomY(this)
            setOnMouseDragged { event ->
                moveRightLine(resizable, event)
                moveBottomLine(resizable, event)
            }
        }
        resizemark(Cursor.S_RESIZE) { resizable ->
            bindMiddleX(this)
            bindBottomY(this)
            setOnMouseDragged { event -> moveBottomLine(resizable, event) }
        }
        resizemark(Cursor.SW_RESIZE) {resizable ->
            bindLeftX(this)
            bindBottomY(this)
            setOnMouseDragged { event ->
                moveLeftLine(resizable, event)
                moveBottomLine(resizable, event)
            }
        }
        resizemark(Cursor.W_RESIZE) { resizable ->
            bindLeftX(this)
            bindMiddleY(this)
            setOnMouseDragged { event -> moveLeftLine(resizable, event) }
        }
    }

    fun endx(): Double {
        return x + width
    }

    fun endy(): Double {
        return y + height
    }

    private fun bindLeftX(marker: Rectangle) {
        marker.xProperty().bind(xProperty().subtract(marker.widthProperty().divide(2.0)))
    }

    private fun bindMiddleX(marker: Rectangle) {
        marker.xProperty().bind(
            xProperty()
                .add(widthProperty().divide(2.0))
                .subtract(marker.widthProperty().divide(2.0))
        )
    }

    private fun bindRightX(marker: Rectangle){
        marker.xProperty().bind(xProperty().add(widthProperty()).subtract(marker.widthProperty().divide(2.0)))
    }

    private fun bindTopY(marker: Rectangle) {
        marker.yProperty().bind(yProperty().subtract(marker.heightProperty().divide(2.0)))
    }

    private fun bindMiddleY(marker: Rectangle){
        marker.yProperty().bind(
            yProperty()
                .add(heightProperty().divide(2.0))
                .subtract(marker.heightProperty().divide(2.0))
        )
    }

    private fun bindBottomY(marker: Rectangle){
        marker.yProperty().bind(yProperty().add(heightProperty()).subtract(marker.heightProperty().divide(2.0)))
    }

    private fun moveTopLine(resizable: ResizableRectangle, event: MouseEvent) {
        if (event.y >= 0 && event.y <= resizable.endy()) {
            resizable.height = resizable.endy() - event.y
            resizable.y = event.y
        }
    }

    private fun moveBottomLine(resizable: ResizableRectangle, event: MouseEvent) {
        val yOffset = event.y - resizable.y

        // Todo fix
        if (yOffset >= 0 && yOffset <= resizable.endy()) {
            resizable.height = yOffset
        }
    }

    private fun moveLeftLine(resizable: ResizableRectangle, event: MouseEvent) {
        if (event.x >= 0 && event.x <= resizable.endx()) {
            resizable.width = resizable.endx() - event.x
            resizable.x = event.x
        }
    }

    private fun moveRightLine(resizable: ResizableRectangle, event: MouseEvent) {
        val xOffset = event.x - resizable.x

        // Todo fix
        if (xOffset >= 0 && xOffset <= resizable.endx() - 5) {
            resizable.width = xOffset
        }
    }
}

private fun ResizableRectangle.resizemark(cursor: Cursor, op: Rectangle.(resizable: ResizableRectangle) -> Unit = {}) {
    val size = 20.0
    val rectangle = Rectangle(size, size)

    rectangle.op(this)
    rectangle.visibleProperty().bind(visibleProperty())
    rectangle.setOnMouseEntered { rectangle.parent.cursor = cursor }
    rectangle.setOnMouseExited { rectangle.parent.cursor = Cursor.DEFAULT }
    rectangle.style {
        stroke = Color.DARKRED
        fill = Color(1.0, 1.0, 1.0, 0.0)
    }

    group.add(rectangle)
}
