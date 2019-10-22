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
        val chooseDirectory = chooseDirectory {
            title = "Choose prints folder"
        }
        if (chooseDirectory != null) {
            UiProperties.changePrintsLocation(chooseDirectory)
        }
    }
}
