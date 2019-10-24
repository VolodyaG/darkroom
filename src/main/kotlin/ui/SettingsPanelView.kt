package ui

import javafx.geometry.Pos
import tornadofx.*

class SettingsPanelView : View() {
    override val root = vbox {
        hbox {
            spacing = 5.0
            alignment = Pos.CENTER_LEFT

            label(UiProperties.printsFolder)
            button("Prints Folder") {
                action {
                    chooseDirectory()
                }
            }
        }
    }

    private fun chooseDirectory() {
        val chosenDirectory = chooseDirectory {
            title = "Choose prints folder"
        }
        if (chosenDirectory != null) {
            UiProperties.changePrintsLocation(chosenDirectory)
        }
    }
}
