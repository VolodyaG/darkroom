package ui.selection

import darkroom.rotate
import darkroom.toFxImage
import javafx.scene.Group
import javafx.scene.image.ImageView
import javafx.scene.shape.Rectangle
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.awt.image.BufferedImage

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ImageViewSelectionTest {
    @ParameterizedTest
    @MethodSource("rotationDataProvider")
    fun `Check coordinates, width and height after rotation`(data: TestData) {
        val resizableRectangle = createRectangle(data.initAngle)
        resizableRectangle.angleProperty().set(data.newAngle)

        Assertions.assertEquals(resizableRectangle.x, data.expectedX)
        Assertions.assertEquals(resizableRectangle.y, data.expectedY)
        Assertions.assertEquals(resizableRectangle.width, data.expectedWidth)
        Assertions.assertEquals(resizableRectangle.height, data.expectedHeight)
    }

    private fun rotationDataProvider() = listOf(
        TestData(-180.0, -90.0, 10.0, 15.0, 100.0, 200.0),
        TestData(-180.0, 0.0, 5.0, 10.0, 200.0, 100.0),
        TestData(-180.0, 90.0, 20.0, 5.0, 100.0, 200.0),
        TestData(-180.0, 180.0, 15.0, 20.0, 200.0, 100.0),

        TestData(-90.0, -180.0, 15.0, 20.0, 200.0, 100.0),
        TestData(-90.0, 0.0, 5.0, 10.0, 200.0, 100.0),
        TestData(-90.0, 90.0, 20.0, 5.0, 100.0, 200.0),
        TestData(-90.0, 180.0, 15.0, 20.0, 200.0, 100.0),

        TestData(0.0, -180.0, 15.0, 20.0, 200.0, 100.0),
        TestData(0.0, -90.0, 10.0, 15.0, 100.0, 200.0),
        TestData(0.0, 90.0, 20.0, 5.0, 100.0, 200.0),
        TestData(0.0, 180.0, 15.0, 20.0, 200.0, 100.0),

        TestData(90.0, -180.0, 15.0, 20.0, 200.0, 100.0),
        TestData(90.0, -90.0, 10.0, 15.0, 100.0, 200.0),
        TestData(90.0, 0.0, 5.0, 10.0, 200.0, 100.0),
        TestData(90.0, 180.0, 15.0, 20.0, 200.0, 100.0),

        TestData(180.0, -180.0, 15.0, 20.0, 200.0, 100.0),
        TestData(180.0, -90.0, 10.0, 15.0, 100.0, 200.0),
        TestData(180.0, 0.0, 5.0, 10.0, 200.0, 100.0),
        TestData(180.0, 90.0, 20.0, 5.0, 100.0, 200.0)
    ).stream()

    private fun createRectangle(initialAngle: Double): ResizableRectangle {
        val bufferedImage = BufferedImage(220, 130, BufferedImage.TYPE_INT_RGB)
        val image = bufferedImage.toFxImage()
        val mainImageView = ImageView(image)
        val group = Group(mainImageView)
        val resizableRectangle = group.imageviewselection(mainImageView)

        resizableRectangle.rectangleProperty().set(Rectangle(5.0, 10.0, 200.0, 100.0))
        resizableRectangle.angleProperty().set(initialAngle)
        mainImageView.imageProperty().set(bufferedImage.rotate(initialAngle).toFxImage())

        return resizableRectangle
    }

    data class TestData(
        val initAngle: Double,
        val newAngle: Double,
        val expectedX: Double,
        val expectedY: Double,
        val expectedWidth: Double,
        val expectedHeight: Double
    )
}