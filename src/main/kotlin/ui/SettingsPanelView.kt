package ui

import darkroom.FilmTypes
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.binding.Bindings
import javafx.geometry.Pos
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.Priority
import javafx.util.StringConverter
import javafx.util.converter.NumberStringConverter
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

                button("rotate clockwise") {
                    graphic = FontAwesomeIconView(FontAwesomeIcon.REPEAT)
                }
                button("rotate counter-clockwise") {
                    graphic = FontAwesomeIconView(FontAwesomeIcon.UNDO)
                }
                button("crop") {
                    graphic = FontAwesomeIconView(FontAwesomeIcon.CROP)
                }
            }
        }
        fold("Adjust Color", expanded = true) {
            label("Contrast")
            hbox {
                slider(-127, 127) {
                    useMaxWidth = true
                    hgrow = Priority.ALWAYS
                    valueProperty().bindBidirectional(SettingsPannelProperties.contrast)
                }
                textfield {
                    prefWidth = 50.0
                    hgrow = Priority.NEVER
                    bind(SettingsPannelProperties.contrast, false, NumberStringConverter())
                }
            }
            label("Brightness")
            hbox {
                slider(-127, 127) {
                    useMaxWidth = true
                    hgrow = Priority.ALWAYS
                    valueProperty().bindBidirectional(SettingsPannelProperties.brightness)
                }
                textfield {
                    prefWidth = 50.0
                    hgrow = Priority.NEVER
                    bind(SettingsPannelProperties.brightness, false, NumberStringConverter())
                }
            }
        }
    }

    init {
        Bindings.bindBidirectional(
            toggleGroup.selectedValueProperty<String>(),
            SettingsPannelProperties.filmType,
            FilmTypeStringConverter()
        )
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

class FilmTypeStringConverter : StringConverter<FilmTypes>() {
    override fun toString(filmType: FilmTypes): String {
        return filmType.displayName
    }

    override fun fromString(string: String): FilmTypes {
        return FilmTypes.fromString(string)
    }
}
