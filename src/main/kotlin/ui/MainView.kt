package ui

import darkroom.toFxImage
import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.HPos
import javafx.geometry.Rectangle2D
import javafx.geometry.VPos
import javafx.scene.Group
import javafx.scene.control.ProgressIndicator
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints
import javafx.scene.shape.Rectangle
import tornadofx.*
import ui.histograms.HistogramPanelView
import ui.selection.Dimensions
import ui.selection.ResizableRectangle
import ui.selection.imageviewselection
import java.awt.Color
import java.awt.Graphics

class MainView : View("Darkroom") {
    private var mainImageView = imageview()
    private val magnifiedImageView = imageview()
    private var selectionRectangle: ResizableRectangle = ResizableRectangle(Group())

    override val root = gridpane {
        addClass(Styles.mainContainer)

        row {
            add(HistogramPanelView())
            vbox {
                addClass(Styles.centeredAlignment)

                group {
                    mainImageView = imageview(FilmPreview) {
                        addClass("mainImageView")

                        fitWidth = FILM_PREVIEW_WINDOW_WIDTH
                        fitHeight = FILM_PREVIEW_WINDOW_HEIGHT
                        isPreserveRatio = true

                        visibleProperty().bind(SettingsPanelProperties.saveInProgress.not())
                    }
                    selectionRectangle = imageviewselection(mainImageView) {
                        addClass(Styles.resizableRectangle)

                        setOnMouseMoved { event -> mainImageView.fireEvent(event) }
                        visibleProperty().bindBidirectional(SettingsPanelProperties.isCropVisible)
                        rectangleProperty().bindBidirectional(SettingsPanelProperties.cropArea)
                    }
                    hbox {
                        addClass(Styles.magnifierView)
                        add(magnifiedImageView)
                        isVisible = false
                    }
                    progressindicator {
                        progress = ProgressIndicator.INDETERMINATE_PROGRESS

                        visibleProperty().bind(SettingsPanelProperties.saveInProgress)
                        visibleProperty().onChange {
                            val selected = it;

                            Platform.runLater {
                                if (selected) {
                                    parent.getChildList()?.removeAll(selectionRectangle.markers)
                                    parent.getChildList()?.remove(selectionRectangle)
                                } else {
                                    parent.getChildList()?.addAll(selectionRectangle.markers)
                                    parent.getChildList()?.add(selectionRectangle)
                                }
                            }
                        }
                    }
                    label {
                        addClass(Styles.dimensionsLabel)

                        FilmPreview.onChange { image ->
                            if (image == null) {
                                return@onChange
                            }

                            layoutXProperty().set(mainImageView.layoutBounds.maxX - width)
                            layoutYProperty().set(mainImageView.layoutBounds.maxY + 10.0)

                            runLater {
                                val dimensions = getImageCropDimensions(image)
                                text = "${dimensions.horizontal}x${dimensions.vertical}px"
                            }
                        }
                    }
                }
            }
            add(SettingsPanelView())
        }

        columnConstraints.addAll(
            ColumnConstraints(
                LEFT_AND_RIGHT_WINDOWS_WIDTH + 40, LEFT_AND_RIGHT_WINDOWS_WIDTH + 40, LEFT_AND_RIGHT_WINDOWS_WIDTH + 40,
                Priority.NEVER, HPos.CENTER, true
            ),
            ColumnConstraints(
                FILM_PREVIEW_WINDOW_WIDTH, FILM_PREVIEW_WINDOW_WIDTH, FILM_PREVIEW_WINDOW_WIDTH,
                Priority.NEVER, HPos.CENTER, true
            ),
            ColumnConstraints(
                LEFT_AND_RIGHT_WINDOWS_WIDTH * 0.5, LEFT_AND_RIGHT_WINDOWS_WIDTH, Double.MAX_VALUE,
                Priority.ALWAYS, HPos.LEFT, true
            )
        )

        rowConstraints.addAll(
            RowConstraints(
                FILM_PREVIEW_WINDOW_HEIGHT, FILM_PREVIEW_WINDOW_HEIGHT, Double.MAX_VALUE,
                Priority.ALWAYS, VPos.CENTER, true
            )
        )
    }

    init {
        var snapshot: Image? = null

        root.setOnKeyPressed { event ->
            if (event.code == KeyCode.ALT) {
                snapshot = createMagnifiedSnapshot()

                magnifiedImageView.parent.visibleProperty().set(true)
            }
        }
        root.setOnKeyReleased { event ->
            if (event.code == KeyCode.ALT) {
                magnifiedImageView.parent.visibleProperty().set(false)
            }
            if (event.code == KeyCode.ESCAPE) {
                selectionRectangle.visibleProperty().set(false)
            }
        }

        mainImageView.setOnMouseMoved { event ->
            if (snapshot == null) {
                return@setOnMouseMoved
            }

            val size = 200.0
            val newX = event.x * snapshot!!.width / mainImageView.boundsInLocal.maxX
            val newY = event.y * snapshot!!.height / mainImageView.boundsInLocal.maxY

            magnifiedImageView.imageProperty().set(snapshot)
            magnifiedImageView.viewport = Rectangle2D(newX - 100, newY - 100, size, size)

            magnifiedImageView.parent.translateX =
                if (event.x + size >= mainImageView.boundsInLocal.maxX) event.x - size - 10 else event.x + 10
            magnifiedImageView.parent.translateY =
                if (event.y + size >= mainImageView.boundsInLocal.maxY) event.y - size - 10 else event.y + 10
        }
    }

    private fun getImageCropDimensions(image: Image): Dimensions {
        if (SettingsPanelProperties.isCropVisible.value) {
            val area = SettingsPanelProperties.cropArea.value
            val scaleFactor = image.width / FILM_PREVIEW_WINDOW_WIDTH

            return Dimensions(
                (area.width * scaleFactor).toInt(),
                (area.height * scaleFactor).toInt()
            )
        }

        return Dimensions(image.width.toInt(), image.height.toInt())
    }

    private fun createMagnifiedSnapshot(): Image {
        if (!selectionRectangle.isVisible) {
            return mainImageView.image
        }

        val image = SwingFXUtils.fromFXImage(mainImageView.image, null)
        val xScale = image.width / mainImageView.boundsInLocal.maxX
        val yScale = image.height / mainImageView.boundsInLocal.maxY
        val graphics = image.createGraphics()
        val stroke = selectionRectangle.stroke as javafx.scene.paint.Color
        val allRectangles = listOf(listOf(selectionRectangle), selectionRectangle.markers).flatten()

        graphics.color = Color(stroke.red.toFloat(), stroke.green.toFloat(), stroke.blue.toFloat())
        graphics.drawRectangles(allRectangles, xScale, yScale)

        return image.toFxImage()
    }
}

fun Graphics.drawRectangles(rectangles: List<Rectangle>, xScaleFactor: Double = 1.0, yScaleFactor: Double = 1.0) {
    for (rectangle in rectangles) {
        drawRect(
            (rectangle.x * xScaleFactor).toInt(),
            (rectangle.y * yScaleFactor).toInt(),
            (rectangle.width * xScaleFactor).toInt(),
            (rectangle.height * yScaleFactor).toInt()
        )
    }
}