package ui

import javafx.application.Platform
import javafx.geometry.HPos
import javafx.geometry.Rectangle2D
import javafx.geometry.VPos
import javafx.scene.Group
import javafx.scene.control.ProgressIndicator
import javafx.scene.input.KeyCode
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints
import tornadofx.*
import ui.histograms.HistogramPanelView
import ui.selection.ResizableRectangle
import ui.selection.imageviewselection

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
                        fitWidth = FILM_PREVIEW_WINDOW_WIDTH
                        fitHeight = FILM_PREVIEW_WINDOW_HEIGHT
                        isPreserveRatio = true

                        visibleProperty().bind(SettingsPanelProperties.saveInProgress.not())
                    }
                    selectionRectangle = imageviewselection(mainImageView) {
                        addClass(Styles.resizableRectangle)

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
