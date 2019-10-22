package ui

import darkroom.Darkroom
import tornadofx.*

class MainView : View("Negative darkroom.Darkroom") {
    override val root = borderpane {
        center {
            useMaxSize = true
            imageview(FilmPreview)
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
    }
}