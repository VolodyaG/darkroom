package ui.selection

import com.google.common.collect.ImmutableList
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.add
import tornadofx.addClass
import tornadofx.onChange
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

    val markers: ImmutableList<Rectangle>
    var markerSize = 14.0

    private val maxX = group.boundsInLocal.maxX
    private val maxY = group.boundsInLocal.maxY

    private val rectangleProperty = SimpleObjectProperty(Rectangle())
    private val angleProperty = SimpleDoubleProperty()

    fun rectangleProperty(): SimpleObjectProperty<Rectangle> {
        return rectangleProperty
    }

    fun angleProperty(): SimpleDoubleProperty {
        return angleProperty
    }

    init {
        isVisible = false

        markers = ImmutableList.of(
            resizemark(Cursor.NW_RESIZE) { resizable ->
                bindLeftX(this)
                bindTopY(this)
                setOnMouseDragged { event ->
                    moveLeftLine(resizable, event)
                    moveTopLine(resizable, event)
                }
            },
            resizemark(Cursor.N_RESIZE) { resizable ->
                bindMiddleX(this)
                bindTopY(this)
                setOnMouseDragged { event -> moveTopLine(resizable, event) }
            },
            resizemark(Cursor.NE_RESIZE) { resizable ->
                bindRightX(this)
                bindTopY(this)
                setOnMouseDragged { event ->
                    moveRightLine(resizable, event)
                    moveTopLine(resizable, event)
                }
            },
            resizemark(Cursor.E_RESIZE) { resizable ->
                bindRightX(this)
                bindMiddleY(this)
                setOnMouseDragged { event -> moveRightLine(resizable, event) }
            },
            resizemark(Cursor.SE_RESIZE) { resizable ->
                bindRightX(this)
                bindBottomY(this)
                setOnMouseDragged { event ->
                    moveRightLine(resizable, event)
                    moveBottomLine(resizable, event)
                }
            },
            resizemark(Cursor.S_RESIZE) { resizable ->
                bindMiddleX(this)
                bindBottomY(this)
                setOnMouseDragged { event -> moveBottomLine(resizable, event) }
            },
            resizemark(Cursor.SW_RESIZE) { resizable ->
                bindLeftX(this)
                bindBottomY(this)
                setOnMouseDragged { event ->
                    moveLeftLine(resizable, event)
                    moveBottomLine(resizable, event)
                }
            },
            resizemark(Cursor.W_RESIZE) { resizable ->
                bindLeftX(this)
                bindMiddleY(this)
                setOnMouseDragged { event -> moveLeftLine(resizable, event) }
            },
            resizemark(Cursor.DEFAULT) {
                bindMiddleX(this)
                bindMiddleY(this)
            }
        )

        rectangleProperty.onChange { newRectangle -> bindRectangleProperties(newRectangle) }
    }

    fun endx(): Double {
        return x + width
    }

    fun endy(): Double {
        return y + height
    }

    fun setOnAngleChanged(listener: (ObservableValue<out Number>?, oldValue: Number, newValue: Number) -> Unit) {
        angleProperty.addListener(listener)
    }

    private fun bindRectangleProperties(newRectangle: Rectangle?) {
        if (newRectangle == null) {
            return
        }
        xProperty().bindBidirectional(newRectangle.xProperty())
        yProperty().bindBidirectional(newRectangle.yProperty())
        widthProperty().bindBidirectional(newRectangle.widthProperty())
        heightProperty().bindBidirectional(newRectangle.heightProperty())
        angleProperty().bindBidirectional(newRectangle.rotateProperty())
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

    private fun bindRightX(marker: Rectangle) {
        marker.xProperty().bind(xProperty().add(widthProperty()).subtract(marker.widthProperty().divide(2.0)))
    }

    private fun bindTopY(marker: Rectangle) {
        marker.yProperty().bind(yProperty().subtract(marker.heightProperty().divide(2.0)))
    }

    private fun bindMiddleY(marker: Rectangle) {
        marker.yProperty().bind(
            yProperty()
                .add(heightProperty().divide(2.0))
                .subtract(marker.heightProperty().divide(2.0))
        )
    }

    private fun bindBottomY(marker: Rectangle) {
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

        if (yOffset >= 0 && yOffset + resizable.y <= maxY) {
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

        if (xOffset >= 0 && xOffset + resizable.x <= maxX) {
            resizable.width = xOffset
        }
    }
}

private fun ResizableRectangle.resizemark(
    cursor: Cursor,
    op: Rectangle.(resizable: ResizableRectangle) -> Unit = {}
): Rectangle {
    val rectangle = Rectangle(markerSize, markerSize)

    rectangle.op(this)
    rectangle.visibleProperty().bind(visibleProperty())
    rectangle.strokeProperty().bind(strokeProperty())
    rectangle.setOnMouseEntered { rectangle.parent.cursor = cursor }
    rectangle.setOnMouseExited { rectangle.parent.cursor = Cursor.DEFAULT }
    rectangle.style {
        fill = Color(1.0, 1.0, 1.0, 0.0)
    }

    group.add(rectangle)

    return rectangle
}
