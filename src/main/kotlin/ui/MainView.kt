package ui

import darkroom.Darkroom
import tornadofx.*

class MainView : View("Darkroom") {

    override val root = gridpane {
        hgap = 10.0
        vgap = 10.0
        padding = insets(10)
        setMinSize(640.0 + 400.0 + 100.0, 580.0)

        row {
            add(HistogramChartsForFilm)
            imageview(FilmPreview) {
                fitWidth = 640.0
                isPreserveRatio = true
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
    }
}
