package ui

import darkroom.Darkroom
import tornadofx.*
import ui.histograms.HistogramPanelView

class MainView : View("Darkroom") {

    override val root = gridpane {
        hgap = 10.0
        vgap = 10.0
        padding = insets(10)

        row {
            add(HistogramPanelView())
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
