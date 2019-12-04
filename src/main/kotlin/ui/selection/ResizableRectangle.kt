package ui.selection

import com.google.common.collect.ImmutableCollection
import com.google.common.collect.ImmutableMap
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.add
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

    private val markersMap: ImmutableMap<MarkerType, Rectangle>
    val markers: ImmutableCollection<Rectangle>
        get() {
            return markersMap.values
        }
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

        group.add(this)
        markersMap = ImmutableMap.builder<MarkerType, Rectangle>()
            .put(
                resizemark(MarkerType.NW) { resizable ->
                    bindLeftX(this)
                    bindTopY(this)
                    setOnMouseDragged { event ->
                        moveLeftLine(resizable, event)
                        moveTopLine(resizable, event)
                    }
                })
            .put(
                resizemark(MarkerType.N) { resizable ->
                    bindMiddleX(this)
                    bindTopY(this)
                    setOnMouseDragged { event -> moveTopLine(resizable, event) }
                })
            .put(
                resizemark(MarkerType.NE) { resizable ->
                    bindRightX(this)
                    bindTopY(this)
                    setOnMouseDragged { event ->
                        moveRightLine(resizable, event)
                        moveTopLine(resizable, event)
                    }
                })
            .put(
                resizemark(MarkerType.E) { resizable ->
                    bindRightX(this)
                    bindMiddleY(this)
                    setOnMouseDragged { event -> moveRightLine(resizable, event) }
                })
            .put(
                resizemark(MarkerType.SE) { resizable ->
                    bindRightX(this)
                    bindBottomY(this)
                    setOnMouseDragged { event ->
                        moveRightLine(resizable, event)
                        moveBottomLine(resizable, event)
                    }
                })
            .put(
                resizemark(MarkerType.S) { resizable ->
                    bindMiddleX(this)
                    bindBottomY(this)
                    setOnMouseDragged { event -> moveBottomLine(resizable, event) }
                })
            .put(
                resizemark(MarkerType.SW) { resizable ->
                    bindLeftX(this)
                    bindBottomY(this)
                    setOnMouseDragged { event ->
                        moveLeftLine(resizable, event)
                        moveBottomLine(resizable, event)
                    }
                })
            .put(
                resizemark(MarkerType.W) { resizable ->
                    bindLeftX(this)
                    bindMiddleY(this)
                    setOnMouseDragged { event -> moveLeftLine(resizable, event) }
                })
            .put(
                resizemark(MarkerType.CENTER) {
                    bindMiddleX(this)
                    bindMiddleY(this)
                })
            .build()

        rectangleProperty.onChange { newRectangle -> bindRectangleProperties(newRectangle) }
    }

    fun endx(): Double {
        return x + width
    }

    fun endy(): Double {
        return y + height
    }

    fun getMarker(markerType: MarkerType): Rectangle {
        return markersMap.get(markerType)!!
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

enum class MarkerType(val cursor: Cursor) {
    NW(Cursor.NW_RESIZE),
    N(Cursor.N_RESIZE),
    NE(Cursor.NE_RESIZE),
    E(Cursor.E_RESIZE),
    SE(Cursor.SE_RESIZE),
    S(Cursor.S_RESIZE),
    SW(Cursor.SW_RESIZE),
    W(Cursor.W_RESIZE),
    CENTER(Cursor.DEFAULT),
}

private fun ResizableRectangle.resizemark(
    type: MarkerType,
    op: Rectangle.(resizable: ResizableRectangle) -> Unit = {}
): Map.Entry<MarkerType, Rectangle> {
    val rectangle = Rectangle(markerSize, markerSize)

    rectangle.op(this)
    rectangle.visibleProperty().bind(visibleProperty())
    rectangle.strokeProperty().bind(strokeProperty())
    rectangle.setOnMouseEntered { rectangle.cursor = type.cursor }
    rectangle.setOnMouseExited { rectangle.cursor = Cursor.DEFAULT }
    rectangle.style {
        fill = Color(1.0, 1.0, 1.0, 0.0)
    }

    group.add(rectangle)

    return object : Map.Entry<MarkerType, Rectangle> {
        override val key: MarkerType
            get() = type
        override val value: Rectangle
            get() = rectangle
    }
}
