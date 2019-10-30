package ui

import darkroom.FilmTypes
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Slider
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.util.StringConverter
import javafx.util.converter.NumberStringConverter
import tornadofx.*

class SettingsPanelView : View() {
    private val toggleGroup = ToggleGroup()

    override val root = vbox {
        spacing = 5.0
        alignment = Pos.TOP_RIGHT

        squeezebox {
            fillHeight = false

            fold("General", expanded = true) {
                vbox {
                    spacing = 5.0 // TODO put all spacings somewhere, ideally to styles

                    label("Save to") {
                        addClass(Styles.propertyLabel)
                    }
                    hbox {
                        spacing = 5.0
                        alignment = Pos.CENTER_LEFT

                        label(SettingsPannelProperties.printsFolder)
                        button {
                            useMaxWidth = true
                            graphic = FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN_ALT)

                            setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE)

                            action {
                                chooseDirectory()
                            }
                        }
                    }
                    label("Mode") {
                        addClass(Styles.propertyLabel)
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
            fold("Crop and Rotate", expanded = false) {
                settingsslider("Rotation Angle", SettingsPannelProperties.rotation) {
                    min = -180.0
                    max = 180.0
                    blockIncrement = 90.0
                    majorTickUnit = 90.0
                    isShowTickLabels = true
                    isSnapToTicks = true

                    padding = Insets(0.0, 5.0, 0.0, 5.0)
                }
                button("crop") {
                    graphic = FontAwesomeIconView(FontAwesomeIcon.CROP)
                }
            }
            fold("Adjust Color", expanded = true) {
                settingsslider("Contrast", SettingsPannelProperties.contrast) {
                    min = -0.5
                    max = 0.5
                }
                settingsslider("Brightness", SettingsPannelProperties.brightness) {
                    min = -0.5
                    max = 0.5
                }
            }
        }
        button("Reset")
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

fun Pane.settingsslider(name: String, property: SimpleDoubleProperty, op: Slider.() -> Unit = {}) {
    add(label(name) {
        addClass(Styles.propertyLabel)
    })
    add(hbox {
        alignment = Pos.CENTER

        val slider = slider {
            useMaxWidth = true
            hgrow = Priority.ALWAYS
            valueProperty().bindBidirectional(property)
        }
        textfield {
            prefWidth = 50.0
            hgrow = Priority.NEVER
            bind(property, false, NumberStringConverter())
        }

        slider.op()
    })
}
