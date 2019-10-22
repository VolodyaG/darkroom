package ui

import tornadofx.*

class SettingsPanelView : View() {
    override val root = vbox {
        hbox {
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
