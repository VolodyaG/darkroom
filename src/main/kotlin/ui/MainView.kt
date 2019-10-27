package ui

import darkroom.Darkroom
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.geometry.VPos
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import tornadofx.*
import ui.histograms.HistogramPanelView

class MainView : View("Darkroom") {
    private val mainImageView = imageview(FilmPreview) {
        fitWidth = FILM_PREVIEW_WINDOW_WIDTH
        fitHeight = FILM_PREVIEW_WINDOW_HEIGHT
        isPreserveRatio = true

        gridpaneConstraints {
            vAlignment = VPos.CENTER
        }
    }
    private val magnifiedImageView = imageview()

    override val root = stackpane {
        gridpane {
            hgap = 10.0
            vgap = 10.0
            padding = insets(10)

            row {
                add(HistogramPanelView())
                add(mainImageView)
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
        }
        hbox {
            maxWidth = 200.0
            maxHeight = 200.0

            add(magnifiedImageView)

            stackpaneConstraints {
                alignment = Pos.TOP_LEFT
            }
            style {
                backgroundColor += c("white")
                borderRadius += box(20.px)
                borderWidth += box(5.px)
                borderColor += box(Color.BLACK)
            }
            visibleProperty().set(false)
        }
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
        }

        mainImageView.setOnMouseMoved { event ->
            val image = mainImageView.image;
            val newX = event.x * image.width / mainImageView.boundsInLocal.maxX;
            val newY = event.y * image.height / mainImageView.boundsInLocal.maxY;

            magnifiedImageView.imageProperty().set(mainImageView.image)
            magnifiedImageView.viewport = Rectangle2D(newX - 100, newY - 100, 200.0, 200.0)
            magnifiedImageView.parent.translateX = event.sceneX + 10
            magnifiedImageView.parent.translateY = event.sceneY + 10
        }
    }
}
