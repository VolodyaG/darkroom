package ui

import darkroom.Darkroom
import tornadofx.*

class MainView : View("Negative Darkroom") {

    override val root = borderpane {
        center {
            useMaxSize = true
            setPrefSize(640.0, 480.0)

            imageview(FilmPreview) {
                fitHeightProperty().bind(parent.prefHeight(640.0).toProperty())
                fitWidthProperty().bind(parent.prefWidth(480.0).toProperty())
            }
        }
        bottom {
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
            }
        }
        right<SettingsPanelView>()
        left = HistogramChartsForFilm.root
    }
}