package ui

import darkroom.FilmTypes
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.geometry.Pos
import javafx.scene.control.ToggleGroup
import tornadofx.*

class SettingsPanelView : View() {
    private val toggleGroup = ToggleGroup()

    override val root = squeezebox {
        fillHeight = false

        fold("Save options", expanded = true) {
            vbox {
                spacing = 5.0 // TODO put all spacings somewhere, ideally to styles

                hbox {
                    spacing = 5.0
                    alignment = Pos.CENTER_LEFT

                    label(SettingsPannelProperties.printsFolder)
                    button("Prints Folder") {
                        action {
                            chooseDirectory()
                        }
                    }
                }
                hbox {
                    spacing = 5.0
                    alignment = Pos.CENTER

                    radiobutton(FilmTypes.BLACK_AND_WHITE.displayName, toggleGroup)
                    radiobutton(FilmTypes.COLOR_NEGATIVE.displayName, toggleGroup)
                    radiobutton(FilmTypes.POSITIVE.displayName, toggleGroup)
                }
            }
        }
        fold("Crop and Rotate", expanded = true) {
            flowpane {
                padding = insets(5)
                hgap = 5.0
                vgap = 5.0

                button("flip vertically") {
                    graphic = FontAwesomeIconView(FontAwesomeIcon.ARROW_DOWN)
                }
                button("flip horizontally") {
                    graphic = FontAwesomeIconView(FontAwesomeIcon.ARROW_LEFT)
                }
                button("rotate clockwise") {
                    graphic = FontAwesomeIconView(FontAwesomeIcon.REPEAT)
                }
                button("rotate counter-clockwise") {
                    graphic = FontAwesomeIconView(FontAwesomeIcon.UNDO)
                }
                button("rotate counter-clockwise") {
                    graphic = FontAwesomeIconView(FontAwesomeIcon.CROP)
                }
            }
        }
        fold("Adjust Color", expanded = true) {
            label("Contrast")
            slider()
            label("Brightness")
            slider()
        }
    }

    private fun chooseDirectory() {
        val chosenDirectory = chooseDirectory {
            title = "Choose prints folder"
        }
        if (chosenDirectory != null) {
            SettingsPannelProperties.changePrintsLocation(chosenDirectory)
        }
    }
}
