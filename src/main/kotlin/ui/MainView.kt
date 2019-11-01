package ui

import darkroom.Darkroom
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.geometry.VPos
import javafx.scene.Group
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
import javafx.scene.paint.Color
import tornadofx.*
import ui.histograms.HistogramPanelView
import ui.selection.ResizableRectangle
import ui.selection.imageviewselection

class MainView : View("Darkroom") {
    private var mainImageView = imageview()
    private val magnifiedImageView = imageview()
    private var selectionRectangle: ResizableRectangle = ResizableRectangle(Group())

    override val root = gridpane {
        hgap = 10.0
        vgap = 10.0
        padding = insets(10)

        row {
            add(HistogramPanelView())
            vbox {
                alignment = Pos.CENTER

                group {
                    mainImageView = imageview(FilmPreview) {
                        fitWidth = FILM_PREVIEW_WINDOW_WIDTH
                        fitHeight = FILM_PREVIEW_WINDOW_HEIGHT
                        isPreserveRatio = true
                    }
                    selectionRectangle = imageviewselection(mainImageView) {
                        visibleProperty().bindBidirectional(SettingsPannelProperties.isCropVisible)
                        rectangleProperty().bindBidirectional(SettingsPannelProperties.cropArea)

                        style {
                            stroke = Color.RED
                            strokeWidth = 1.px
                            fill = Color(1.0, 1.0, 1.0, 0.0)
                        }
                    }
                    hbox {
                        maxWidth = 200.0
                        maxHeight = 200.0

                        add(magnifiedImageView)

                        style {
                            backgroundColor += c("white")
                            borderWidth += box(5.px)
                            borderColor += box(Color.RED)
                        }
                        visibleProperty().set(false)
                    }
                }
            }
            add(SettingsPanelView())
        }
        row {
            button {
                text = "Make a print!"
                useMaxWidth = true
                action {
                    runAsync {
                        isDisable = true
                        Darkroom.printImage()
                        isDisable = false
                    }
                }
                gridpaneConstraints {
                    columnSpan = 3
                }
            }
        }

        columnConstraints.addAll(
            ColumnConstraints(
                LEFT_AND_RIGHT_WINDOWS_WIDTH, LEFT_AND_RIGHT_WINDOWS_WIDTH, LEFT_AND_RIGHT_WINDOWS_WIDTH,
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
        root.setOnKeyPressed { event ->
            if (event.code == KeyCode.ALT) {
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
            val size = 200.0
            val image = mainImageView.image
            val newX = event.x * image.width / mainImageView.boundsInLocal.maxX
            val newY = event.y * image.height / mainImageView.boundsInLocal.maxY

            magnifiedImageView.imageProperty().set(mainImageView.image)
            magnifiedImageView.viewport = Rectangle2D(newX - 100, newY - 100, size, size)

            magnifiedImageView.parent.translateX =
                if (event.x + size >= mainImageView.boundsInLocal.maxX) event.x - size - 10 else event.x + 10
            magnifiedImageView.parent.translateY =
                if (event.y + size >= mainImageView.boundsInLocal.maxY) event.y - size - 10 else event.y + 10
        }
    }
}
